package com.devplmr.mtkpAssistant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayChange
{
	private List<String[]> changedDay = new ArrayList<>();

	private boolean isTopWeek = false;
	private boolean isLefortovo = false;
	private boolean isChangesAvailable = false;

	private Date dateDate;
	private String dateString;

	private String groupName;

	public void addSubject(String[] changedSubject)
	{
		this.changedDay.add(changedSubject);
	}

	public void setTopWeek(boolean isTopWeek)
	{
		this.isTopWeek = isTopWeek;
	}

	public boolean isTopWeek()
	{
		return this.isTopWeek;
	}

	public void setLefortovo(boolean isLefortovo)
	{
		this.isLefortovo = isLefortovo;
	}

	public boolean isLefortovo()
	{
		return this.isLefortovo;
	}

	public void setChangesAvailable(boolean isChangesAvailable)
	{
		this.isChangesAvailable = isChangesAvailable;
	}

	public boolean isChangesAvailable()
	{
		return this.isChangesAvailable;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}

	public String getGroupName()
	{
		return this.groupName;
	}

	public void setDate(Date dateDate)
	{
		this.dateDate = dateDate;
		this.dateString = DateParser.getStringDate(this.dateDate);
	}

	public Date getDateDate()
	{
		return this.dateDate;
	}

	public String getDateString()
	{
		return this.dateString;
	}

	public int getDayOfWeek()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.dateDate);

		int javaDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int myDayOfWeek = -1;

		switch (javaDayOfWeek)
		{
			case 1:
				myDayOfWeek = 6;
				break;

			case 2:
				myDayOfWeek = 0;
				break;

			case 3:
				myDayOfWeek = 1;
				break;

			case 4:
				myDayOfWeek = 2;
				break;

			case 5:
				myDayOfWeek = 3;
				break;

			case 6:
				myDayOfWeek = 4;
				break;

			case 7:
				myDayOfWeek = 5;
				break;
		}

		return myDayOfWeek;
	}

	public List<String[]> getChangedDay()
	{
		return this.changedDay;
	}

	public void debug_printChanges()
	{
		System.out.println("-------------------------------------------------------");
		System.out.println("Замены для " + this.groupName);
		System.out.println("Дата " + this.getDateString());
		System.out.println("Неделя верхняя - " + this.isTopWeek);
		System.out.println("----------------------");
		for (String[] subject : this.changedDay)
		{
			System.out.println(subject[0] + " - " + subject[1]);
		}
		System.out.println("-------------------------------------------------------");
	}
}
