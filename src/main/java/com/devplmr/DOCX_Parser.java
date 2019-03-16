package com.devplmr;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

public class DOCX_Parser
{

	public static String [] getChanges(String file, String group)
	{
		boolean isFound = false;
		String resultLine = "";
		String [] resultLineList = {"", "", "", "", "", ""};

		try
		{
			FileInputStream fis = new FileInputStream(file);

			XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));

			Iterator<IBodyElement> bodyElementIterator = xdoc.getBodyElementsIterator();

			while (bodyElementIterator.hasNext())
			{
				IBodyElement element = bodyElementIterator.next();

				if ("TABLE".equalsIgnoreCase(element.getElementType().name()))
				{
					List<XWPFTable> tableList = element.getBody().getTables();

					for (XWPFTable table: tableList)
					{
						for (int i = 0; i < table.getRow(0).getTableCells().size(); i++)
						{
							if (table.getRow(0).getCell(i).getText().contains(group))
							{
								if (table.getRow(0).getCell(i).getText().contains("Лефортово"))
								{
									ServerThread.dateOfFileNextDayString += " (Лефортово)";
								}
								for (int n = 1; n <= table.getNumberOfRows(); n++)
								{
									try
									{
										if (table.getRow(n).getCell(i + (-1 + i)).getText().equals("группа отпущена"))
										{
											resultLine = "[" + n + "] " + "*ГРУППА ОТПУЩЕНА*";
										}
										else if (table.getRow(n).getCell(i + (-1 + i)).getText().isEmpty())
										{
											resultLine = "[" + n + "]";
										}
										else if ((!table.getRow(n).getCell(i + (-1 + i)).getText().isEmpty()) && (table.getRow(n).getCell(i + (-1 + (i + 1))).getText().isEmpty()))
										{
											resultLine = "[" + n + "] " + table.getRow(n).getCell(i + (-1 + i)).getText() + " - " + "?";
										}
										else
											resultLine = "[" + n + "] " + table.getRow(n).getCell(i + (-1 + i)).getText() + " - " + table.getRow(n).getCell(i + (-1 + (i + 1))).getText();
									}
									catch (NullPointerException e)
									{
										continue;
									}
									resultLineList[n-1] = resultLine;
								}
								isFound = true;
							}
						}
						if (isFound)
							return resultLineList;
					}
				}
			}

			if (!isFound)
			{
				resultLineList[0] = "Замен нет.";
				return resultLineList;
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return resultLineList;
	}

}
