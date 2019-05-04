package com.devplmr;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateParser
{
	private static int getMonthNumber(@NotNull String month) throws ArrayIndexOutOfBoundsException
	{
		switch (month.toLowerCase())
		{
			case "января":
				return 1;

			case "февраля":
				return 2;

			case "марта":
				return 3;

			case "апреля":
				return 4;

			case "мая":
				return 5;

			case "июня":
				return 6;

			case "июля":
				return 7;

			case "августа":
				return 8;

			case "сентября":
				return 9;

			case "октября":
				return 10;

			case "ноября":
				return 11;

			case "декабря":
				return 12;

		}

		throw new ArrayIndexOutOfBoundsException("Month number not found.");
	}

	public static String parseDate(@NotNull String[] inputDate)
	{
		String inputDay = inputDate[0];
		String inputMonth = inputDate[1];
		String inputYear = inputDate[2];

		String stringMonth;
		if (getMonthNumber(inputMonth) >= 10)
		{
			stringMonth = String.valueOf(getMonthNumber(inputMonth));
		}
		else
		{
			stringMonth = "0" + getMonthNumber(inputMonth);
		}

		String parsedDate = inputDay + stringMonth + inputYear;

		return parsedDate;
	}

	public static Date getDateByString(String inputDate)
	{
		String dateFormat = "ddMMyyyy";
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);

		Date date;
		{
			try
			{
				date = df.parse(inputDate);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
				date = null;
			}
		}

		return date;
	}

	public static int getWeekNumber(@NotNull String[] inputDate)
	{
		String input = parseDate(inputDate);
		Date date = getDateByString(input);

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int weekNumber = c.get(Calendar.WEEK_OF_YEAR);

		return weekNumber;
	}

	public static int getDayOfWeek(@NotNull String[] inputDate)
	{
		String input = parseDate(inputDate);
		Date date = getDateByString(input);

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

		return dayOfWeek;
	}

	public static boolean getWeekParity(int weekNumber)
	{
		return weekNumber % 2 == 0;
	}
}
