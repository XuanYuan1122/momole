package com.moemoe.lalala.event;

/**
 *
 * Created by yi on 2017/3/3.
 */

public class SearchChangedEvent {
    private String keyWord;

    public SearchChangedEvent(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof SearchChangedEvent && ((SearchChangedEvent) obj).keyWord.equals(keyWord);
    }
}
