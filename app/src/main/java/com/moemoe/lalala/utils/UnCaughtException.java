package com.moemoe.lalala.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.moemoe.lalala.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Haru on 2016/5/13 0013.
 */
public class UnCaughtException implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "UnCaughtException";

    /**
     * 客服支持邮箱
     */
    private static final String SUPPORT_EMAIL_ADDRESS = "233@moemoe.la";

    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "STACK_TRACE";
    /**
     * 错误日志文件扩展名
     */
    private static final String CRASH_EXTENSION = ".log";

    /**
     * 系统默认的异常处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static UnCaughtException sInstance;

    private Context mContext;
    /**
     * 保存设备信息和错误堆栈信息
     */
    private Properties mDeviceCrashInfo = new Properties();

    private String mErrorFileName;

    private String mErrorStr;

    private UnCaughtException() {
    };

    public static UnCaughtException getInstance() {
        if (sInstance == null) {
            sInstance = new UnCaughtException();
        }
        return sInstance;
    }

    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.exit(0);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        if (msg == null) {
            return false;
        }
        collectCrashDeviceInfo();

        saveCrashInfoToFile(ex);
//		sendReportErrorEmail();
//		sendCrashReportsToServer();


        return true;
    }


    /**
     * 上传错误日式到服务器
     */
    private void sendCrashReportsToServer() {
        //TODO 后续版本完成
    }
    /***
     * 发送错误报告邮件
     */
    private void sendReportErrorEmail() {
        if (!TextUtils.isEmpty(mErrorFileName)) {
            Uri logUri = Uri.fromFile(new File(mErrorFileName));
            Intent sendIntent = new Intent();
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            sendIntent.putExtra(Intent.EXTRA_TEXT, mErrorStr);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("application/zip");
            sendIntent.putExtra(Intent.EXTRA_STREAM, logUri);
            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { SUPPORT_EMAIL_ADDRESS });
            sendIntent = Intent.createChooser(sendIntent, mContext.getString(R.string.a_dlg_title_send_error));
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(sendIntent);
        }
    }

    private void saveCrashInfoToFile(Throwable ex) {

        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        mErrorStr = info.toString();
        printWriter.close();
        mDeviceCrashInfo.put("EXEPTION", ex.getLocalizedMessage());
        mDeviceCrashInfo.put(STACK_TRACE, mErrorStr);
        FileOutputStream trace = null;
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String date = sDateFormat.format(new Date());
            if (!StorageUtils.GetSDState()) {
                return;
            }
            File file = StorageUtils.getTempFile("crash-" + date + CRASH_EXTENSION);
            mErrorFileName = file.getAbsolutePath();
            // FileOutputStream trace = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            trace = new FileOutputStream(file);
            // mDeviceCrashInfo.store(trace, "");
            byte[] bytes = mDeviceCrashInfo.toString().getBytes();
            trace.write(bytes);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (trace != null) {
                try {
                    trace.flush();
                    trace.close();
                    trace = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 收集程序设备信息
     */
    private void collectCrashDeviceInfo() {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);
                mDeviceCrashInfo.put(VERSION_CODE, "" + pi.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), "" + field.get(null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
