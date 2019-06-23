package com.devplmr.mtkpAssistant;

import com.devplmr.mtkpAssistant.parsers.DOCX_Parser;
import com.devplmr.mtkpAssistant.parsers.XLSX_Parser;

import java.io.*;

public class Main
{
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
//	    VK_API_Thread serverThread = new VK_API_Thread();
//	    serverThread.start();

        MyFirebaseAdminService myFirebaseAdminService = new MyFirebaseAdminService();

//	    GroupSchedule groupSchedule = new GroupSchedule("ТМП-62", XLSX_Parser.getSchedule("C:\\Users\\Ilya\\IdeaProjects\\mtkpAssistant\\mtkpAssistant-Server\\SCHEDULE\\МТКП_5.xlsx", "ТМП-62"));
//	    groupSchedule.debug_printWholeWeek();

//	    ObjectIO.writeToFile(groupSchedule, groupSchedule.getFilePath());

//	    Object gs1 = ObjectIO.readFromFile("C:\\Users\\Ilya\\IdeaProjects\\mtkpAssistant\\mtkpAssistant-Server\\GROUP_SCHEDULE_FILES\\ТМП-64");
//
//	    if (gs1 instanceof GroupSchedule)
//	    {
//		    GroupSchedule groupSchedule1 = (GroupSchedule) gs1;
//		    groupSchedule1.debug_printDay((groupSchedule1.getDay(2)));
//		    groupSchedule1.debug_printWholeWeek();
//	    }

//	    DateParser dateParser = new DateParser();
//	    System.out.println(dateParser.getWeekNumber());

//		DayChange dayChange = DOCX_Parser.getChanges("C:\\Users\\Ilya\\IdeaProjects\\mtkpAssistant\\mtkpAssistant-Server\\CHANGES\\Замены на 24.06.19.docx", "ТМП-62");
//		dayChange.debug_printChanges();
//
//	    UpdatedDay updatedDay = new UpdatedDay(groupSchedule, dayChange);
//	    updatedDay.debug_printUpdatedDay();

//        String kek = ObjectSerialization.toString(updatedDay);
//
//        UpdatedDay lol = (UpdatedDay) ObjectSerialization.fromString(kek);
//        lol.debug_printUpdatedDay();

//	    DB_Handler dbHandler = null;
//	    try
//	    {
//		    dbHandler = new DB_Handler();
//	    }
//	    catch (SQLException e)
//	    {
//		    e.printStackTrace();
//	    }
//
//	    dbHandler.createNewDatabase();
//	    dbHandler.createNewGroupTable();
//
//	    try
//	    {
//		    dbHandler.insertGroups(new XLSX_Parser().getGroups());
//	    }
//	    catch (IOException e)
//	    {
//		    e.printStackTrace();
//	    }
    }
}
