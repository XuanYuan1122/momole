package com.moemoe.lalala.event;

/**
 * 标签操作
 * Created by yi on 2018/1/22.
 */

public class TagSelectEvent {
    private String operation;//add del top
    private String id;
    private String text;
    private int position;

    public TagSelectEvent(String operation, String id, String text,int position) {
        this.operation = operation;
        this.id = id;
        this.text = text;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
