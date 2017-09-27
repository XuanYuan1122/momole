/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moemoe.lalala.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;


import com.moemoe.lalala.broadcast.AlarmClockBroadcast;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.AlarmClockEntity;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.BookInfo;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.NewUploadEntity;
import com.moemoe.lalala.model.entity.UploadResultEntity;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhangshaowen on 16/4/7.
 */
public class Utils {

    /**
     * the error code define by myself
     * should after {@code ShareConstants.ERROR_PATCH_INSERVICE
     */
    public static final int ERROR_PATCH_GOOGLEPLAY_CHANNEL      = -5;
    public static final int ERROR_PATCH_ROM_SPACE               = -6;
    public static final int ERROR_PATCH_MEMORY_LIMIT            = -7;
    public static final int ERROR_PATCH_ALREADY_APPLY           = -8;
    public static final int ERROR_PATCH_CRASH_LIMIT             = -9;
    public static final int ERROR_PATCH_RETRY_COUNT_LIMIT       = -10;
    public static final int ERROR_PATCH_CONDITION_NOT_SATISFIED = -11;

    public static final String PLATFORM = "platform";

    public static final int MIN_MEMORY_HEAP_SIZE = 45;

    private static boolean background = false;

    public static boolean isGooglePlay() {
        return false;
    }

    public static boolean isBackground() {
        return background;
    }

    public static void setBackground(boolean back) {
        background = back;
    }

