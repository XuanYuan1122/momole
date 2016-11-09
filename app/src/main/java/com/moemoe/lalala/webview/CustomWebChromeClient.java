package com.moemoe.lalala.webview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.R;

/**
 * Created by Haru on 2016/4/30 0030.
 */
public class CustomWebChromeClient extends WebChromeClient implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private BaseActivity mActivity;
    public interface ToggledFullscreenCallback {
        public void toggledFullscreen(boolean fullscreen);
    }

    private View activityNonVideoView;
    private ViewGroup activityVideoView;
   // private View loadingView;
    private CustomWebView webView;

    public boolean isVideoFullscreen; // Indicates if the video is being displayed using a custom view (typically full-screen)
    private FrameLayout videoViewContainer;
    private CustomViewCallback videoViewCallback;

    private ToggledFullscreenCallback toggledFullscreenCallback;

    public CustomWebChromeClient(BaseActivity activity){
        super();
        mActivity = activity;
    }

    @SuppressWarnings("unused")
    public CustomWebChromeClient(BaseActivity activity,View activityNonVideoView, ViewGroup activityVideoView) {
        mActivity = activity;
        this.activityNonVideoView = activityNonVideoView;
        this.activityVideoView = activityVideoView;
        //this.loadingView = null;
        this.webView = null;
        this.isVideoFullscreen = false;
    }

    @SuppressWarnings("unused")
    public CustomWebChromeClient(BaseActivity activity,View activityNonVideoView, ViewGroup activityVideoView, View loadingView) {
        mActivity = activity;
        this.activityNonVideoView = activityNonVideoView;
        this.activityVideoView = activityVideoView;
       // this.loadingView = loadingView;
        this.webView = null;
        this.isVideoFullscreen = false;
    }

    public CustomWebChromeClient(BaseActivity activity,View activityNonVideoView, ViewGroup activityVideoView, View loadingView, CustomWebView webView) {
        mActivity = activity;
        this.activityNonVideoView = activityNonVideoView;
        this.activityVideoView = activityVideoView;
       // this.loadingView = loadingView;
        this.webView = webView;
        this.isVideoFullscreen = false;
    }

    public boolean isVideoFullscreen() {
        return isVideoFullscreen;
    }

    public void setOnToggledFullscreen(ToggledFullscreenCallback callback) {
        this.toggledFullscreenCallback = callback;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if(view instanceof FrameLayout){
            FrameLayout frameLayout = (FrameLayout) view;
            View focusedChild = frameLayout.getFocusedChild();
            // Save video related variables
            isVideoFullscreen = true;
            videoViewContainer = frameLayout;
            videoViewCallback = callback;
            // Hide the non-video view, add the video view, and show it
            activityNonVideoView.setVisibility(View.INVISIBLE);
            activityVideoView.addView(videoViewContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            activityVideoView.setVisibility(View.VISIBLE);
            if (focusedChild instanceof android.widget.VideoView) {
                // android.widget.VideoView (typically API level <11)
                android.widget.VideoView videoView = (android.widget.VideoView) focusedChild;
                // Handle all the required events
                videoView.setOnPreparedListener(this);
                videoView.setOnCompletionListener(this);
                videoView.setOnErrorListener(this);
            }else {
                // Other classes, including:
                // - android.webkit.HTML5VideoFullScreen$VideoSurfaceView, which inherits from android.view.SurfaceView (typically API level 11-18)
                // - android.webkit.HTML5VideoFullScreen$VideoTextureView, which inherits from android.view.TextureView (typically API level 11-18)
                // - com.android.org.chromium.content.browser.ContentVideoView$VideoSurfaceView, which inherits from android.view.SurfaceView (typically API level 19+)

                // Handle HTML5 video ended event only if the class is a SurfaceView
                // Test case: TextureView of Sony Xperia T API level 16 doesn't work fullscreen when loading the javascript below
                if (webView != null && webView.getSettings().getJavaScriptEnabled() && focusedChild instanceof SurfaceView) {
                    // Run javascript code that detects the video end and notifies the Javascript interface
                    String js = "javascript:";
                    js += "var _ytrp_html5_video_last;";
                    js += "var _ytrp_html5_video = document.getElementsByTagName('video')[0];";
                    js += "if (_ytrp_html5_video != undefined && _ytrp_html5_video != _ytrp_html5_video_last) {";
                    {
                        js += "_ytrp_html5_video_last = _ytrp_html5_video;";
                        js += "function _ytrp_html5_video_ended() {";
                        {
                            js += "_VideoEnabledWebView.notifyVideoEnd();"; // Must match Javascript interface name and method of VideoEnableWebView
                        }
                        js += "}";
                        js += "_ytrp_html5_video.addEventListener('ended', _ytrp_html5_video_ended);";
                    }
                    js += "}";
                    webView.loadUrl(js);
                }
            }
            // Notify full-screen change
            if (toggledFullscreenCallback != null) {
                toggledFullscreenCallback.toggledFullscreen(true);
            }
            mActivity.showCustomView(view,callback,false);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        if (isVideoFullscreen) {
            // Hide the video view, remove it, and show the non-video view
            activityVideoView.setVisibility(View.INVISIBLE);
            activityVideoView.removeView(videoViewContainer);
            activityNonVideoView.setVisibility(View.VISIBLE);

            // Call back (only in API level <19, because in API level 19+ with chromium webview it crashes)
            if (videoViewCallback != null && !videoViewCallback.getClass().getName().contains(".chromium.")) {
                videoViewCallback.onCustomViewHidden();
            }
            // Reset video related variables
            isVideoFullscreen = false;
            videoViewContainer = null;
            videoViewCallback = null;
            // Notify full-screen change
            if (toggledFullscreenCallback != null) {
                toggledFullscreenCallback.toggledFullscreen(false);
            }
            mActivity.hideCustomView(false);
        }
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        mActivity.mProgressBar.setProgress(newProgress);
        if(newProgress > 90){
            mActivity.cancelProgressBar();
        }else {

        }
    }

    @Override
    public void onRequestFocus(WebView view) {
        super.onRequestFocus(view);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message,final JsResult result) {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                .setCancelable(false)
                .create()
                .show();

        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message,final JsResult result) {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                .create()
                .show();

        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,final JsPromptResult result) {

        final LayoutInflater factory = LayoutInflater.from(mActivity);
        final View v = factory.inflate(R.layout.javascript_prompt_dialog, null);
        ((TextView) v.findViewById(R.id.JavaScriptPromptMessage)).setText(message);
        ((EditText) v.findViewById(R.id.JavaScriptPromptInput)).setText(defaultValue);

        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.app_name)
                .setView(v)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String value = ((EditText) v.findViewById(R.id.JavaScriptPromptInput)).getText()
                                        .toString();
                                result.confirm(value);
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                result.cancel();
                            }
                        })
                .setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                result.cancel();
                            }
                        })
                .show();

        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onHideCustomView();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
//        if (loadingView != null) {
//            loadingView.setVisibility(View.GONE);
//        }
    }

    @Override
    public View getVideoLoadingProgressView() {
//        if (loadingView != null) {
//            loadingView.setVisibility(View.VISIBLE);
//            return loadingView;
//        } else {
            return super.getVideoLoadingProgressView();
//        }
    }

    public boolean onBackPressed() {
        if (isVideoFullscreen) {
            onHideCustomView();
            return true;
        } else {
            return false;
        }
    }
}
