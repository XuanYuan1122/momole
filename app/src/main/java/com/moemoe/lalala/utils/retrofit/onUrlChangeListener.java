package com.moemoe.lalala.utils.retrofit;

import okhttp3.HttpUrl;

/**
 *
 * Created by yi on 2018/1/10.
 */

public interface onUrlChangeListener {

    void onUrlChangeBefore(HttpUrl oldUrl,String domainName);

    void onUrlChanged(HttpUrl newUrl,HttpUrl oldUrl);
}
