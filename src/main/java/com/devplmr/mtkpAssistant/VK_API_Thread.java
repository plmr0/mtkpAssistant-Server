package com.devplmr.mtkpAssistant;

import com.devplmr.mtkpAssistant.parsers.DOCX_Parser;
import com.devplmr.mtkpAssistant.parsers.XLSX_Parser;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.docs.Doc;
import com.vk.api.sdk.objects.wall.WallPost;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostAttachmentType;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.wall.WallGetFilter;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.SQLException;
import java.util.List;

public class VK_API_Thread extends Thread
{
	private File getLastFileModified(String dir)
	{
		File fl = new File(dir);
		File[] files = fl.listFiles(new FileFilter()
		{
			public boolean accept(File file)
			{
				return file.isFile();
			}
		});
		long lastMod = Long.MIN_VALUE;
		File choice = null;
		for (File file : files)
		{
			if (file.lastModified() > lastMod)
			{
				choice = file;
				lastMod = file.lastModified();
			}
		}
		return choice;
	}

	private void downloadFile(String url, String outputFilename) throws IOException
	{
		URL website = new URL(url);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(outputFilename);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	}

	public VK_API_Thread()
	{
		File scheduleFolder = new File("SCHEDULE");
		if (!scheduleFolder.exists())
		{
			scheduleFolder.mkdir();
		}
		else
		{
			/* PASS */
		}

		File changesFolder = new File("CHANGES");
		if (!changesFolder.exists())
		{
			changesFolder.mkdir();
		}
		else
		{
			/* PASS */
		}
	}

	public final String GROUPNAME = "ТМП-72"; // TODO: TEMPORARY

	private static final String USER_DIR = System.getProperty("user.dir");
	private static final String PATH_TO_SCHEDULE_FOLDER = USER_DIR  + "/SCHEDULE/";
	private static final String PATH_TO_CHANGES_FOLDER = USER_DIR + "/CHANGES/";

	private String pathToCurrentSchedule = getLastFileModified(PATH_TO_SCHEDULE_FOLDER).toString();

	private DateMap mapForSchedule = new DateMap();
	private DateMap mapForChanges = new DateMap();

	@Override
	public void run()
	{
		TransportClient transportClient = HttpTransportClient.getInstance();
		VkApiClient vk = new VkApiClient(transportClient);
		GetResponse getQuery;

		MyFirebaseAdminService myFirebaseAdminService = new MyFirebaseAdminService();

		while (true)
		{
			try
			{
				getQuery = vk.wall().get(new ServiceActor(VK_Data.APP_ID, VK_Data.ACCESS_TOKEN))
						.ownerId(VK_Data.GROUP_ID)
						.count(5)
						.filter(WallGetFilter.OWNER)
						.execute();

				List<WallPostFull> respondedWallPosts = getQuery.getItems();

				for (WallPostFull wallPost : respondedWallPosts)
				{
					List<WallpostAttachment> wallpostAttachments;

					if (wallPost.getAttachments() == null)
					{
						continue;
					}
					else
					{
						wallpostAttachments = wallPost.getAttachments();
					}

					String lowerCaseWallpostDocText;

					for (WallpostAttachment wallpostAttachment: wallpostAttachments)
					{
						if (wallpostAttachment.getType() == WallpostAttachmentType.DOC)
						{
							lowerCaseWallpostDocText = wallpostAttachment.getDoc().getTitle().toLowerCase();

							if (lowerCaseWallpostDocText.contains("замены на "))
							{
								DocAsAttachment docAsAttachment = new DocAsAttachment(wallpostAttachment.getDoc());

								if (mapForChanges.checkValue(docAsAttachment.getCuttedTitleOfFile(), docAsAttachment.getTimestampOfFileCreation()))
								{
									String changeFilepath = PATH_TO_CHANGES_FOLDER + docAsAttachment.getTitleOfFile();

									downloadFile(docAsAttachment.getUrl(), changeFilepath);

									GroupSchedule groupSchedule = new GroupSchedule(GROUPNAME, XLSX_Parser.getSchedule(pathToCurrentSchedule, GROUPNAME));
									UpdatedDay updatedDay = new UpdatedDay(groupSchedule, DOCX_Parser.getChanges(changeFilepath, GROUPNAME));

									myFirebaseAdminService.sendChanges(updatedDay, true);

									/* TODO: ОБРАБОТКА ЗАМЕН */
								}
								else
								{
									/* PASS */
								}
							}
							else if (lowerCaseWallpostDocText.contains("мткп") && wallPost.getIsPinned() == 1)
							{
								DocAsAttachment docAsAttachment = new DocAsAttachment(wallpostAttachment.getDoc());

								if (mapForSchedule.checkValue(docAsAttachment.getCuttedTitleOfFile(), docAsAttachment.getTimestampOfFileCreation()))
								{
									String scheduleFilepath = PATH_TO_SCHEDULE_FOLDER + docAsAttachment.getTitleOfFile();

									downloadFile(docAsAttachment.getUrl(), scheduleFilepath);

									pathToCurrentSchedule = scheduleFilepath;

									/* TODO: УВЕДОМЛЕНИЕ О НОВОМ РАСПИСАНИИ */
								}
								else
								{
									/* PASS */
								}
							}
							else
							{
								/* OTHER POSTS - PASS */
							}
						}
					}
				}
			}
			catch (ApiException | IOException | ClientException e)
			{
				e.printStackTrace();
			}

			try
			{
				Thread.sleep(20000);
			}
			catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}
		}
	}

}
