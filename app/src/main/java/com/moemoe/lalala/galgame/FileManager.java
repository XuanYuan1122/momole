package com.moemoe.lalala.galgame;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Haru on 2016/7/18 0018.
 */
public class FileManager {
    static Context context ;
    private static File source;

    public static void init( Context c ){
        context = c ;
    }

    public static InputStream open(String path  ) throws IOException {
        return open(path,false);
    }

    public static InputStream open(String path , boolean isCache ) throws IOException {
        if( isCache ){
            return open_cache(path) ;
        }
        else{
            return open_resource(path) ;
        }
    }

    public static InputStream open_cache(String path ) throws FileNotFoundException {
        File f = new File( context.getCacheDir() , path ) ;
        return new FileInputStream(f) ;
    }

    public static InputStream open_resource(String path ) throws IOException {
        return context.getAssets().open(path) ;
    }
}
