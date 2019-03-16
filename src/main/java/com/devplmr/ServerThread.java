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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class ServerThread extends Thread
{
	private long dateOfFileCreatedTimestamp;
	static public String dateOfFileNextDayString;
	private String currentDateString;

	int timestampSchedule = 0;

	void downloadFile(String url, String outputFilename) throws IOException
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
				getQuery = vk.wall().get(new ServiceActor(VKData.appId, VKData.accessToken))
						.ownerId(VKData.groupId)
						.count(5)
						.filter(WallGetFilter.OWNER)
						.execute();

				List<WallPostFull> wallPosts = getQuery.getItems();

				for (WallPostFull wallPost : wallPosts)
				{
					if (wallPost.toString().contains("Замены на"))
					{
						dateOfFileNextDayString = wallPost.getAttachments().get(0).getDoc().getTitle();
						dateOfFileCreatedTimestamp = wallPost.getDate();

						Changes.currentChangeDay = dateOfFileNextDayString;

						downloadFile(wallPost.getAttachments().get(0).getDoc().getUrl(), Changes.currentChangeDay);

						SimpleDateFormat _dd_mm_yy = new SimpleDateFormat("dd.MM.YY");

						Timestamp fileDate = new Timestamp(dateOfFileCreatedTimestamp * 1000);
						Timestamp currentDate = new Timestamp(System.currentTimeMillis());

						dateOfFileNextDayString = dateOfFileNextDayString.replace("Замены на ", "").replace(".docx", "");
						currentDateString = _dd_mm_yy.format(currentDate);

						if (DateMap.checkChangeMap(dateOfFileNextDayString, dateOfFileCreatedTimestamp))
						{
							String arr [] = DOCX_Parser.getChanges(Changes.currentChangeDay, GroupInfo.name);

							if (arr[0].equals("Замен нет."))
							{
								String cmd [] = {
										"python",
										"send_empty_notification.py",
										"Замены на " + dateOfFileNextDayString,
										arr[0]
								};

								Process proc = Runtime.getRuntime().exec(cmd);
								proc.waitFor();
								proc.destroy();
							}
							else
							{
								String cmd [] = {
										"python",
										"send_notification.py",
										"Замены на " + dateOfFileNextDayString,
										arr[0],
										arr[1],
										arr[2],
										arr[3],
										arr[4],
										arr[5]
								};

								Process proc = Runtime.getRuntime().exec(cmd);
								proc.waitFor();
								proc.destroy();
							}

						}
						else
						{

						}
					}
					try
					{
						if (wallPost.getIsPinned() == 1)
						{
							if (timestampSchedule == 0)
							{
								downloadFile(wallPost.getAttachments().get(0).getDoc().getUrl(), wallPost.getAttachments().get(0).getDoc().getTitle());

								timestampSchedule = wallPost.getDate();
							}
							else if (wallPost.getDate() > timestampSchedule)
							{
								downloadFile(wallPost.getAttachments().get(0).getDoc().getUrl(), wallPost.getAttachments().get(0).getDoc().getTitle());

								timestampSchedule = wallPost.getDate();
							}
							else
							{

							}
						}
					}
					catch (NullPointerException e)
					{

					}
				}
			}
			catch (ApiException e)
			{
				e.printStackTrace();
			}
			catch (ClientException e)
			{
				e.printStackTrace();
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			try
			{
				sleep(20000);
			}
			catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}
		}
	}

}
