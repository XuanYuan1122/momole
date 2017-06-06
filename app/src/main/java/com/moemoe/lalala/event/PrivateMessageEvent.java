package com.moemoe.lalala.event;

/**
 * Created by yi on 2017/3/13.
 */

public class PrivateMessageEvent {
    private boolean show;
    private String talkId;
    private boolean delete;

    public PrivateMessageEvent(boolean show, String talkId, boolean delete) {
        this.show = show;
        this.talkId = talkId;
        this.delete = delete;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId;
    }
}
