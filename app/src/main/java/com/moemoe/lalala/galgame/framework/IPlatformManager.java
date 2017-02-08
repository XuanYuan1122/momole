package com.moemoe.lalala.galgame.framework;

import jp.live2d.ALive2DModel;

/**
 * Created by Haru on 2016/7/18 0018.
 */
public interface IPlatformManager {
    public byte[] loadBytes(String path);
    public String loadString(String path);
    public ALive2DModel loadLive2DModel(String path);
    public void loadTexture(ALive2DModel model, int no, String path);
    public void log(String txt);
}
