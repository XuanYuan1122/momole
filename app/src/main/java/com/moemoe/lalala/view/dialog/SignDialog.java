package com.moemoe.lalala.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.PreferenceManager;

/**
 * Created by yi on 2016/9/26.
 */

public class SignDialog extends Dialog implements View.OnClickListener {

    private View mDialogView;
    private ImageView mClose,mSign,mInfo,mDetail;
    private TextView mTvLevel,mTvCoin;
    private RecyclerView mRvList;
    private OnPositiveListener mPositiveListener;
    private AnimationSet mDialogIn,mDialogOut;
    private boolean mIsShowAnim;
    private Context mContext;
    private SimpleAdapter mAdapter;
    private boolean mIsSign;
    private int mDay;

    public SignDialog(Context context) {
        this(context, 0);
    }

    public SignDialog(Context context, int themeResId) {
        super(context, R.style.NetaDialog);
        mContext = context;
        init();
    }

    private void callDismiss(){
        super.dismiss();
    }

    private void  init(){
        mDialogIn = getInAnimation();
        mDialogOut = getOutAnimation();
        initAnimListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = View.inflate(getContext(),R.layout.dialog_sign,null);
        setContentView(contentView);
        setCanceledOnTouchOutside(false);
        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mClose = (ImageView) findViewById(R.id.iv_close);
        mSign = (ImageView) findViewById(R.id.iv_sign);
        mTvLevel = (TextView) findViewById(R.id.tv_level);
        mTvCoin = (TextView) findViewById(R.id.tv_coin);
        mRvList = (RecyclerView) findViewById(R.id.rv_info);
        mInfo = (ImageView) findViewById(R.id.iv_info);
        mDetail = (ImageView) findViewById(R.id.iv_detail_info);
        LinearLayoutManager l = new LinearLayoutManager(mContext);
        mRvList.setLayoutManager(l);
        int level =  PreferenceManager.getInstance(mContext).getThirdPartyLoginMsg().getLevel();
        mTvLevel.setText(mContext.getString(R.string.label_level,level));
        mTvCoin.setText(""+getCurSignCoin(level));
        mAdapter = new SimpleAdapter(getCurSignCoin(level),mDay);
        mRvList.setAdapter(mAdapter);
        mClose.setOnClickListener(this);
        mSign.setOnClickListener(this);
        mInfo.setOnClickListener(this);
        if (mIsSign){
            mSign.setImageResource(R.drawable.btn_signmain_signed);
        }else {
            mSign.setImageResource(R.drawable.btn_sign);
        }

    }

    private int getCurSignCoin(int level){
        int res = 0;
        if(level >= 1 && level <= 10){
            res = 1;
        }else if(level > 10 && level <= 20){
            res = 2;
        }else if(level > 20 && level <=30){
            res = 3;
        }else if(level > 30 && level <= 40){
            res = 4;
        }else if(level > 40 && level <= 50){
            res = 5;
        }else if(level > 50 && level <= 55){
            res = 6;
        }else if(level > 55 && level < 60){
            res = 7;
        }else if(level >= 60){
            res = 8;
        }
        return res;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mDetail.getVisibility() == View.VISIBLE){
            mDetail.setVisibility(View.GONE);
        }
        return super.dispatchTouchEvent(ev);
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

    public SignDialog setIsSign(boolean isSign){
        mIsSign = isSign;
        return this;
    }

    public SignDialog setAnimationEnable(boolean enable){
        mIsShowAnim = enable;
        return this;
    }

    public SignDialog setPositiveListener(OnPositiveListener l){
        mPositiveListener = l;
        return this;
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
        }
    }

    public SignDialog setSignDay(int day){
        mDay = day;
        return this;
    }

    public void changeSignState(){
        if (mIsSign){
            mSign.setImageResource(R.drawable.btn_signmain_signed);
        }else {
            mSign.setImageResource(R.drawable.btn_sign);
        }
        mAdapter.setCoin(mDay);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(R.id.iv_close == id){
            dismissWithAnimation(mIsShowAnim);
        }else if(id == R.id.iv_sign){
            if(mPositiveListener != null){
                mPositiveListener.onClick(this);
            }
        }else if(id == R.id.iv_info){
            if(mDetail.getVisibility() == View.VISIBLE) {
                mDetail.setVisibility(View.GONE);
            }else{
                mDetail.setVisibility(View.VISIBLE);
            }
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

    public interface OnPositiveListener{
        void onClick(SignDialog dialog);
    }

    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder>{

        private int coin;
        private int day;

        public SimpleAdapter(int coin,int day){
            this.coin = coin;
            this.day = day;
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SimpleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sign_info,parent,false));
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            holder.times.setVisibility(View.INVISIBLE);
            if(position < day){
                holder.root.setBackgroundResource(R.drawable.shape_rect_greed_sign);
                holder.num.setBackgroundResource(R.color.label_green);
                holder.score.setTextColor(mContext.getResources().getColor(R.color.label_green));
                holder.coin.setTextColor(mContext.getResources().getColor(R.color.label_green));
            }else {
                holder.root.setBackgroundResource(R.drawable.shape_rect_gray_sign);
                holder.num.setBackgroundResource(R.color.setting_gray);
                holder.score.setTextColor(mContext.getResources().getColor(R.color.setting_gray));
                holder.coin.setTextColor(mContext.getResources().getColor(R.color.setting_gray));
            }
            if(position == 2 || position == 6){
                holder.times.setVisibility(View.VISIBLE);
                holder.times.setText(mContext.getString(R.string.label_sign_times,position == 2?2:3));

            }
            holder.num.setText("" + (position + 1));
            holder.score.setText(mContext.getString(R.string.label_sign_info_score,20));
            holder.coin.setText(mContext.getString(R.string.label_sign_info_coin,coin));
        }

        @Override
        public int getItemCount() {
            return 7;
        }

        public void setCoin(int day){
            this.day = day;
            notifyDataSetChanged();
        }

        public class SimpleViewHolder extends RecyclerView.ViewHolder{

            public LinearLayout root;
            public TextView num,score,coin,times;

            public SimpleViewHolder(View itemView) {
                super(itemView);
                root = (LinearLayout) itemView.findViewById(R.id.ll_root);
                num = (TextView) itemView.findViewById(R.id.tv_num);
                score = (TextView) itemView.findViewById(R.id.tv_score);
                coin = (TextView) itemView.findViewById(R.id.tv_coin);
                times = (TextView) itemView.findViewById(R.id.tv_times);
            }
        }
    }
}
