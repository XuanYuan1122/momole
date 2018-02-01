package com.moemoe.lalala.model.entity;

/**
 * 
 * Created by yi on 2017/8/18.
 */

public enum FolderType {
    ZH("ZH"),MH("MH"),TJ("TJ"),XS("XS"),WZ("WZ"),MHD("MHD"),YY("YY"),SP("SP"),MOVIE("MOVIE"),MUSIC("MUSIC");

    String value;

    FolderType(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
