package com.moemoe.lalala.model.entity;

/**
 * Created by Haru on 2016/4/24 0024.
 */
public enum CalendarDayType {
    BANNER_X55("BANNER_X55"),BAR("BAR"),RSS("RSS"),DOC_V_1("DOC_V_1"),DOC_V_2("DOC_V_2"),DOC_G_2("DOC_G_2"),DOC_H_1("DOC_H_1"), DOC_V_3("DOC_V_3");

    public String value;

    CalendarDayType(String str){
        this.value = str;
    }

    public static int getType(String value){
        if(BANNER_X55.value.equals(value)){
            return 0;
        }else if(BAR.value.equals(value)){
            return 1;
        }else if(RSS.value.equals(value)){
            return 2;
        }else if(DOC_V_1.value.equals(value)){
            return 3;
        }else if(DOC_V_2.value.equals(value)){
            return 4;
        }else if(DOC_G_2.value.equals(value)){
            return 5;
        }else if(DOC_H_1.value.equals(value)){
            return 6;
        }else if(DOC_V_3.value.equals(value)){
            return 7;
        }
        return -1;
    }


    public static int valueOf(CalendarDayType value){
        switch (value){
            case BANNER_X55:
                return 0;
            case BAR:
                return 1;
            case RSS:
                return 2;
            case DOC_V_1:
                return 3;
            case DOC_V_2:
                return 4;
            case DOC_G_2:
                return 5;
            case DOC_H_1:
                return 6;
            case DOC_V_3:
                return 7;
            default:
                return -1;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
