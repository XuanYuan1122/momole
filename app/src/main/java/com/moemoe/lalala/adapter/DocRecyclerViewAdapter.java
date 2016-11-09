package com.moemoe.lalala.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.Utils;
import com.app.common.Callback;
import com.app.common.util.DensityUtil;
import com.app.common.util.IOUtil;
import com.app.common.util.MD5;
import com.app.image.ImageOptions;
import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.FriendsMainActivity;
import com.moemoe.lalala.ImageBigSelectActivity;
import com.moemoe.lalala.JuBaoActivity;
import com.moemoe.lalala.NewDocDetailActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.WebViewActivity;
import com.moemoe.lalala.data.DocTag;
import com.moemoe.lalala.data.Image;
import com.moemoe.lalala.data.NewCommentBean;
import com.moemoe.lalala.data.NewDocBean;
import com.moemoe.lalala.data.NewDocType;
import com.moemoe.lalala.data.REPORT;
import com.moemoe.lalala.download.DownloadInfo;
import com.moemoe.lalala.download.DownloadService;
import com.moemoe.lalala.download.DownloadViewHolder;
import com.moemoe.lalala.netamusic.data.model.PlayList;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.netamusic.player.IPlayBack;
import com.moemoe.lalala.netamusic.player.Player;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.IConstants;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.utils.filedownloader.DownloadFileInfo;
import com.moemoe.lalala.utils.filedownloader.FileDownloader;
import com.moemoe.lalala.utils.filedownloader.listener.OnFileDownloadStatusListener;
import com.moemoe.lalala.utils.filedownloader.util.MathUtil;
import com.moemoe.lalala.view.DocLabelView;
import com.moemoe.lalala.view.FullyLinearLayoutManager;
import com.moemoe.lalala.view.MyRoundedImageView;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.longimage.LongImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by Haru on 2016/4/14 0014.
 */
public class DocRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IConstants,SeekBar.OnSeekBarChangeListener,IPlayBack.Callback,OnFileDownloadStatusListener {

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

    private LayoutInflater mLayoutInflater;
    private Context mContext;
   // private MusicServiceManager mServiceManager;
    private Player mPlayer;
    private MusicHolder mMusicHolder;
    private LabelHolder mLabelHolder;
    private Song mMusicInfo;
   // private MusicTimer mMusicTimer;
    private int mMusicPosition = -1;
    private int mTagsPosition = -1;
    private NewDocBean mDocBean;
    private ArrayList<NewCommentBean> mComments;
    private ArrayList<DocTag> mTags;
    private OnItemClickListener mOnItemClickListener;
    private PopupWindow mPop;
    private int mCurFirstFloor = 0;
    private String mTargetId;
    private int[] ids = new int[] { R.id.tv_comment_delete, R.id.tv_comment_report , R.id.tv_comment_reply,R.id.tv_comment_copy};
    //private ArrayList<NewDocBean.DocDetail> coinDetail;
//
//    public Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            //refreshSeekProgress(mServiceManager.position(), mServiceManager.duration(),true);
//        }
//    };
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

    public DocRecyclerViewAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
       // mServiceManager = serviceManager;
        mComments = new ArrayList<>();
        mTags = new ArrayList<>();
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
        FileDownloader.registerDownloadStatusListener(this);
    }

    public void releaseAdapter(){
        mPlayer.unregisterCallback(this);
        FileDownloader.unregisterDownloadStatusListener(this);
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
        //notifyItemChanged(mMusicPosition);
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

    @Override
    public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long remainingTime) {
        if(downloadFileInfo.getUrl().equals(mMusicInfo.getPath())){
            long totalSize =  downloadFileInfo.getFileSizeLong();
            long downloaded = downloadFileInfo.getDownloadedSizeLong();
            double downloadSize = downloaded / 1024f / 1024;
            double fileSize = totalSize / 1024f / 1024;
            downloadSize = MathUtil.formatNumber(downloadSize);
            fileSize = MathUtil.formatNumber(fileSize);
            int progress = (int) (downloadSize / fileSize * mMusicInfo.getDuration());
            mMusicHolder.sbMusicTime.setSecondaryProgress(progress);
        }
    }

    @Override
    public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
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
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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
        }else if(holder instanceof CoinViewHolder){
            CoinViewHolder coinViewHolder = (CoinViewHolder) holder;
            createCoin(coinViewHolder);
        }else if(holder instanceof HideTextHolder){
            HideTextHolder textHolder = (HideTextHolder) holder;
            createHideText(textHolder, position);
            HideTextHolder hideTextHolder = (HideTextHolder) holder;
            if (position == mDocBean.details.size() + mDocBean.coinDetails.size() + 1){
                hideTextHolder.mRoot.setBackgroundResource(R.drawable.shape_dash_foot);
            }else {
                hideTextHolder.mRoot.setBackgroundResource(R.drawable.shape_dash_mid);
            }
        }else if(holder instanceof HideImageHolder){
            HideImageHolder imageHolder = (HideImageHolder) holder;
            createHideImage(imageHolder, position,40);
            HideImageHolder hideImageHolder = (HideImageHolder) holder;
            if (position == mDocBean.details.size() + mDocBean.coinDetails.size() + 1){
                hideImageHolder.mRoot.setBackgroundResource(R.drawable.shape_dash_foot);
            }else {
                hideImageHolder.mRoot.setBackgroundResource(R.drawable.shape_dash_mid);
            }
        }else if(holder instanceof CoinHideViewHolder){
            CoinHideViewHolder hideViewHolder = (CoinHideViewHolder) holder;
            if(mDocBean.coinDetails.size() > 0){
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
        }
    }

    public int getMusicPosition(){
        return mMusicPosition;
    }

    public void setMusicInfo(Song musicInfo){
        this.mMusicInfo = musicInfo;
    }

