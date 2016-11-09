package com.moemoe.lalala;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
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

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.Callback;
import com.app.common.util.DensityUtil;
import com.app.common.util.IOUtil;
import com.app.image.ImageOptions;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.DustImageBean;
import com.moemoe.lalala.data.DustResponse;
import com.moemoe.lalala.data.Image;
import com.moemoe.lalala.data.ImageDustListBean;
import com.moemoe.lalala.download.DownloadInfo;
import com.moemoe.lalala.download.DownloadManager;
import com.moemoe.lalala.download.DownloadService;
import com.moemoe.lalala.download.DownloadViewHolder;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.IConstants;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.utils.TrashListAnim;
import com.moemoe.lalala.utils.ViewSwitchUtils;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.recycler.RecyclerViewPositionHelper;
import com.moemoe.lalala.view.speedrecycler.CardLinearSnapHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Haru on 2016/9/20.
 */
@ContentView(R.layout.ac_trash)
public class ImageTrashActivity extends BaseActivity implements View.OnClickListener{

    private static final int STATE_BACK = 0;
    private static final int STATE_MY_TRASH = 1;
    private static final int STATE_YESTERDAY = 2;
    private static final int STATE_SEND_TRASH = 3;
    private static final int REQ_GET_EDIT_VERSION_IMG = 2333;
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
    @FindView(R.id.et_img_title)
    private EditText mEtTitle;
    @FindView(R.id.ll_text_root)
    private View mLlTextRoot;
    @FindView(R.id.ll_img_root)
    private View mLlImgRoot;
    @FindView(R.id.iv_add_img)
    private ImageView mIvAdd;

