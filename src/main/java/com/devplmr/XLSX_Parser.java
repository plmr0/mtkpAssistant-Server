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
	public XLSX_Parser() throws IOException {}

	List<String> getGroups() throws IOException
	{
		List<String> groups = new ArrayList<>();

		File myFile = new File(""); // todo automate pathname
		FileInputStream fis = new FileInputStream(myFile);

		XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);

		XSSFSheet mySheet = myWorkBook.getSheetAt(0);

		Iterator<Row> rowIterator = mySheet.rowIterator();

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

	void getSchedule(String group) {} // todo function
}
