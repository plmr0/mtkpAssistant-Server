package com.devplmr.mtkpAssistant;

import java.io.*;
import java.util.Base64;

public class ObjectSerialization
{
	public static Object fromString(String string) throws IOException, ClassNotFoundException
	{
		byte [] data = Base64.getDecoder().decode(string);
		ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		return object;
	}

	public static String toString(Serializable object) throws IOException
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(object);
		objectOutputStream.close();
		return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
	}
}