    private ArrayList<ImageDustListBean.DustInfo> infos;
    private ArrayList<ImageDustListBean.DustInfo> myInfos;
    private ArrayList<ImageDustListBean.DustInfo> yesterdayInfos;
    private TrashAdapter mTrashAdapter;
    private int[] backIds = {R.drawable.bg_spitball_paper_clean,R.drawable.bg_spitball_paper_dirty,R.drawable.bg_spitball_paper_golden};
    private RecyclerViewPositionHelper mRecyclerViewHelper;
    private CardLinearSnapHelper mLinearSnapHelper = new CardLinearSnapHelper();
    private int mState = STATE_BACK;
    private int mCurGetSize;
    private int mCurSendSize = 0;
    private ArrayList<String> mIconPaths = new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveTrashList();
    }

    @Override
    protected void initView() {
        mLlTextRoot.setVisibility(View.GONE);
        mLlImgRoot.setVisibility(View.VISIBLE);
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
        mIvMyTrash.setImageResource(R.drawable.btn_my_img_trash);
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
        mIvAdd.setOnClickListener(this);
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
        String trash = mPreferMng.getImgTrash();
        if(!TextUtils.isEmpty(trash)){
            ImageDustListBean bean = ImageDustListBean.readFromJsonStr(trash);
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
        }else if(id == R.id.iv_add_img){
            choosePhoto();
        }
    }

    private void choosePhoto(){
       // if (mIconPaths.size() < 1) {
            if (AppSetting.IS_EDITOR_VERSION) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                try {
                    startActivityForResult(intent, REQ_GET_EDIT_VERSION_IMG);
                } catch (Exception e) {
                }
            } else {
                try {
                    DialogUtils.createImgChooseDlg(this, null, this, mIconPaths, 1).show();
                } catch (Exception e) {
                }
            }
     //   } else {
     //       ToastUtil.showToast(this, R.string.msg_create_doc_9_jpg);
      //  }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_GET_EDIT_VERSION_IMG) {
            if (resultCode == RESULT_OK && data != null) {
                String photoPath = null;
                Uri u = data.getData();
                if (u != null) {
                    String schema = u.getScheme();
                    if ("file".equals(schema)) {
                        photoPath = u.getPath();
                    }else if ("content".equals(schema)) {
                        photoPath = StorageUtils.getTempFile(System.currentTimeMillis() + ".jpg").getAbsolutePath();
                        InputStream is = null;
                        FileOutputStream fos = null;
                        try {
                            is = getContentResolver().openInputStream(u);
                            fos = new FileOutputStream(new File(photoPath));
                            FileUtil.copyFile(is, fos);
                        } catch (Exception e) {
                        }
                        if (FileUtil.isValidGifFile(photoPath)) {
                            String newFile = StorageUtils.getTempFile(System.currentTimeMillis() + ".gif").getAbsolutePath();
                            FileUtil.copyFile(photoPath, newFile);
                            FileUtil.deleteOneFile(photoPath);
                            photoPath = newFile;
                        }
                    }
                    mIconPaths.add(photoPath);
                    onGetPhotos();
                }
            }
        }else {
            if (resultCode == Activity.RESULT_OK) {
                // file
                if (data != null) {
                    ArrayList<String> paths = data
                            .getStringArrayListExtra(MultiImageChooseActivity.EXTRA_KEY_SELETED_PHOTOS);
                    if (paths != null && paths.size() == 1) {
                        mIconPaths = paths;
                        onGetPhotos();
                    }
                }
            }
        }
    }

    private void onGetPhotos() {
        if (mIconPaths.size() == 0) {
            // 取消选择了所有图
            mIvAdd.setImageResource(R.drawable.bg_spitball_addpic);
        }else if(mIconPaths.size() <= 1){
            Utils.image().bind(mIvAdd, "file://" + mIconPaths.get(0), new ImageOptions.Builder()
                    .setSize(DensityUtil.dip2px(170), DensityUtil.dip2px(170))
                    .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    .setFailureDrawableId(R.drawable.ic_default_club_l)
                    .setLoadingDrawableId(R.drawable.ic_default_club_l)
                    .build());
        }
    }

    private void sendTrash(){
        final String title = mEtTitle.getText().toString();
        //String content = mEtContent.getText().toString();
        if(mIconPaths.size() == 0){
            ToastUtil.showCenterToast(this,R.string.msg_trash_img_cannot_null);
        }else {
            final ArrayList<Image> images = BitmapUtils.handleUploadImage(mIconPaths);
            ArrayList<String> paths = new ArrayList<String>();
            if(images != null && images.size() > 0){
                for(int i = 0; i < images.size(); i++){
                    paths.add(images.get(i).path);
                }
            }
            createDialog();
            Otaku.getAccountV2().uploadFilesToQiniu(mPreferMng.getToken(), paths, new OnNetWorkCallback<String, ArrayList<String>>() {
                @Override
                public void success(String token, ArrayList<String> result) {
                    DustImageBean bean = new DustImageBean(title,result.get(0));
                    Otaku.getCommonV2().sendImgDust(mPreferMng.getToken(),bean).enqueue(CallbackFactory.getInstance().callback1(new OnNetWorkCallback<String, String>() {
                        @Override
                        public void success(String token, String s) {
                            finalizeDialog();
                            ToastUtil.showCenterToast(ImageTrashActivity.this,R.string.msg_dust_send_success);
                            showGetTrashUi();
//                            DustResponse dustResponse = new DustResponse();
//                            dustResponse.readFromJsonContent(s);
//                            if(dustResponse.overTimes){
//                                ToastUtil.showCenterToast(ImageTrashActivity.this,R.string.msg_trash_send);
//                            }else {
//                                ToastUtil.showCenterToast(ImageTrashActivity.this,R.string.msg_dust_send_success);
//                                showGetTrashUi();
//                            }
                        }

                        @Override
                        public void failure(int code,String e) {
                            finalizeDialog();
                            ErrorCodeUtils.showErrorMsgByCode(ImageTrashActivity.this,code);
                            //ToastUtil.showCenterToast(ImageTrashActivity.this,R.string.msg_dust_send_fail);
                        }
                    }));
                }

                @Override
                public void failure(int code,String e) {
                    finalizeDialog();
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
            Otaku.getCommonV2().top3ImgDustList(mPreferMng.getToken()).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    finalizeDialog();
                    yesterdayInfos.addAll(ImageDustListBean.readListFromJson(s));
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
        String str = ImageDustListBean.saveToJson(infos);
        mPreferMng.saveGetImgTrash(str);
    }

    private void showMyGetTrash(){
        if(NetworkUtils.checkNetworkAndShowError(this)){
            mCurGetSize = infos.size();
            if(mCurGetSize == Otaku.DOUBLE_LENGTH){
                ToastUtil.showCenterToast(ImageTrashActivity.this, R.string.label_get_more_trash);
                return;
            }
            createDialog();
            Otaku.getCommonV2().getImgDustList(mPreferMng.getToken(),Otaku.DOUBLE_LENGTH - mCurGetSize).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    finalizeDialog();
                    ImageDustListBean listBean = ImageDustListBean.readFromJsonStr(s);
                    if (listBean.serverStatus.equals(IConstants.CLOSE)) {
                        ToastUtil.showCenterToast(ImageTrashActivity.this, R.string.msg_trash_close);
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
                    ToastUtil.showCenterToast(ImageTrashActivity.this, R.string.msg_trash_used_up);
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
            Otaku.getCommonV2().sotImgDustList(mPreferMng.getToken(),index,Otaku.DOUBLE_LENGTH - mCurSendSize).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                @Override
                public void success(String token, String s) {
                    finalizeDialog();
                    myInfos.addAll(ImageDustListBean.readListFromJson(s));

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
        private ArrayList<ImageDustListBean.DustInfo> dustList = null;

        public TrashAdapter(){
        }

        public void setData(ArrayList<ImageDustListBean.DustInfo> infos){
            dustList = infos;
            notifyDataSetChanged();
        }

        @Override
        public TrashHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trash_img,parent,false);
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
            final ImageDustListBean.DustInfo info = dustList.get(position);
            holder.ivAnim.setImageResource(R.drawable.bg_spitball_close);
            holder.downloadTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadRaw(info);
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
                                    holder.downloadTrash.setVisibility(View.VISIBLE);
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
                holder.downloadTrash.setVisibility(View.VISIBLE);
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
                holder.downloadTrash.setVisibility(View.GONE);
                holder.deleteTrash.setVisibility(View.GONE);
            }

            holder.title.setText(info.title);
           // holder.content.setText(info.content);
            Picasso.with(ImageTrashActivity.this)
                    .load(StringUtils.getUrl(ImageTrashActivity.this, info.image.path, DensityUtil.dip2px(170), DensityUtil.dip2px(170), false, true))
                    .resize(DensityUtil.dip2px(170), DensityUtil.dip2px(170))
                    .placeholder(R.drawable.ic_default_avatar_l)
                    .error(R.drawable.ic_default_avatar_l)
                    .centerCrop()
                    .config(Bitmap.Config.RGB_565)
                    .into(holder.content);
            holder.content.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    final ArrayList<Image> temp = new ArrayList<>();
                    temp.add(info.image);
                    Intent intent = new Intent(ImageTrashActivity.this, ImageBigSelectActivity.class);
                    intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, temp);
                    intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                            0);
                    // 以后可选择 有返回数据
                    startActivity(intent);
                }
            });
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
            public ImageView ivAnim,like,dislike, downloadTrash,deleteTrash,content;
            public View mainRoot,likeRoot,dislikeRoot;
            public TextView title,likeNum,dislikeNum;
            public AnimationDrawable mDustAniDraw;

            public TrashHolder(View itemView){
                super(itemView);
                ivAnim = (ImageView) itemView.findViewById(R.id.iv_anim);
                like = (ImageView) itemView.findViewById(R.id.iv_like);
                dislike = (ImageView) itemView.findViewById(R.id.iv_dislike);
                downloadTrash = (ImageView) itemView.findViewById(R.id.iv_download_trash);
                deleteTrash = (ImageView) itemView.findViewById(R.id.iv_delete_trash);
                mainRoot = itemView.findViewById(R.id.ll_dust_container);
                likeRoot = itemView.findViewById(R.id.ll_like_container);
                dislikeRoot = itemView.findViewById(R.id.ll_dislike_container);
                title = (TextView) itemView.findViewById(R.id.tv_title);
                content = (ImageView) itemView.findViewById(R.id.iv_content);
                likeNum = (TextView) itemView.findViewById(R.id.tv_like_num);
                dislikeNum = (TextView) itemView.findViewById(R.id.tv_dislike_num);
            }
        }

        private void fun(final ImageDustListBean.DustInfo info,final TrashHolder holder){
            if(NetworkUtils.checkNetworkAndShowError(ImageTrashActivity.this)){
                Otaku.getCommonV2().funImgDust(mPreferMng.getToken(),info.id).enqueue(CallbackFactory.getInstance().callback1(new OnNetWorkCallback<String, String>() {
                    @Override
                    public void success(String token, String s) {
                        ToastUtil.showCenterToast(ImageTrashActivity.this,R.string.label_like_trash);
                        info.fun++;
                        holder.likeNum.setText((Integer.valueOf(holder.likeNum.getText().toString()) + 1 )+ "");
                    }

                    @Override
                    public void failure(int code,String e) {
                        ErrorCodeUtils.showErrorMsgByCode(ImageTrashActivity.this,code);
                    }
                }));
            }
        }

        private void shit(final ImageDustListBean.DustInfo info,final TrashHolder holder){
            if(NetworkUtils.checkNetworkAndShowError(ImageTrashActivity.this)){
                Otaku.getCommonV2().shitImgDust(mPreferMng.getToken(),info.id).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                    @Override
                    public void success(String token, String s) {
                        ToastUtil.showCenterToast(ImageTrashActivity.this,R.string.label_dislike_trash);
                        info.shit++;
                        holder.dislikeNum.setText((Integer.valueOf(holder.dislikeNum.getText().toString()) + 1 )+ "");
                    }


                    @Override
                    public void failure(String e) {

                    }
                }));
            }
        }

        public void downloadRaw(ImageDustListBean.DustInfo bean){
            final Image image = bean.image;
            final DownloadInfo info = new DownloadInfo();
            info.setUrl(image.real_path);
            info.setFileSavePath(StringUtils.createImageFile(FileUtil.isGif(image.real_path)));
            info.setAutoRename(false);
            info.setAutoResume(true);
            DownloadManager downloadManager = DownloadService.getDownloadManager();
            downloadManager.startDownload(info, new DownloadViewHolder(null,info) {
                @Override
                public void onWaiting() {
                }

                @Override
                public void onStarted() {
                }

                @Override
                public void onLoading(long total, long current) {
                }

                @Override
                public void onSuccess(File result) {
                    image.local_path = info.getFileSavePath();
                    BitmapUtils.galleryAddPic(ImageTrashActivity.this, image.local_path);
                    ToastUtil.showToast(ImageTrashActivity.this,
                            getString(R.string.msg_register_to_gallery_success, image.local_path));

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onCancelled(Callback.CancelledException cex) {
                    IOUtil.deleteFileOrDir(new File(info.getFileSavePath()));
                }
            });
        }
    }
}
