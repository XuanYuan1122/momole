package com.moemoe.lalala.view.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.DocDetailEntity;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.NewDocType;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;
import com.moemoe.lalala.netamusic.data.model.PlayList;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.netamusic.player.IPlayBack;
import com.moemoe.lalala.netamusic.player.Player;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GlideCircleTransform;
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.activity.JuBaoActivity;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;
import com.moemoe.lalala.view.activity.NewPersonalActivity;
import com.moemoe.lalala.view.activity.WebViewActivity;
import com.moemoe.lalala.view.widget.adapter.NewDocLabelAdapter;
import com.moemoe.lalala.view.widget.longimage.LongImageView;
import com.moemoe.lalala.view.widget.view.DocLabelView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zlc.season.rxdownload.RxDownload;
import zlc.season.rxdownload.entity.DownloadStatus;


/**
 * Created by Haru on 2016/4/14 0014.
 */
public class DocRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SeekBar.OnSeekBarChangeListener,IPlayBack.Callback {

    private static final int TYPE_CREATOR = 0;
    private static final int TYPE_TEXT = 1;
    private static final int TYPE_IMAGE = 2;
    private static final int TYPE_MUSIC = 3;
    private static final int TYPE_LINK = 4;
    private static final int TYPE_CHAPTER = 5;
    private static final int TYPE_LABEL = 6;
    private static final int TYPE_COMMENT = 7;
    private static final int TYPE_COIN = 8;
    private static final int TYPE_COIN_TEXT = 9;
    private static final int TYPE_COIN_IMAGE = 10;
    private static final int TYPE_FLOOR = 11;
    private static final int TYPE_FOLDER = 12;
    private static final long LONG_PRESS_TIME = 500;

