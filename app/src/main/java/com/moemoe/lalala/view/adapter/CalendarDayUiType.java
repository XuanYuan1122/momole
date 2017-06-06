package com.moemoe.lalala.view.adapter;

/**
 * Created by Haru on 2016/4/24 0024.
 */
public enum CalendarDayUiType {
    RES("RES"),MUSIC("MUSIC"),VIDEO("VIDEO"),ANIMATION("ANIMATION"),NOVEL("NOVEL"),MANGA("MANGA"),NEWS("NEWS"),ANNIVERSARY("ANNIVERSARY");

    String value;

    CalendarDayUiType(String str){
        this.value = str;
    }

    public static int getType(String value){
        if(RES.value.equals(value)){
            return 0;
        }else if(MUSIC.value.equals(value)){
            return 1;
        }else if(VIDEO.value.equals(value)){
            return 2;
        }else if(ANIMATION.value.equals(value)){
            return 3;
        }else if(NOVEL.value.equals(value)){
            return 4;
        }else if(MANGA.value.equals(value)){
            return 5;
        }else if(NEWS.value.equals(value)){
            return 6;
        }else if(ANNIVERSARY.value.equals(value)){
            return 7;
        }
        return -1;
    }

    public static int valueOf(CalendarDayUiType value){
        switch (value){
            case RES:
                return 0;
            case MUSIC:
                return 1;
            case VIDEO:
                return 2;
            case ANIMATION:
                return 3;
            case NOVEL:
                return 4;
            case MANGA:
                return 5;
            case NEWS:
                return 6;
            case ANNIVERSARY:
                return 7;
            default:
                return -1;
        }
    }
}
