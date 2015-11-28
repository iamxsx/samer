package com.xsx.samer.model;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;


/**
 * 帖子
 * 帖子分两种：
 *      1.发布在主界面的帖子
 *      2.发布在各个话题版块里的帖子
 *      添加时将帖子添加到所属的版块中
 * 查找相应话题里的帖子时要增加判断条件，
 * 另外给主界面的帖子一个标志，表明帖子是发布在主界面中的
 * 在主界面的帖子所属的版块为空
 */
public class Post extends BmobObject{

	private static final long serialVersionUID = 1L;

	protected User author;

	private Topic topic;

	protected String avator;

	protected String content;

	protected List<Reply> replys;

	protected String time;



	protected BmobRelation likes;

	protected Boolean isPraise=false;

	protected Integer praiseCount;

	/**
	 * 是否是发布在主界面的帖子
	 */
	private Boolean main;
	

	protected List<String> images;
	
	protected String imageUrl;
	
	

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}
	
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public String getAvator() {
		return avator;
	}
	public void setAvator(String avator) {
		this.avator = avator;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<Reply> getReplys() {
		return replys;
	}
	public void setReplys(List<Reply> replys) {
		this.replys = replys;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public BmobRelation getLikes() {
		return likes;
	}
	public void setLikes(BmobRelation likes) {
		this.likes = likes;
	}
	public Boolean getIsPraise() {
		return isPraise;
	}
	public void setIsPraise(Boolean isPraise) {
		this.isPraise = isPraise;
	}
	public Integer getPraiseCount() {
		return praiseCount;
	}
	public void setPraiseCount(Integer praiseCount) {
		this.praiseCount = praiseCount;
	}


	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public Boolean getIsMain() {
		return main;
	}

	public void setIsMain(Boolean main) {
		this.main = main;
	}


}
