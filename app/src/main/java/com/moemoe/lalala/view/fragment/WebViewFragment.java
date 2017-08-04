package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.modules.SimpleModule;
import com.moemoe.lalala.presenter.SimpleContract;
import com.moemoe.lalala.presenter.SimplePresenter;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.webview.CustomWebChromeClient;
import com.moemoe.lalala.webview.CustomWebView;
import com.moemoe.lalala.webview.CustomWebViewClient;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Haru on 2016/4/29 0029.
 */
public class WebViewFragment extends BaseFragment{

    @BindView(R.id.webView)
    CustomWebView mWebView;
    @BindView(R.id.nonVideoLayout)
    View nonVideoLayout;
    @BindView(R.id.videoLayout)
    ViewGroup videoLayout;
    public CustomWebChromeClient mChromeClient;

    private String mUrl;

    public static WebViewFragment newInstance(String url){
        WebViewFragment fragment = new WebViewFragment();
        Bundle b = new Bundle();
        b.putString("url",url);
        fragment.setArguments(b);
        return fragment;
    }

    public void shareUrl(){
        String temp = "javascript:android_share_call_back('" + PreferenceUtils.getAuthorInfo().getUserId() + "')";
        mWebView.loadUrl(temp);
    }

    public void resultMota(String data){
        String temp = "javascript:Pay.result(" + data + ")";
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
    protected int getLayoutId() {
        return R.layout.frag_webview;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mWebView.setWebViewClient(new CustomWebViewClient((BaseAppCompatActivity) getActivity()));
        mChromeClient = new CustomWebChromeClient((BaseAppCompatActivity)getActivity(),nonVideoLayout, videoLayout,mWebView);
        mWebView.setWebChromeClient(mChromeClient,(BaseAppCompatActivity) getActivity());
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(mUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        mUrl = getArguments().getString("url");
//        if(BuildConfig.DEBUG){
//            mWebView.setWebContentsDebuggingEnabled(true);
//        }
        setUrl(mUrl);
    }



    @Override
    public void onDestroyView() {
        mWebView.loadUrl("about:blank");
        mWebView.stopLoading();
        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        mWebView.setVisibility(View.GONE);
        mWebView.destroy();
        mWebView = null;
        super.onDestroyView();
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
