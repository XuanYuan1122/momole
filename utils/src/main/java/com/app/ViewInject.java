package com.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Haru on 2016/3/28 0028.
 */
public interface ViewInject {
    /* 注入控件 */
    void inject(View view);
    /* 注入布局 */
    void inject(Activity activity);
    /*  */
    void inject(Object handler,View view);
    View inject(Object fragment,LayoutInflater inflater,ViewGroup container);
}
