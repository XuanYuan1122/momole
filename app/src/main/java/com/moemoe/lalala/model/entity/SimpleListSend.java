package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * 单个list请求
 * Created by yi on 2017/12/5.
 */

public class SimpleListSend {
    public ArrayList<String> ids;

    public SimpleListSend(ArrayList<String> ids) {
        this.ids = ids;
    }
}
