package com.moemoe.lalala.data;

import com.app.annotation.Column;
import com.app.annotation.Table;

/**
 * Created by Haru on 2016/5/12 0012.
 */
@Table(name = "doc_old")
public class DocDbBean {
    @Column(name = "uuid",isId = true,autoGen = false)
    public String uuid;
    @Column(name = "json")
    public String json;
}
