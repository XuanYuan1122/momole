package com.moemoe.lalala.music;

/**
 * Created by Haru on 2016/3/22 0022.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.HashMap;

/**
 * 获取封面图片等
 */
public class MusicUtils {

    private static String[] proj_music = new String[]{
            MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION};

    private static String[] proj_album = new String[]{
            MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART};

    private static String[] proj_artist = new String[]{
            MediaStore.Audio.Artists.ARTIST,MediaStore.Audio.Artists.NUMBER_OF_TRACKS};

    private static String[] proj_folder = new String[]{MediaStore.Files.FileColumns.DATA};

    public static final int FILTER_SIZE = 1 * 1024 * 1024;//1MB
    public static final int FILTER_DURATION = 1 * 60 * 1000;//1min
    private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static final HashMap<Long,Bitmap> sArtCache = new HashMap<>();
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    static {
        sBitmapOptionsCache.inPreferredConfig = Bitmap.Config.RGB_565;
        sBitmapOptionsCache.inDither = false;

        sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        sBitmapOptions.inDither = false;
    }
}
