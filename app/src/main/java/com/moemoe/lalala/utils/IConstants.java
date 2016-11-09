package com.moemoe.lalala.utils;

/**
 * Created by Haru on 2016/4/26 0026.
 */
public interface IConstants {

   // String baseUrl = "http://123.59.81.176/";
    String baseUrl = "http://123.59.81.176/";
   // String debugBaseUrl = "http://123.58.136.98/";
    String debugBaseUrl = "http://183.131.152.216:8088/";
    String notFoundPage = "http://7xl0tq.com2.z0.glb.qiniucdn.com/404.html";
    //String debugBaseUrl = "http://192.168.1.129:9090/";

    public static final String BROADCAST_NAME = "com.moemoe.lalala.broadcast";
    public static final String SERVICE_NAME = "com.moemoe.lalala.music.MusicService";
    public static final String BROADCAST_QUERY_COMPLETE_NAME = "com.moemoe.lalala.querycomplete.broadcast";
    public static final String BROADCAST_CHANGEBG = "com.moemoe.lalala.changebg";

    // 播放状态
    public static final int MPS_NOFILE = -1; // 无音乐文件
    public static final int MPS_INVALID = 0; // 当前音乐文件无效
    public static final int MPS_PREPARE = 1; // 准备就绪
    public static final int MPS_PLAYING = 2; // 播放中
    public static final int MPS_PAUSE = 3; // 暂停
    public static final int MPS_RESET = 4;

    // 播放模式
    public static final int MPM_LIST_LOOP_PLAY = 0; // 列表循环
    public static final int MPM_ORDER_PLAY = 1; // 顺序播放
    public static final int MPM_RANDOM_PLAY = 2; // 随机播放
    public static final int MPM_SINGLE_LOOP_PLAY = 3; // 单曲循环
    public static final int MPM_SINGLE_PLAY = 4;//单曲播放

    public static final String PLAY_STATE_NAME = "PLAY_STATE_NAME";
    public static final String PLAY_MUSIC_INDEX = "PLAY_MUSIC_INDEX";
    public static final String PLAY_PRE_MUSIC_POSITION = "PLAY_PRE_MUSIC_POSITION";
    public static final String MUSIC_NUM = "music_num";

    String CLOSE = "CLOSE";
    String OPEN = "OPEN";
}
