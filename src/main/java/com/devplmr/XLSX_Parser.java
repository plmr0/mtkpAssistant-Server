package com.devplmr;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XLSX_Parser
{
	XLSX_Parser() throws IOException {}

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
						cell.getCellType() == Cell.CELL_TYPE_STRING     &&
						cell.getStringCellValue().contains("-")         &&
						cell.getStringCellValue().length() <= 6
				   )
				{
					groups.add(cell.toString());
				}
			}
		}
		return groups;
	}

	void getSchedule(String group)
	{
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

						for (int i = cell.getRowIndex() + 1; ; i++)
						{
							if (firstSheet.getRow(i).getCell(cell.getColumnIndex()).toString().length() == 0)
							{
								System.out.println("<Пустая пара>");
							}
							else
							{
								subjectCell = firstSheet.getRow(i).getCell(cell.getColumnIndex());
								classCell = firstSheet.getRow(i).getCell(cell.getColumnIndex() + 1);

								if (!subjectCell.toString().contains("вакансия"))
								{
									System.out.println(subjectCell.toString().replace('\n', '\t')+ " - " + classCell.toString());
								}
								else {}
							}
						}
					}
					else {}
				}
			}
		}
		catch (Exception e)
		{

		}
	}
}
