package com.moemoe.lalala.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.moemoe.lalala.R;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class PhoneUtil {
    private static final String TAG = "PhoneUtil";

    /**
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isChinaPhoneFormated(String phoneNumber){
        return isPhoneFormated(phoneNumber, "86");
    }


    /**
     *
     * @param phoneNumber
     * @param countryCode eg. 86,0,...
     * @return
     */
    public static boolean isPhoneFormated(String phoneNumber, String countryCode){
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phone;
        boolean isValide = false;
        try {
            phone = phoneUtil.parse(phoneNumber, getCountryStringFromCode(countryCode));
            isValide = phoneUtil.isValidNumber(phone);
        }catch (Exception e) {
            //Util.LOGE(TAG, e);
        }
        return isValide;
    }
    public static boolean isPossiablePhoneFormated(String phone){
        final String Express = "[0-9]+";
        Pattern pattern = Pattern.compile(Express, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
    /**
     * 根据 (手机号+国家码)获取无国家码的手机号；
     * @param context
     * @param mobile ; if null, return null
     * @param countryCode eg. 86 ;  if null, use default Country Code
     * @return
     */
    public static String parsePhoneNumber(Context context, String mobile, String countryCode){
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        String phoneStr = null;
        if (!TextUtils.isEmpty(mobile)) {
            try {
                if(countryCode == null || countryCode.length() == 0){
                    //没有传入国家码：采用默认码--SIM卡 > 语言
                    //phoneNumber = phoneNumberUtil.parse(mobile, getLocaleRegion(context));
                    //不做处理
                    phoneStr = mobile;
                }else{
                    //根据传入国家码分析手机号
                    phoneNumber = phoneNumberUtil.parse(
                            mobile, getCountryStringFromCode(countryCode));
                    phoneStr = String.valueOf(phoneNumber.getNationalNumber());
                }

            }catch (NumberParseException e) {
            }
        }
        return phoneStr;
    }
    public static String getLocalCountryCode(Context context){
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        int countryCode = phoneNumberUtil.getCountryCodeForRegion(getLocaleRegion(context));
        String localCode = String.valueOf(countryCode);
        return localCode;
    }
    public static String getLocalDisplayStr(Context context){
        Locale[] locales = Locale.getAvailableLocales();
        String localStr = getLocaleRegion(context);
        //Util.LOGE(TAG, "getLocaleRegion: " + localStr);
        for (Locale locale : locales) {
            //Util.LOGE(TAG, "locale.getCountry(): " + locale.getCountry());
            if (locale.getCountry().equalsIgnoreCase(localStr)) {	// BUG:
                String disPlayStr = locale.getDisplayCountry(Locale.getDefault());
                //Util.LOGD(TAG, "Display region code: " + disPlayStr);
                return disPlayStr;
            }
        }
        // not in installed local
        Locale newLocal = new Locale("", localStr);
        return newLocal.getDisplayCountry(Locale.getDefault());
    }

    public static List<CountryCode> getAllCountryCodes(Context context) {
        List<CountryCode> countryCodes = new ArrayList<CountryCode>();
        String[] countryArray = context.getResources().getStringArray(R.array.array_country_name);
        String[] codeArray = context.getResources().getStringArray(R.array.array_country_code);
        Assert.assertEquals("Country name & code different", countryArray.length, codeArray.length);
        int len = countryArray.length;
        for (int i = 0; i < len; i++) {
            CountryCode code = new CountryCode();
            code.setCode(codeArray[i]);
            code.setCountry(countryArray[i]);
            countryCodes.add(code);
        }
        return countryCodes;
    }
    /**
     * 获得默认的本地国家码 such as CN：若有SIM卡，则SIM卡的地区；若无，则采用系统语言（can be optimized）代表的国家
     * @param context
     * @return eg. CN
     */
    private static String getLocaleRegion(Context context){
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iso = telephonyManager.getSimCountryIso();
        if (TextUtils.isEmpty(iso)) {	// 没有SIM卡， to be optimized
            iso = Locale.getDefault().getCountry();//fixed me getISO3Country
        }
        iso = iso.toUpperCase();
        try {
        } catch (Exception e) {
        }
        return iso;
    }
    /**
     * convert String: 86 ---> CN
     * @param countryCode such as 86
     * @return such as CN
     */
    private static String getCountryStringFromCode(String countryCode){
        String regionCode = null;
        try {
            int countryCodrInt = Integer.valueOf(countryCode);
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            regionCode = phoneUtil.getRegionCodeForCountryCode(countryCodrInt);
        } catch (NumberFormatException e) {
        }
        return regionCode;
    }

    public static boolean isInChina(Context context){
        boolean result = false;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String country = telephonyManager.getSimCountryIso();
        if (TextUtils.isEmpty(country)) {
            country = Locale.getDefault().getCountry();
        }
        if ("cn".equalsIgnoreCase(country)) {
            result = true;
        }
        return result;
    }

    public static String getLocaldeviceId(Context context){
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if(TextUtils.isEmpty(deviceId)){
            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);

            deviceId = wifi.getConnectionInfo().getMacAddress();
        }
        if(TextUtils.isEmpty(deviceId)){
            // 测试机
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        if(TextUtils.isEmpty(deviceId)){
            // 无法判断唯一设备，随机数字吧
            deviceId = "nada" + new Random(System.currentTimeMillis()).nextInt() % 1000000000;
        }
        return deviceId ;
    }
}
