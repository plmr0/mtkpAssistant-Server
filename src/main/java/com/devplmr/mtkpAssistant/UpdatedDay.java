package com.devplmr.mtkpAssistant;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdatedDay implements Serializable
{
	private List<String[]> updatedDay = new ArrayList<>();
	private String groupName;

	private boolean isTopWeek;
	private boolean isLefortovo;
	private boolean isChangesAvailable;

	private Date dateDate;
	private String dateString;

	public UpdatedDay(@NotNull GroupSchedule groupSchedule, @NotNull DayChange dayChange)
	{
		this.isTopWeek = dayChange.isTopWeek();
		this.isLefortovo = dayChange.isLefortovo();
		this.isChangesAvailable = dayChange.isChangesAvailable();
		this.dateDate = dayChange.getDateDate();
		this.dateString = dayChange.getDateString();

		List<String[]> scheduleForCurrentDay = new ArrayList<>();
		List<String[][]> fullDay = groupSchedule.getDay(dayChange.getDayOfWeek());

		this.groupName = dayChange.getGroupName();

		int indexForWeek = this.isTopWeek ? 0 : 1;

		for (String [][] subject: fullDay)
		{
			scheduleForCurrentDay.add(subject[indexForWeek]);
		}

		if (this.isChangesAvailable)
		{
			int indexOfPratice = -1;
			boolean isPractice = false;
			String[] practiceString = null;

			for (int j = 0; j < 6; j++)
			{
				String[] tempChangedSubject = dayChange.getChangedDay().get(j);
				String tempSubject = tempChangedSubject[0];

				if (tempSubject.toLowerCase().contains("*прак"))
				{
					indexOfPratice = j;
					practiceString = tempChangedSubject;
					isPractice = true;
					break;
				}
			}

			for (int i = 0; i < 6; i++)
			{
				String[] currentScheduleSubject = scheduleForCurrentDay.get(i);

				String[] currentChangedSubject = dayChange.getChangedDay().get(i);
				String changedSubject = currentChangedSubject[0];

				if (isPractice)
				{
					String[] emptySubjectForPractice = new String[] {"<Нет пары>", "<?>"};

					if (i == indexOfPratice)
					{
						this.updatedDay.add(practiceString);
					}
					else if (i < indexOfPratice)
					{
						this.updatedDay.add(emptySubjectForPractice);
					}
					else if (i < indexOfPratice + 3)
					{
						this.updatedDay.add(practiceString);
					}
					else
					{
						this.updatedDay.add(emptySubjectForPractice);
					}
				}
				else
				{
					if (changedSubject.length() == 0)
					{
						this.updatedDay.add(currentScheduleSubject);
					}
					else
					{
						this.updatedDay.add(currentChangedSubject);
					}
				}
			}
		}
		else
		{
			this.updatedDay = scheduleForCurrentDay;
		}
	}

	public boolean isTopWeek()
	{
		return this.isTopWeek;
	}

	public boolean isLefortovo()
	{
		return this.isLefortovo;
	}

	public boolean isChangesAvailable()
	{
		return this.isChangesAvailable;
	}

	public Date getDateDate()
	{
		return this.dateDate;
	}

	public String getDateString()
	{
		return this.dateString;
	}

	public String getGroupName()
	{
		return this.groupName;
	}

	public void debug_printUpdatedDay()
	{
		System.out.println("-------------------------------------------------------");
		System.out.println("Итог для " + this.groupName);
		System.out.println("Дата: " + this.dateString);
		System.out.println("----------------------");
		for (String[] subject : this.updatedDay)
		{
			System.out.println(subject[0] + " - " + subject[1]);
		}
		System.out.println("-------------------------------------------------------");
	}

	public List<String> getUpdatedDay()
	{
		List<String> subjects = new ArrayList<>();

		for (String [] subject : this.updatedDay)
		{
			subjects.add(subject[0] + " ‒ " + subject[1]);
		}

		return subjects;
	}
}
