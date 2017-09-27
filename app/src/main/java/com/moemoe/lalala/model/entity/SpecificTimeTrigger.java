package com.moemoe.lalala.model.entity;

public class SpecificTimeTrigger {
	private String startYear; // 开始年份
	private int startHour;// 开始小时
	private int startMinute; // 开始分钟
	private int startSecond; // 开始秒
	private String endYear; // 结束年份
	private int endHour;
	private int endMinute;
	private int endSecond;

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}

	public int getStartSecond() {
		return startSecond;
	}

	public void setStartSecond(int startSecond) {
		this.startSecond = startSecond;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public int getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(int endMinute) {
		this.endMinute = endMinute;
	}

	public int getEndSecond() {
		return endSecond;
	}

	public void setEndSecond(int endSecond) {
		this.endSecond = endSecond;
	}
}
