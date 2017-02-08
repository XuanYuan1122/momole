package com.moemoe.lalala.model.entity;

/**
 * Created by Haru on 2016/8/3 0003.
 */
public enum REPORT {

    DOC("DOC"),TAG("TAG"),USER("USER"),DOC_COMMENT("DOC_COMMENT"),BAG("BAG"),FOLDER("FOLDER");

    String value;

    REPORT(String str){ value = str;}

    @Override
    public String toString() {
        return value;
    }
}
