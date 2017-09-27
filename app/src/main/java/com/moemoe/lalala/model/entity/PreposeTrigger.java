package com.moemoe.lalala.model.entity;

import java.util.UUID;

public class PreposeTrigger {
	private int day;
	private int hour;
	private int minute;
	private int second;
	private String storyGroup;// 剧情组
	private String story;// 剧情

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public String getStoryGroup() {
		return storyGroup;
	}

	public void setStoryGroup(String storyGroup) {
		this.storyGroup = storyGroup;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}
}
