package com.moemoe.lalala.android.storage;


import com.moemoe.lalala.android.http.ResponseInfo;

import org.json.JSONObject;

/**
 * 定义数据或文件上传结束后的处理动作
 */
public interface UpCompletionHandler {

    /**
     * 用户自定义的内容上传完成后处理动作必须实现的方法
     *
     * @param key      文件上传保存名称
     * @param info     上传完成返回日志信息
     * @param response 上传完成的回复内容
     */
    void complete(String key, ResponseInfo info, JSONObject response);
}
