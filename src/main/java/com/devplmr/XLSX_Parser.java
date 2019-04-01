package com.devplmr;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class XLSX_Parser
{
	XLSX_Parser() throws IOException {}

	private final int ACADEMIC_SUBJECT = 2;
	private final int SUBJECTS_PER_DAY = 12;
	private final int SUBJECTS_PER_WEEK = 72;

	private File scheduleFile = new File("МТКП_4.xlsx"); // todo automate pathname
	private FileInputStream fis = new FileInputStream(scheduleFile);
	private XSSFWorkbook workBook = new XSSFWorkbook(fis);
	private XSSFSheet firstSheet = workBook.getSheetAt(0);

	List<String> getGroups() throws IOException
	{
		List<String> groups = new ArrayList<>();

		Iterator<Row> rowIterator = firstSheet.rowIterator();

		while (rowIterator.hasNext())
		{
			Row row = rowIterator.next();

			Iterator<Cell> cellIterator = row.cellIterator();

			while (cellIterator.hasNext())
			{
				Cell cell = cellIterator.next();

				if (
						cell.getCellType() == Cell.CELL_TYPE_STRING &&
						cell.getStringCellValue().contains("-") &&
						cell.getStringCellValue().length() <= 6)
				{
					groups.add(cell.toString());
				}
			}
		}
		return groups;
	}

	Object[] getSchedule(String group)
	{
		Map<String, String> currentSubjectAndClass = null;
		Object[] oneAcademicSubject = new Object[2];
		Object[] dayOfWeek = new Object[6];
		Object[] scheduleForWeek = new Object[6];

		try
		{
			for (Row row : firstSheet)
			{
				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext())
				{
					Cell cell = cellIterator.next();

					if (cell.toString().toLowerCase().contains(group.toLowerCase()))
					{
						Cell subjectCell;
						Cell classCell;

						int groupCellRowIndex = cell.getRowIndex();
						int subjectStartCellRowIndex = groupCellRowIndex + 1;

						int subject = 0;
						int day = 0;

						boolean isFreeDay = false;

						String currentSubject = "";
						String lastSubject = "";

						for (int i = subjectStartCellRowIndex;  i < subjectStartCellRowIndex + SUBJECTS_PER_WEEK; i++)
						{
							if (firstSheet.getRow(i).getCell(cell.getColumnIndex()).toString().length() == 0)
							{
								if (isFreeDay)
									currentSubject = "Выходной";
								else
									currentSubject = "<Пустая пара>";
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
									}
									else
										currentSubject = subjectCell.toString().replace("\n", "\\n") + " -- " + classCell.toString();
								}
								else {}
							}

							subject++;
							day++;

							if (subject % ACADEMIC_SUBJECT == 0)
							{
								System.out.println(lastSubject);
								System.out.println(currentSubject);
								System.out.println("---------------------");
							}
							if (day % SUBJECTS_PER_DAY == 0)
							{
								System.out.println("----------------------------------------------");
								isFreeDay = false;
							}

							lastSubject = currentSubject;
						}
					}
					else {}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return scheduleForWeek;
	}
}
