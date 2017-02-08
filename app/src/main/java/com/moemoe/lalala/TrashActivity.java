package com.moemoe.lalala;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moemoe.lalala.data.DustSendTextBean;
import com.moemoe.lalala.data.DustTextBean;
import com.moemoe.lalala.network.OneParameterCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.network.SimpleCallback;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.utils.TrashListAnim;
import com.moemoe.lalala.utils.ViewSwitchUtils;
import com.moemoe.lalala.view.recycler.RecyclerViewPositionHelper;
import com.moemoe.lalala.view.speedrecycler.CardLinearSnapHelper;

import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Haru on 2016/9/20.
 */
@ContentView(R.layout.ac_trash)
public class TrashActivity extends BaseActivity implements View.OnClickListener{

    private static final int STATE_BACK = 0;
    private static final int STATE_MY_TRASH = 1;
    private static final int STATE_YESTERDAY = 2;
    private static final int STATE_SEND_TRASH = 3;

    @ViewInject(R.id.tv_trash_count)
    private TextView mTvCount;
    @ViewInject(R.id.rv_trash)
    private RecyclerView mRvList;
    @ViewInject(R.id.ll_btn_container)
    private View mBtnRoot;
    @ViewInject(R.id.tv_send)
    private TextView mTvSend;
    @ViewInject(R.id.tv_get)
    private TextView mTvGet;
    @ViewInject(R.id.iv_my_trash)
    private ImageView mIvMyTrash;
    @ViewInject(R.id.iv_yesterday_best_trash)
    private ImageView mIvYesterday;
    @ViewInject(R.id.iv_back)
    private ImageView mIvBack;
    @ViewInject(R.id.ll_dust_container)
    private LinearLayout mLlSendRoot;
    @ViewInject(R.id.et_title)
    private EditText mEtTitle;
    @ViewInject(R.id.et_content)
    private EditText mEtContent;

