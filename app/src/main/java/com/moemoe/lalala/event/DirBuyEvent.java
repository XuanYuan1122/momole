package com.moemoe.lalala.event;

/**
 * Created by yi on 2017/3/3.
 */

public class DirBuyEvent {
    private int position;
    private boolean buy;

    public DirBuyEvent(int position, boolean buy) {
        this.position = position;
        this.buy = buy;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }
}
