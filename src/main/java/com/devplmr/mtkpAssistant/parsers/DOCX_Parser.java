package com.devplmr.mtkpAssistant.parsers;

import java.io.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.devplmr.mtkpAssistant.DateParser;
import com.devplmr.mtkpAssistant.DayChange;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;

public class DOCX_Parser
{
	public static DayChange getChanges(String file, String groupName)
	{
		DayChange dayChange = new DayChange();

		String[] changedSubject;

		boolean isFound = false;

		dayChange.setGroupName(groupName);

		try
		{
			FileInputStream fis = new FileInputStream(file);

			XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));

			Iterator<IBodyElement> bodyElementIterator = xdoc.getBodyElementsIterator();

			while (bodyElementIterator.hasNext())
			{
				IBodyElement element = bodyElementIterator.next();

				if ("PARAGRAPH".equalsIgnoreCase(element.getElementType().name()))
				{
					List<XWPFParagraph> paragraphList = xdoc.getParagraphs();

					String paragraphText = "";
					String notFormattedDate;

					for (XWPFParagraph paragraph: paragraphList)
					{
						paragraphText += (' ' + paragraph.getText().toLowerCase());
					}

					paragraphText = paragraphText
							.replace("   ", " ")
							.replace("  ", " ")
							.replace("\n", "");

					int startIndex = paragraphText.indexOf("на ") + 3;
					int endIndex = paragraphText.indexOf("года") - 1;

					notFormattedDate = paragraphText.substring(startIndex, endIndex);
					String [] changeDate = notFormattedDate
							.replace("  ", " ")
							.split(" ");

					Date dateOfChange = DateParser.getDateByString(DateParser.parseDate(changeDate));
					dayChange.setDate(dateOfChange);

					boolean isTopWeek = !DateParser.getWeekParity(DateParser.getWeekNumber(changeDate));
					dayChange.setTopWeek(isTopWeek);
				}

				if ("TABLE".equalsIgnoreCase(element.getElementType().name()))
				{
					List<XWPFTable> tableList = element.getBody().getTables();

					for (XWPFTable table: tableList)
					{
						for (int i = 0; i < table.getRow(0).getTableCells().size(); i++)
						{
							if (table.getRow(0).getCell(i).getText().contains(groupName))
							{
								if (table.getRow(0).getCell(i).getText().toLowerCase().contains("лефор"))
								{
									dayChange.setLefortovo(true);
								}
								else
								{
									dayChange.setLefortovo(false);
								}

								for (int n = 1; n < table.getNumberOfRows(); n++)
								{
									changedSubject = new String[2];

									String textAtCurrentCell = table.getRow(n).getCell(i).getText();
									String lowerCaseTextAtCurrentCell = textAtCurrentCell.toLowerCase();

									String textUnderCurrentCell = table.getRow(n + 1).getCell(i).getText();

									try
									{
										if (lowerCaseTextAtCurrentCell.contains("отпущ"))
										{
											changedSubject[0] = "*ГРУППА ОТПУЩЕНА*";
											changedSubject[1] = "<?>";
										}
										else if (lowerCaseTextAtCurrentCell.contains("репетиц"))
										{
											changedSubject[0] = "*РЕПЕТИЦИЯ*";
											changedSubject[1] = "<?>";
										}
										else if (lowerCaseTextAtCurrentCell.contains("консульт"))
										{
											changedSubject[0] = "*КОНСУЛЬТАЦИЯ*";
											changedSubject[1] = textUnderCurrentCell;
										}
										else if (lowerCaseTextAtCurrentCell.contains("практ"))
										{
											String cuttedString = lowerCaseTextAtCurrentCell.replace("практика", "");
											String trimmedStringToUpperCase = cuttedString.trim().toUpperCase();

											changedSubject[0] = "*ПРАКТИКА*";
											changedSubject[1] = trimmedStringToUpperCase;
										}
										else if (lowerCaseTextAtCurrentCell.isEmpty())
										{
											changedSubject[0] = "";
											changedSubject[1] = "";
										}
										else
										{
											if ((!textAtCurrentCell.isEmpty()) && (textUnderCurrentCell.isEmpty()))
											{
												changedSubject[0] = textAtCurrentCell;
											    changedSubject[1] = "?";
											}
											else
											{
												changedSubject[0] = textAtCurrentCell;
												changedSubject[1] = textUnderCurrentCell;
											}
										}

										n += 1;

										dayChange.addSubject(changedSubject);
									}
									catch (NullPointerException e)
									{
										/* PASS */
									}
								}
								isFound = true;
							}
						}
						if (isFound)
						{
							dayChange.setChangesAvailable(true);
							return dayChange;
						}
					}
				}
			}

			dayChange.setChangesAvailable(false);
			return dayChange;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return dayChange;
	}
}
