package com.moemoe.lalala.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by yi on 2017/7/18.
 */

public class IpAdressUtils {

    /**
     * gps获取ip
     * @return
     */
    public static String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (Exception ex) {
        }
        return null;
    }

    /**
     * wifi获取ip
     * @param context
     * @return
     */
    public static String getWifiIp(Context context){
        try {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager)context. getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = intToIp(ipAddress);
            return ip;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 格式化ip地址（192.168.11.1）
     * @param i
     * @return
     */
    private static String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    /**
     *  3G/4g网络IP
     */
    public static String get3Gor4GIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取本机的ip地址（3中方法都包括）
     * @param context
     * @return
     */
    public static String getIpAdress(Context context){
        String ip = "127.0.0.1";
        try {
            ip= getWifiIp(context);
            if (ip==null){
                ip = get3Gor4GIp();
                if (ip==null){
                    ip = getLocalIpAddress();
                }
            }
        } catch (Exception e) {
        }
        return ip;
    }
}
