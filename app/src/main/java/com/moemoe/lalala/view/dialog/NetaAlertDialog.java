package com.moemoe.lalala.view.dialog;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.common.Callback;
import com.app.http.request.UriRequest;
import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.MapActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.DustResponse;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Haru on 2016/8/25.
 */
public class NetaAlertDialog extends Dialog implements View.OnClickListener,View.OnLongClickListener {
    public static final int STATE_GET_FIRST_DUST = 0;
    public static final int STATE_SEND_SOME_DUST = 1;
    public static final int STATE_COPY_DUST = 2;
    public static final int STATE_GET_NEXT_DUST = 3;
    public static final int STATE_SEND_DUST = 4;
    public static final int STATE_CANCEL_SEND_DUST = 5;

    private View mDialogView;
    private TextView mTvTitle,mContent,mPositive,mNegative;
    private ImageView mClose,mDust;
    private EditText mEtTitle,mEtContent;
    private View mLlBtn,mLlMain;
    private AnimationSet mDialogIn,mDialogOut;
    private AnimationDrawable mDustAniDraw;
    private OnPositiveListener mPositiveListener;
    private OnNegativeListener mNegativeListener;
    private OnLongClickListener mLongClickListener;
    private String mContentText,mTitle;
    private boolean mIsShowAnim;
    private Context mContext;

    public NetaAlertDialog(Context context){ this(context, 0);}

    public NetaAlertDialog(Context context,int theme){
        super(context, R.style.NetaDialog);
        mContext = context;
        init();
    }

    private void callDismiss(){
        super.dismiss();
    }

