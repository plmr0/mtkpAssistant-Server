package com.devplmr.mtkpAssistant;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectIO
{
	public static void writeToFile(@NotNull Object object, @NotNull String filePath)
	{
		try
		{
			FileOutputStream fileOut = new FileOutputStream(filePath);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

			objectOut.writeObject(object);
			objectOut.close();

			System.out.println("The Object " + object.toString() + " was succesfully written to a file");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static Object readFromFile(@NotNull String filePath)
	{
		try
		{
			FileInputStream fileIn = new FileInputStream(filePath);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);

			Object object = objectIn.readObject();
			objectIn.close();

			System.out.println("The Object " + object.toString() + " has been read from the file");
			return object;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
