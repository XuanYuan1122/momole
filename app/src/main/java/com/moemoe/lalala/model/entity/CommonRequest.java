package com.moemoe.lalala.model.entity;

/**
 * 三个string及以下请求
 * Created by yi on 2018/1/24.
 */

public class CommonRequest {
    public String text1;
    public String text2;
    public String text3;

    public CommonRequest(String text1) {
        this.text1 = text1;
    }

    public CommonRequest(String text1, String text2) {
        this.text1 = text1;
        this.text2 = text2;
    }

    public CommonRequest(String text1, String text2, String text3) {
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
    }
}
