package com.moemoe.lalala.utils;

import java.io.Serializable;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class CountryCode implements Serializable {
    private static final String TAG = "CountryCode";
    private static final long SERIAL_VERSION_UID = -149508491541657249L;
    private String mCode;
    private String mCountry;
    private String mISOCountryCode;

    public String getISOCountryCode(){
        return mISOCountryCode;
    }
    public void setISOCountryCode(String countryCode){
        mISOCountryCode = countryCode;
    }
    public String getCode(){
        return mCode;
    }
    public void setCode(String code){
        mCode = code;
    }
    public String getCountry(){
        return mCountry;
    }
    public void setCountry(String country){
        mCountry = country;
    }
    @Override
    public String toString(){
        return mCountry;//for adapter filter
    }
}
