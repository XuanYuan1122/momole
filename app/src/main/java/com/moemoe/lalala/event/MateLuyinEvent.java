package com.moemoe.lalala.event;

/**
 * Created by yi on 2017/9/24.
 */

public class MateLuyinEvent {
    private String selectMate;
    public MateLuyinEvent(String selectMate) {
        this.selectMate = selectMate;
    }

    public String getSelectMate() {
        return selectMate;
    }

    public void setSelectMate(String selectMate) {
        this.selectMate = selectMate;
    }
}
