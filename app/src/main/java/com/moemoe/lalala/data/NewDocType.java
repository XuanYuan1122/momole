package com.moemoe.lalala.data;

/**
 * Created by Haru on 2016/4/24 0024.
 */
public enum NewDocType {
    DOC_TEXT("DOC_TEXT"),DOC_IMAGE("DOC_IMAGE"),DOC_MUSIC("DOC_MUSIC"),DOC_LINK("DOC_LINK"),DOC_GROUP_LINK("DOC_GROUP_LINK"),CLUB_DOC("CLUB_DOC");

    String value;

    NewDocType(String str){
        this.value = str;
    }

    public static int getType(String value){
        if(DOC_TEXT.value.equals(value)){
            return 1;
        }else if(DOC_IMAGE.value.equals(value)){
            return 2;
        }else if(DOC_MUSIC.value.equals(value)){
            return 3;
        }else if(DOC_LINK.value.equals(value)){
            return 4;
        }else if(DOC_GROUP_LINK.value.equals(value)){
            return 5;
        }
        return -1;
    }

    public static int valueOf(NewDocType value){
        switch (value){
            case DOC_TEXT:
                return 1;
            case DOC_IMAGE:
                return 2;
            case DOC_MUSIC:
                return 3;
            case DOC_LINK:
                return 4;
            case DOC_GROUP_LINK:
                return 5;
            default:
                return -1;
        }
    }

    @Override
    public String toString() {
        return this.value;
    }
}
