package com.devplmr;

import com.vk.api.sdk.objects.wall.WallPostFull;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WallPosts
{
	public WallPosts(@NotNull List<WallPostFull> wallPosts)
	{
		this.wallPosts = wallPosts;
	}

	private List<WallPostFull> wallPosts = null;

	public void setWallPosts(@NotNull List<WallPostFull> wallPosts)
	{
		this.wallPosts = wallPosts;
	}

	@NotNull
	public List<WallPostFull> getWallPosts()
	{
		return this.wallPosts;
	}

	class AttachedFile
	{
		private WallPostFull wallPost;

		public void setAttachedFile(@NotNull WallPostFull wallPost)
		{
			this.wallPost = wallPost;
		}

		public String getTitleOfFile()
		{
			return this.wallPost.getAttachments().get(0).getDoc().getTitle();
		}

		public long getTimestampOfFileCreation()
		{
			return this.wallPost.getDate();
		}

		public String getUrl()
		{
			return this.wallPost.getAttachments().get(0).getDoc().getUrl();
		}

		public String getCuttedTitleOfFile()
		{
			// dd.MM.YY
			String fullTitle = this.getTitleOfFile();
			String cuttedTitle = fullTitle.replace("Замены на ", "").replace(".docx", "");

			return cuttedTitle;
		}
	}

	AttachedFile attachedFile = new AttachedFile();
}