    private static final long UPDATE_PROGRESS_INTERVAL = 1000;
    /**
     * 当前触摸点相对于屏幕的坐标
     */
    private int mCurrentInScreenX;
    private int mCurrentInScreenY;
    /**
     * 触摸点按下时的相对于屏幕的坐标
     */
    private int mDownInScreenX;
    private int mDownInScreenY;
    /**
     * 当前点击时间
     */
    private long mCurrentClickTime;
    private RxDownload downloadSub;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private Player mPlayer;
    private MusicHolder mMusicHolder;
    private LabelHolder mLabelHolder;
    private Song mMusicInfo;
    private int mTagsPosition = -1;
    private DocDetailEntity mDocBean;
    private ArrayList<NewCommentEntity> mComments;
    private ArrayList<DocTagEntity> mTags;
    private PopupWindow mPop;
    private int mCurFirstFloor = 0;
    private boolean mTargetId;
    private OnItemClickListener onItemClickListener;
    private int[] ids = new int[] { R.id.tv_comment_delete, R.id.ll_own_del_root,R.id.tv_comment_report , R.id.tv_comment_reply, R.id.tv_comment_copy};
    private Handler mHandler = new Handler();
    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (mPlayer.isPlaying()) {
                int progress = (int) (mMusicHolder.sbMusicTime.getMax()
                        * ((float) mPlayer.getProgress() / (float) getCurrentSongDuration()));
                updateProgressTextWithDuration(mPlayer.getProgress());
                if (progress >= 0 && progress <= mMusicHolder.sbMusicTime.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mMusicHolder.sbMusicTime.setProgress(progress, true);
                    } else {
                        mMusicHolder.sbMusicTime.setProgress(progress);
                    }
                    mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL);
                }
            }
        }
    };

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public DocRecyclerViewAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mComments = new ArrayList<>();
        mTags = new ArrayList<>();
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
        downloadSub = RxDownload.getInstance()
                .maxThread(3)
                .maxRetryCount(3)
                .defaultSavePath(StorageUtils.getGalleryDirPath())
                .retrofit(MoeMoeApplication.getInstance().getNetComponent().getRetrofit());
    }

    public void releaseAdapter(){
        mPlayer.pause();
        mPlayer.unregisterCallback(this);
    }

    @Override
    public void onSwitchLast(@Nullable Song last) {
        onSongUpdate(last);
    }

    @Override
    public void onSwitchNext(@Nullable Song next) {
        onSongUpdate(next);
    }

    @Override
    public void onComplete(@Nullable Song next) {
        onSongUpdate(next);
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        if(mMusicHolder != null && mMusicHolder.ivMusicCtrl != null){
            if (isPlaying) {
                Song playing = mPlayer.getPlayingSong();
                if(mMusicInfo != null && playing.getPath().equals(mMusicInfo.getPath())){
                    mHandler.removeCallbacks(mProgressCallback);
                    mHandler.post(mProgressCallback);
                    mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_stop);
                }
            } else {
                mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_play);
                mHandler.removeCallbacks(mProgressCallback);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_CREATOR:
                return new CreatorHolder(mLayoutInflater.inflate(R.layout.item_new_doc_creator,parent,false));
            case TYPE_TEXT:
                return new TextHolder(mLayoutInflater.inflate(R.layout.item_new_doc_text,parent,false));
            case TYPE_IMAGE:
                return new ImageHolder(mLayoutInflater.inflate(R.layout.item_new_doc_image,parent,false));
            case TYPE_MUSIC:
                return new MusicHolder(mLayoutInflater.inflate(R.layout.item_new_doc_music,parent,false));
            case TYPE_LINK:
                return new LinkHolder(mLayoutInflater.inflate(R.layout.item_new_doc_music,parent,false));
            case TYPE_CHAPTER:
                return new ChapterHolder(mLayoutInflater.inflate(R.layout.item_new_doc_label_root,parent,false));
            case TYPE_LABEL:
                return new LabelHolder(mLayoutInflater.inflate(R.layout.item_new_doc_label,parent,false));
            case TYPE_COMMENT:
                return new CommentHolder(mLayoutInflater.inflate(R.layout.item_post_comment,parent,false));
            case TYPE_COIN:
                return new CoinHideViewHolder(mLayoutInflater.inflate(R.layout.item_new_doc_hide_top,parent,false));
            case TYPE_COIN_TEXT:
                return new HideTextHolder(mLayoutInflater.inflate(R.layout.item_new_doc_hide_text,parent,false));
            case TYPE_COIN_IMAGE:
                return new HideImageHolder(mLayoutInflater.inflate(R.layout.item_new_doc_hide_image,parent,false));
            case TYPE_FLOOR:
                return new FloorHolder((mLayoutInflater.inflate(R.layout.item_new_doc_floor,parent,false)));
            case TYPE_FOLDER:
                return new BagFavoriteHolder(mLayoutInflater.inflate(R.layout.item_bag_get,parent,false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof CreatorHolder){
            CreatorHolder creatorHolder = (CreatorHolder) holder;
            createCreator(creatorHolder);
        }else if(holder instanceof TextHolder){
            TextHolder textHolder = (TextHolder) holder;
            createText(textHolder, position);
        }else if(holder instanceof ImageHolder){
            ImageHolder imageHolder = (ImageHolder) holder;
            createImage(imageHolder, position,20);
        }else if(holder instanceof MusicHolder){
            mMusicHolder = (MusicHolder) holder;
            createMusic(position);
        }else if(holder instanceof LinkHolder){
            LinkHolder linkHolder = (LinkHolder) holder;
            createLink(linkHolder,position);
        } else if(holder instanceof ChapterHolder){
            ChapterHolder chapterHolder = (ChapterHolder) holder;
            createChapter(chapterHolder, position);
        }else if(holder instanceof LabelHolder){
            mLabelHolder = (LabelHolder) holder;
            createLabel();
        }else if(holder instanceof CommentHolder){
            CommentHolder commentHolder = (CommentHolder) holder;
            createComment(commentHolder,position);
        }else if(holder instanceof HideTextHolder){
            HideTextHolder textHolder = (HideTextHolder) holder;
            createHideText(textHolder, position);
            HideTextHolder hideTextHolder = (HideTextHolder) holder;
            if (position == mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 1){
                hideTextHolder.mRoot.setBackgroundResource(R.drawable.shape_dash_foot);
            }else {
                hideTextHolder.mRoot.setBackgroundResource(R.drawable.shape_dash_mid);
            }
        }else if(holder instanceof HideImageHolder){
            HideImageHolder imageHolder = (HideImageHolder) holder;
            createHideImage(imageHolder, position,40);
            HideImageHolder hideImageHolder = (HideImageHolder) holder;
            if (position == mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 1){
                hideImageHolder.mRoot.setBackgroundResource(R.drawable.shape_dash_foot);
            }else {
                hideImageHolder.mRoot.setBackgroundResource(R.drawable.shape_dash_mid);
            }
        }else if(holder instanceof CoinHideViewHolder){
            CoinHideViewHolder hideViewHolder = (CoinHideViewHolder) holder;
            if(mDocBean.getCoinDetails().size() > 0){
                hideViewHolder.llHide.setVisibility(View.GONE);
                hideViewHolder.llTop.setVisibility(View.VISIBLE);
            }else {
                hideViewHolder.llHide.setVisibility(View.VISIBLE);
                hideViewHolder.llTop.setVisibility(View.GONE);
            }
            hideViewHolder.llHide.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                    alertDialogUtil.createNormalDialog(mContext,null);
                    alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void CancelOnClick() {
                            alertDialogUtil.dismissDialog();
                        }

                        @Override
                        public void ConfirmOnClick() {
                            getCoinContent();
                            alertDialogUtil.dismissDialog();
                        }
                    });
                    alertDialogUtil.showDialog();
                }
            });
        }else if(holder instanceof FloorHolder){
            FloorHolder floorHolder = (FloorHolder) holder;
            createFloor(floorHolder);
        }else if(holder instanceof BagFavoriteHolder){
            BagFavoriteHolder bagFavoriteHolder = (BagFavoriteHolder) holder;
            createFolderItem(bagFavoriteHolder,position);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(view,position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemLongClick(view,position);
                }
                return false;
            }
        });
    }

    private void setMusicInfo(Song musicInfo){
        this.mMusicInfo = musicInfo;
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if(mDocBean != null){
            size = mDocBean.getDetails().size() + mComments.size() + 3;
            if(mDocBean.getCoin() > 0){
                size += mDocBean.getCoinDetails().size() + 1;
            }
            if(mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                size++;
            }
        }
        return size;
    }

    public Object getItem(int position){
        if(position == 0){
            return "";
        }else if(position < mDocBean.getDetails().size() + 1){
            return  mDocBean.getDetails().get(position - 1).getTrueData();
        }else if( position ==  mDocBean.getDetails().size() + 1){
            if(mDocBean.getCoin() > 0){
                return "";
            }else if(mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                return mDocBean.getFolderInfo();
            }else {
                return mDocBean.getTags();
            }
        }else if(mDocBean.getCoinDetails().size() > 0 && position > mDocBean.getDetails().size() + 1 && position < mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 2){
            return mDocBean.getCoinDetails().get(position - 2 - mDocBean.getDetails().size()).getTrueData();
        }else if(mDocBean.getCoin() > 0 && position == mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 2){
            if(mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                return  mDocBean.getFolderInfo();
            }else {
                return mDocBean.getTags();
            }
        }else if(position == mTagsPosition){
            return mDocBean.getTags();
        }else if(position == mTagsPosition + 1){
            return "";
        }else {
            if(mDocBean.getCoin() > 0 && mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                return mComments.get(position - mDocBean.getDetails().size() - 5 - mDocBean.getCoinDetails().size());
            }else if(mDocBean.getCoin() > 0){
                return mComments.get(position - mDocBean.getDetails().size() - 4 - mDocBean.getCoinDetails().size());
            }else if(mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                return mComments.get(position - mDocBean.getDetails().size() - 4);
            }else {
                return mComments.get(position - mDocBean.getDetails().size() - 3);
            }
        }
    }

    public void setTags(ArrayList<DocTagEntity> entities){
        mDocBean.setTags(entities);
        notifyItemChanged(mTagsPosition);
    }

    public void setData(DocDetailEntity docBean){
        this.mComments.clear();
        this.mDocBean = docBean;
        if(mDocBean != null){
            if(mDocBean.getCoin() > 0 && mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                mTagsPosition = mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 3;
            }else if(mDocBean.getCoin() > 0){
                mTagsPosition = mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 2;
            }else if(mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                mTagsPosition = mDocBean.getDetails().size() + 2;
            }else {
                mTagsPosition = mDocBean.getDetails().size() + 1;
            }
        }
        notifyDataSetChanged();
    }

    public int getTagsPosition(){
        return mTagsPosition;
    }

    public void setComment(ArrayList<NewCommentEntity> beans,boolean targetId){
        if(beans.size() > 0){
            int bgSize = getItemCount() - mComments.size();
            int bfSize = mComments.size();
            this.mComments.clear();
            mComments.addAll(beans);
            mTargetId = targetId;
            int afSize = mComments.size();
            int btSize = afSize - bfSize;
            mCurFirstFloor = mComments.get(0).getIdx();
            notifyItemChanged(mTagsPosition + 1);
            if(btSize > 0){
                notifyItemRangeChanged(bgSize,bfSize);
                notifyItemRangeInserted(bgSize + bfSize,btSize);
            }else {
                notifyItemRangeChanged(bgSize,afSize);
                notifyItemRangeRemoved(bgSize + afSize,-btSize);
            }
        }else {
            int bgSize = getItemCount() - mComments.size();
            int bfSize = mComments.size();
            this.mComments.clear();
            mCurFirstFloor = 0;
            notifyItemChanged(mTagsPosition + 1);
            notifyItemRangeRemoved(bgSize,bfSize);
        }
    }

    public void addComment(ArrayList<NewCommentEntity> beans, boolean targetId,boolean addBefore){
        int bgSize;
        int bfSize = mComments.size();
        if(addBefore){
            bgSize = getItemCount() - bfSize;
            this.mComments.addAll(0,beans);
        }else {
            bgSize = getItemCount();
            this.mComments.addAll(beans);
        }
        int afSize = mComments.size();
        int btSize = afSize - bfSize;
        mTargetId = targetId;
        if(afSize > 0){
            mCurFirstFloor = mComments.get(0).getIdx();
        }else {
            mCurFirstFloor = 0;
        }
        if(addBefore){
            if(btSize < ApiService.LENGHT){
                mCurFirstFloor = 0;
            }
        }
        notifyItemChanged(mTagsPosition + 1);
        notifyItemRangeInserted(bgSize,btSize);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_CREATOR;
        }else if(position < mDocBean.getDetails().size() + 1){
            String type = mDocBean.getDetails().get(position - 1).getType();
            return NewDocType.getType(type);
        }else if( position ==  mDocBean.getDetails().size() + 1){
            if(mDocBean.getCoin() > 0){
                return TYPE_COIN;
            }else if(mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                return TYPE_FOLDER;
            }else {
                return TYPE_LABEL;
            }
        }else if(mDocBean.getCoinDetails().size() > 0 && position > mDocBean.getDetails().size() + 1 && position < mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 2){
            String type = mDocBean.getCoinDetails().get(position - 2 - mDocBean.getDetails().size()).getType();
            return NewDocType.getType(type) + 8;
        }else if(mDocBean.getCoin() > 0 && position == mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 2){
            if(mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                return TYPE_FOLDER;
            }else {
                return TYPE_LABEL;
            }
        }else if(position == mTagsPosition){
            return TYPE_LABEL;
        } else if(position == mTagsPosition + 1){
            return TYPE_FLOOR;
        }else {
            return TYPE_COMMENT;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            updateProgressTextWithProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekTo(getDuration(seekBar.getProgress()));
        if (mPlayer.isPlaying()) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        }
    }

    private static class CreatorHolder extends RecyclerView.ViewHolder{

        ImageView mIvCreator;
        View mIvClubOwnerFlag;
        TextView mTvCreator;
        View ivLevelColor;
        TextView tvLevel;
        View rlLevelPack;
        TextView mTvTime;
        View rlHuiZhang1;
        View rlHuiZhang2;
        View rlHuiZhang3;
        TextView tvHuiZhang1;
        TextView tvHuiZhang2;
        TextView tvHuiZhang3;
        View[] huiZhangRoots;
        TextView[] huiZhangTexts;

        CreatorHolder(View itemView) {
            super(itemView);
            mIvCreator = (ImageView) itemView.findViewById(R.id.iv_post_creator);
            mTvCreator = (TextView) itemView.findViewById(R.id.tv_post_creator_name);
            mIvClubOwnerFlag = itemView.findViewById(R.id.iv_post_owner_flag);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_post_update_time);
            ivLevelColor = itemView.findViewById(R.id.rl_level_bg);
            tvLevel = (TextView)itemView.findViewById(R.id.tv_level);
            tvHuiZhang1 = (TextView)itemView.findViewById(R.id.tv_huizhang_1);
            tvHuiZhang2 = (TextView)itemView.findViewById(R.id.tv_huizhang_2);
            tvHuiZhang3 = (TextView)itemView.findViewById(R.id.tv_huizhang_3);
            rlLevelPack = itemView.findViewById(R.id.rl_level_pack);
            rlHuiZhang1 = itemView.findViewById(R.id.fl_huizhang_1);
            rlHuiZhang2 = itemView.findViewById(R.id.fl_huizhang_2);
            rlHuiZhang3 = itemView.findViewById(R.id.fl_huizhang_3);
            huiZhangRoots = new View[]{rlHuiZhang1,rlHuiZhang2,rlHuiZhang3};
            huiZhangTexts = new TextView[]{tvHuiZhang1,tvHuiZhang2,tvHuiZhang3};
        }
    }

    private void createCreator(final CreatorHolder holder){
        Glide.with(mContext)
                .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + mDocBean.getUserIcon(), DensityUtil.dip2px(mContext,44), DensityUtil.dip2px(mContext,44),false,false))
                .override(DensityUtil.dip2px(mContext,44), DensityUtil.dip2px(mContext,44))
                .placeholder(R.drawable.bg_default_circle)
                .error(R.drawable.bg_default_circle)
                .transform(new GlideCircleTransform(mContext))
                .into(holder.mIvCreator);
        holder.mIvClubOwnerFlag.setVisibility(View.GONE);
        holder.mTvCreator.setText(mDocBean.getUserName());
        holder.rlLevelPack.setVisibility(View.VISIBLE);
        holder.tvLevel.setText(String.valueOf(mDocBean.getUserLevel()));
        holder.mTvTime.setText(StringUtils.timeFormate(mDocBean.getCreateTime()));
        holder.mIvCreator.setTag(R.id.id_creator_uuid, mDocBean.getUserId());
        holder.mIvCreator.setOnClickListener(mAvatarListener);
        int radius1 = DensityUtil.dip2px(mContext,5);
        float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
        RoundRectShape roundRectShape1 = new RoundRectShape(outerR1, null, null);
        ShapeDrawable shapeDrawable1 = new ShapeDrawable();
        shapeDrawable1.setShape(roundRectShape1);
        shapeDrawable1.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable1.getPaint().setColor(StringUtils.readColorStr(mDocBean.getUserLevelColor(), ContextCompat.getColor(mContext, R.color.main_cyan)));
        holder.ivLevelColor.setBackgroundDrawable(shapeDrawable1);
        Observable.range(0,3)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer i) {
                        holder.huiZhangTexts[i].setVisibility(View.INVISIBLE);
                        holder.huiZhangRoots[i].setVisibility(View.INVISIBLE);
                    }
                });
        if(mDocBean.getBadgeList().size() > 0){
            int size = 3;
            if(mDocBean.getBadgeList().size() < 3){
                size = mDocBean.getBadgeList().size();
            }
            for (int i = 0;i < size;i++){
                holder.huiZhangTexts[i].setVisibility(View.VISIBLE);
                holder.huiZhangRoots[i].setVisibility(View.VISIBLE);
                BadgeEntity badgeEntity = mDocBean.getBadgeList().get(i);
                TextView tv = holder.huiZhangTexts[i];
                tv.setText(badgeEntity.getTitle());
                tv.setText(badgeEntity.getTitle());
                tv.setBackgroundResource(R.drawable.bg_badge_cover);
                int px = DensityUtil.dip2px(mContext,4);
                tv.setPadding(px,0,px,0);
                int radius2 = DensityUtil.dip2px(mContext,2);
                float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
                RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
                ShapeDrawable shapeDrawable2 = new ShapeDrawable();
                shapeDrawable2.setShape(roundRectShape2);
                shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
                shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(mContext, R.color.main_cyan)));
                holder.huiZhangRoots[i].setBackgroundDrawable(shapeDrawable2);
            }
        }
    }

    private static class TextHolder extends RecyclerView.ViewHolder{

        TextView mTvText;

        TextHolder(View itemView) {
            super(itemView);
            mTvText = (TextView) itemView.findViewById(R.id.tv_doc_content);
        }
    }

    private void createText(final TextHolder holder,int position){
        holder.mTvText.setText(StringUtils.getUrlClickableText(mContext, (String) getItem(position)));
        holder.mTvText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private static class HideTextHolder  extends RecyclerView.ViewHolder{
        private LinearLayout mRoot;
        private TextView mTvText;

        HideTextHolder(View itemView) {
            super(itemView);
            mRoot = (LinearLayout) itemView.findViewById(R.id.ll_root);
            mTvText = (TextView) itemView.findViewById(R.id.tv_doc_content);
        }
    }

    private void createHideText(final HideTextHolder holder,int position){
        holder.mTvText.setText(StringUtils.getUrlClickableText(mContext, (String) getItem(position)));
        holder.mTvText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private static class ImageHolder extends RecyclerView.ViewHolder{
        ImageView mIvImage;
        LongImageView mIvLongImage;

        ImageHolder(View itemView) {
            super(itemView);
            mIvImage = (ImageView) itemView.findViewById(R.id.iv_doc_image);
            mIvLongImage = (LongImageView) itemView.findViewById(R.id.iv_doc_long_image);
        }
    }

    private void createImage(final ImageHolder holder, final int position, int size){
        final Image image  = (Image) getItem(position);
        final int[] wh = BitmapUtils.getDocIconSize(image.getW() * 2, image.getH() * 2, DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,size));
        if(wh[1] > 2048){
            holder.mIvImage.setVisibility(View.GONE);
            holder.mIvLongImage.setVisibility(View.VISIBLE);
            String temp = EncoderUtils.MD5(ApiService.URL_QINIU + image.getPath()) + ".jpg";
            final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
            ViewGroup.LayoutParams layoutParams = holder.mIvLongImage.getLayoutParams();
            layoutParams.width = wh[0];
            layoutParams.height = wh[1];
            holder.mIvLongImage.setLayoutParams(layoutParams);
            holder.mIvLongImage.requestLayout();
            if(longImage.exists()){
                holder.mIvLongImage.setImage(longImage.getAbsolutePath());
            }else {
                downloadSub.download(ApiService.URL_QINIU + image.getPath(),temp,null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<DownloadStatus>() {
                            @Override
                            public void onCompleted() {
                                BitmapUtils.galleryAddPic(mContext, longImage.getAbsolutePath());
                                holder.mIvLongImage.setImage(longImage.getAbsolutePath());
                                notifyItemChanged(position);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(DownloadStatus downloadStatus) {

                            }
                        });
            }
        }else {
            holder.mIvImage.setVisibility(View.VISIBLE);
            holder.mIvLongImage.setVisibility(View.GONE);
            if(FileUtil.isGif(image.getPath())){
                ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                holder.mIvImage.setLayoutParams(layoutParams);
                holder.mIvImage.requestLayout();
                Glide.with(mContext)
                        .load(ApiService.URL_QINIU + image.getPath())
                        .asGif()
                        .override(wh[0], wh[1])
                        .dontAnimate()
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(holder.mIvImage);
            }else {
                ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                holder.mIvImage.setLayoutParams(layoutParams);
                holder.mIvImage.requestLayout();
                Glide.with(mContext)
                        .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + image.getPath(), wh[0], wh[1], true, true))
                        .override(wh[0], wh[1])
                        .dontAnimate()
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(holder.mIvImage);
            }
        }
    }

    private static class HideImageHolder extends RecyclerView.ViewHolder{
        private LinearLayout mRoot;
        private ImageView mIvImage;
        private LongImageView mIvLongImage;

        HideImageHolder(View itemView) {
            super(itemView);
            mRoot = (LinearLayout) itemView.findViewById(R.id.ll_root);
            mIvImage = (ImageView) itemView.findViewById(R.id.iv_doc_image);
            mIvLongImage = (LongImageView) itemView.findViewById(R.id.iv_doc_long_image);
        }
    }

    private void createHideImage(final HideImageHolder holder, final int position, int size){
        Image image  = (Image) getItem(position);
        final int[] wh = BitmapUtils.getDocIconSize(image.getW() * 2, image.getH() * 2, DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,size));
        if(wh[1] > 4000){
            holder.mIvImage.setVisibility(View.GONE);
            holder.mIvLongImage.setVisibility(View.VISIBLE);
            String temp = EncoderUtils.MD5(ApiService.URL_QINIU + image.getPath()) + ".jpg";
            final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
            ViewGroup.LayoutParams layoutParams = holder.mIvLongImage.getLayoutParams();
            layoutParams.width = wh[0];
            layoutParams.height = wh[1];
            holder.mIvLongImage.setLayoutParams(layoutParams);
            holder.mIvLongImage.requestLayout();
            if(longImage.exists()){
                holder.mIvLongImage.setImage(longImage.getAbsolutePath());
            }else {
                downloadSub.download(ApiService.URL_QINIU + image.getPath(),temp,null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<DownloadStatus>() {
                            @Override
                            public void onCompleted() {
                                BitmapUtils.galleryAddPic(mContext, longImage.getAbsolutePath());
                                holder.mIvLongImage.setImage(longImage.getAbsolutePath());
                                notifyItemChanged(position);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(DownloadStatus downloadStatus) {

                            }
                        });
            }
        }else {
            if(FileUtil.isGif(image.getPath())){
                ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                holder.mIvImage.setLayoutParams(layoutParams);
                holder.mIvImage.requestLayout();
                Glide.with(mContext)
                        .load(ApiService.URL_QINIU + image.getPath())
                        .asGif()
                        .override(wh[0], wh[1])
                        .dontAnimate()
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(holder.mIvImage);
            }else {
                ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                holder.mIvImage.setLayoutParams(layoutParams);
                holder.mIvImage.requestLayout();
                Glide.with(mContext)
                        .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + image.getPath(), wh[0], wh[1], true, true))
                        .override(wh[0], wh[1])
                        .dontAnimate()
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(holder.mIvImage);
            }
        }
    }

    private static class MusicHolder extends RecyclerView.ViewHolder{
        ImageView mIvImage;
        LongImageView mIvLongImage;
        ImageView ivMusicCtrl;
        TextView tvMusicTitle;
        TextView tvMusicTime;
        SeekBar sbMusicTime;
        View musicRoot;

        MusicHolder(View itemView) {
            super(itemView);
            ivMusicCtrl = (ImageView) itemView.findViewById(R.id.iv_music_ctrl);
            tvMusicTitle = (TextView) itemView.findViewById(R.id.tv_music_name);
            tvMusicTime = (TextView) itemView.findViewById(R.id.tv_music_seek);
            sbMusicTime = (SeekBar) itemView.findViewById(R.id.sb_music);
            musicRoot = itemView.findViewById(R.id.rl_music_root);
            mIvImage = (ImageView) itemView.findViewById(R.id.iv_doc_image);
            mIvLongImage = (LongImageView) itemView.findViewById(R.id.iv_doc_long_image);
        }
    }

    private void createMusic(final int position){
        Object o = getItem(position);
        if(o instanceof DocDetailEntity.DocMusic){
            DocDetailEntity.DocMusic music = (DocDetailEntity.DocMusic) o;
            mMusicHolder.tvMusicTitle.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            mMusicHolder.tvMusicTime.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            mMusicHolder.musicRoot.setBackgroundResource(R.drawable.bg_rect_gray_doc_music);
            mMusicHolder.tvMusicTitle.setText(music.getName());
            mMusicHolder.ivMusicCtrl.setOnClickListener(musicCtrl);
            mMusicHolder.sbMusicTime.setOnSeekBarChangeListener(this);
            if(mPlayer.isPlaying()){
                Song musicInfo = mPlayer.getPlayingSong();
                if(musicInfo.getPath().equals(music.getUrl())){
                    musicInfo.setDuration(music.getTimestamp());
                    mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_stop);
                }else {
                    mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_play);
                }
            }else {
                mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_play);
            }
            Song musicInfo = new Song();
            musicInfo.setCoverPath(music.getCover().getPath());
            musicInfo.setPath(music.getUrl());
            musicInfo.setDisplayName(music.getName());
            musicInfo.setDuration(music.getTimestamp());
            setMusicInfo(musicInfo);
            mMusicHolder.sbMusicTime.setMax(music.getTimestamp());
            Song curMusic = mPlayer.getPlayingSong();
            mMusicHolder.tvMusicTime.setText(getMinute(0) + "/" + getMinute(music.getTimestamp()));
            if(curMusic != null){
                if(curMusic.getPath().equals(musicInfo.getPath())){
                    onSongUpdate(musicInfo);
                }
            }
            Image image = music.getCover();
            final int[] wh = BitmapUtils.getDocIconSize(image.getW(), image.getH(), DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,20));
            if(wh[1] > 4000){
                mMusicHolder.mIvImage.setVisibility(View.GONE);
                mMusicHolder.mIvLongImage.setVisibility(View.VISIBLE);
                String temp = EncoderUtils.MD5(ApiService.URL_QINIU + image.getPath()) + ".jpg";
                final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
                ViewGroup.LayoutParams layoutParams = mMusicHolder.mIvLongImage.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                mMusicHolder.mIvLongImage.setLayoutParams(layoutParams);
                mMusicHolder.mIvLongImage.requestLayout();
                if(longImage.exists()){
                    mMusicHolder.mIvLongImage.setImage(longImage.getAbsolutePath());
                }else {
                    downloadSub.download(ApiService.URL_QINIU + image.getPath(),temp,null)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<DownloadStatus>() {
                                @Override
                                public void onCompleted() {
                                    BitmapUtils.galleryAddPic(mContext, longImage.getAbsolutePath());
                                    mMusicHolder.mIvLongImage.setImage(longImage.getAbsolutePath());
                                    notifyItemChanged(position);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(DownloadStatus downloadStatus) {

                                }
                            });
                }
            }else {
                if(FileUtil.isGif(image.getPath())){
                    ViewGroup.LayoutParams layoutParams = mMusicHolder.mIvImage.getLayoutParams();
                    layoutParams.width = wh[0];
                    layoutParams.height = wh[1];
                    mMusicHolder.mIvImage.setLayoutParams(layoutParams);
                    mMusicHolder.mIvImage.requestLayout();
                    Glide.with(mContext)
                            .load(ApiService.URL_QINIU + image.getPath())
                            .asGif()
                            .override(wh[0], wh[1])
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .into(mMusicHolder.mIvImage);
                }else {
                    ViewGroup.LayoutParams layoutParams = mMusicHolder.mIvImage.getLayoutParams();
                    layoutParams.width = wh[0];
                    layoutParams.height = wh[1];
                    mMusicHolder.mIvImage.setLayoutParams(layoutParams);
                    mMusicHolder.mIvImage.requestLayout();
                    Glide.with(mContext)
                            .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + image.getPath(), wh[0], wh[1], true, true))
                            .override(wh[0], wh[1])
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .into(mMusicHolder.mIvImage);
                }
            }
        }
    }

    private void onSongUpdate(@Nullable Song song){
        if(song == null || mMusicInfo == null || !mMusicInfo.getPath().equals(song.getPath())){
            mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_play);
            mMusicHolder.sbMusicTime.setProgress(0);
            updateProgressTextWithProgress(0);
            seekTo(0);
            mHandler.removeCallbacks(mProgressCallback);
            return;
        }
        updateProgressTextWithDuration(mPlayer.getProgress());
        mHandler.removeCallbacks(mProgressCallback);
        if (mPlayer.isPlaying()) {
            mHandler.post(mProgressCallback);
            mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_stop);
        }
    }

    private void seekTo(int duration) {
        mPlayer.seekTo(duration);
    }

    private void updateProgressTextWithDuration(int duration) {
        mMusicHolder.tvMusicTime.setText(getMinute(duration) + "/" + getMinute(mPlayer.getPlayingSong().getDuration()));
    }

    private int getDuration(int progress) {
        return (int) (getCurrentSongDuration() * ((float) progress / mMusicHolder.sbMusicTime.getMax()));
    }

    private void updateProgressTextWithProgress(int progress) {
        int targetDuration = getDuration(progress);
        Song playing = mPlayer.getPlayingSong();
        if(playing != null){
            mMusicHolder.tvMusicTime.setText(getMinute(targetDuration) + "/" + getMinute(playing.getDuration()));
        }
    }

    private int getCurrentSongDuration() {
        Song currentSong = mPlayer.getPlayingSong();
        int duration = 0;
        if (currentSong != null) {
            duration = currentSong.getDuration();
        }
        return duration;
    }

    private class BagFavoriteHolder extends RecyclerView.ViewHolder{

        ImageView ivBg;
        TextView tvNum,tvName,tvTime,ivGot;

        public BagFavoriteHolder(View itemView) {
            super(itemView);
            ivBg = (ImageView) itemView.findViewById(R.id.iv_bg);
            tvNum = (TextView) itemView.findViewById(R.id.tv_num);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            ivGot = (TextView) itemView.findViewById(R.id.tv_got);
            ivGot.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
            layoutParams.height = DensityUtil.dip2px(mContext,120);
            layoutParams.width = DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,20);
            ivBg.setLayoutParams(layoutParams);
        }
    }

    private void createFolderItem(BagFavoriteHolder holder,int position){
        BagDirEntity entity = (BagDirEntity) getItem(position);
        Glide.with(mContext)
                .load(StringUtils.getUrl(mContext, ApiService.URL_QINIU + entity.getCover(), DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,20), DensityUtil.dip2px(mContext,120), false, true))
                .override(DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,20), DensityUtil.dip2px(mContext,120))
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .transform(new GlideRoundTransform(mContext,5))
                .into(holder.ivBg);
        holder.tvNum.setText(entity.getNumber() + "项");
        holder.tvName.setText(entity.getName());
        holder.tvTime.setText(entity.getUpdateTime() + " 更新");
    }

    private static class LinkHolder extends RecyclerView.ViewHolder{

        View mCoverRoot;
        ImageView ivMusicCtrl;
        TextView tvMusicTitle;
        TextView tvMusicTime;
        SeekBar sbMusicTime;
        View musicRoot;

        LinkHolder(View itemView) {
            super(itemView);
            ivMusicCtrl = (ImageView) itemView.findViewById(R.id.iv_music_ctrl);
            tvMusicTitle = (TextView) itemView.findViewById(R.id.tv_music_name);
            tvMusicTime = (TextView) itemView.findViewById(R.id.tv_music_seek);
            sbMusicTime = (SeekBar) itemView.findViewById(R.id.sb_music);
            musicRoot = itemView.findViewById(R.id.rl_music_root);
            mCoverRoot = itemView.findViewById(R.id.rl_image_root);
            mCoverRoot.setVisibility(View.GONE);
        }
    }

    private void createLink(final LinkHolder holder,int position){
        DocDetailEntity.DocLink bean = (DocDetailEntity.DocLink) getItem(position);
        holder.tvMusicTitle.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        holder.tvMusicTime.setTextColor(ContextCompat.getColor(mContext, R.color.gray_929292));
        holder.musicRoot.setBackgroundResource(R.drawable.bg_rect_gray_doc_link);
        holder.tvMusicTitle.setText(bean.getName());
        holder.tvMusicTime.setText(bean.getUrl());
        holder.sbMusicTime.setVisibility(View.GONE);
        Glide.with(mContext)
                .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + bean.getIcon().getPath(), DensityUtil.dip2px(mContext,45), DensityUtil.dip2px(mContext,45), false, true))
                .override(DensityUtil.dip2px(mContext,45), DensityUtil.dip2px(mContext,45))
                .placeholder(R.drawable.bg_default_circle)
                .error(R.drawable.bg_default_circle)
                .into(holder.ivMusicCtrl);
    }

    private static class ChapterHolder extends RecyclerView.ViewHolder{

        private DocLabelView mDvLabel;

        ChapterHolder(View itemView) {
            super(itemView);
            mDvLabel = (DocLabelView) itemView.findViewById(R.id.dv_doc_label_root);
        }
    }

    private void createChapter(ChapterHolder holder,int position){
        final ArrayList<DocDetailEntity.DocGroupLink.DocGroupLinkDetail> details = ((DocDetailEntity.DocGroupLink)getItem(position)).getItems();
        holder.mDvLabel.setChapter(details, mContext);
        holder.mDvLabel.setItemClickListener(new DocLabelView.LabelItemClickListener() {
            @Override
            public void itemClick(int position) {
                DocDetailEntity.DocGroupLink.DocGroupLinkDetail detail = details.get(position);
                WebViewActivity.startActivity(mContext, detail.getUrl());
            }
        });
    }

    private static class LabelHolder extends RecyclerView.ViewHolder{

        DocLabelView mDvLabel;
        NewDocLabelAdapter docLabelAdapter;

        LabelHolder(View itemView) {
            super(itemView);
            mDvLabel = (DocLabelView) itemView.findViewById(R.id.dv_doc_label_root);
            docLabelAdapter = new NewDocLabelAdapter(itemView.getContext(),false);
        }
    }

    private void createLabel(){
        mTags.clear();
        mTags.addAll(mDocBean.getTags());
        mLabelHolder.mDvLabel.setDocLabelAdapter(mLabelHolder.docLabelAdapter);
        mLabelHolder.docLabelAdapter.setData(mTags,true);
        mLabelHolder.mDvLabel.setItemClickListener(new DocLabelView.LabelItemClickListener() {
            @Override
            public void itemClick(int position) {
                addlabel(position);
            }
        });
    }

    private void giveCoin(){
        if (!NetworkUtils.checkNetworkAndShowError(mContext)){
            return;
        }
        ((NewDocDetailActivity)mContext).giveCoin();
    }

    public void onGiveCoin(){
        mDocBean.setCoinPays(mDocBean.getCoinPays() + 1);
        notifyItemChanged(mTagsPosition + 1);
    }

    private void getCoinContent(){
        if (!NetworkUtils.checkNetworkAndShowError(mContext)){
            return;
        }
        ((NewDocDetailActivity)mContext).getCoinContent();
    }


    private static class FloorHolder extends RecyclerView.ViewHolder{

        ImageView mIvGiveCoin;
        TextView mTvCoinNum;
        TextView mGoTop;
        TextView mLoadBefore;
        View lLFloorRoot;


        FloorHolder(View itemView) {
            super(itemView);
            mIvGiveCoin = (ImageView) itemView.findViewById(R.id.iv_give_coin);
            mTvCoinNum = (TextView) itemView.findViewById(R.id.tv_got_coin);
            lLFloorRoot = itemView.findViewById(R.id.ll_floor_jump_root);
            mGoTop = (TextView) itemView.findViewById(R.id.tv_to_top);
            mLoadBefore = (TextView) itemView.findViewById(R.id.tv_load_before);
        }
    }

    private void createFloor(FloorHolder holder){
        if (mCurFirstFloor > 1){
            holder.lLFloorRoot.setVisibility(View.VISIBLE);
        }else {
            holder.lLFloorRoot.setVisibility(View.GONE);
        }
        holder.mGoTop.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ((NewDocDetailActivity)mContext).createDialog();
                ((NewDocDetailActivity)mContext).requestCommentsByFloor(1,mTargetId,true,false);
            }
        });
        holder.mLoadBefore.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                int floor = 1;
                int length = mCurFirstFloor - 1;
                if (mCurFirstFloor > ApiService.LENGHT){
                    floor = mCurFirstFloor - ApiService.LENGHT ;
                    length = ApiService.LENGHT;
                }
                ((NewDocDetailActivity)mContext).createDialog();
                ((NewDocDetailActivity)mContext).requestCommentsByFloor(floor,mTargetId,false,true,length,false);
            }
        });
        holder.mTvCoinNum.setText(mContext.getString(R.string.label_got_coin,mDocBean.getCoinPays()));
        if(mDocBean.getUserId().equals(PreferenceUtils.getUUid())){
            holder.mIvGiveCoin.setImageResource(R.drawable.btn_doc_givecoins_given_enabel);
            holder.mIvGiveCoin.setEnabled(false);
        }else {
            holder.mIvGiveCoin.setEnabled(true);
            holder.mIvGiveCoin.setImageResource(R.drawable.btn_give_coin);
            holder.mIvGiveCoin.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                    alertDialogUtil.createNormalDialog(mContext,null);
                    alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void CancelOnClick() {
                            alertDialogUtil.dismissDialog();
                        }

                        @Override
                        public void ConfirmOnClick() {
                            giveCoin();
                            alertDialogUtil.dismissDialog();
                        }
                    });
                    alertDialogUtil.showDialog();
                }
            });
        }
    }


    private static class CommentHolder extends RecyclerView.ViewHolder{

        ImageView mIvCreator;
        TextView mTvCreatorName;
        TextView mTvTime;
        TextView mTvContent;
        View mIvOwnerFlag;
        ImageView mIvOpsOpen;
        View mIvLevelColor;
        TextView mTvLevel;
        TextView mFloor;
        LinearLayout llImg;
        View rlHuiZhang1;
        View rlHuiZhang2;
        View rlHuiZhang3;
        TextView tvHuiZhang1;
        TextView tvHuiZhang2;
        TextView tvHuiZhang3;
        View[] huiZhangRoots;
        TextView[] huiZhangTexts;

        CommentHolder(View itemView) {
            super(itemView);
            mIvCreator = (ImageView) itemView.findViewById(R.id.iv_comment_creator);
            mTvCreatorName = (TextView) itemView.findViewById(R.id.tv_comment_creator_name);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_comment_time);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_comment);
            mIvOwnerFlag = itemView.findViewById(R.id.iv_club_owner_flag);
            mIvOpsOpen = (ImageView) itemView.findViewById(R.id.iv_comment_open);
            mIvLevelColor = itemView.findViewById(R.id.rl_level_bg);
            mTvLevel = (TextView)itemView.findViewById(R.id.tv_level);
            mFloor = (TextView)itemView.findViewById(R.id.tv_floor);
            llImg = (LinearLayout) itemView.findViewById(R.id.ll_comment_img);
            llImg.setVisibility(View.GONE);
            tvHuiZhang1 = (TextView)itemView.findViewById(R.id.tv_huizhang_1);
            tvHuiZhang2 = (TextView)itemView.findViewById(R.id.tv_huizhang_2);
            tvHuiZhang3 = (TextView)itemView.findViewById(R.id.tv_huizhang_3);
            rlHuiZhang1 = itemView.findViewById(R.id.fl_huizhang_1);
            rlHuiZhang2 = itemView.findViewById(R.id.fl_huizhang_2);
            rlHuiZhang3 = itemView.findViewById(R.id.fl_huizhang_3);
            huiZhangRoots = new View[]{rlHuiZhang1,rlHuiZhang2,rlHuiZhang3};
            huiZhangTexts = new TextView[]{tvHuiZhang1,tvHuiZhang2,tvHuiZhang3};
        }
    }


    private void createComment(final CommentHolder holder, final int position){
        final NewCommentEntity bean = (NewCommentEntity)getItem(position);
        Glide.with(mContext)
                .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + bean.getFromUserIcon().getPath(), DensityUtil.dip2px(mContext,35), DensityUtil.dip2px(mContext,35), false, false))
                .override(DensityUtil.dip2px(mContext,35), DensityUtil.dip2px(mContext,35))
                .placeholder(R.drawable.bg_default_circle)
                .error(R.drawable.bg_default_circle)
                .transform(new GlideCircleTransform(mContext))
                .into(holder.mIvCreator);
        holder.mTvCreatorName.setText(bean.getFromUserName());
        holder.mTvTime.setText(StringUtils.timeFormate(bean.getCreateTime()));
        if(bean.isDeleteFlag()){
            holder.mTvContent.setText(mContext.getString(R.string.label_comment_already));
            holder.llImg.setVisibility(View.GONE);
        }else {
            String comm;
            if (!TextUtils.isEmpty(bean.getToUserName()) ) {
                comm = "回复 " + (TextUtils.isEmpty(bean.getToUserName()) ? "" :bean.getToUserName()) + ": "
                        + bean.getContent();
            } else {
                comm = bean.getContent();
            }
            holder.mTvContent.setText(StringUtils.getUrlClickableText(mContext, comm));
            holder.mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
            if(bean.getImages().size() > 0 && !bean.isNewDeleteFlag()){
                holder.llImg.setVisibility(View.VISIBLE);
                holder.llImg.removeAllViews();
                for (int i = 0;i < bean.getImages().size();i++){
                    final int pos = i;
                    Image image = bean.getImages().get(i);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.topMargin = DensityUtil.dip2px(mContext,5);
                    if(FileUtil.isGif(image.getPath())){
                        ImageView imageView = new ImageView(mContext);
                        setGif(image, imageView,params);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, ImageBigSelectActivity.class);
                                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, bean.getImages());
                                intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                                        pos);
                                // 以后可选择 有返回数据
                                mContext.startActivity(intent);
                            }
                        });
                        holder.llImg.addView(imageView,holder.llImg.getChildCount(),params);
                    }else {
                        ImageView imageView = new ImageView(mContext);
                        setImage(image, imageView,params);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, ImageBigSelectActivity.class);
                                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, bean.getImages());
                                intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                                        pos);
                                // 以后可选择 有返回数据
                                mContext.startActivity(intent);
                            }
                        });
                        holder.llImg.addView(imageView,holder.llImg.getChildCount(),params);
                    }
                }
            }else {
                holder.llImg.setVisibility(View.GONE);
            }
        }
        holder.mIvOwnerFlag.setVisibility(View.GONE);
        holder.mTvLevel.setText(String.valueOf(bean.getFromUserLevel()));
        int radius1 = DensityUtil.dip2px(mContext,5);
        float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
        RoundRectShape roundRectShape1 = new RoundRectShape(outerR1, null, null);
        ShapeDrawable shapeDrawable1 = new ShapeDrawable();
        shapeDrawable1.setShape(roundRectShape1);
        shapeDrawable1.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable1.getPaint().setColor(StringUtils.readColorStr(bean.getFromUserLevelColor(), ContextCompat.getColor(mContext, R.color.main_cyan)));
        holder.mIvLevelColor.setBackgroundDrawable(shapeDrawable1);
        Observable.range(0,3)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer i) {
                        holder.huiZhangTexts[i].setVisibility(View.INVISIBLE);
                        holder.huiZhangRoots[i].setVisibility(View.INVISIBLE);
                    }
                });
        if(bean.getBadgeList().size() > 0){
            int size = 3;
            if(bean.getBadgeList().size() < 3){
                size = bean.getBadgeList().size();
            }
            for (int i = 0;i < size;i++){
                holder.huiZhangTexts[i].setVisibility(View.VISIBLE);
                holder.huiZhangRoots[i].setVisibility(View.VISIBLE);
                BadgeEntity badgeEntity = bean.getBadgeList().get(i);
                TextView tv = holder.huiZhangTexts[i];
                tv.setText(badgeEntity.getTitle());
                tv.setText(badgeEntity.getTitle());
                tv.setBackgroundResource(R.drawable.bg_badge_cover);
                int px = DensityUtil.dip2px(mContext,4);
                tv.setPadding(px,0,px,0);
                int radius2 = DensityUtil.dip2px(mContext,2);
                float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
                RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
                ShapeDrawable shapeDrawable2 = new ShapeDrawable();
                shapeDrawable2.setShape(roundRectShape2);
                shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
                shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(mContext, R.color.main_cyan)));
                holder.huiZhangRoots[i].setBackgroundDrawable(shapeDrawable2);
            }
        }
        if(!mTargetId) {
            holder.mFloor.setVisibility(View.VISIBLE);
            holder.mFloor.setText(mContext.getString(R.string.label_comment_floor,bean.getIdx()));
        }else {
            holder.mFloor.setVisibility(View.GONE);
        }
        holder.mIvCreator.setTag(R.id.id_creator_uuid, bean.getFromUserId());
        holder.mIvOpsOpen.setTag(position);
        if(!bean.isNewDeleteFlag()){
            holder.mTvContent.setTextColor(ContextCompat.getColor(mContext,R.color.gray_595e64));
            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    dismissPopupWindow();
                    //获取相对屏幕的坐标，即以屏幕左上角为原点
                    mCurrentInScreenX = (int)event.getRawX();
                    mCurrentInScreenY = (int)event.getRawY();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //记录Down下时的坐标
                            mDownInScreenX = (int)event.getRawX();
                            mDownInScreenY = (int)event.getRawY();
                            mCurrentClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                            if(Calendar.getInstance().getTimeInMillis() - mCurrentClickTime <= LONG_PRESS_TIME){
                                if(Math.abs(mDownInScreenX - mCurrentInScreenX) <= 10 && Math.abs(mDownInScreenY - mCurrentInScreenY) <= 10 ){
                                    iniPopupWindow(v.getContext(), bean, position);
                                    int[] location = new int[2];
                                    v.getLocationOnScreen(location);
                                    mPop.showAtLocation(v, Gravity.START | Gravity.TOP, mDownInScreenX, mDownInScreenY);
                                }
                            }
                    }
                    return false;
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String content = bean.getContent();
                    ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("回复内容", content);
                    cmb.setPrimaryClip(mClipData);
                    ToastUtils.showShortToast(mContext, mContext.getString(R.string.label_level_copy_success));
                    return false;
                }
            });
        }else {
            holder.mTvContent.setTextColor(ContextCompat.getColor(mContext,R.color.gray_d7d7d7));
            holder.itemView.setOnTouchListener(null);
            holder.itemView.setOnLongClickListener(null);
        }
        holder.mIvOpsOpen.setOnClickListener(mOpListener);
        holder.mIvCreator.setOnClickListener(mAvatarListener);
    }

     private static class CoinHideViewHolder extends RecyclerView.ViewHolder{

         View llTop;
         View llHide;
         CoinHideViewHolder(View itemView) {
            super(itemView);
            llTop = itemView.findViewById(R.id.ll_hide_top);
            llHide = itemView.findViewById(R.id.ll_hide);
        }
    }

    private void setGif(Image image, ImageView gifImageView, LinearLayout.LayoutParams params){
        final int[] wh = BitmapUtils.getDocIconSize(image.getW(), image.getH(), DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,66));
        params.width = wh[0];
        params.height = wh[1];
        Glide.with(mContext)
                .load(ApiService.URL_QINIU + image.getPath())
                .asGif()
                .override(wh[0], wh[1])
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into(gifImageView);
    }

    private void setImage(Image image, final ImageView imageView, LinearLayout.LayoutParams params){
        final int[] wh = BitmapUtils.getDocIconSize(image.getW(), image.getH(), DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,66));
        params.width = wh[0];
        params.height = wh[1];
        Glide.with(mContext)
                .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + image.getPath(), wh[0], wh[1], true, true))
                .override(wh[0], wh[1])
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into(imageView);
    }

   private View.OnClickListener musicCtrl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(NetworkUtils.isNetworkAvailable(mContext)){
                Song musicInfo = mPlayer.getPlayingSong();
                if(mPlayer.isPlaying() && musicInfo.getPath().equals(mMusicInfo.getPath())){
                    mPlayer.pause();
                    mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_play);
                }else if(musicInfo != null && musicInfo.getPath().equals(mMusicInfo.getPath())){
                    mPlayer.play();
                    mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_stop);
                }else {
                    PlayList playList = new PlayList(mMusicInfo);
                    mPlayer.play(playList,0);
                    mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_stop);
                }
            }else {
                ToastUtils.showShortToast(mContext, mContext.getString(R.string.msg_connection));
            }
        }
    };

    /**
     * 时间毫秒转分钟
     */
    private static String getMinute(int time) {
        int h = time / (1000 * 60 * 60);
        String minute;
        int sec = (time % (1000 * 60)) / 1000;
        int min = time % (1000 * 60 * 60) / (1000 * 60);
        String hS = h < 10 ? "0" + h : "" + h;
        String secS = sec < 10 ? "0" + sec : "" + sec;
        String minS = min < 10 ? "0" + min : "" + min;
        if (h == 0) {
            minute = minS + ":" + secS;
        } else {
            minute = hS + ":" + minS + ":" + secS;
        }
        return minute;
    }

    public void createLabel(final String tagName){
        if (!NetworkUtils.checkNetworkAndShowError(mContext)) {
            return;
        }
        if (mDocBean != null) {
            if (DialogUtils.checkLoginAndShowDlg(mContext)) {
                TagSendEntity bean = new TagSendEntity(mDocBean.getId(),tagName);
                ((NewDocDetailActivity)mContext).createLabel(bean);
            }
        }
    }

    public void onCreateLabel(String s,String name){
        DocTagEntity tag = new DocTagEntity();
        tag.setLiked(true);
        tag.setId(s);
        tag.setLikes(1);
        tag.setName(name);
        mTags.add(tag);
        mLabelHolder.mDvLabel.notifyAdapter();
    }

    private void deleteComment(final NewCommentEntity bean,final int position) {
        if (!NetworkUtils.checkNetworkAndShowError(mContext)) {
            return;
        }
        ((NewDocDetailActivity)mContext).deleteComment(bean,position);
    }

    public void deleteCommentSuccess(NewCommentEntity entity,int position){
        entity.setDeleteFlag(true);
        notifyItemChanged(position);
    }

    public void ownerDelSuccess(int position){
        NewCommentEntity bean = (NewCommentEntity)getItem(position);
        bean.setContent("已被楼主删除");
        bean.setNewDeleteFlag(true);
        notifyItemChanged(position);
    }

    private void plusLabel(final int position){
        if (!NetworkUtils.checkNetworkAndShowError(mContext)) {
            return;
        }
        if (mDocBean != null) {
            if (DialogUtils.checkLoginAndShowDlg(mContext)) {
                final DocTagEntity tagBean = mTags.get(position);
                TagLikeEntity bean = new TagLikeEntity(mDocBean.getId(),tagBean.getId());
                ((BaseAppCompatActivity)mContext).createDialog();
                ((NewDocDetailActivity)mContext).likeTag(tagBean.isLiked(),position,bean);
            }
        }
    }

    public void plusSuccess(boolean isLike,int position){
        DocTagEntity tagBean = mTags.get(position);
        mTags.remove(position);
        tagBean.setLiked(isLike);
        if(isLike){
            tagBean.setLikes(tagBean.getLikes() + 1);
            mTags.add(position, tagBean);
        }else {
            tagBean.setLikes(tagBean.getLikes() - 1);
            if (tagBean.getLikes() > 0) {
                mTags.add(position, tagBean);
            }
        }
        mLabelHolder.mDvLabel.notifyAdapter();
    }


    private void addlabel(int position){
        if (position < mTags.size()) {
            plusLabel(position);
        } else {
            ((NewDocDetailActivity)mContext).addDocLabelView();
        }
    }

    private void iniPopupWindow(Context context, NewCommentEntity bean, int position) {
        View layout = LayoutInflater.from(context).inflate(R.layout.popupwindow_comment, null);
        View[] clickView = new View[ids.length];
        for (int i = 0; i < ids.length; i++) {
            clickView[i] = layout.findViewById(ids[i]);
            clickView[i].setOnClickListener(mOpListener);
        }

        if (TextUtils.equals(PreferenceUtils.getUUid(), bean.getFromUserId())) {
            clickView[0].setVisibility(View.VISIBLE);
            clickView[1].setVisibility(View.GONE);
            clickView[3].setVisibility(View.GONE);
        } else if(TextUtils.equals(PreferenceUtils.getUUid(), mDocBean.getUserId())){
            clickView[0].setVisibility(View.GONE);
            clickView[1].setVisibility(View.VISIBLE);
            clickView[3].setVisibility(View.VISIBLE);
        } else{
            clickView[0].setVisibility(View.GONE);
            clickView[1].setVisibility(View.GONE);
            clickView[3].setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < ids.length; i++) {
            clickView[i].setTag(position);
        }
        mPop = new PopupWindow(layout, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mPop.setOutsideTouchable(true);
        mPop.setAnimationStyle(R.style.Popwindow_anim_style);
    }

    public void dismissPopupWindow() {
        if (mPop != null && mPop.isShowing()) {
            mPop.dismiss();
            mPop = null;
        }
    }

    private View.OnClickListener mOpListener = new View.OnClickListener() {

        @SuppressLint("RtlHardcoded")
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.tv_comment_reply) {
                dismissPopupWindow();
                NewCommentEntity bean = (NewCommentEntity) getItem((Integer) v.getTag()); //mComments.get((Integer) v.getTag());
                if (bean != null) {
                    ((NewDocDetailActivity)mContext).reply(bean);
                }
            } else if (id == R.id.tv_comment_report) {
                NewCommentEntity bean = (NewCommentEntity) getItem((Integer) v.getTag());
                Intent intent = new Intent(mContext, JuBaoActivity.class);
                intent.putExtra(JuBaoActivity.EXTRA_NAME, bean.getFromUserName());
                intent.putExtra(JuBaoActivity.EXTRA_CONTENT, bean.getContent());
                intent.putExtra(JuBaoActivity.UUID,bean.getId());
                intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.DOC_COMMENT.toString());
                mContext.startActivity(intent);
                dismissPopupWindow();
            } else if (id == R.id.tv_comment_delete) {
                NewCommentEntity bean = (NewCommentEntity) getItem((Integer) v.getTag());
                deleteComment(bean,(Integer) v.getTag());
                dismissPopupWindow();
            } else if (id == R.id.iv_comment_open) {
                Integer position = (Integer) v.getTag();
                NewCommentEntity bean = (NewCommentEntity) getItem((Integer) v.getTag());
                dismissPopupWindow();
                iniPopupWindow(v.getContext(), bean, position);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                mPop.showAtLocation(v, Gravity.LEFT | Gravity.TOP, location[0], location[1]);
            }else if(id == R.id.tv_comment_copy){
                dismissPopupWindow();
                NewCommentEntity bean = (NewCommentEntity) getItem((Integer) v.getTag());
                String content = bean.getContent();
                ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("回复内容", content);
                cmb.setPrimaryClip(mClipData);
                ToastUtils.showShortToast(mContext, mContext.getString(R.string.label_level_copy_success));
            }else if(id == R.id.ll_own_del_root){
                NewCommentEntity bean = (NewCommentEntity) getItem((Integer) v.getTag());
                Intent intent = new Intent(mContext, JuBaoActivity.class);
                intent.putExtra(JuBaoActivity.EXTRA_NAME, bean.getFromUserName());
                intent.putExtra(JuBaoActivity.EXTRA_CONTENT, bean.getContent());
                intent.putExtra(JuBaoActivity.UUID,bean.getId());
                intent.putExtra(JuBaoActivity.EXTRA_TYPE,2);
                intent.putExtra(JuBaoActivity.EXTRA_POSITION,(Integer) v.getTag());
                intent.putExtra(JuBaoActivity.EXTRA_DOC_ID,mDocBean.getId());
                intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.DOC_COMMENT.toString());
                ((NewDocDetailActivity)mContext).startActivityForResult(intent,6666);
                dismissPopupWindow();
            }
        }
    };

    private View.OnClickListener mAvatarListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String uuid = (String) v.getTag(R.id.id_creator_uuid);
            if (!TextUtils.isEmpty(uuid) && !uuid.equals(PreferenceUtils.getUUid())) {
                Intent i = new Intent(mContext,NewPersonalActivity.class);
                i.putExtra(BaseAppCompatActivity.UUID,uuid);
                mContext.startActivity(i);
            }
        }
    };
}