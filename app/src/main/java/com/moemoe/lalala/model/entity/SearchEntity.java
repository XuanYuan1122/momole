package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/3/3.
 */

public class SearchEntity {
    public Integer page;
    public String searchContent;

    public SearchEntity(Integer page, String searchContent) {
        this.page = page;
        this.searchContent = searchContent;
    }
}
