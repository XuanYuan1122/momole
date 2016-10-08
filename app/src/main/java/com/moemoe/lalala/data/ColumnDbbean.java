package com.moemoe.lalala.data;

import com.app.annotation.Column;
import com.app.annotation.Table;

/**
 * Created by Haru on 2016/5/11 0011.
 */
@Table(name = "column_list")
public class ColumnDbbean {
    @Column(name = "id",isId = true,autoGen = false)
    public String id;
    @Column(name = "future")
    public String futureJson;
    @Column(name = "past")
    public String pastJson;
}
