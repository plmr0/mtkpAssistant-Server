package com.devplmr.mtkpAssistant;

import com.vk.api.sdk.objects.docs.Doc;
import org.jetbrains.annotations.NotNull;

public class DocAsAttachment
{
	DocAsAttachment(@NotNull Doc document)
	{
		this.document = document;
	}

	private Doc document = null;

	public String getTitleOfFile()
	{
		return this.document.getTitle();
	}

	@NotNull
	public long getTimestampOfFileCreation()
	{
		return this.document.getDate();
	}

	@NotNull
	public String getUrl()
	{
		return this.document.getUrl();
	}

	public String getCuttedTitleOfFile()
	{
		// dd.MM.YY
		String fullTitle = this.getTitleOfFile();
		String cuttedTitle = fullTitle.replace("Замены на ", "").replace(".docx", "");

		return cuttedTitle;
	}
}
