package com.xsx.samer.model;

import cn.bmob.v3.BmobObject;


/**
 * 帖子的回复或者是活动的回复
 */
public class Reply extends BmobObject{
	

	private static final long serialVersionUID = 1L;

	private User author;

	private User replyTo;


	private Post post;

	private ClubEvent clubEvent;

	private String replyContent;

	private String content;

	private String avator;
	
	public String getAvator() {
		return avator;
	}
	public void setAvator(String avator) {
		this.avator = avator;
	}
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
	public User getReplyTo() {
		return replyTo;
	}
	public void setReplyTo(User replyTo) {
		this.replyTo = replyTo;
	}
	
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public ClubEvent getClubEvent() {
		return clubEvent;
	}

	public void setClubEvent(ClubEvent clubEvent) {
		this.clubEvent = clubEvent;
	}
}
