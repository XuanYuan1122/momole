package com.moemoe.lalala.fragment;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.webview.CustomWebChromeClient;
import com.moemoe.lalala.webview.CustomWebView;
import com.moemoe.lalala.webview.CustomWebViewClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by Haru on 2016/4/29 0029.
 */
@ContentView(R.layout.frag_webview)
public class WebViewFragment extends BaseFragment {

    @FindView(R.id.webView)
    public CustomWebView mWebView;
    @FindView(R.id.nonVideoLayout)
    private View nonVideoLayout;
    @FindView(R.id.videoLayout)
    private ViewGroup videoLayout;
    public CustomWebChromeClient mChromeClient;
    private String mUrl;

    public static WebViewFragment newInstance(String url){
        WebViewFragment fragment = new WebViewFragment();
        Bundle b = new Bundle();
        b.putString("url",url);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView.setWebViewClient(new CustomWebViewClient((BaseActivity) getActivity()));
        mChromeClient = new CustomWebChromeClient((BaseActivity)getActivity(),nonVideoLayout, videoLayout,mWebView);
        mWebView.setWebChromeClient(mChromeClient,(BaseActivity) getActivity());
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(mUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        mUrl = getArguments().getString("url");
        setUrl(mUrl);
    }

    public void shareUrl(){
        String temp = "javascript:android_share_call_back('" + PreferenceManager.getInstance(getActivity()).getThirdPartyLoginMsg().getmUUid() + "')";
        mWebView.loadUrl(temp);
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
        /**
         * 设置为横屏
         */
        if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // mVideoFullView.removeAllViews();
        mWebView.loadUrl("about:blank");
        mWebView.stopLoading();
        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        mWebView.setVisibility(View.GONE);
        mWebView.destroy();
        mWebView = null;
    }

    public void setUrl(String url){
        mWebView.doOnResume();
        mWebView.loadUrl(addHttpProtocolIfNotExist(url));
    }


    private String addHttpProtocolIfNotExist(String oriUrl) {
        boolean containScheme = false;
        try {
            URL url = new URL(oriUrl);
            if (url.getProtocol() != null) {
                containScheme = true;
            }
        } catch (MalformedURLException e) {
            // 不规范的url链接都会抛这个问题，只需要log message信息
        }
        String res = oriUrl;
        // mailto:lalal@yopmail.com 这种不需要加
        if (!containScheme && oriUrl != null && !oriUrl.contains(":")) {
            res = "http://" + oriUrl;
        }
        return res;
    }
}
