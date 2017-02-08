package com.moemoe.lalala.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haru on 2016/7/12 0012.
 */
public class MusicLoader {

    private static final String TAG = "MusicLoader";

    private static List<MusicInfo> musicList = new ArrayList<>();

    private static MusicLoader musicLoader;

    private static ContentResolver contentResolver;

    //Uri，指向external的database
    private Uri contentUri = Media.EXTERNAL_CONTENT_URI;
    //projection：选择的列; where：过滤条件; sortOrder：排序。
    private String[] projection = {
            Media._ID,
            Media.DISPLAY_NAME,
            Media.DATA,
            Media.ALBUM,
            Media.ARTIST,
            Media.DURATION,
            Media.SIZE
    };
    private String where =  "mime_type in ('audio/mpeg','audio/x-ms-wma') and bucket_display_name <> 'audio' and is_music > 0 " ;
    private String sortOrder = Media.DATA;

    public static MusicLoader instance(ContentResolver pContentResolver){
        if(musicLoader == null){
            contentResolver = pContentResolver;
            musicLoader = new MusicLoader();
        }
        return musicLoader;
    }

    private MusicLoader(){//利用ContentResolver的query函数来查询数据，然后将得到的结果放到MusicInfo对象中，最后放到数组中
       // Cursor cursor = contentResolver.query(contentUri, projection, where, null, sortOrder);
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                null);
        if(cursor == null){
            Log.v(TAG, "Line(37  )   Music Loader cursor == null.");
        }else if(!cursor.moveToFirst()){
            Log.v(TAG,"Line(39  )   Music Loader cursor.moveToFirst() returns false.");
        }else{
            int displayNameCol = cursor.getColumnIndex(Media.DISPLAY_NAME);
            int albumCol = cursor.getColumnIndex(Media.ALBUM);
            int idCol = cursor.getColumnIndex(Media._ID);
            int durationCol = cursor.getColumnIndex(Media.DURATION);
            int sizeCol = cursor.getColumnIndex(Media.SIZE);
            int artistCol = cursor.getColumnIndex(Media.ARTIST);
            int urlCol = cursor.getColumnIndex(Media.DATA);
            do{
                String title = cursor.getString(displayNameCol);
                String album = cursor.getString(albumCol);
                long id = cursor.getLong(idCol);
                int duration = cursor.getInt(durationCol);
                long size = cursor.getLong(sizeCol);
                String artist = cursor.getString(artistCol);
                String url = cursor.getString(urlCol);

                MusicInfo musicInfo = new MusicInfo(id, title);
                musicInfo.setAlbum(album);
                musicInfo.setDuration(duration);
                musicInfo.setSize(size);
                musicInfo.setArtist(artist);
                musicInfo.setUrl(url);
                musicList.add(musicInfo);

            }while(cursor.moveToNext());
        }
    }
//
//    // 查找sdcard卡上的所有歌曲信息
//    public List<MusicInfo> getMultiData() {
//        ArrayList<MusicInfo> musicList = new ArrayList>();
//
//// 加入封装音乐信息的代码
//// 查询所有歌曲
//        ContentResolver musicResolver = this.getContentResolver();
//        Cursor musicCursor = musicResolver.query(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
//                null);
//
//        int musicColumnIndex;
//
//        if (null != musicCursor && musicCursor.getCount() > 0) {
//            for (musicCursor.moveToFirst(); !musicCursor.isAfterLast(); musicCursor
//                    .moveToNext()) {
//                Map musicDataMap = new HashMap();
//
//                Random random = new Random();
//                int musicRating = Math.abs(random.nextInt()) % 10;
//
//                musicDataMap.put("musicRating", musicRating);
//
//// 取得音乐播放路径
//                musicColumnIndex = musicCursor
//                        .getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
//                musicPath = musicCursor.getString(musicColumnIndex);
//                musicDataMap.put("musicPath", musicPath);
//
//// 取得音乐的名字
//                musicColumnIndex = musicCursor
//                        .getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);
//                musicName = musicCursor.getString(musicColumnIndex);
//                musicDataMap.put("musicName", musicName);
//
//// 取得音乐的专辑名称
//                musicColumnIndex = musicCursor
//                        .getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM);
//                musicAlbum = musicCursor.getString(musicColumnIndex);
//                musicDataMap.put("musicAlbum", musicAlbum);
//
//// 取得音乐的演唱者
//                musicColumnIndex = musicCursor
//                        .getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);
//                musicArtist = musicCursor.getString(musicColumnIndex);
//                musicDataMap.put("musicArtist", musicArtist);
//
//// 取得歌曲对应的专辑对应的Key
//                musicColumnIndex = musicCursor
//                        .getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_KEY);
//                musicAlbumKey = musicCursor.getString(musicColumnIndex);
//
//                String[] argArr = { musicAlbumKey };
//                ContentResolver albumResolver = this.getContentResolver();
//                Cursor albumCursor = albumResolver.query(
//                        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null,
//                        MediaStore.Audio.AudioColumns.ALBUM_KEY + " = ?",
//                        argArr, null);
//
//                if (null != albumCursor && albumCursor.getCount() > 0) {
//                    albumCursor.moveToFirst();
//                    int albumArtIndex = albumCursor
//                            .getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART);
//                    musicAlbumArtPath = albumCursor.getString(albumArtIndex);
//                    if (null != musicAlbumArtPath
//                            && !"".equals(musicAlbumArtPath)) {
//                        musicDataMap.put("musicAlbumImage", musicAlbumArtPath);
//                    } else {
//                        musicDataMap.put("musicAlbumImage",
//                                "file:///mnt/sdcard/alb.jpg");
//                    }
//                } else {
//// 没有专辑定义，给默认图片
//                    musicDataMap.put("musicAlbumImage",
//                            "file:///mnt/sdcard/alb.jpg");
//                }
//                musicList.add(musicDataMap);
//            }
//        }
//        return musicList;
//    }


    public List<MusicInfo> getMusicList(){
        return musicList;
    }

    public Uri getMusicUriById(long id){
        Uri uri = ContentUris.withAppendedId(contentUri, id);
        return uri;
    }

    public static class MusicInfo implements Parcelable {
        private long id;
        private String title;
        private String album;
        private int duration;
        private long size;
        private String artist;
        private String url;

        public MusicInfo(){

        }

        public MusicInfo(long pId, String pTitle){
            id = pId;
            title = pTitle;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(title);
            dest.writeString(album);
            dest.writeString(artist);
            dest.writeString(url);
            dest.writeInt(duration);
            dest.writeLong(size);
        }

        public static final Parcelable.Creator<MusicInfo>
                CREATOR = new Creator<MusicInfo>() {

            @Override
            public MusicInfo[] newArray(int size) {
                return new MusicInfo[size];
            }

            @Override
            public MusicInfo createFromParcel(Parcel source) {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setId(source.readLong());
                musicInfo.setTitle(source.readString());
                musicInfo.setAlbum(source.readString());
                musicInfo.setArtist(source.readString());
                musicInfo.setUrl(source.readString());
                musicInfo.setDuration(source.readInt());
                musicInfo.setSize(source.readLong());
                return musicInfo;
            }
        };
    }
}
