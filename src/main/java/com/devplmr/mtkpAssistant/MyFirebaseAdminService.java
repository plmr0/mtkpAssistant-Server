package com.devplmr.mtkpAssistant;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MyFirebaseAdminService
{
    private Firestore db;

    private String topic;

	public MyFirebaseAdminService(boolean isDebug)
	{
        if (isDebug)
        {
            this.topic = "/topics/debug";
        }
        else
        {
            this.topic = "/topics/push";
        }

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

        FirebaseApp.initializeApp(options);

        this.db = FirestoreClient.getFirestore();
	}

    private void sendNotification(@NotNull Message message)
    {
        String response = null;
        try
        {
            response = FirebaseMessaging.getInstance().send(message);
        }
        catch (FirebaseMessagingException e)
        {
            e.printStackTrace();
        }
        System.out.println("Successfully sent message: " + response);
    }

    private void setUsualNotification(String title, String body)
    {
        Message message = Message.builder()
                .setNotification(new Notification(title, body))
                .setTopic(this.topic)
                .build();

        sendNotification(message);
    }

    private void setCompositeNotification(@NotNull Map<String, String> objectMap)
    {
        Message message = Message.builder()
                .putAllData(objectMap)
                .setTopic(this.topic)
                .build();

        sendNotification(message);
    }

    public void sendNewGroups(@NotNull List<String> groups)
    {
        Map<String, String> objectMap = new HashMap<>();

        objectMap.put("channel_id", "schedule");
        try
        {
            objectMap.put("groups", ObjectSerialization.toString((Serializable) groups));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        setCompositeNotification(objectMap);
    }

    public void sendChanges(@NotNull UpdatedDay updatedDay)
    {
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

        setCompositeNotification(objectMap);
    }

    public void addNewGroupsToFirestore(@NotNull List<String> groupList)
    {
        for (String group : groupList)
        {
            Map<String, Object> groups = new HashMap<>();
            groups.put("subscribers", 0);
            ApiFuture<WriteResult> future = db
                    .collection("groups")
                    .document(group)
                    .set(groups);
            try
            {
                System.out.println("Update time : " + future.get().getUpdateTime());
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
        }
    }
}
