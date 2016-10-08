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
import android.widget.Toast;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.moemoe.lalala.data.DustListBean;
import com.moemoe.lalala.data.DustResponse;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.IConstants;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.utils.TrashListAnim;
import com.moemoe.lalala.utils.ViewSwitchUtils;
import com.moemoe.lalala.view.recycler.RecyclerViewPositionHelper;
import com.moemoe.lalala.view.speedrecycler.CardLinearSnapHelper;

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

    @FindView(R.id.tv_trash_count)
    private TextView mTvCount;
    @FindView(R.id.rv_trash)
    private RecyclerView mRvList;
    @FindView(R.id.ll_btn_container)
    private View mBtnRoot;
    @FindView(R.id.tv_send)
    private TextView mTvSend;
    @FindView(R.id.tv_get)
    private TextView mTvGet;
    @FindView(R.id.iv_my_trash)
    private ImageView mIvMyTrash;
    @FindView(R.id.iv_yesterday_best_trash)
    private ImageView mIvYesterday;
    @FindView(R.id.iv_back)
    private ImageView mIvBack;
    @FindView(R.id.iv_background)
    private ImageView mIvBackground;
    @FindView(R.id.ll_dust_container)
    private LinearLayout mLlSendRoot;
    @FindView(R.id.et_title)
    private EditText mEtTitle;
    @FindView(R.id.et_content)
    private EditText mEtContent;

    private ArrayList<DustListBean.DustInfo> infos;
    private ArrayList<DustListBean.DustInfo> myInfos;
    private ArrayList<DustListBean.DustInfo> yesterdayInfos;
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
                    if(pos == 0 || pos == mRvList.getAdapter().getItemCount() - 1){
                        mLinearSnapHelper.mNoNeedToScroll = true;
                    }else {
                        mLinearSnapHelper.mNoNeedToScroll = false;
                    }
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
            DustListBean bean = DustListBean.readFromJsonStr(trash);
            if(bean != null && bean.items.size() > 0){
                infos.addAll(bean.items);
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
        if(TextUtils.isEmpty(content)){
            ToastUtil.showCenterToast(this,R.string.msg_doc_content_cannot_null);
        }else {
            createDialog();
            Otaku.getCommonV2().sendDust(mPreferMng.getToken(), title, content).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    finalizeDialog();
                    DustResponse dustResponse = new DustResponse();
                    dustResponse.readFromJsonContent(s);
                    if(dustResponse.overTimes){
                        ToastUtil.showCenterToast(TrashActivity.this,R.string.msg_trash_send);
                    }else {
                        ToastUtil.showCenterToast(TrashActivity.this,R.string.msg_dust_send_success);
                        showGetTrashUi();
                    }
                }

                @Override
                public void failure(String e) {
                    finalizeDialog();
                    ToastUtil.showCenterToast(TrashActivity.this,R.string.msg_dust_send_fail);
                }
            }));
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
            Otaku.getCommonV2().top3DustList(mPreferMng.getToken()).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    finalizeDialog();
                    yesterdayInfos.addAll(DustListBean.readListFromJson(s));
                    if(yesterdayInfos.size() > 0){
                        mRvList.scrollToPosition(0);
                        mTvCount.setText(getString(R.string.label_yesterday_best_trash));
                    }
                    mTrashAdapter.setData(yesterdayInfos);
                    mRvList.scrollToPosition(0);
                }

                @Override
                public void failure(String e) {
                    finalizeDialog();
                }
            }));
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
        String str = DustListBean.saveToJson(infos);
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
            Otaku.getCommonV2().getDustList(mPreferMng.getToken(),Otaku.DOUBLE_LENGTH - mCurGetSize).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    finalizeDialog();
                    DustListBean listBean = DustListBean.readFromJsonStr(s);
                    if (listBean.serverStatus.equals(IConstants.CLOSE)) {
                        ToastUtil.showCenterToast(TrashActivity.this, R.string.msg_trash_close);
                    }else {
                        infos.addAll(listBean.items);
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
                }

                @Override
                public void failure(String e) {
                    finalizeDialog();
                    ToastUtil.showCenterToast(TrashActivity.this, R.string.msg_trash_used_up);
                }
            }));
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
            Otaku.getCommonV2().sotDustList(mPreferMng.getToken(),index,Otaku.DOUBLE_LENGTH - mCurSendSize).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    finalizeDialog();
                    myInfos.addAll(DustListBean.readListFromJson(s));

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

                @Override
                public void failure(String e) {
                    finalizeDialog();
                }
            }));
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
        private ArrayList<DustListBean.DustInfo> dustList = null;

        public TrashAdapter(){
        }

        public void setData(ArrayList<DustListBean.DustInfo> infos){
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
            final DustListBean.DustInfo info = dustList.get(position);
            holder.ivAnim.setImageResource(R.drawable.bg_spitball_close);
            holder.copyTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("绅士内容", info.content);
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
                    if(!info.isOpen){
                        if(info.background == backIds[0]){
                            holder.ivAnim.setImageResource(R.drawable.dust_open_1_anim);
                        }else if(info.background == backIds[1]){
                            holder.ivAnim.setImageResource(R.drawable.dust_open_2_anim);
                        }else if(info.background == backIds[2]){
                            holder.ivAnim.setImageResource(R.drawable.dust_open_3_anim);
                        }
                        if(holder.mDustAniDraw == null){
                            holder.mDustAniDraw = (AnimationDrawable) holder.ivAnim.getDrawable();
                            holder.mDustAniDraw.start();
                            info.isOpen = true;
                            holder.ivAnim.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    holder.mDustAniDraw.stop();
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
            if(info.isOpen || mState == STATE_YESTERDAY || mState == STATE_MY_TRASH){
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

            holder.title.setText(info.title);
            holder.content.setText(info.content);
            holder.likeNum.setText(info.fun + "");
            holder.dislikeNum.setText(info.shit + "");
            if(mState == STATE_YESTERDAY){
                holder.mainRoot.setBackgroundResource(R.drawable.bg_spitball_paper_golden);
            }else {
                holder.mainRoot.setBackgroundResource(info.background);
            }
        }

        @Override
        public int getItemCount() {
            return dustList.size();
        }

        public int getCurBackground(int pos){
            return dustList.get(pos).background;
        }

        public class TrashHolder extends RecyclerView.ViewHolder{
            public ImageView ivAnim,like,dislike,copyTrash,deleteTrash;
            public View mainRoot,likeRoot,dislikeRoot;
            public TextView title,content,likeNum,dislikeNum;
            public AnimationDrawable mDustAniDraw;

            public TrashHolder(View itemView){
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

        private void fun(final DustListBean.DustInfo info,final TrashHolder holder){
            if(NetworkUtils.checkNetworkAndShowError(TrashActivity.this)){
                Otaku.getCommonV2().funDust(mPreferMng.getToken(),info.id).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                    @Override
                    public void success(String token, String s) {
                        ToastUtil.showCenterToast(TrashActivity.this,R.string.label_like_trash);
                        info.fun++;
                        holder.likeNum.setText((Integer.valueOf(holder.likeNum.getText().toString()) + 1 )+ "");
                    }

                    @Override
                    public void failure(String e) {

                    }
                }));
            }
        }

        private void shit(final DustListBean.DustInfo info,final TrashHolder holder){
            if(NetworkUtils.checkNetworkAndShowError(TrashActivity.this)){
                Otaku.getCommonV2().shitDust(mPreferMng.getToken(),info.id).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                    @Override
                    public void success(String token, String s) {
                        ToastUtil.showCenterToast(TrashActivity.this,R.string.label_dislike_trash);
                        info.shit++;
                        holder.dislikeNum.setText((Integer.valueOf(holder.dislikeNum.getText().toString()) + 1 )+ "");
                    }


                    @Override
                    public void failure(String e) {

                    }
                }));
            }
        }
    }
}