//    public void setMusicTimer(MusicTimer mMusicTimer){
//        this.mMusicTimer = mMusicTimer;
//    }

    @Override
    public int getItemCount() {
        int size = 0;
        if(mDocBean != null){
            size = mDocBean.details.size() + mComments.size() + 3;
            if(mDocBean.coin > 0){
                size += mDocBean.coinDetails.size() + 1;
            }
        }
        return size;
    }

    public Object getItem(int position){
        if(position == 0){
            return "";
        }else if(position < mDocBean.details.size() + 1){
            return  mDocBean.details.get(position - 1).data;
        }else if( position ==  mDocBean.details.size() + 1){
            if(mDocBean.coin > 0){
                return "";
            }else {
                return mDocBean.tags;
            }
        }else if(mDocBean.coinDetails.size() > 0 && position > mDocBean.details.size() + 1 && position < mDocBean.details.size() + mDocBean.coinDetails.size() + 2){
            return mDocBean.coinDetails.get(position - 2 - mDocBean.details.size()).data;
        }else if(mDocBean.coin > 0 && position == mDocBean.details.size() + mDocBean.coinDetails.size() + 2){
            return mDocBean.tags;
        }else if(position == mTagsPosition + 1){
            return "";
        }else if(mDocBean.coin > 0){
            return mComments.get(position - mDocBean.details.size() - 4 - mDocBean.coinDetails.size());
        }else {
            return mComments.get(position - mDocBean.details.size() - 3);
        }
    }

    public void setData(NewDocBean docBean){
        this.mComments.clear();
        this.mDocBean = docBean;
        if(mDocBean != null){
            if(mDocBean.coin > 0){
                mTagsPosition = mDocBean.details.size() + mDocBean.coinDetails.size() + 2;
            }else {
                mTagsPosition = mDocBean.details.size() + 1;
            }
        }
        notifyDataSetChanged();
    }

    public int getTagsPosition(){
        return mTagsPosition;
    }

    public void notifyTags(){
        if(mTagsPosition != -1){
            notifyItemChanged(mTagsPosition);
        }else {
            notifyDataSetChanged();
        }
    }

    public void addComment(int bfSize,int afSize,ArrayList<NewCommentBean> beans,String targetId,boolean isJump,boolean addBefore){

       // int bfSize = mComments.size();
        this.mComments = beans;
        //int afSize = mComments.size();
        int btSize = afSize - bfSize;
        int size = getItemCount() - btSize;
        mTargetId = targetId;
        if(afSize > 0){
            mCurFirstFloor = mComments.get(0).idx;
        }else {
            mCurFirstFloor = 0;
        }
         if(addBefore){
            if(btSize < Otaku.LENGTH){
                mCurFirstFloor = 0;
            }
        }
        notifyItemChanged(mTagsPosition + 1);
        if(isJump){
            if(btSize >= 0){
                //notifyItemRangeRemoved(size - bfSize,bfSize);
                notifyItemRangeChanged(size - bfSize,bfSize);
                notifyItemRangeInserted(size,btSize);
            }else {
                notifyItemRangeRemoved(size + btSize,-btSize);
                notifyItemRangeChanged(size - bfSize,afSize);
            }
        }else {
            if(addBefore){
                notifyItemRangeInserted(size - bfSize,btSize);
            }else {
                notifyItemRangeInserted(size, btSize);
            }
        }
    }

    public void addComment(ArrayList<NewCommentBean> beans){
        int size = getItemCount();
        this.mComments.addAll(beans);
        notifyItemRangeInserted(size, beans.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_CREATOR;
        }else if(position < mDocBean.details.size() + 1){
            String type = mDocBean.details.get(position - 1).type;
            return NewDocType.getType(type);
        }else if( position ==  mDocBean.details.size() + 1){
            if(mDocBean.coin > 0){
                return TYPE_COIN;
            }else {
                //mTagsPosition = position;
                return TYPE_LABEL;
            }
        }else if(mDocBean.coinDetails.size() > 0 && position > mDocBean.details.size() + 1 && position < mDocBean.details.size() + mDocBean.coinDetails.size() + 2){
            String type = mDocBean.coinDetails.get(position - 2 - mDocBean.details.size()).type;
            return NewDocType.getType(type) + 8;
        }else if(mDocBean.coin > 0 && position == mDocBean.details.size() + mDocBean.coinDetails.size() + 2){
           // mTagsPosition = position;
            return TYPE_LABEL;
        }else if(position == mTagsPosition + 1){
            return TYPE_FLOOR;
        } else {
            return TYPE_COMMENT;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
//            mServiceManager.seekTo(progress);
//            refreshSeekProgress(mServiceManager.position(),
//                    mServiceManager.duration(), true);
            updateProgressTextWithProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
//        if(mMusicInfo.playState == MPS_PLAYING){
//            mMusicTimer.stopTimer();
//            mServiceManager.pause();
//            changePlayState();
//        }
        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
//        if(mMusicInfo.playState == MPS_PAUSE){
//            mServiceManager.playByUrl(mMusicInfo.url);
//            mMusicTimer.startTimer();
//            changePlayState();
//        }
        seekTo(getDuration(seekBar.getProgress()));
        if (mPlayer.isPlaying()) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        }
    }

    public static class CreatorHolder extends RecyclerView.ViewHolder{

        private ImageView mIvCreator;
        private View mIvClubOwnerFlag;
        private TextView mTvCreator;
        public View ivLevelColor;
        public TextView tvLevel;
        public View rlLevelPack;
        private TextView mTvTime;

        public CreatorHolder(View itemView) {
            super(itemView);
            mIvCreator = (ImageView) itemView.findViewById(R.id.iv_post_creator);
            mTvCreator = (TextView) itemView.findViewById(R.id.tv_post_creator_name);
            mIvClubOwnerFlag = itemView.findViewById(R.id.iv_post_owner_flag);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_post_update_time);
            ivLevelColor = itemView.findViewById(R.id.iv_level_bg);
            tvLevel = (TextView)itemView.findViewById(R.id.tv_level);
            rlLevelPack = itemView.findViewById(R.id.rl_level_pack);
        }
    }

    private void createCreator(CreatorHolder holder){
        Picasso.with(mContext)
                .load(StringUtils.getUrl(mContext, mDocBean.userIcon.path,DensityUtil.dip2px(44), DensityUtil.dip2px(44),false,false))
                .resize(DensityUtil.dip2px(44), DensityUtil.dip2px(44))
                .placeholder(R.drawable.ic_default_avatar_m)
                .error(R.drawable.ic_default_avatar_m)
                .tag(NewDocDetailActivity.TAG)
                .config(Bitmap.Config.RGB_565)
                .into(holder.mIvCreator);
        holder.mIvClubOwnerFlag.setVisibility(View.GONE);
        holder.mTvCreator.setText(mDocBean.userName);
        holder.rlLevelPack.setVisibility(View.VISIBLE);
        holder.ivLevelColor.setBackgroundColor(mDocBean.userLevelColor);
        holder.tvLevel.setText(mDocBean.userLevel + "");
        holder.tvLevel.setTextColor(mDocBean.userLevelColor);
        holder.mTvTime.setText(StringUtils.timeFormate(mDocBean.createTime));
        holder.mIvCreator.setTag(R.id.id_creator_uuid, mDocBean.userId);
        holder.mIvCreator.setOnClickListener(mAvatarListener);
    }

    public static class TextHolder extends RecyclerView.ViewHolder{

        private TextView mTvText;

        public TextHolder(View itemView) {
            super(itemView);
            mTvText = (TextView) itemView.findViewById(R.id.tv_doc_content);
        }
    }

    private void createText(final TextHolder holder,int position){
        holder.mTvText.setText(StringUtils.getUrlClickableText(mContext, ((NewDocBean.DocText) getItem(position)).content));
        holder.mTvText.setMovementMethod(LinkMovementMethod.getInstance());
        holder.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                int pos = holder.getLayoutPosition();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });
    }

    public static class HideTextHolder  extends RecyclerView.ViewHolder{
        private LinearLayout mRoot;
        private TextView mTvText;

        public HideTextHolder(View itemView) {
            super(itemView);
            mRoot = (LinearLayout) itemView.findViewById(R.id.ll_root);
            mTvText = (TextView) itemView.findViewById(R.id.tv_doc_content);
        }
    }

    private void createHideText(final HideTextHolder holder,int position){
        holder.mTvText.setText(StringUtils.getUrlClickableText(mContext, ((NewDocBean.DocText) getItem(position)).content));
        holder.mTvText.setMovementMethod(LinkMovementMethod.getInstance());
        holder.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                int pos = holder.getLayoutPosition();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });
    }

    public static class ImageHolder extends RecyclerView.ViewHolder{
        private ImageView mIvImage;
        private LongImageView mIvLongImage;

        public ImageHolder(View itemView) {
            super(itemView);
            mIvImage = (ImageView) itemView.findViewById(R.id.iv_doc_image);
            mIvLongImage = (LongImageView) itemView.findViewById(R.id.iv_doc_long_image);
        }
    }

    private void createImage(final ImageHolder holder, final int position, int size){
        final Image image  = ((NewDocBean.DocImage) getItem(position)).image;
        final int[] wh = BitmapUtils.getDocIconSize(image.w, image.h, DensityUtil.getScreenWidth() - DensityUtil.dip2px(size));
        if(wh[1] > 2048){
            holder.mIvImage.setVisibility(View.GONE);
            holder.mIvLongImage.setVisibility(View.VISIBLE);
            //holder.mIvLongImage.setDefaulImage(mContext.getResources().getDrawable(R.drawable.ic_default_doc_l));
            String temp = MD5.md5(image.path) + ".jpg";
            final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
            if(longImage.exists()){
                holder.mIvLongImage.setImage(longImage.getAbsolutePath());
            }else {
                final DownloadInfo info = new DownloadInfo();
                info.setUrl(image.real_path);
                info.setFileSavePath(longImage.getAbsolutePath());
                info.setAutoRename(false);
                info.setAutoResume(true);
                com.moemoe.lalala.download.DownloadManager downloadManager = DownloadService.getDownloadManager();
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
                        BitmapUtils.galleryAddPic(mContext, result.getAbsolutePath());
                        holder.mIvLongImage.setImage(result.getAbsolutePath());
                        notifyItemChanged(position);
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
        }else {
            holder.mIvImage.setVisibility(View.VISIBLE);
            holder.mIvLongImage.setVisibility(View.GONE);
            if(FileUtil.isGif(image.path)){
                ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                holder.mIvImage.setLayoutParams(layoutParams);
                holder.mIvImage.requestLayout();
                Utils.image().bind(holder.mIvImage,image.real_path,new ImageOptions.Builder()
                            .setIgnoreGif(false)
                            .setSize(wh[0], wh[1])
                            .setFailureDrawableId(R.drawable.ic_default_doc_l)
                            .setLoadingDrawableId(R.drawable.ic_default_doc_l)
                            .build());
            }else {
                ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                holder.mIvImage.setLayoutParams(layoutParams);
                holder.mIvImage.requestLayout();
                Picasso.with(mContext)
                        .load(StringUtils.getUrl(mContext, image.path, wh[0], wh[1], true, true))
                        .resize(wh[0], wh[1])
                        .placeholder(R.drawable.ic_default_doc_l)
                        .error(R.drawable.ic_default_doc_l)
                        .tag(NewDocDetailActivity.TAG)
                        .config(Bitmap.Config.RGB_565)
                        .into(holder.mIvImage);
            }
        }
        holder.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                int pos = holder.getLayoutPosition();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });
    }

    public static class HideImageHolder extends RecyclerView.ViewHolder{
        private LinearLayout mRoot;
        private ImageView mIvImage;
        private LongImageView mIvLongImage;

        public HideImageHolder(View itemView) {
            super(itemView);
            mRoot = (LinearLayout) itemView.findViewById(R.id.ll_root);
            mIvImage = (ImageView) itemView.findViewById(R.id.iv_doc_image);
            mIvLongImage = (LongImageView) itemView.findViewById(R.id.iv_doc_long_image);
        }
    }

    private void createHideImage(final HideImageHolder holder, final int position, int size){
        Image image  = ((NewDocBean.DocImage) getItem(position)).image;
        final int[] wh = BitmapUtils.getDocIconSize(image.w, image.h, DensityUtil.getScreenWidth() - DensityUtil.dip2px(size));
        if(wh[1] > 4000){
            holder.mIvImage.setVisibility(View.GONE);
            holder.mIvLongImage.setVisibility(View.VISIBLE);
            String temp = MD5.md5(image.path) + ".jpg";
            final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
            if(longImage.exists()){
                holder.mIvLongImage.setImage(longImage.getAbsolutePath());
            }else {
                final DownloadInfo info = new DownloadInfo();
                info.setUrl(image.real_path);
                info.setFileSavePath(longImage.getAbsolutePath());
                info.setAutoRename(false);
                info.setAutoResume(true);
                com.moemoe.lalala.download.DownloadManager downloadManager = DownloadService.getDownloadManager();
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
                        BitmapUtils.galleryAddPic(mContext, result.getAbsolutePath());
                        holder.mIvLongImage.setImage(result.getAbsolutePath());
                        notifyItemChanged(position);
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
        }else {
            if(FileUtil.isGif(image.path)){
                ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                holder.mIvImage.setLayoutParams(layoutParams);
                holder.mIvImage.requestLayout();
//                Glide.with(mContext)
//                        .load(image.real_path)
//                        .asGif()
//                        .override(wh[0], wh[1])
//                        .placeholder(R.drawable.ic_default_doc_l)
//                        .error(R.drawable.ic_default_doc_l)
//                        .dontAnimate()
//                        .into(holder.mIvImage);
                Utils.image().bind(holder.mIvImage,image.real_path,new ImageOptions.Builder()
                        .setIgnoreGif(false)
                        .setSize(wh[0], wh[1])
                        .setFailureDrawableId(R.drawable.ic_default_doc_l)
                        .setLoadingDrawableId(R.drawable.ic_default_doc_l)
                        .build());
            }else {
                ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                holder.mIvImage.setLayoutParams(layoutParams);
                holder.mIvImage.requestLayout();
                Picasso.with(mContext)
                        .load(StringUtils.getUrl(mContext, image.path, wh[0], wh[1], true, true))
                        .resize(wh[0], wh[1])
                        .placeholder(R.drawable.ic_default_doc_l)
                        .error(R.drawable.ic_default_doc_l)
                        .tag(NewDocDetailActivity.TAG)
                        .config(Bitmap.Config.RGB_565)
                        .into(holder.mIvImage);
            }
        }
        holder.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                int pos = holder.getLayoutPosition();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });
    }

    public static class MusicHolder extends RecyclerView.ViewHolder{

        private ImageView ivMusicCtrl;
        private TextView tvMusicTitle;
        private TextView tvMusicTime;
        private SeekBar sbMusicTime;
        private View musicRoot;

        public MusicHolder(View itemView) {
            super(itemView);
            ivMusicCtrl = (ImageView) itemView.findViewById(R.id.iv_music_ctrl);
            tvMusicTitle = (TextView) itemView.findViewById(R.id.tv_music_name);
            tvMusicTime = (TextView) itemView.findViewById(R.id.tv_music_seek);
            sbMusicTime = (SeekBar) itemView.findViewById(R.id.sb_music);
            musicRoot = itemView.findViewById(R.id.rl_music_root);

        }
    }

    private void createMusic(int position){
        Object o = getItem(position);
        if(o instanceof NewDocBean.DocMusic){
            NewDocBean.DocMusic music = (NewDocBean.DocMusic) o;
            mMusicHolder.tvMusicTitle.setTextColor(mContext.getResources().getColor(R.color.white));
            mMusicHolder.tvMusicTime.setTextColor(mContext.getResources().getColor(R.color.white));
            mMusicHolder.musicRoot.setBackgroundResource(R.drawable.bg_rect_gray_doc_music);
            mMusicHolder.tvMusicTitle.setText(music.name);
            mMusicHolder.ivMusicCtrl.setOnClickListener(musicCtrl);
            mMusicHolder.sbMusicTime.setOnSeekBarChangeListener(this);
            if(mPlayer.isPlaying()){
                Song musicInfo = mPlayer.getPlayingSong();
                if(musicInfo.getPath().equals(music.url)){
                    musicInfo.setDuration(music.timestamp);
                    mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_stop);
                }else {
                    mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_play);
                }
            }else {
                mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_play);
            }
            Song musicInfo = new Song();
            musicInfo.setCoverPath(music.cover.path);
            musicInfo.setPath(music.url);
            musicInfo.setDisplayName(music.name);
            musicInfo.setDuration(music.timestamp);
            setMusicInfo(musicInfo);
            mMusicHolder.sbMusicTime.setMax(music.timestamp);
            Song curMusic = mPlayer.getPlayingSong();
            mMusicHolder.tvMusicTime.setText(getMinute(0) + "/" + getMinute(music.timestamp));
            if(curMusic != null){
                if(curMusic.getPath().equals(musicInfo.getPath())){
                    onSongUpdate(musicInfo);
                }
            }
            //changePlayState();
            mMusicPosition = position;
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
        mMusicHolder.tvMusicTime.setText(getMinute(targetDuration) + "/" + getMinute(mPlayer.getPlayingSong().getDuration()));
    }

    private int getCurrentSongDuration() {
        Song currentSong = mPlayer.getPlayingSong();
        int duration = 0;
        if (currentSong != null) {
            duration = currentSong.getDuration();
        }
        return duration;
    }

    public static class LinkHolder extends RecyclerView.ViewHolder{

        private ImageView ivMusicCtrl;
        private TextView tvMusicTitle;
        private TextView tvMusicTime;
        private SeekBar sbMusicTime;
        private View musicRoot;

        public LinkHolder(View itemView) {
            super(itemView);
            ivMusicCtrl = (ImageView) itemView.findViewById(R.id.iv_music_ctrl);
            tvMusicTitle = (TextView) itemView.findViewById(R.id.tv_music_name);
            tvMusicTime = (TextView) itemView.findViewById(R.id.tv_music_seek);
            sbMusicTime = (SeekBar) itemView.findViewById(R.id.sb_music);
            musicRoot = itemView.findViewById(R.id.rl_music_root);
        }
    }

    private void createLink(final LinkHolder holder,int position){
        NewDocBean.DocLink bean = (NewDocBean.DocLink) getItem(position);
        holder.tvMusicTitle.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.tvMusicTime.setTextColor(mContext.getResources().getColor(R.color.gray_txt_main));
        holder.musicRoot.setBackgroundResource(R.drawable.bg_rect_gray_doc_link);
        holder.tvMusicTitle.setText(bean.name);
        holder.tvMusicTime.setText(bean.url);
        holder.sbMusicTime.setVisibility(View.GONE);
        Picasso.with(mContext)
                .load(StringUtils.getUrl(mContext, bean.icon.path, DensityUtil.dip2px(45), DensityUtil.dip2px(45), false, true))
                .resize(DensityUtil.dip2px(45), DensityUtil.dip2px(45))
                .placeholder(R.drawable.ic_default_avatar_m)
                .error(R.drawable.ic_default_avatar_m)
                .tag(NewDocDetailActivity.TAG)
                .config(Bitmap.Config.RGB_565)
                .into(holder.ivMusicCtrl);
        holder.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                int pos = holder.getLayoutPosition();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });
    }

    public static class ChapterHolder extends RecyclerView.ViewHolder{

        private DocLabelView mDvLabel;

        public ChapterHolder(View itemView) {
            super(itemView);
            mDvLabel = (DocLabelView) itemView.findViewById(R.id.dv_doc_label_root);
        }
    }

    private void createChapter(ChapterHolder holder,int position){
        final ArrayList<NewDocBean.DocGroupLink.DocGroupLinkDetail> details = ((NewDocBean.DocGroupLink)getItem(position)).details;
        holder.mDvLabel.setChapter(details, mContext);
        holder.mDvLabel.setItemClickListener(new DocLabelView.LabelItemClickListener() {
            @Override
            public void itemClick(int position) {
                NewDocBean.DocGroupLink.DocGroupLinkDetail detail = details.get(position);
                WebViewActivity.startActivity(mContext, detail.url);
            }
        });
    }

    public static class LabelHolder extends RecyclerView.ViewHolder{

        private DocLabelView mDvLabel;

        public LabelHolder(View itemView) {
            super(itemView);
            mDvLabel = (DocLabelView) itemView.findViewById(R.id.dv_doc_label_root);
        }
    }

    private void createLabel(){
        mTags.clear();
        mTags.addAll(mDocBean.tags);
        mLabelHolder.mDvLabel.setContentAndNumList(true, mTags);
       // mLabelHolder.mTvCommentNum.setText(mContext.getResources().getString(R.string.label_comment) + " " + mDocBean.comments);
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
        Otaku.getDocV2().giveCoinToDoc(PreferenceManager.getInstance(mContext).getToken(),mDocBean.id,1).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                if(s.equals(mDocBean.id)){
                    mDocBean.coinPays++;
                    notifyItemChanged(mTagsPosition + 1);
                    ToastUtil.showCenterToast(mContext,R.string.label_give_coin_success);
                }else {
                    ToastUtil.showCenterToast(mContext,R.string.label_give_coin_error);
                }
            }

            @Override
            public void failure(String e) {
                if(!TextUtils.isEmpty(e)){
                    try {
                        JSONObject json = new JSONObject(e);
                        String data = json.optString("data");
                        if(data.equals("COIN_LITTLE")){
                            ToastUtil.showCenterToast(mContext,R.string.label_have_no_coin);
                        }else {
                            ToastUtil.showCenterToast(mContext,R.string.label_give_coin_error);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        ToastUtil.showCenterToast(mContext,R.string.label_give_coin_error);
                    }
                }else {
                    ToastUtil.showCenterToast(mContext,R.string.label_give_coin_error);
                }
            }
        }));
    }

    private void getCoinContent(){
        if (!NetworkUtils.checkNetworkAndShowError(mContext)){
            return;
        }
        Otaku.getDocV2().requestDocHidePath(PreferenceManager.getInstance(mContext).getToken(),mDocBean.id).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                if(s.equals(mDocBean.id)){
                    ((NewDocDetailActivity)mContext).autoSendComment();
                    ToastUtil.showCenterToast(mContext,R.string.label_use_coin_success);
                }else {
                    ToastUtil.showCenterToast(mContext,R.string.label_use_coin_error);
                }

            }

            @Override
            public void failure(String e) {
                if(!TextUtils.isEmpty(e)){
                    try {
                        JSONObject json = new JSONObject(e);
                        String data = json.optString("data");
                        if(data.equals("COIN_LITTLE")){
                            ToastUtil.showCenterToast(mContext,R.string.label_have_no_coin);
                        }else {
                            ToastUtil.showCenterToast(mContext,R.string.label_use_coin_error);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        ToastUtil.showCenterToast(mContext,R.string.label_use_coin_error);
                    }
                }else {
                    ToastUtil.showCenterToast(mContext,R.string.label_use_coin_error);
                }
            }
        }));
    }

    public static class FloorHolder extends RecyclerView.ViewHolder{

        private View mRlCoin;
        private ImageView mIvGiveCoin;
        private TextView mTvCoinNum;
        private TextView mGoTop;
        private TextView mLoadBefore;
        private ImageView mIvLeft;
        private View lLFloorRoot;


        public FloorHolder(View itemView) {
            super(itemView);
            mRlCoin = itemView.findViewById(R.id.rl_coin_root);
            mIvGiveCoin = (ImageView) itemView.findViewById(R.id.iv_give_coin);
            mTvCoinNum = (TextView) itemView.findViewById(R.id.tv_got_coin);
            mIvLeft = (ImageView) itemView.findViewById(R.id.iv_left);
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
                if (mCurFirstFloor > Otaku.LENGTH){
                    floor = mCurFirstFloor - Otaku.LENGTH ;
                    length = Otaku.LENGTH;
                }
                ((NewDocDetailActivity)mContext).createDialog();
                ((NewDocDetailActivity)mContext).requestCommentsByFloor(floor,mTargetId,false,true,length,false);
            }
        });
        holder.mTvCoinNum.setText(mContext.getString(R.string.label_got_coin,mDocBean.coinPays));
        if(mDocBean.userId.equals(PreferenceManager.getInstance(mContext).getUUid())){
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


    public static class CommentHolder extends RecyclerView.ViewHolder{

        public MyRoundedImageView mIvCreator;
        public TextView mTvCreatorName;
        public TextView mTvTime;
        public TextView mTvContent;
        public View mIvOwnerFlag;
        public ImageView mIvOpsOpen;
        public View mIvLevelColor;
        public TextView mTvLevel;
        public TextView mFloor;
        public LinearLayout llImg;

        public CommentHolder(View itemView) {
            super(itemView);
            mIvCreator = (MyRoundedImageView) itemView.findViewById(R.id.iv_comment_creator);
            mTvCreatorName = (TextView) itemView.findViewById(R.id.tv_comment_creator_name);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_comment_time);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_comment);
            mIvOwnerFlag = itemView.findViewById(R.id.iv_club_owner_flag);
            mIvOpsOpen = (ImageView) itemView.findViewById(R.id.iv_comment_open);
            mIvLevelColor = itemView.findViewById(R.id.iv_level_bg);
            mTvLevel = (TextView)itemView.findViewById(R.id.tv_level);
            mFloor = (TextView)itemView.findViewById(R.id.tv_floor);
            llImg = (LinearLayout) itemView.findViewById(R.id.ll_comment_img);
            llImg.setVisibility(View.GONE);
        }
    }


    private void createComment(CommentHolder holder, final int position){
        final NewCommentBean bean = (NewCommentBean)getItem(position);
        Picasso.with(mContext)
                .load(StringUtils.getUrl(mContext, bean.fromUserIcon.path, DensityUtil.dip2px(35), DensityUtil.dip2px(35), false, false))
                .resize(DensityUtil.dip2px(35), DensityUtil.dip2px(35))
                .placeholder(R.drawable.ic_default_avatar_m)
                .error(R.drawable.ic_default_avatar_m)
                .tag(NewDocDetailActivity.TAG)
                .config(Bitmap.Config.RGB_565)
                .into(holder.mIvCreator);
        holder.mTvCreatorName.setText(bean.fromUserName);
        holder.mTvTime.setText(StringUtils.timeFormate(bean.createTime));
        if(bean.deleteFlag){
            holder.mTvContent.setText(mContext.getString(R.string.label_comment_already));
        }else {
            String comm = "";
            if (!TextUtils.isEmpty(bean.toUserName) ) {
                comm = "回复 " + (TextUtils.isEmpty(bean.toUserName) ? "" :bean.toUserName) + ": "
                        + bean.content;
            } else {
                comm = bean.content;
            }
            holder.mTvContent.setText(StringUtils.getUrlClickableText(mContext, comm));
            holder.mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
        }
        holder.mIvOwnerFlag.setVisibility(View.GONE);
        holder.mIvLevelColor.setBackgroundColor(bean.fromUserLevelColor);
        holder.mTvLevel.setText(bean.fromUserLevel + "");
        if(TextUtils.isEmpty(mTargetId)) {
            holder.mFloor.setVisibility(View.VISIBLE);
            holder.mFloor.setText(mContext.getString(R.string.label_comment_floor,bean.idx));
        }else {
            holder.mFloor.setVisibility(View.GONE);
        }
        holder.mTvLevel.setTextColor(bean.fromUserLevelColor);
        holder.mIvCreator.setTag(R.id.id_creator_uuid, bean.fromUserId);
        holder.mIvOpsOpen.setTag(position);
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
                                int x = (int) event.getX();
                                int y = (int) event.getY();
                                v.getLocationOnScreen(location);
                                mPop.showAtLocation(v, Gravity.LEFT | Gravity.TOP, mDownInScreenX, mDownInScreenY);
                            }
                        }
                }
                return false;
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String content = bean.content;
                ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("回复内容", content);
                cmb.setPrimaryClip(mClipData);
                ToastUtil.showToast(mContext, R.string.label_level_copy_success);
                return false;
            }
        });
        holder.mIvOpsOpen.setOnClickListener(mOpListener);
        holder.mIvCreator.setOnClickListener(mAvatarListener);
        if(bean.images.size() > 0){
            holder.llImg.setVisibility(View.VISIBLE);
            holder.llImg.removeAllViews();
            for (int i = 0;i < bean.images.size();i++){
                final int pos = i;
                Image image = bean.images.get(i);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.topMargin = DensityUtil.dip2px(5);
                if(FileUtil.isGif(image.path)){
                    ImageView imageView = new ImageView(mContext);
                    setGif(image, imageView,null,null,params);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ImageBigSelectActivity.class);
                            intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, bean.images);
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
                            intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, bean.images);
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

    public static class CoinHideViewHolder extends RecyclerView.ViewHolder{

        public View llTop;
        public View llHide;
        public CoinHideViewHolder(View itemView) {
            super(itemView);
            llTop = itemView.findViewById(R.id.ll_hide_top);
            llHide = itemView.findViewById(R.id.ll_hide);
        }
    }

    public static class CoinViewHolder extends RecyclerView.ViewHolder{

        public RecyclerView rvCoin;
        public DocCoinAdapter adapter;

        public CoinViewHolder(View itemView) {
            super(itemView);
            rvCoin = (RecyclerView) itemView.findViewById(R.id.rv_hide);
        }
    }

    public void createCoin(CoinViewHolder holder){
        holder.adapter = new DocCoinAdapter(mContext,mDocBean.coinDetails);
        FullyLinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(mContext);
        holder.rvCoin.setLayoutManager(linearLayoutManager);
        holder.rvCoin.setAdapter(holder.adapter);
    }

    private void setGif(Image image,ImageView gifImageView,ProgressBar progressBar,TextView textView,LinearLayout.LayoutParams params){
        final int[] wh = BitmapUtils.getDocIconSize(image.w, image.h, DensityUtil.getScreenWidth() - DensityUtil.dip2px(66));
        params.width = wh[0];
        params.height = wh[1];
//        Glide.with(mContext)
//                .load(image.real_path)
//                .asGif()
//                .override(wh[0], wh[1])
//                .placeholder(R.drawable.ic_default_doc_l)
//                .error(R.drawable.ic_default_doc_l)
//                .dontAnimate()
//                .into(gifImageView);
        Utils.image().bind(gifImageView,image.real_path,new ImageOptions.Builder()
                .setIgnoreGif(false)
                .setSize(wh[0], wh[1])
                .setFailureDrawableId(R.drawable.ic_default_doc_l)
                .setLoadingDrawableId(R.drawable.ic_default_doc_l)
                .build());
    }

    private void setImage(Image image, final ImageView imageView,LinearLayout.LayoutParams params){
        final int[] wh = BitmapUtils.getDocIconSize(image.w, image.h, DensityUtil.getScreenWidth() - DensityUtil.dip2px(66));
        params.width = wh[0];
        params.height = wh[1];
        Picasso.with(mContext)
                .load(StringUtils.getUrl(mContext, image.path, wh[0], wh[1], true, true))
                .resize(wh[0], wh[1])
                .placeholder(R.drawable.ic_default_doc_l)
                .error(R.drawable.ic_default_doc_l)
                .tag(NewDocDetailActivity.TAG)
                .config(Bitmap.Config.RGB_565)
                .into(imageView);
    }

    View.OnClickListener musicCtrl = new View.OnClickListener() {
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
                    //onSongUpdate(mMusicInfo);
                   // mHandler.removeCallbacks(mProgressCallback);
                   // mHandler.post(mProgressCallback);
                }
            }else {
                ToastUtil.showCenterToast(mContext, R.string.msg_server_connection);
            }
        }
    };

