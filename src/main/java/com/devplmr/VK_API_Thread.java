package com.devplmr;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.wall.WallGetFilter;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
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
					String lowerCaseWallPostText = wallPost.toString().toLowerCase();
					boolean isChanges;
					try
					{
						isChanges = lowerCaseWallPostText.contains("замены на ");
					}
					catch (NullPointerException e)
					{
						isChanges = false;
					}

					boolean isSchedule;
					try
					{
						isSchedule = wallPost.getIsPinned() == 1;
					}
					catch (NullPointerException e)
					{
						isSchedule = false;
					}

					if (isChanges)
					{
						wallPosts.attachedFile.setAttachedFile(wallPost);

						if (mapForChanges.checkValue(wallPosts.attachedFile.getCuttedTitleOfFile(), wallPosts.attachedFile.getTimestampOfFileCreation()))
						{
							downloadFile(wallPosts.attachedFile.getUrl(), PATH_TO_CHANGES_FOLDER + wallPosts.attachedFile.getTitleOfFile());

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
							downloadFile(wallPosts.attachedFile.getUrl(), PATH_TO_SCHEDULE_FOLDER + wallPosts.attachedFile.getTitleOfFile());

							/* TODO: ОБРАБОТКА РАСПИСАНИЯ */
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
