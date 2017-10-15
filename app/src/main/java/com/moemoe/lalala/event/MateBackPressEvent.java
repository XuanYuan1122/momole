package com.moemoe.lalala.event;

/**
 * Created by yi on 2017/9/24.
 */

public class MateBackPressEvent {

    private String name;

    public MateBackPressEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
