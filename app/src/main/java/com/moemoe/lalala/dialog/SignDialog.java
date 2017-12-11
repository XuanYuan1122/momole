package com.moemoe.lalala.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DailyTaskEntity;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;

/**
 * 签到对话框
 * Created by yi on 2016/11/28.
 */

public class SignDialog extends Dialog implements View.OnClickListener {
    private View mDialogView;
    private ImageView mClose,mSign,mInfo;
    private TextView mTvLevel,mTvCoin,mTotalScore;
    private RecyclerView mRvTaskList;
    private RecyclerView mRvSignList;
    private ProgressBar mProgress;
    private OnPositiveListener mPositiveListener;
    private AnimationSet mDialogIn,mDialogOut;
    private boolean mIsShowAnim;
    private Context mContext;
    private SimpleAdapter mAdapter;
    private TaskAdapter mTaskAdapter;
    private boolean mIsSign;
    private int mDay;
    private DailyTaskEntity mEntity;

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
        mClose = findViewById(R.id.iv_close);
        mSign = findViewById(R.id.iv_sign);
        mTvLevel = findViewById(R.id.tv_level);
        mTvCoin = findViewById(R.id.tv_coin);
        mTotalScore = findViewById(R.id.tv_total_score);
        mRvTaskList = findViewById(R.id.rv_task_list);
        mRvSignList = findViewById(R.id.rv_sign_info);
        mProgress = findViewById(R.id.pb_score);
        mInfo = findViewById(R.id.iv_info);
        LinearLayoutManager l1 = new LinearLayoutManager(mContext);
        mRvTaskList.setLayoutManager(l1);
        mRvTaskList.setHasFixedSize(true);
        mTaskAdapter = new TaskAdapter();
        mRvTaskList.setAdapter(mTaskAdapter);

        LinearLayoutManager l = new LinearLayoutManager(mContext);
        l.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvSignList.setHasFixedSize(true);
        mRvSignList.setLayoutManager(l);
        mTvLevel.setText(mContext.getString(R.string.label_level,mEntity.getLevel()));
        mTvCoin.setText(""+mEntity.getSignCoin());
        mTotalScore.setText(mEntity.getNowScore() + "/" + mEntity.getUpperLimit());
        mAdapter = new SimpleAdapter();
        mRvSignList.setAdapter(mAdapter);
        mClose.setOnClickListener(this);
        mSign.setOnClickListener(this);
        mInfo.setOnClickListener(this);
        mProgress.setMax(mEntity.getUpperLimit());
        mProgress.setProgress(mEntity.getNowScore());
        mIsSign = mEntity.isCheckState();
        if (mIsSign){
            mSign.setImageResource(R.drawable.btn_signmain_signed);
        }else {
            mSign.setImageResource(R.drawable.btn_sign);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
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
        mEntity.setCheckState(mIsSign);
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

    public void setTask(DailyTaskEntity entity){
        mEntity = entity;
    }


    public void changeSignState(){
        if (mIsSign){
            mSign.setEnabled(false);
        }else {
            mSign.setEnabled(true);
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
            ((PhoneMainV2Activity)mContext).loadPerson();
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

    public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

        public TaskAdapter(){
        }

        @Override
        public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TaskViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_daily_task,parent,false));
        }

        @Override
        public void onBindViewHolder(TaskViewHolder holder, int position) {
            DailyTaskEntity.TaskItem  taskItem = mEntity.getItems().get(position);
            holder.title.setText(taskItem.getTaskName());
            holder.desc.setText(taskItem.getDesc());
            holder.score.setText(taskItem.getNowScore() + "");
            holder.limit.setText(mContext.getString(R.string.label_task_limit,taskItem.getUpperLimit()));
            if(taskItem.getNowScore() == taskItem.getUpperLimit()){
                holder.title.setSelected(true);
                holder.score.setSelected(true);
                holder.limit.setSelected(true);
            }else {
                holder.title.setSelected(false);
                holder.score.setSelected(false);
                holder.limit.setSelected(false);
            }
        }

        @Override
        public int getItemCount() {
            return mEntity.getItems().size();
        }

        public void setCoin(int day){
            mEntity.setSignDay(day);
            notifyDataSetChanged();
        }

        public class TaskViewHolder extends RecyclerView.ViewHolder{

            public TextView title,desc,score,limit;

            public TaskViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.tv_title);
                desc = (TextView) itemView.findViewById(R.id.tv_desc);
                score = (TextView) itemView.findViewById(R.id.tv_score);
                limit = (TextView) itemView.findViewById(R.id.tv_limit);
            }
        }
    }

    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder>{

        public SimpleAdapter(){
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SimpleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sign_info,parent,false));
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            DailyTaskEntity.SignItem  signItem = mEntity.getSignItem().get(position);
            if(position < mEntity.getSignDay()){
                holder.root.setBackgroundColor(ContextCompat.getColor(mContext,R.color.green_93d856));
                holder.score.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.coin.setTextColor(mContext.getResources().getColor(R.color.white));
            }else {
                holder.root.setBackgroundResource(R.drawable.shape_rect_greed_sign);
                holder.score.setTextColor(mContext.getResources().getColor(R.color.green_93d856));
                holder.coin.setTextColor(mContext.getResources().getColor(R.color.green_93d856));
            }
            holder.score.setText(mContext.getString(R.string.label_sign_info_score,signItem.getScore()));
            holder.coin.setText(mContext.getString(R.string.label_sign_info_coin,signItem.getCoin()));
        }

        @Override
        public int getItemCount() {
            return mEntity.getSignItem().size();
        }

        public void setCoin(int day){
            mEntity.setSignDay(day);
            notifyDataSetChanged();
        }

        public class SimpleViewHolder extends RecyclerView.ViewHolder{

            public LinearLayout root;
            public TextView score,coin;

            public SimpleViewHolder(View itemView) {
                super(itemView);
                root = (LinearLayout) itemView.findViewById(R.id.ll_root);
                score = (TextView) itemView.findViewById(R.id.tv_score);
                coin = (TextView) itemView.findViewById(R.id.tv_coin);
            }
        }
    }
}
