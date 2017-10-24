package com.moemoe.lalala.model.entity;

import java.util.ArrayList;
import java.util.List;

public class DocResponse {
	private String id;// 帖子ID
	private UserTopEntity createUser;// 创建人
	private String createTime;// 创建时间
	private long timestamp;
	private String title;
	private String content;
	private ArrayList<Image> images;
	private ArrayList<DocTagEntity> tags;
	private int retweets;// 转发数
	private int comments;// 评论数
	private int likes;// 喜欢数
	private String cover;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserTopEntity getCreateUser() {
		return createUser;
	}

	public void setCreateUser(UserTopEntity createUser) {
		this.createUser = createUser;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ArrayList<Image> getImages() {
		return images;
	}

	public void setImages(ArrayList<Image> images) {
		this.images = images;
	}

	public ArrayList<DocTagEntity> getTags() {
		return tags;
	}

	public void setTags(ArrayList<DocTagEntity> tags) {
		this.tags = tags;
	}

	public int getRetweets() {
		return retweets;
	}

	public void setRetweets(int retweets) {
		this.retweets = retweets;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}
}
