package com.devplmr;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdatedDay
{
	private List<String[]> updatedDay = new ArrayList<>();
	private String groupName;

	private boolean isTopWeek = false;
	private boolean isLefortovo = false;
	private boolean isChangesAvailable = false;

	private Date date;

	public UpdatedDay(@NotNull GroupSchedule groupSchedule, @NotNull DayChange dayChange)
	{
		this.isTopWeek = dayChange.isTopWeek();
		this.isLefortovo = dayChange.isLefortovo();
		this.isChangesAvailable = dayChange.isChangesAvailable();
		this.date = dayChange.getDate();

		List<String[]> scheduleForCurrentDay = new ArrayList<>();
		List<String[][]> fullDay = groupSchedule.getDay(dayChange.getDayOfWeek());

		this.groupName = dayChange.getGroupName();

		int indexForWeek = this.isTopWeek ? 0 : 1;

		for (String subject[][]: fullDay)
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

	public void debug_printUpdatedDay()
	{
		System.out.println("-------------------------------------------------------");
		System.out.println("Итог для " + this.groupName);
		System.out.println("Дата: " + this.date);
		System.out.println("----------------------");
		for (String[] subject : this.updatedDay)
		{
			System.out.println(subject[0] + " - " + subject[1]);
		}
		System.out.println("-------------------------------------------------------");
	}
}