    private ArrayList<DustTextBean> infos;
    private ArrayList<DustTextBean> myInfos;
    private ArrayList<DustTextBean> yesterdayInfos;
    private TrashAdapter mTrashAdapter;
    private int[] backIds = {R.drawable.bg_spitball_paper_clean,R.drawable.bg_spitball_paper_dirty,R.drawable.bg_spitball_paper_golden};
    private RecyclerViewPositionHelper mRecyclerViewHelper;
    private CardLinearSnapHelper mLinearSnapHelper = new CardLinearSnapHelper();
    private int mState = STATE_BACK;
    private int mCurGetSize;
    private int mCurSendSize = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveTrashList();
    }

    @Override
    protected void initView() {
        mTvCount.setText(getString(R.string.label_count_dust,0,20));
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvList.setLayoutManager(linearLayoutManager);
        infos = new ArrayList<>();
        myInfos = new ArrayList<>();
        yesterdayInfos = new ArrayList<>();
        mTrashAdapter = new TrashAdapter();
        mRvList.setAdapter(mTrashAdapter);
        mRvList.setItemAnimator(new TrashListAnim());
        mLinearSnapHelper.attachToRecyclerView(mRvList);
        initTrash();
        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(mRvList);
        mRvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int pos = mRecyclerViewHelper.findFirstCompletelyVisibleItemPosition();
                    mLinearSnapHelper.mNoNeedToScroll = pos == 0 || pos == mRvList.getAdapter().getItemCount() - 1;
                    if(pos >= 0 && mState != STATE_YESTERDAY){
                        int total = 0;
                        if(mState == STATE_MY_TRASH){
                            total = myInfos.size();
                        }else if(mState == STATE_BACK){
                            total = infos.size();
                        }
                        mTvCount.setText(getString(R.string.label_count_dust,pos + 1,total));
                    }
                    if (mState == STATE_YESTERDAY){
                        if(pos == 0){
                            mTvCount.setText(getString(R.string.label_yesterday_best_trash));
                        }else if(pos == 1){
                            mTvCount.setText(getString(R.string.label_yesterday_better_trash));
                        }else if(pos == 2){
                            mTvCount.setText(getString(R.string.label_yesterday_good_trash));
                        }
                    }
                }
            }
        });
        mTvSend.setOnClickListener(this);
        mTvGet.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mIvMyTrash.setOnClickListener(this);
        mIvYesterday.setOnClickListener(this);
    }

    private void initTrash(){
        mState = STATE_BACK;
        mTvCount.setText(getString(R.string.label_count_dust,infos.size() == 0? 0 : 1,infos.size()));
        mTrashAdapter.setData(infos);
        mRvList.scrollToPosition(0);
        mRvList.setVisibility(View.VISIBLE);
        mBtnRoot.setVisibility(View.VISIBLE);
        mIvMyTrash.setVisibility(View.VISIBLE);
        mIvYesterday.setVisibility(View.VISIBLE);
        mIvBack.setVisibility(View.VISIBLE);
        mLlSendRoot.setVisibility(View.INVISIBLE);
        mTvSend.setText(getString(R.string.label_send_some_dust));
        mTvGet.setText(getString(R.string.label_get_dust));
        String trash = mPreferMng.getTrash();
        if(!TextUtils.isEmpty(trash)){
            //DustListBean bean = DustListBean.readFromJsonStr(trash);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<DustTextBean>>(){}.getType();
            ArrayList<DustTextBean> beans = gson.fromJson(trash,type);
            if(beans != null && beans.size() > 0){
                infos.addAll(beans);
                mCurGetSize = infos.size();
                mTvCount.setText(getString(R.string.label_count_dust,1,infos.size()));
                mTrashAdapter.setData(infos);
            }else {
                mCurGetSize = 0;
                mTvCount.setText(getString(R.string.label_empty_trash));
            }
        }else {
            mCurGetSize = 0;
            mTvCount.setText(getString(R.string.label_empty_trash));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.iv_my_trash){
            if(mCurSendSize == 0){
                showMySendTrash(mCurSendSize);
            }else {
                showMySendTrash(mCurSendSize - 1);
            }

        }else if(id == R.id.iv_yesterday_best_trash){
            showYesterdayTrash();
        }else if(id == R.id.iv_back){
            if(mState == STATE_BACK){
                finish();
            }else{
                showGetTrashUi();
            }
        }else if(id == R.id.tv_send){
            if(mState == STATE_BACK){
                showSendTrashDialog();
            }else {
                showGetTrashUi();
            }
        }else if(id == R.id.tv_get){
            if(mState == STATE_BACK){
                showMyGetTrash();
            }else {
                sendTrash();
            }
        }
    }

    private void sendTrash(){
        String title = mEtTitle.getText().toString();
        String content = mEtContent.getText().toString();
        if (!NetworkUtils.checkNetworkAndShowError(this)){
            return;
        }
        if(TextUtils.isEmpty(content)){
            ToastUtil.showCenterToast(this,R.string.msg_doc_content_cannot_null);
        }else {
            createDialog();
            DustSendTextBean bean = new DustSendTextBean(title, content);
            Otaku.getCommonV2().sendDust(bean, new SimpleCallback() {
                @Override
                public void action() {
                    finalizeDialog();
                    ToastUtil.showCenterToast(TrashActivity.this,R.string.msg_dust_send_success);
                    showGetTrashUi();
                }
            }, new OneParameterCallback<Integer>() {
                @Override
                public void action(Integer integer) {
                    finalizeDialog();
                    ErrorCodeUtils.showErrorMsgByCode(TrashActivity.this,integer);
                }
            });
        }
    }

    private void showSendTrashDialog(){
        mLlSendRoot.setVisibility(View.VISIBLE);
        mTvCount.setVisibility(View.GONE);
        mIvBack.setVisibility(View.GONE);
        mRvList.setVisibility(View.GONE);
        mBtnRoot.setVisibility(View.VISIBLE);
        mIvMyTrash.setVisibility(View.GONE);
        mIvYesterday.setVisibility(View.GONE);
        mTvSend.setText(getString(R.string.label_cancel_send_dust));
        mTvGet.setText(getString(R.string.label_send_dust));
        mState = STATE_SEND_TRASH;
    }

    private void showYesterdayTrash(){
        mState = STATE_YESTERDAY;
        mTvCount.setVisibility(View.VISIBLE);
        mTvCount.setText("");
        mBtnRoot.setVisibility(View.GONE);
        mIvMyTrash.setVisibility(View.GONE);
        mIvYesterday.setVisibility(View.GONE);
        yesterdayInfos.clear();
        mTrashAdapter.setData(yesterdayInfos);
        if (NetworkUtils.checkNetworkAndShowError(this)){
            createDialog();
            Otaku.getCommonV2().top3DustList(new OneParameterCallback<ArrayList<DustTextBean>>() {
                @Override
                public void action(ArrayList<DustTextBean> dustTextBeen) {
                    finalizeDialog();
                    yesterdayInfos.addAll(dustTextBeen);
                    if(yesterdayInfos.size() > 0){
                        mRvList.scrollToPosition(0);
                        mTvCount.setText(getString(R.string.label_yesterday_best_trash));
                    }
                    mTrashAdapter.setData(yesterdayInfos);
                    mRvList.scrollToPosition(0);
                }
            }, new OneParameterCallback<Integer>() {
                @Override
                public void action(Integer integer) {
                    finalizeDialog();
                }
            });
        }
    }

    private void showGetTrashUi(){
        mState = STATE_BACK;
        mTvCount.setVisibility(View.VISIBLE);
        if(mCurGetSize == 0){
            mTvCount.setText(getString(R.string.label_empty_trash));
        }else {
            mTvCount.setText(getString(R.string.label_count_dust,1,infos.size()));
            mRvList.scrollToPosition(0);
        }
        mRvList.setVisibility(View.VISIBLE);
        mBtnRoot.setVisibility(View.VISIBLE);
        mIvMyTrash.setVisibility(View.VISIBLE);
        mIvYesterday.setVisibility(View.VISIBLE);
        mIvBack.setVisibility(View.VISIBLE);
        mLlSendRoot.setVisibility(View.INVISIBLE);
        mTvSend.setText(getString(R.string.label_send_some_dust));
        mTvGet.setText(getString(R.string.label_get_dust));
        mTrashAdapter.setData(infos);
    }

    private void saveTrashList(){
        //String str = DustListBean.saveToJson(infos);
        Gson gson = new Gson();
        String str = gson.toJson(infos);
        mPreferMng.saveGetTrash(str);
    }

    private void showMyGetTrash(){
        if(NetworkUtils.checkNetworkAndShowError(this)){
            mCurGetSize = infos.size();
            if(mCurGetSize == Otaku.DOUBLE_LENGTH){
                ToastUtil.showCenterToast(TrashActivity.this, R.string.label_get_more_trash);
                return;
            }
            createDialog();
            Otaku.getCommonV2().getDustList(Otaku.DOUBLE_LENGTH - mCurGetSize, new OneParameterCallback<ArrayList<DustTextBean>>() {
                @Override
                public void action(ArrayList<DustTextBean> dustTextBeen) {
                    finalizeDialog();
                    infos.addAll(dustTextBeen);
                    if(infos.size() == 0){
                        mTvCount.setText(getString(R.string.label_empty_trash));
                    }else {
                        mTvCount.setText(getString(R.string.label_count_dust,mRecyclerViewHelper.findFirstCompletelyVisibleItemPosition() + 1,infos.size()));
                    }
                    if(mCurGetSize == 0 && infos.size() != 0){
                        mTvCount.setText(getString(R.string.label_count_dust,1,infos.size()));
                    }
                    mCurGetSize = infos.size();
                    mTrashAdapter.setData(infos);
                }
            }, new OneParameterCallback<Integer>() {
                @Override
                public void action(Integer integer) {
                    finalizeDialog();
                    ErrorCodeUtils.showErrorMsgByCode(TrashActivity.this,integer);
                }
            });
        }
    }

    private void showMySendTrash(int index){
        mState = STATE_MY_TRASH;
        mTvCount.setVisibility(View.VISIBLE);
        mTvCount.setText(getString(R.string.label_send_empty_trash));
        mBtnRoot.setVisibility(View.GONE);
        mIvMyTrash.setVisibility(View.GONE);
        mIvYesterday.setVisibility(View.GONE);
        myInfos.clear();
        mTrashAdapter.setData(myInfos);
        if(NetworkUtils.checkNetworkAndShowError(this)){
            createDialog();
            Otaku.getCommonV2().sotDustList(index, Otaku.DOUBLE_LENGTH - mCurSendSize, new OneParameterCallback<ArrayList<DustTextBean>>() {
                @Override
                public void action(ArrayList<DustTextBean> dustTextBeen) {
                    finalizeDialog();
                    myInfos.addAll(dustTextBeen);
                    if(myInfos.size() == 0){
                        mTvCount.setText(getString(R.string.label_send_empty_trash));
                    }else {
                        mTvCount.setText(getString(R.string.label_count_dust,mRecyclerViewHelper.findFirstCompletelyVisibleItemPosition() + 1,myInfos.size()));
                    }
                    if(mCurSendSize == 0 && myInfos.size() != 0){
                        mTvCount.setText(getString(R.string.label_count_dust,1,myInfos.size()));
                    }
                    mCurSendSize = myInfos.size();
                    mTrashAdapter.setData(myInfos);
                }
            }, new OneParameterCallback<Integer>() {
                @Override
                public void action(Integer integer) {
                    finalizeDialog();
                }
            });
        }
    }

    private void clickImageAnim(View view){
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f,
                1.3f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1f,
                1.3f, 1f);
        PropertyValuesHolder roteZ = PropertyValuesHolder.ofFloat("rotation", 0f,
                -30f, 30f,0f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY,roteZ).setDuration(500).start();


    }

    class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.TrashHolder>{
        private ArrayList<DustTextBean> dustList = null;

        TrashAdapter(){
        }

        public void setData(ArrayList<DustTextBean> infos){
            dustList = infos;
            notifyDataSetChanged();
        }

        @Override
        public TrashHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trash,parent,false);
            return new TrashHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final TrashHolder holder, final int position) {
            if(position == 0){
                int leftMargin = DensityUtil.dip2px(53);
                ViewSwitchUtils.setViewMargin(holder.itemView,leftMargin,0,0,0);
            }else if(getItemCount() - 1 == position){
                int rightMargin = DensityUtil.dip2px(53);
                ViewSwitchUtils.setViewMargin(holder.itemView,0,0,rightMargin,0);
            }else {
                ViewSwitchUtils.setViewMargin(holder.itemView,0,0,0,0);
            }
            final DustTextBean info = dustList.get(position);
            if(info.getShit() <= 0){
                info.setBackground(R.drawable.bg_spitball_paper_clean);
            }else {
                if(info.getFun() >= 20 && info.getFun() / info.getShit() > 2){
                    info.setBackground(R.drawable.bg_spitball_paper_golden);
                }else if(info.getShit() >= 20 && info.getShit() > info.getFun()){
                    info.setBackground(R.drawable.bg_spitball_paper_dirty);
                }else {
                    info.setBackground(R.drawable.bg_spitball_paper_clean);
                }
            }
            holder.ivAnim.setImageResource(R.drawable.bg_spitball_close);
            holder.copyTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("绅士内容", info.getContent());
                    cmb.setPrimaryClip(mClipData);
                    ToastUtil.showCenterToast(TrashActivity.this, R.string.msg_trash_get);
                }
            });
            holder.deleteTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mState == STATE_BACK){
                        int temp = mRecyclerViewHelper.findFirstCompletelyVisibleItemPosition();
                        //infos.remove(temp);
                        dustList.remove(info);
                        int i = temp + 1;
                        if(i > dustList.size()){
                            mTvCount.setText(getString(R.string.label_count_dust,dustList.size(),dustList.size()));
                        }else {
                            mTvCount.setText(getString(R.string.label_count_dust,i,dustList.size()));
                        }
                        if (dustList.size() == 0){
                            mCurGetSize = 0;
                            mTvCount.setText(getString(R.string.label_empty_trash));
                        }
                        notifyDataSetChanged();
                    }
                }
            });
            holder.likeRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mState != STATE_MY_TRASH){
                        clickImageAnim(holder.like);
                        fun(info,holder);
                    }
                }
            });
            holder.dislikeRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mState != STATE_MY_TRASH) {
                        clickImageAnim(holder.dislike);
                        shit(info,holder);
                    }
                }
            });
            holder.ivAnim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!info.isOpen()){
                        if(info.getBackground() == backIds[0]){
                            holder.ivAnim.setImageResource(R.drawable.dust_open_1_anim);
                        }else if(info.getBackground() == backIds[1]){
                            holder.ivAnim.setImageResource(R.drawable.dust_open_2_anim);
                        }else if(info.getBackground() == backIds[2]){
                            holder.ivAnim.setImageResource(R.drawable.dust_open_3_anim);
                        }
                        if(holder.mDustAniDraw == null){
                            holder.mDustAniDraw = (AnimationDrawable) holder.ivAnim.getDrawable();
                            holder.mDustAniDraw.start();
                            info.setOpen(true);
                            holder.ivAnim.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(holder.mDustAniDraw != null) holder.mDustAniDraw.stop();
                                    holder.ivAnim.setVisibility(View.GONE);
                                    holder.mainRoot.setVisibility(View.VISIBLE);
                                    holder.likeRoot.setVisibility(View.VISIBLE);
                                    holder.dislikeRoot.setVisibility(View.VISIBLE);
                                    holder.copyTrash.setVisibility(View.VISIBLE);
                                    holder.deleteTrash.setVisibility(View.VISIBLE);
                                }
                            },900);
                        }
                    }
                }
            });
            if(holder.mDustAniDraw != null){
                holder.mDustAniDraw.stop();
                holder.mDustAniDraw = null;
            }
            if(info.isOpen() || mState == STATE_YESTERDAY || mState == STATE_MY_TRASH){
                holder.ivAnim.setVisibility(View.GONE);
                holder.mainRoot.setVisibility(View.VISIBLE);
                holder.likeRoot.setVisibility(View.VISIBLE);
                holder.dislikeRoot.setVisibility(View.VISIBLE);
                holder.copyTrash.setVisibility(View.VISIBLE);
                if (mState == STATE_MY_TRASH || mState == STATE_YESTERDAY){
                    holder.deleteTrash.setVisibility(View.GONE);
                }else {
                    holder.deleteTrash.setVisibility(View.VISIBLE);
                }
            }else {
                holder.ivAnim.setVisibility(View.VISIBLE);
                holder.mainRoot.setVisibility(View.INVISIBLE);
                holder.likeRoot.setVisibility(View.GONE);
                holder.dislikeRoot.setVisibility(View.GONE);
                holder.copyTrash.setVisibility(View.GONE);
                holder.deleteTrash.setVisibility(View.GONE);
            }

            holder.title.setText(info.getTitle());
            holder.content.setText(info.getContent());
            holder.likeNum.setText(String.valueOf(info.getFun() ));
            holder.dislikeNum.setText(String.valueOf(info.getShit()));
            if(mState == STATE_YESTERDAY){
                holder.mainRoot.setBackgroundResource(R.drawable.bg_spitball_paper_golden);
            }else {
                holder.mainRoot.setBackgroundResource(info.getBackground());
            }
        }

        @Override
        public int getItemCount() {
            return dustList.size();
        }

        class TrashHolder extends RecyclerView.ViewHolder{
            ImageView ivAnim,like,dislike,copyTrash,deleteTrash;
            View mainRoot,likeRoot,dislikeRoot;
            TextView title,content,likeNum,dislikeNum;
            AnimationDrawable mDustAniDraw;

            TrashHolder(View itemView){
                super(itemView);
                ivAnim = (ImageView) itemView.findViewById(R.id.iv_anim);
                like = (ImageView) itemView.findViewById(R.id.iv_like);
                dislike = (ImageView) itemView.findViewById(R.id.iv_dislike);
                copyTrash = (ImageView) itemView.findViewById(R.id.iv_copy_trash);
                deleteTrash = (ImageView) itemView.findViewById(R.id.iv_delete_trash);
                mainRoot = itemView.findViewById(R.id.ll_dust_container);
                likeRoot = itemView.findViewById(R.id.ll_like_container);
                dislikeRoot = itemView.findViewById(R.id.ll_dislike_container);
                title = (TextView) itemView.findViewById(R.id.tv_title);
                content = (TextView) itemView.findViewById(R.id.tv_content);
                likeNum = (TextView) itemView.findViewById(R.id.tv_like_num);
                dislikeNum = (TextView) itemView.findViewById(R.id.tv_dislike_num);
            }
        }

        private void fun(final DustTextBean info, final TrashHolder holder){
            if(NetworkUtils.checkNetworkAndShowError(TrashActivity.this)){
                Otaku.getCommonV2().funDust(info.getId(), new SimpleCallback() {
                    @Override
                    public void action() {
                        ToastUtil.showCenterToast(TrashActivity.this,R.string.label_like_trash);
                        info.setFun(info.getFun() + 1);
                        holder.likeNum.setText(String.valueOf((Integer.valueOf(holder.likeNum.getText().toString()) + 1 )));
                    }
                }, new OneParameterCallback<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        ErrorCodeUtils.showErrorMsgByCode(TrashActivity.this,integer);
                    }
                });
            }
        }

        private void shit(final DustTextBean info, final TrashHolder holder){
            if(NetworkUtils.checkNetworkAndShowError(TrashActivity.this)){
                Otaku.getCommonV2().shitDust(info.getId(), new SimpleCallback() {
                    @Override
                    public void action() {
                        ToastUtil.showCenterToast(TrashActivity.this,R.string.label_dislike_trash);
                        info.setShit(info.getShit() + 1);
                        holder.dislikeNum.setText(String.valueOf((Integer.valueOf(holder.dislikeNum.getText().toString()) + 1 )));
                    }
                }, new OneParameterCallback<Integer>() {
                    @Override
                    public void action(Integer integer) {
                        ErrorCodeUtils.showErrorMsgByCode(TrashActivity.this,integer);
                    }
                });
            }
        }
    }
}
