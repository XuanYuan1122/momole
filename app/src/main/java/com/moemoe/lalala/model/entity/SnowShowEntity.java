package com.moemoe.lalala.model.entity;

import android.content.Context;

import com.moemoe.lalala.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by yi on 2016/12/20.
 */

public class SnowShowEntity {

    private static ArrayList<PositionInfo> positions;
    private static ArrayList<PositionInfo> positionCache;

    public static void init(){
        positions = new ArrayList<>();
        positionCache = new ArrayList<>();
        PositionInfo info = new PositionInfo(341,1782);
        positions.add(info);
        info = new PositionInfo(465,1364);
        positions.add(info);
        info = new PositionInfo(434,792);
        positions.add(info);
        info = new PositionInfo(589,1122);
        positions.add(info);
        info = new PositionInfo(868,484);
        positions.add(info);
        info = new PositionInfo(868,1364);
        positions.add(info);
        info = new PositionInfo(1457,264);
        positions.add(info);
        info = new PositionInfo(1054,1826);
        positions.add(info);
        info = new PositionInfo(1147,1892);
        positions.add(info);
        info = new PositionInfo(1178,1078);
        positions.add(info);
        info = new PositionInfo(1240,1694);
        positions.add(info);
        info = new PositionInfo(1302,2046);
        positions.add(info);
        info = new PositionInfo(1302,1034);
        positions.add(info);
        info = new PositionInfo(1364,1386);
        positions.add(info);
        info = new PositionInfo(1457,1650);
        positions.add(info);
        info = new PositionInfo(1488,1936);
        positions.add(info);
        info = new PositionInfo(1488,924);
        positions.add(info);
        info = new PositionInfo(1550,1584);
        positions.add(info);
        info = new PositionInfo(1643,1936);
        positions.add(info);
        info = new PositionInfo(1643,1474);
        positions.add(info);
        info = new PositionInfo(1643,1012);
        positions.add(info);
        info = new PositionInfo(1643,836);
        positions.add(info);
        info = new PositionInfo(1829,1430);
        positions.add(info);
        info = new PositionInfo(1860,704);
        positions.add(info);
        info = new PositionInfo(2077,594);
        positions.add(info);
        info = new PositionInfo(2294,682);
        positions.add(info);
        info = new PositionInfo(2418,2002);
        positions.add(info);
        info = new PositionInfo(2635,1166);
        positions.add(info);
        info = new PositionInfo(2666,550);
        positions.add(info);
        info = new PositionInfo(2666,1892);
        positions.add(info);
        info = new PositionInfo(2852,1276);
        positions.add(info);
        info = new PositionInfo(2883,1782);
        positions.add(info);
    }

    public static void initFromCache(Context context){
        positions = new ArrayList<>();
        positionCache = new ArrayList<>();
        positions.addAll(PreferenceUtils.getSnowCache(context));
        positionCache.addAll(PreferenceUtils.getSnowTemp(context));
    }

    public static void onDestroy(Context context){
        if(positionCache != null) PreferenceUtils.setSnowTemp(context,positionCache);
        if(positions != null) PreferenceUtils.setSnowCache(context,positions);
    }

    public static PositionInfo getOnePosition(){
        if(positions == null || positions.size() == 0){
            init();
        }
        Random random = new Random();
        int index = random.nextInt(positions.size());
        PositionInfo info = positions.remove(index);
        positionCache.add(info);
        return info;
    }

    public static void clearCache(){
        if(positionCache != null){
            positionCache.clear();
        }
    }

    public static void removeFromCache(int x,int y){
        PositionInfo cache = null;
        for (PositionInfo positionInfo : positionCache){
            if (positionInfo.x == x && positionInfo.y == y){
                cache = positionInfo;
                break;
            }
        }
        if(cache != null){
            positionCache.remove(cache);
        }
    }

    public static PositionInfo getCachePosition(int position){
        return positionCache.get(position);
    }

    public static ArrayList<PositionInfo> getPositionCache(){
        return positionCache;
    }

    public static class PositionInfo{
        public int x;
        public int y;

        public PositionInfo(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