    private void init(){
        mDialogIn = getInAnimation();
        mDialogOut = getOutAnimation();
        initAnimListener();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title.toString();
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getText(titleId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = View.inflate(getContext(),R.layout.dialog_trash,null);
        setContentView(contentView);
        setCanceledOnTouchOutside(false);

        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mContent = (TextView) contentView.findViewById(R.id.tv_content);
        mPositive = (TextView) contentView.findViewById(R.id.tv_get);
        mNegative = (TextView) contentView.findViewById(R.id.tv_send);
        mClose = (ImageView) contentView.findViewById(R.id.iv_close);
        mTvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        mDust = (ImageView) contentView.findViewById(R.id.iv_anim);
        mEtTitle = (EditText) contentView.findViewById(R.id.et_title);
        mEtContent = (EditText) contentView.findViewById(R.id.et_content);
        mLlBtn = contentView.findViewById(R.id.ll_btn_container);
        mLlMain = contentView.findViewById(R.id.ll_dust_container);
        mLlMain.setVisibility(View.GONE);
        mPositive.setText(getContext().getString(R.string.label_get_dust));
        mNegative.setText(getContext().getString(R.string.label_send_some_dust));
        mDustAniDraw = (AnimationDrawable) mDust.getDrawable();

        mClose.setOnClickListener(this);
        mPositive.setOnClickListener(this);
        mNegative.setOnClickListener(this);
        //mContent.setText(mContentText);
        if(null == mPositiveListener && null == mNegativeListener){
            mPositive.setVisibility(View.GONE);
            mNegative.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startWithAnimation(mIsShowAnim);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dismissWithAnimation(mIsShowAnim);
    }

    private void startWithAnimation(Boolean showInAnimation){
        if(showInAnimation){
            mDialogView.startAnimation(mDialogIn);
        }
    }

    private void dismissWithAnimation(Boolean showOutAnimation){
        if(showOutAnimation){
            mDialogView.startAnimation(mDialogOut);
        }else {
            super.dismiss();
            //((MapActivity)mContext).initMap();
           // ((MapActivity)mContext).getMap().setIsDialogCause(false);
        }
    }

    private void initAnimListener(){
        mDialogOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        callDismiss();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void stopAnim(){
        mDustAniDraw.stop();
        mClose.setVisibility(View.VISIBLE);
        mPositive.setVisibility(View.VISIBLE);
        mNegative.setVisibility(View.VISIBLE);
        mPositive.setText(getContext().getString(R.string.label_get_dust));
        mNegative.setText(getContext().getString(R.string.label_send_some_dust));
    }

    public void showDust(){
        stopAnim();
        mPositive.setVisibility(View.VISIBLE);
        mPositive.setText(getContext().getString(R.string.label_copy_dust));
        mNegative.setVisibility(View.VISIBLE);
        mNegative.setText(getContext().getString(R.string.label_get_next_dust));
        mLlMain.setVisibility(View.VISIBLE);
        mClose.setVisibility(View.VISIBLE);
        mTvTitle.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.VISIBLE);
        mDust.setVisibility(View.GONE);
        mEtTitle.setVisibility(View.GONE);
        mEtContent.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(R.id.iv_close == id){
            dismissWithAnimation(mIsShowAnim);
        }else if(R.id.tv_get == id){
            if(mLlMain.getVisibility() == View.GONE){
                mDustAniDraw.start();
                mPositive.setVisibility(View.INVISIBLE);
                mNegative.setVisibility(View.INVISIBLE);
                mClose.setVisibility(View.INVISIBLE);
                mDust.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPositiveListener.onClick(NetaAlertDialog.this,STATE_GET_FIRST_DUST);
                    }
                },1800);
            }else {
                if(mTvTitle.getVisibility() == View.VISIBLE){
                    ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("绅士内容", mContentText);
                    cmb.setPrimaryClip(mClipData);
                    ToastUtil.showCenterToast(getContext(), R.string.msg_trash_get);
                    mPositiveListener.onClick(this, STATE_COPY_DUST);
                }else if(mEtTitle.getVisibility() == View.VISIBLE){
                    sendDust();
                    mPositiveListener.onClick(this,STATE_SEND_DUST);
                }
            }
        }else if(R.id.tv_send == id){
            if(mLlMain.getVisibility() == View.GONE){
                mTvTitle.setVisibility(View.GONE);
                mContent.setVisibility(View.GONE);
                mDust.setVisibility(View.GONE);
                mLlMain.setVisibility(View.VISIBLE);
                mEtTitle.setVisibility(View.VISIBLE);
                mEtContent.setVisibility(View.VISIBLE);
                mPositive.setText(getContext().getString(R.string.label_send_dust));
                mNegative.setText(getContext().getString(R.string.label_cancel_send_dust));
            }else {
                if(mTvTitle.getVisibility() == View.VISIBLE){
                    mLlMain.setVisibility(View.GONE);
                    mDust.setVisibility(View.VISIBLE);
                    mDustAniDraw.start();
                    mPositive.setVisibility(View.INVISIBLE);
                    mNegative.setVisibility(View.INVISIBLE);
                    mClose.setVisibility(View.INVISIBLE);
                    mDust.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mNegativeListener.onClick(NetaAlertDialog.this, STATE_GET_NEXT_DUST);
                        }
                    }, 1800);
                }else if(mEtTitle.getVisibility() == View.VISIBLE){
                    mNegativeListener.onClick(this,STATE_CANCEL_SEND_DUST);
                }
            }
        }
    }

    private void sendDust(){
        String title = mEtTitle.getText().toString();
        String content = mEtContent.getText().toString();
        if(TextUtils.isEmpty(content)){
            ToastUtil.showCenterToast(getContext(),R.string.msg_doc_content_cannot_null);
        }else {
            ((BaseActivity)mContext).createDialog();
            Otaku.getCommonV2().sendDust(PreferenceManager.getInstance(getContext()).getToken(), title, content).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    DustResponse dustResponse = new DustResponse();
                    dustResponse.readFromJsonContent(s);
                    if(dustResponse.overTimes){
                        ToastUtil.showCenterToast(getContext(),R.string.msg_trash_send);
                    }else {
                        ToastUtil.showCenterToast(getContext(),R.string.msg_dust_send_success);
                    }
                }

                @Override
                public void failure(String e) {
                    ((BaseActivity)mContext).finalizeDialog();
                }
            }));
        }
    }

    public NetaAlertDialog setAnimationEnable(boolean enable){
        mIsShowAnim = enable;
        return this;
    }

    public NetaAlertDialog setPositiveListener(OnPositiveListener l){
        mPositiveListener = l;
        return this;
    }

    public NetaAlertDialog setNegativeListener(OnNegativeListener l){
        mNegativeListener = l;
        return this;
    }

    public NetaAlertDialog setContentLongClickListener(OnLongClickListener l){
        if(mLongClickListener == null){
            mLongClickListener = l;
        }
        return this;
    }

    public NetaAlertDialog setDialogTitle(String title){
        mTitle = title;
        mTvTitle.setText(title);
        return this;
    }

    public NetaAlertDialog setDialogTitle(int titleId) {
        return setDialogTitle(getContext().getString(titleId));
    }

    public NetaAlertDialog setContentText(String text){
        mContentText = text;
        mContent.setText(text);
        return this;
    }

    public NetaAlertDialog setContentText(int textId){
        return setContentText(getContext().getString(textId));
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        if(R.id.tv_content == id){
            if(mLongClickListener != null){
                mLongClickListener.onLongClick();
            }
        }
        return false;
    }

    public interface OnLongClickListener{
        void onLongClick();
    }

    public interface OnPositiveListener{
        void onClick(NetaAlertDialog dialog,int state);
    }

    public interface OnNegativeListener{
        void onClick(NetaAlertDialog dialog,int state);
    }

    private AnimationSet getInAnimation(){
        AnimationSet in = new AnimationSet(getContext(),null);
        AlphaAnimation alpha = new AlphaAnimation(.0f,1.0f);
        alpha.setDuration(100);

        ScaleAnimation scale1 = new ScaleAnimation(0.7f,1.05f,0.7f,1.05f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scale1.setDuration(135);

        ScaleAnimation scale2 = new ScaleAnimation(1.05f,.95f,1.05f,.95f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scale2.setDuration(105);
        scale2.setStartOffset(135);

        ScaleAnimation scale3 = new ScaleAnimation(.95f,1.0f,.95f,1.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scale3.setDuration(60);
        scale3.setStartOffset(240);

        in.addAnimation(alpha);
        in.addAnimation(scale1);
        in.addAnimation(scale2);
        in.addAnimation(scale3);

        return in;
    }

    private AnimationSet getOutAnimation(){
        AnimationSet out = new AnimationSet(getContext(), null);
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
        alpha.setDuration(150);

        ScaleAnimation scale = new ScaleAnimation(1.0f, 0.6f, 1.0f, 0.6f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(150);
        out.addAnimation(alpha);
        out.addAnimation(scale);

        return out;
    }

}
