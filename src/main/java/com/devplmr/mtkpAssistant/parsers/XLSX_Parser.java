package com.devplmr.mtkpAssistant.parsers;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class XLSX_Parser
{
	private static final int ACADEMIC_SUBJECT = 2;
	private static final int SUBJECTS_PER_DAY = 12;
	private static final int SUBJECTS_PER_WEEK = 72;

	public static List<String> getGroups(String pathName) throws IOException
	{
		List<String> groups = new ArrayList<>();

		File scheduleFile = new File(pathName);
		FileInputStream fis = new FileInputStream(scheduleFile);
		XSSFWorkbook workBook = new XSSFWorkbook(fis);
		XSSFSheet firstSheet = workBook.getSheetAt(0);

		Iterator<Row> rowIterator = firstSheet.rowIterator();

		while (rowIterator.hasNext())
		{
			Row row = rowIterator.next();

			Iterator<Cell> cellIterator = row.cellIterator();

			while (cellIterator.hasNext())
			{
				Cell cell = cellIterator.next();

				if
				(
						cell.getCellTypeEnum() == CellType.STRING &&
						cell.getStringCellValue().contains("-") &&
						cell.getStringCellValue().length() <= 6
				)
				{
					groups.add(cell.toString());
				}
			}
		}
		return groups;
	}

	public static List<String[][]> getSchedule(String pathName, String groupName) throws IOException
	{
		List<String[][]> wholeWeek = new ArrayList<>();
		String[][] subjectAndClass;

		File scheduleFile = new File(pathName);
		FileInputStream fis = new FileInputStream(scheduleFile);
		XSSFWorkbook workBook = new XSSFWorkbook(fis);
		XSSFSheet firstSheet = workBook.getSheetAt(0);

		try
		{
			for (Row row : firstSheet)
			{
				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext())
				{
					Cell cell = cellIterator.next();

					if (cell.toString().toLowerCase().contains(groupName.toLowerCase()))
					{
						Cell subjectCell;
						Cell classCell;

						int groupCellRowIndex = cell.getRowIndex();
						int subjectStartCellRowIndex = groupCellRowIndex + 1;

						int subject = 0;
						int day = 0;

						boolean isFreeDay = false;

						String currentSubject = "";
						String currentClass = "";

						String lastSubject = "";
						String lastClass = "";

						for (int i = subjectStartCellRowIndex;  i < subjectStartCellRowIndex + SUBJECTS_PER_WEEK; i++)
						{
							if (firstSheet.getRow(i).getCell(cell.getColumnIndex()).toString().length() == 0)
							{
								if (isFreeDay)
								{
									currentSubject = "Выходной";
									currentClass = "<?>";
								}
								else
								{
									currentSubject = "<Нет пары>";
									currentClass = "<?>";
								}
							}
							else
							{
								subjectCell = firstSheet.getRow(i).getCell(cell.getColumnIndex());
								classCell = firstSheet.getRow(i).getCell(cell.getColumnIndex() + 1);

								if (!subjectCell.toString().toLowerCase().contains("вакансия"))
								{
									if (subjectCell.toString().toLowerCase().contains("самост") || isFreeDay)
									{
										isFreeDay = true;
										currentSubject = "Выходной";
										currentClass = "<?>";
									}
									else
									{
										currentClass = classCell.toString();

										currentSubject = subjectCell.toString();

										if (currentSubject.contains("\n"))
										{
											int indexOfN = currentSubject.indexOf("\n");
											currentSubject = currentSubject.substring(0, indexOfN);
										}
										else
										{
											/* TODO: REMOVE TEACHER NAMES */
										}

										if (currentClass.equals(""))
										{
											currentClass = "<?>";
										}

										if (currentClass.contains("."))
										{
											int indexOfDot = currentClass.indexOf(".");
											currentClass = currentClass.substring(0, indexOfDot);
										}
									}
								}
								else
								{
									/* PASS */
								}
							}

							subject++;
							day++;

							if (subject % ACADEMIC_SUBJECT == 0)
							{
								subjectAndClass = new String[2][2];

								subjectAndClass[0][0] = lastSubject;
								subjectAndClass[0][1] = lastClass;

								subjectAndClass[1][0] = currentSubject;
								subjectAndClass[1][1] = currentClass;

								wholeWeek.add(subjectAndClass);
							}

							if (day % SUBJECTS_PER_DAY == 0)
							{
								isFreeDay = false;
							}

							lastSubject = currentSubject;
							lastClass = currentClass;
						}
					}
					else
					{
						/* PASS */
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return wholeWeek;
	}
}
