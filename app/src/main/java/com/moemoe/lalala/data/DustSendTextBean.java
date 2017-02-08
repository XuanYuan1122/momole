package com.moemoe.lalala.data;

/**
 * Created by yi on 2016/11/9.
 */

public class DustSendTextBean {

    private String title;
    private String content;

    public DustSendTextBean(String title, String content){
        this.title = title;
        this.content = content;
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
}