    public static boolean isXposedExists(Throwable thr) {
        StackTraceElement[] stackTraces = thr.getStackTrace();
        for (StackTraceElement stackTrace : stackTraces) {
            final String clazzName = stackTrace.getClassName();
            if (clazzName != null && clazzName.contains("de.robv.android.xposed.XposedBridge")) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public static boolean checkRomSpaceEnough(long limitSize) {
        long allSize;
        long availableSize = 0;
        try {
            File data = Environment.getDataDirectory();
            StatFs sf = new StatFs(data.getPath());
            availableSize = (long) sf.getAvailableBlocks() * (long) sf.getBlockSize();
            allSize = (long) sf.getBlockCount() * (long) sf.getBlockSize();
        } catch (Exception e) {
            allSize = 0;
        }

        if (allSize != 0 && availableSize > limitSize) {
            return true;
        }
        return false;
    }

    public static String getExceptionCauseString(final Throwable ex) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(bos);

        try {
            // print directly
            Throwable t = ex;
            while (t.getCause() != null) {
                t = t.getCause();
            }
            t.printStackTrace(ps);
            return toVisualString(bos.toString());
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String toVisualString(String src) {
        boolean cutFlg = false;

        if (null == src) {
            return null;
        }

        char[] chr = src.toCharArray();
        if (null == chr) {
            return null;
        }

        int i = 0;
        for (; i < chr.length; i++) {
            if (chr[i] > 127) {
                chr[i] = 0;
                cutFlg = true;
                break;
            }
        }

        if (cutFlg) {
            return new String(chr, 0, i);
        } else {
            return src;
        }
    }

    public static int[] getDocIconSize(int width, int height, int widthLimit){
        int[] res = new int[2];
        res[0] = widthLimit;
        res[1] = height * widthLimit / width;
        return res;
    }

    public static void startAlarmClock(Context context, AlarmClockEntity entity){
        Intent intent = new Intent(context, AlarmClockBroadcast.class);
        intent.putExtra("alarm", entity);
        PendingIntent pi = PendingIntent.getBroadcast(context,
                (int) entity.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        long nextTime = calculateNextTime(entity.getHour(),
                entity.getMinute(), entity.getWeeks());
        // 设置闹钟
        // 当前版本为19（4.4）或以上使用精准闹钟
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTime, pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pi);
        }
    }

    /**
     * 取消闹钟
     *
     * @param context        context
     * @param alarmClockCode 闹钟启动code
     */
    public static void cancelAlarmClock(Context context, int alarmClockCode) {
        Intent intent = new Intent(context, AlarmClockBroadcast.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, alarmClockCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context
                .getSystemService(Activity.ALARM_SERVICE);
        am.cancel(pi);
    }

    /**
     * 取得下次响铃时间
     *
     * @param hour   小时
     * @param minute 分钟
     * @param weeks  周
     * @return 下次响铃时间
     */
    public static long calculateNextTime(int hour, int minute, String weeks) {
        // 当前系统时间
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 下次响铃时间
        long nextTime = calendar.getTimeInMillis();
        // 当单次响铃时
        if (weeks == null) {
            // 当设置时间大于系统时间时
            if (nextTime > now) {
                return nextTime;
            } else {
                // 设置的时间加一天
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                nextTime = calendar.getTimeInMillis();
                return nextTime;
            }
        } else {
            nextTime = 0;
            // 临时比较用响铃时间
            long tempTime;
            // 取得响铃重复周期
            final String[] weeksValue = weeks.split(",");
            for (String aWeeksValue : weeksValue) {
                int week = Integer.parseInt(aWeeksValue);
                // 设置重复的周
                calendar.set(Calendar.DAY_OF_WEEK, week);
                tempTime = calendar.getTimeInMillis();
                // 当设置时间小于等于当前系统时间时
                if (tempTime <= now) {
                    // 设置时间加7天
                    tempTime += AlarmManager.INTERVAL_DAY * 7;
                }

                if (nextTime == 0) {
                    nextTime = tempTime;
                } else {
                    // 比较取得最小时间为下次响铃时间
                    nextTime = Math.min(tempTime, nextTime);
                }

            }

            return nextTime;
        }
    }

    public static void uploadFile(ApiService apiService, final String path, Observer<UploadResultEntity> callback){
        final ArrayList<NewUploadEntity> entities = new ArrayList<>();
        entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(path)),FileUtil.getExtensionName(path)));
        apiService.checkMd5(entities)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<ApiResult<ArrayList<UploadResultEntity>>, ObservableSource<UploadResultEntity>>() {
                    @Override
                    public ObservableSource<UploadResultEntity> apply(@NonNull ApiResult<ArrayList<UploadResultEntity>> arrayListApiResult) throws Exception {
                        final UploadResultEntity uploadResultEntity = arrayListApiResult.getData().get(0);
                        final File file = new File(path);
                        final UploadManager uploadManager = new UploadManager();
                        return Observable.create(new ObservableOnSubscribe<UploadResultEntity>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<UploadResultEntity> res) throws Exception {
                                final UploadResultEntity entity = new UploadResultEntity();
                                if(!uploadResultEntity.isSave()){
                                    try {
                                        uploadManager.put(file,uploadResultEntity.getPath(), uploadResultEntity.getUploadToken(), new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                if (info.isOK()) {
                                                    entity.setFileName(file.getName());
                                                    entity.setMd5(uploadResultEntity.getMd5());
                                                    entity.setPath(uploadResultEntity.getPath());
                                                    entity.setSave(uploadResultEntity.isSave());
                                                    entity.setSize(file.length());
                                                    entity.setType(uploadResultEntity.getType());
                                                    res.onNext(entity);
                                                    res.onComplete();
                                                } else {
                                                    res.onError(null);
                                                }
                                            }
                                        }, null);
                                    }catch (Exception e){
                                        res.onError(e);
                                    }
                                }else {
                                    entity.setAttr(uploadResultEntity.getAttr());
                                    entity.setFileName(file.getName());
                                    entity.setMd5(uploadResultEntity.getMd5());
                                    entity.setPath(uploadResultEntity.getPath());
                                    entity.setSave(uploadResultEntity.isSave());
                                    entity.setSize(uploadResultEntity.getSize());
                                    entity.setType(uploadResultEntity.getType());
                                    res.onNext(entity);
                                    res.onComplete();
                                }
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static void uploadFiles(ApiService apiService, final ArrayList<Object> items, final String cover, final int coverSize, final String folderType, final String folderName,Observer<UploadResultEntity> callback){
        final ArrayList<NewUploadEntity> entities = new ArrayList<>();
        final ArrayList<Integer> range = new ArrayList<>();
        if(!TextUtils.isEmpty(cover) && coverSize != -1){
            entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(cover)), FileUtil.getExtensionName(cover)));
            range.add(0);
        }
        for(Object o : items){
            if (o instanceof String){
                entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File((String) o)),FileUtil.getExtensionName((String) o)));
            }else if(o instanceof MusicLoader.MusicInfo){
                MusicLoader.MusicInfo info = (MusicLoader.MusicInfo) o;
                entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(info.getUrl())),FileUtil.getExtensionName(info.getUrl())));
            }else if(o instanceof BookInfo){
                BookInfo entity = (BookInfo) o;
                entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(entity.getPath())),FileUtil.getExtensionName(entity.getPath())));
            }
            range.add(range.size());
        }
        apiService.checkMd5(entities)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(new Function<ApiResult<ArrayList<UploadResultEntity>>, ObservableSource<UploadResultEntity>>() {
                    @Override
                    public ObservableSource<UploadResultEntity> apply(@NonNull ApiResult<ArrayList<UploadResultEntity>> arrayListApiResult) throws Exception {
                        return Observable.zip(
                                Observable.fromIterable(range),
                                Observable.fromIterable(arrayListApiResult.getData()),
                                new BiFunction<Integer, UploadResultEntity, UploadResultEntity>() {
                                    @Override
                                    public UploadResultEntity apply(@NonNull Integer integer, @NonNull UploadResultEntity uploadResultEntity) throws Exception {
                                        if(integer == 0 && !TextUtils.isEmpty(cover) && coverSize != -1){
                                            uploadResultEntity.setType("cover");
                                            uploadResultEntity.setFilePath(cover);
                                        }else {
                                            Object o ;
                                            if(!TextUtils.isEmpty(cover) && coverSize != -1){
                                                o = items.get(integer - 1);
                                            }else {
                                                o = items.get(integer);
                                            }
                                            if(o instanceof String){
                                                uploadResultEntity.setFilePath((String) o);
                                                uploadResultEntity.setType("image");
                                            }else if(o instanceof MusicLoader.MusicInfo){
                                                uploadResultEntity.setFilePath(((MusicLoader.MusicInfo) o).getUrl());
                                                uploadResultEntity.setType("music");
                                                uploadResultEntity.setMusicTime(((MusicLoader.MusicInfo) o).getDuration());
                                            }else if(o instanceof BookInfo){
                                                uploadResultEntity.setFilePath(((BookInfo) o).getPath());
                                                uploadResultEntity.setType("txt");
                                            }
                                        }
                                        return uploadResultEntity;
                                    }
                                }
                        );
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Function<UploadResultEntity, ObservableSource<UploadResultEntity>>() {
                    @Override
                    public ObservableSource<UploadResultEntity> apply(@NonNull final UploadResultEntity uploadResultEntity) throws Exception {
                        final File file = new File(uploadResultEntity.getFilePath());
                        final UploadManager uploadManager = new UploadManager();
                        return Observable.create(new ObservableOnSubscribe<UploadResultEntity>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<UploadResultEntity> res) throws Exception {
                                final UploadResultEntity entity = new UploadResultEntity();
                                if(!uploadResultEntity.isSave()){
                                    try {
                                        uploadManager.put(file,uploadResultEntity.getPath(), uploadResultEntity.getUploadToken(), new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                if (info.isOK()) {
                                                    entity.setFileName(file.getName());
                                                    entity.setMd5(uploadResultEntity.getMd5());
                                                    entity.setPath(uploadResultEntity.getPath());
                                                    entity.setSave(uploadResultEntity.isSave());
                                                    entity.setSize(file.length());
                                                    entity.setType(uploadResultEntity.getType());
                                                    if(uploadResultEntity.getType().equals("image")){
                                                        try {
                                                            String attr = "{\"h\":" + response.getInt("h") + ",\"w\":" + response.getInt("w") + "}";
                                                            entity.setAttr(attr);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }else if(uploadResultEntity.getType().equals("music")){
                                                        String attr = "{\"timestamp\":" + uploadResultEntity.getMusicTime() + "}";
                                                        entity.setAttr(attr);
                                                    }else if(uploadResultEntity.getType().equals("txt")){
                                                        String attr = "{\"size\":"+ file.length() +"}";
                                                        entity.setAttr(attr);
                                                        if(!TextUtils.isEmpty(folderType) && folderType.equals(FolderType.XS.toString())){
                                                            entity.setNum((int)file.length());
                                                            entity.setTitle(folderName);
                                                            String content = FileUtil.readFileToString(file);
                                                            if(content.length() > 100){
                                                                content = content.substring(0,100);
                                                            }
                                                            entity.setContent(content);
                                                        }
                                                    }
                                                    res.onNext(entity);
                                                    res.onComplete();
                                                } else {
                                                    res.onError(null);
                                                }
                                            }
                                        }, null);
                                    }catch (Exception e){
                                        res.onError(e);
                                    }
                                }else {
                                    entity.setAttr(uploadResultEntity.getAttr());
                                    entity.setFileName(file.getName());
                                    entity.setMd5(uploadResultEntity.getMd5());
                                    entity.setPath(uploadResultEntity.getPath());
                                    entity.setSave(uploadResultEntity.isSave());
                                    entity.setSize(uploadResultEntity.getSize());
                                    entity.setType(uploadResultEntity.getType());
                                    res.onNext(entity);
                                    res.onComplete();
                                }
                            }

                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }
}
