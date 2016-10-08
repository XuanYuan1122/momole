package com.moemoe.lalala.log;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Haru on 2016/4/29 0029.
 */
public class UserLogWriter {
    DataOutputStream[] out = new DataOutputStream[2];
    File[] outFile = new File[2];

    private static final byte TAB = 0x09;
    private static final byte[] CRLF ={0x0d,0x0a};

//	DataOutputStream outEventWithValue;
//	File outEventWithValueFile;

    long size;
    private UserLogWriter(Context context) {
        super();
        File folder = context.getFilesDir();
        File logFolder = new File(folder, "log");
        File logEventWithValue = new File(folder, "log_event_with_value");
        if (!logFolder.exists())
            logFolder.mkdirs();
        if (!logEventWithValue.exists())
            logEventWithValue.mkdirs();
        String logs[] = logFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".log"))
                    return true;
                return false;
            }
        });
        String logEventWithValues[] = logEventWithValue.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".log"))
                    return true;
                return false;
            }
        });
//		System.out.println("logs "+logs);// should be 0 or 1.
        try {
            if (logs != null && logs.length > 0) {
                outFile[0] = new File(logFolder, logs[0]);
                out[0] = new DataOutputStream(new FileOutputStream(outFile[0], true));
            } else {
                outFile[0] = new File(logFolder, (System.currentTimeMillis()+10)+".log"); //每个文件文件名不一样，所以加10
                out[0] = new DataOutputStream(new FileOutputStream(outFile[0]));
            }

            if (logEventWithValues != null && logEventWithValues.length > 0) {
                outFile[1] = new File(logEventWithValue, logEventWithValues[0]);
                out[1] = new DataOutputStream(new FileOutputStream(outFile[1], true));
            } else {
                outFile[1] = new File(logEventWithValue, (System.currentTimeMillis()+10)+".log"); //每个文件文件名不一样，所以加10
                out[1] = new DataOutputStream(new FileOutputStream(outFile[1]));
            }

            size = outFile[0].length() + outFile[1].length();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeEvent(int eventId) {
        try {
            out[0].write(intToByte(eventId));
            int time = (int) (System.currentTimeMillis() / 1000);
            out[0].write(intToByte(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeEventWithValue(int eventId, String value){
        try {
            out[1].write(intToByte(eventId));
            out[1].write(TAB);
            out[1].write(value.getBytes("utf-8"));
            out[1].write(TAB);
            int time = (int) (System.currentTimeMillis() / 1000);
            out[1].write(intToByte(time));
            out[1].write(CRLF);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] close() {
        try {
            for (int i = 0; i < out.length; i++) {
                out[i].close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] result = new String[2];
        for (int i = 0; i < outFile.length; i++) {
            result[i] = outFile[i].getAbsolutePath();
        }
        return result;
    }
    private static UserLogWriter LOGGER ;
    private static Object obj = new Object();
    public static long init(Context c){
        synchronized(obj){
            if(LOGGER==null)
                LOGGER = new UserLogWriter(c);
        }
        return LOGGER.size;
    }
    public static void print(int eventId){
        synchronized(obj){
            if(LOGGER!=null)
                LOGGER.writeEvent(eventId);
        }
    }

    public static void printValue(int eventId, int value){
        synchronized(obj){
            if(LOGGER!=null)
                LOGGER.writeEventWithValue(eventId, Integer.toString(value));
        }
    }

    public static void printValue(int eventId, String value){
        synchronized(obj){
            if(LOGGER!=null)
                LOGGER.writeEventWithValue(eventId, value);
        }
    }

    public static String[] cloose(){
        String[] log = new String[2];
        synchronized(obj){
            if(LOGGER!=null){
                log = LOGGER.close();
                LOGGER = null;
            }
        }
        return log;
    }

    public static String exportZip(Context context, byte[] statusData){
        UserLogWriter.init(context); //make sure Logger.cloose() return log file path.
        String[] paths = UserLogWriter.cloose();

        File[] log = new File[2];
        File[] tmpLog = new File[2];
        for (int i = 0; i < paths.length; i++) {
            log[i] = new File(paths[i]);
            tmpLog[i] = new File(paths[i]+".old");
            log[i].renameTo(tmpLog[i]);// rename
        }
        UserLogWriter.init(context);
        File folder = context.getFilesDir();
        String zip = folder.getAbsolutePath()+"/log/log.zip";
        File zipFile = new File(zip);
        try {
            GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(zipFile));
            byte[] buffer =statusData;// status buffer;
            out.write(("StatusBin,Length="+buffer.length).getBytes());
            out.write(CRLF);
            out.write(buffer);
            out.write(CRLF);
            out.write("StatusBinEnd".getBytes());
            out.write(CRLF);

            long size = tmpLog[0].length();
            out.write(("EventBin,Length="+size).getBytes());
            out.write(CRLF);

            FileInputStream fis = new FileInputStream(tmpLog[0]);
            byte[] buf = new byte[1024];
            int len = 0;
            while((len=fis.read(buf))!=-1){
                out.write(buf,0,len);
            }
            fis.close();
            tmpLog[0].delete();//delete old log data
            out.write(CRLF);
            out.write("EventBinEnd".getBytes());
            out.write(CRLF);


            long size2 = tmpLog[1].length();
            if (size2 > 0) {
                out.write(("EventWithValue,Length="+size2).getBytes());
                out.write(CRLF);

                FileInputStream fis2 = new FileInputStream(tmpLog[1]);
                buf = new byte[1024];
                len = 0;
                while((len=fis2.read(buf))!=-1){
                    out.write(buf,0,len);
                }
                fis2.close();

                tmpLog[1].delete();//delete old log data
                out.write("EventWithValueEnd".getBytes());
                out.write(CRLF);

            }
            out.close();

        }  catch (IOException e) {
            e.printStackTrace();
            zipFile.delete();
            zip=null;
        }

        return zip;
    }
    public static byte[] intToByte(int n){
        byte[] b = new byte[4];
        b[0] = (byte)(n&0xff);
        b[1] = (byte)(n>>8&0xff);
        b[2] = (byte)(n>>16&0xff);
        b[3] = (byte)(n>>24&0xff);
        return b;
    }

    public static long getLogSize() {
        if (LOGGER != null) {
            return LOGGER.size;
        } else {
            return 0;
        }
    }
}
