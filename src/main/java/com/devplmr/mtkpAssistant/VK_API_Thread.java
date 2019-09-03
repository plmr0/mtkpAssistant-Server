package com.devplmr.mtkpAssistant;

import com.devplmr.mtkpAssistant.parsers.XLSX_Parser;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.docs.Doc;
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

	private static final String USER_DIR = System.getProperty("user.dir");
	private static final String PATH_TO_SCHEDULE_FOLDER = USER_DIR  + "/SCHEDULE/";
	private static final String PATH_TO_CHANGES_FOLDER = USER_DIR + "/CHANGES/";

	private DateMap mapForSchedule = new DateMap();
	private DateMap mapForChanges = new DateMap();

	private void downloadFile(String url, String outputFilename) throws IOException
	{
		URL website = new URL(url);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(outputFilename);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	}

	@Override
	public void run()
	{
		DB_Handler db_handler = null;
		try
		{
			db_handler = new DB_Handler();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		TransportClient transportClient = HttpTransportClient.getInstance();
		VkApiClient vk = new VkApiClient(transportClient);
		GetResponse getQuery;

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

				WallPosts wallPosts = new WallPosts(respondedWallPosts);

				for (WallPostFull wallPost : wallPosts.getWallPosts())
				{
					List<WallpostAttachment> wallpostAttachments = wallPost.getAttachments();

					boolean isChanges = false;
					boolean isSchedule = false;
					Doc schedule;
					Doc changes;
					String lowerCaseWallpostDocText;

					for (WallpostAttachment wallpostAttachment: wallpostAttachments)
					{
						if (wallpostAttachment.getType() == WallpostAttachmentType.DOC)
						{
							lowerCaseWallpostDocText = wallpostAttachment.getDoc().getTitle().toLowerCase();

							if (lowerCaseWallpostDocText.contains("замены на "))
							{
								isChanges = true;
								changes = wallpostAttachment.getDoc();
							}
							else if (lowerCaseWallpostDocText.contains("мткп") && wallPost.getIsPinned() == 1)
							{
								isSchedule = true;
								schedule = wallpostAttachment.getDoc();
							}
						}
					}



					/* TODO: ------------------------------ ПЕРЕПИСАТЬ НИЖЕ ------------------------------ */



					if (isChanges)
					{
						wallPosts.attachedFile.setAttachedFile(wallPost);

						if (mapForChanges.checkValue(wallPosts.attachedFile.getCuttedTitleOfFile(), wallPosts.attachedFile.getTimestampOfFileCreation()))
						{
							String changeFilepath = PATH_TO_CHANGES_FOLDER + wallPosts.attachedFile.getTitleOfFile();

							downloadFile(wallPosts.attachedFile.getUrl(), changeFilepath);

							/* TODO: ОБРАБОТКА ЗАМЕН */
						}
						else
						{
							/* PASS */
						}
					}
					else if (isSchedule)
					{
						wallPosts.attachedFile.setAttachedFile(wallPost);

						if (mapForSchedule.checkValue(wallPosts.attachedFile.getCuttedTitleOfFile(), wallPosts.attachedFile.getTimestampOfFileCreation()))
						{
							String scheduleFilepath = PATH_TO_SCHEDULE_FOLDER + wallPosts.attachedFile.getTitleOfFile();

							downloadFile(wallPosts.attachedFile.getUrl(), scheduleFilepath);

							List<String> groupList = XLSX_Parser.getGroups(scheduleFilepath);
							db_handler.insertGroups(groupList);

							for (String group : groupList)
							{
								GroupSchedule groupSchedule = new GroupSchedule(group, XLSX_Parser.getSchedule(scheduleFilepath, group));

								ObjectIO.writeToFile(groupSchedule, groupSchedule.getFilePath());
							}

							/* TODO: УВЕДОМЛЕНИЕ ДЛЯ МОБИЛЬНЫХ УСТРОЙСТВ О НОВОМ РАСПИСАНИИ */
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



					/* TODO: ------------------------------ ПЕРЕПИСАТЬ ВЫШЕ ------------------------------ */



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
