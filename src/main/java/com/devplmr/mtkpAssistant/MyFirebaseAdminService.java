package com.devplmr.mtkpAssistant;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyFirebaseAdminService
{
	public MyFirebaseAdminService()
	{
        FileInputStream serviceAccount = null;
        try
        {
            serviceAccount = new FileInputStream(System.getProperty("user.dir") + "/mtkp-assistant-firebase-adminsdk-rb5gt-919367d8f4.json");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        FirebaseOptions options = null;
        try
        {
            assert serviceAccount != null;
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://mtkp-assistant.firebaseio.com")
                    .build();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        assert options != null;
        FirebaseApp.initializeApp(options);
	}

	private String isDebugSet(boolean isDebug)
    {
        if (isDebug)
        {
            return "/topics/debug";
        }
        else
        {
            return "/topics/push";
        }
    }

	public void sendScheduleAlert(@NotNull GroupSchedule groupSchedule, boolean isDebug)
    {
        /* TODO: УВЕДОМЛЕНИЕ О НОВОМ РАСПИСАНИИ */
    }

    public void sendChanges(@NotNull UpdatedDay updatedDay, boolean isDebug)
    {
        String topic = isDebugSet(isDebug);

        String subjects = "";

        for (int i = 0; i < 6; i++)
        {
            subjects += ((i + 1) + ")\t");

            if (i == 5)
            {
                subjects += updatedDay.getUpdatedDay().get(i);
            }
            else
            {
                subjects += (updatedDay.getUpdatedDay().get(i) + ',');
            }
        }

        Map<String, String> objectMap = new HashMap<>();
        try
        {
            objectMap.put("channel_id", "changes");
            objectMap.put("date", updatedDay.getDateString());
            objectMap.put("groupName", updatedDay.getGroupName());
            objectMap.put("subjects", subjects);
            objectMap.put("isLefortovo", ObjectSerialization.toString(updatedDay.isLefortovo()));
            objectMap.put("isTopWeek", ObjectSerialization.toString(updatedDay.isTopWeek()));
            objectMap.put("isChangesAvailable", ObjectSerialization.toString(updatedDay.isChangesAvailable()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Message message = Message.builder()
                .putAllData(objectMap)
                .setTopic(topic)
                .build();

        String response = null;
        try
        {
            response = FirebaseMessaging.getInstance().send(message);
        }
        catch (FirebaseMessagingException e)
        {
            e.printStackTrace();
        }
        System.out.println("Successfully sent message: " + response); // Debug
    }
}
