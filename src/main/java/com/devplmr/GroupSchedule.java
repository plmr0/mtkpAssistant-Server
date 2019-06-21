package com.devplmr;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupSchedule implements Serializable
{
	private String groupName;
	private String filePath;
	private List<String[][]> wholeWeek;

	public GroupSchedule(@NotNull String groupName, @NotNull List<String[][]> wholeWeek)
	{
		this.groupName = groupName;
		this.filePath = System.getProperty("user.dir") + "\\GROUP_SCHEDULE_FILES\\" + groupName;
		this.wholeWeek = wholeWeek;

		File scheduleByGroupFilesFolder = new File("GROUP_SCHEDULE_FILES");
		if (!scheduleByGroupFilesFolder.exists())
		{
			scheduleByGroupFilesFolder.mkdir();
		}
		else
		{
			/* PASS */
		}
	}

	@Deprecated
	public void setGroupNameAndFilePath(String groupName)
	{
		this.groupName = groupName;
		this.filePath = System.getProperty("user.dir") + "\\GROUP_SCHEDULE_FILES\\" + groupName;
	}

	@Deprecated
	public void setWholeWeek(List<String[][]> wholeWeek)
	{
		this.wholeWeek = wholeWeek;
	}

	@NotNull
	public String getGroupName()
	{
		return this.groupName;
	}

	@NotNull
	public String getFilePath()
	{
		return this.filePath;
	}

	public void fillNull()
	{
		String[] nullStr = new String[] {"<Нет пары>", "<?>"};
		String[][] nullArr = new String[][] {nullStr, nullStr};

		for (int i = 0; i < 36; i++)
		{
			this.wholeWeek.add(nullArr);
		}
	}

	public void addSubjectPerDay(@NotNull String[][] subjectPerDay)
	{
		this.wholeWeek.add(subjectPerDay);
	}

	public List<String[][]> getWholeWeek()
	{
		return this.wholeWeek;
	}

	public void debug_printWholeWeek()
	{
		System.out.println("\t\t\t\tРасписание для <" + this.groupName + ">");
		System.out.println("-------------------------------------------------------");

		for (int i = 0; i < this.wholeWeek.size(); i++)
		{
			String[][] academicSubject = this.getWholeWeek().get(i);

			for (String[] strings : academicSubject)
			{
				System.out.println("[" + i + "] " + strings[0] + " - " + strings[1]);
			}

			System.out.println("----------------------");

			if ((i + 1) % 6 == 0)
			{
				System.out.println("-------------------------------------------------------");
			}
		}
	}

	public List<String[][]> getDay(int dayNumber)
	{
		List<String[][]> tempDay = new ArrayList<>();

		int startIndex = 6 * dayNumber;
		int dayLength = startIndex + 6;

		for (int i = startIndex; i < dayLength; i++)
		{
			tempDay.add(this.wholeWeek.get(i));
		}

		return tempDay;
	}

	public void debug_printDay(@NotNull List<String[][]> day)
	{
		for (int i = 0; i < day.size(); i++)
		{
			String[][] academicSubject = day.get(i);

			for (String[] strings : academicSubject)
			{
				System.out.println("[" + i + "] " + strings[0] + " - " + strings[1]);
			}

			System.out.println("----------------------");
		}
	}
}
