package com.moemoe.lalala.data;

import com.app.annotation.Column;
import com.app.annotation.Table;

/**
 * Created by Haru on 2016/5/11 0011.
 */
@Table(name = "rss_list")
public class RssDbBean {

    @Column(name = "day",isId = true,autoGen = false)
    public String day;
    @Column(name = "json")
    public String json;
}