//    private void changePlayState(){
//        if(mMusicInfo.playState == MPS_PLAYING || mMusicInfo.playState == MPS_PREPARE){
//            mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_stop);
//        }else {
//            mMusicHolder.ivMusicCtrl.setImageResource(R.drawable.btn_doc_video_play);
//        }
//    }

//    public void refreshSeekProgress(int curTime,int totalTime,boolean hasBuffer) {
//        mMusicHolder.sbMusicTime.setMax(totalTime);
//        mMusicHolder.tvMusicTime.setText(getMinute(curTime) + "/" + getMinute(totalTime));
//        mMusicHolder.sbMusicTime.setProgress(curTime);
//        if(hasBuffer){
//            mMusicHolder.sbMusicTime.setSecondaryProgress(mServiceManager.getBufferProgress());
//        }else {
//            mMusicHolder.sbMusicTime.setSecondaryProgress(0);
//        }
//    }

    /**
     * 时间毫秒转分钟
     */
    public static String getMinute(int time) {
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
                ((BaseActivity)mContext).createDialog();
                Otaku.getDocV2().createNewTag(PreferenceManager.getInstance(mContext).getToken(), mDocBean.id, tagName).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                    @Override
                    public void success(String token, String s) {
                        ((BaseActivity) mContext).finalizeDialog();
                        DocTag tag = new DocTag();
                        tag.liked = true;
                        tag.id = s;
                        tag.likes = 1;
                        tag.name = tagName;
                        mTags.add(tag);
                        mLabelHolder.mDvLabel.notifyAdapter();
                    }

                    @Override
                    public void failure(String e) {
                        ((BaseActivity) mContext).finalizeDialog();
                    }
                }));
            }
        }
    }

    private void deleteComment(final NewCommentBean bean,final int position) {
        if (!NetworkUtils.checkNetworkAndShowError(mContext)) {
            return;
        }
        //System.out.println("token:"+PreferenceManager.getInstance(mContext).getToken());
       // System.out.println("bean.id:"+ bean.id);

        Otaku.getDocV2().deleteNewComment(PreferenceManager.getInstance(mContext).getToken(), bean.id).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                ToastUtil.showToast(mContext, R.string.msg_comment_delete_success);
                //mComments.remove(bean);
                bean.deleteFlag = true;
                //((NewDocDetailActivity) mContext).removeComment(bean);
                notifyItemChanged(position);
            }

            @Override
            public void failure(String e) {
                ToastUtil.showToast(mContext, R.string.msg_comment_delete_fail);
            }
        }));
    }

    private void plusLabel(final int position){
        if (!NetworkUtils.checkNetworkAndShowError(mContext)) {
            return;
        }
        if (mDocBean != null) {
            if (DialogUtils.checkLoginAndShowDlg(mContext)) {
                final DocTag tagBean = mTags.get(position);
                if(tagBean.liked){
                    ((BaseActivity)mContext).createDialog();
                    Otaku.getDocV2().dislikeNewTag(PreferenceManager.getInstance(mContext).getToken(), tagBean.id, mDocBean.id).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                        @Override
                        public void success(String token, String s) {
                            ((BaseActivity)mContext).finalizeDialog();
                            mTags.remove(position);
                            tagBean.liked = false;
                            tagBean.likes--;
                            if (tagBean.likes > 0) {
                                mTags.add(position, tagBean);
                            }
                            mLabelHolder.mDvLabel.notifyAdapter();
                        }

                        @Override
                        public void failure(String e) {
                            ((BaseActivity)mContext).finalizeDialog();
                        }
                    }));
                }else {
                    ((BaseActivity)mContext).createDialog();
                    Otaku.getDocV2().likeNewTag(PreferenceManager.getInstance(mContext).getToken(), tagBean.id, mDocBean.id).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                        @Override
                        public void success(String token, String s) {
                            ((BaseActivity)mContext).finalizeDialog();
                            mTags.remove(position);
                            tagBean.liked = true;
                            tagBean.likes++;
                            mTags.add(position, tagBean);
                            mLabelHolder.mDvLabel.notifyAdapter();
                        }

                        @Override
                        public void failure(String e) {
                            ((BaseActivity)mContext).finalizeDialog();
                        }
                    }));
                }
            }
        }
    }


    private void addlabel(int position){
        if (position < mTags.size()) {
            plusLabel(position);
        } else {
            ((NewDocDetailActivity)mContext).addDocLabelView();
        }
    }

    private void iniPopupWindow(Context context, NewCommentBean bean, int position) {
        View layout = LayoutInflater.from(context).inflate(R.layout.popupwindow_comment, null);
        View[] clickView = new View[ids.length];
        for (int i = 0; i < ids.length; i++) {
            clickView[i] = layout.findViewById(ids[i]);
            clickView[i].setOnClickListener(mOpListener);
        }

        if (TextUtils.equals(PreferenceManager.getInstance(mContext).getUUid(), bean.fromUserId)) {
            clickView[0].setVisibility(View.VISIBLE);
            clickView[2].setVisibility(View.GONE);
        } else {
            clickView[0].setVisibility(View.GONE);
            clickView[2].setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < ids.length; i++) {
            clickView[i].setTag(position);
        }
        mPop = new PopupWindow(layout, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
       // mPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                NewCommentBean bean = (NewCommentBean) getItem((Integer) v.getTag()); //mComments.get((Integer) v.getTag());
                if (bean != null) {
                    ((NewDocDetailActivity)mContext).reply(bean);
                }
            } else if (id == R.id.tv_comment_report) {
                NewCommentBean bean = (NewCommentBean) getItem((Integer) v.getTag());
                Intent intent = new Intent(mContext, JuBaoActivity.class);
                intent.putExtra(JuBaoActivity.EXTRA_NAME, bean.fromUserName);
                intent.putExtra(JuBaoActivity.EXTRA_CONTENT, bean.content);
                intent.putExtra(JuBaoActivity.EXTRA_KEY_UUID,bean.id);
                intent.putExtra(JuBaoActivity.EXTRA_TARGET, REPORT.DOC_COMMENT.toString());
                mContext.startActivity(intent);
                dismissPopupWindow();
            } else if (id == R.id.tv_comment_delete) {
                NewCommentBean bean = (NewCommentBean) getItem((Integer) v.getTag());
                deleteComment(bean,(Integer) v.getTag());
                dismissPopupWindow();
            } else if (id == R.id.iv_comment_open) {
                Integer position = (Integer) v.getTag();
                NewCommentBean bean = (NewCommentBean) getItem((Integer) v.getTag());
                dismissPopupWindow();
                iniPopupWindow(v.getContext(), bean, position);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                mPop.showAtLocation(v, Gravity.LEFT | Gravity.TOP, location[0], location[1]);
            }else if(id == R.id.tv_comment_copy){
                dismissPopupWindow();
                NewCommentBean bean = (NewCommentBean) getItem((Integer) v.getTag());
                String content = bean.content;
                ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("回复内容", content);
                cmb.setPrimaryClip(mClipData);
                ToastUtil.showToast(mContext, R.string.label_level_copy_success);
            }
        }
    };

    private View.OnClickListener mAvatarListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String uuid = (String) v.getTag(R.id.id_creator_uuid);
            if (!TextUtils.isEmpty(uuid)) {
                Intent i = new Intent(mContext,FriendsMainActivity.class);
                i.putExtra(BaseActivity.EXTRA_KEY_UUID,uuid);
                mContext.startActivity(i);
            }
        }
    };
}