package com.moemoe.lalala.webview;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.text.TextUtils;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.moemoe.lalala.utils.BrowserJsInject;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;

/**
 * Created by Haru on 2016/4/29 0029.
 */
public class CustomWebViewClient extends WebViewClient {
    private BaseAppCompatActivity mActivity;

    public CustomWebViewClient(BaseAppCompatActivity activity){
        super();
        mActivity = activity;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        ((CustomWebView) view).notifyPageFinished();
        view.loadUrl(BrowserJsInject.fullScreenByJs(url));
        mActivity.onPageFinished(url);
        mActivity.cancelProgressBar();
        super.onPageFinished(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        ((CustomWebView) view).notifyPageStarted();
        mActivity.onPageStarted(url);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (!TextUtils.isEmpty(url)) view.loadUrl(url);
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }
}
