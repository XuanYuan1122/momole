package com.moemoe.lalala.model.entity.tag;

import com.moemoe.lalala.utils.BaseUrlSpan;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yi on 2017/9/19.
 */

public class BaseTag {
    protected BaseUrlSpan span;
    protected String tag;
    protected HashMap<String,String> attrs;

    public BaseUrlSpan getSpan() {
        return span;
    }

    public void setSpan(BaseUrlSpan span) {
        this.span = span;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public HashMap<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(HashMap<String, String> attrs) {
        this.attrs = attrs;
    }
}
