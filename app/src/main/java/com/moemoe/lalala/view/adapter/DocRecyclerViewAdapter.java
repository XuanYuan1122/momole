package com.moemoe.lalala.view.adapter;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.CommentV2SecEntity;
import com.moemoe.lalala.model.entity.DocDetailEntity;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.NewDocType;
import com.moemoe.lalala.model.entity.ShareArticleEntity;
import com.moemoe.lalala.model.entity.TabEntity;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.model.entity.tag.UserUrlSpan;
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
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.LevelSpan;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.CommentListActivity;
import com.moemoe.lalala.view.activity.CommentSecListActivity;
import com.moemoe.lalala.view.activity.CreateCommentActivity;
import com.moemoe.lalala.view.activity.CreateForwardActivity;
import com.moemoe.lalala.view.activity.DynamicActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.activity.JuBaoActivity;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;
import com.moemoe.lalala.view.activity.PersonalV2Activity;
import com.moemoe.lalala.view.activity.WebViewActivity;
import com.moemoe.lalala.view.widget.longimage.LongImageView;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.view.DocLabelView;
import com.moemoe.lalala.view.widget.view.NewDocLabelAdapter;

import java.io.File;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 *
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
    private static final int TYPE_TITLE = 13;

    private static final long UPDATE_PROGRESS_INTERVAL = 1000;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private Player mPlayer;
    private MusicHolder mMusicHolder;
    private LabelHolder mLabelHolder;
    private Song mMusicInfo;
    private int mTagsPosition = -1;
    private DocDetailEntity mDocBean;
    private ArrayList<CommentV2Entity> mComments;
    private ArrayList<DocTagEntity> mTags;
    private OnItemClickListener onItemClickListener;
    private BottomMenuFragment fragment;
    private boolean sortTime;
    private int commentType = 1;//0 转发 1 评论
    private Handler mHandler = new Handler();
    private ArrayList<CommentV2Entity> mPreComments;
    private boolean showFavorite;
    private int mPrePosition;
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

    public boolean isShowFavorite() {
        return showFavorite;
    }

    public void setShowFavorite(boolean showFavorite) {
        this.showFavorite = showFavorite;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public DocRecyclerViewAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mComments = new ArrayList<>();
        mTags = new ArrayList<>();
        mPreComments = new ArrayList<>();
        mPlayer = Player.getInstance(mContext);
        mPlayer.registerCallback(this);
        fragment = new BottomMenuFragment();
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
                return new CommentHolder(mLayoutInflater.inflate(R.layout.item_new_comment,parent,false));
            case TYPE_COIN:
                return new CoinHideViewHolder(mLayoutInflater.inflate(R.layout.item_new_doc_hide_top,parent,false));
            case TYPE_COIN_TEXT:
                return new HideTextHolder(mLayoutInflater.inflate(R.layout.item_new_doc_hide_text,parent,false));
            case TYPE_COIN_IMAGE:
                return new HideImageHolder(mLayoutInflater.inflate(R.layout.item_new_doc_hide_image,parent,false));
            case TYPE_FLOOR:
                return new FloorHolder((mLayoutInflater.inflate(R.layout.item_dynamic_coin,parent,false)));
            case TYPE_FOLDER:
                return new BagFavoriteHolder(mLayoutInflater.inflate(R.layout.item_bag_get,parent,false));
            case TYPE_TITLE:
                return new TextHolder(mLayoutInflater.inflate(R.layout.item_new_doc_text,parent,false));
            default:
                return new EmptyViewHolder(mLayoutInflater.inflate(R.layout.item_empty,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
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
        if(holder instanceof CreatorHolder){
            CreatorHolder creatorHolder = (CreatorHolder) holder;
            createCreator(creatorHolder);
        }else if(holder instanceof TextHolder){
            TextHolder textHolder = (TextHolder) holder;
            createText(textHolder, position);
        }else if(holder instanceof ImageHolder){
            ImageHolder imageHolder = (ImageHolder) holder;
            createImage(imageHolder, position,36);
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
            if (position == mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 2){
                hideTextHolder.mRoot.setBackgroundResource(R.drawable.shape_dash_foot);
            }else {
                hideTextHolder.mRoot.setBackgroundResource(R.drawable.shape_dash_mid);
            }
        }else if(holder instanceof HideImageHolder){
            HideImageHolder imageHolder = (HideImageHolder) holder;
            createHideImage(imageHolder, position,72);
            HideImageHolder hideImageHolder = (HideImageHolder) holder;
            if (position == mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 2){
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
                if(!mDocBean.isCoinComment()){
                    ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.pink_fb7ba2));
                    String content = "需要消耗 " + mDocBean.getCoin() + "枚节操";
                    String extra = mDocBean.getCoin() + "枚节操";
                    SpannableStringBuilder style = new SpannableStringBuilder(content);
                    style.setSpan(span, content.indexOf(extra), content.indexOf(extra) + extra.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    hideViewHolder.hideText.setText(style);
                    hideViewHolder.lock.setText("(解锁 "+ mDocBean.getNowNum() + "/" + mDocBean.getMaxNum() + ")");
                    hideViewHolder.shareRoot.setVisibility(View.VISIBLE);
                    hideViewHolder.shareRoot2.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            ((NewDocDetailActivity)mContext).showShareToBuy();
                        }
                    });
                }else {
                    hideViewHolder.shareRoot.setVisibility(View.GONE);
                    hideViewHolder.hideText.setText(mContext.getString(R.string.label_reply_show));
                }
            }
            hideViewHolder.llHide.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(!mDocBean.isCoinComment()){
                        final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                        alertDialogUtil.createGetHideDialog(mContext,mDocBean.getCoin());
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
                    }else {
                        ((NewDocDetailActivity)mContext).replyNormal();
                    }
                }
            });
        }else if(holder instanceof FloorHolder){
            FloorHolder floorHolder = (FloorHolder) holder;
            createFloor(floorHolder);
        }else if(holder instanceof BagFavoriteHolder){
            BagFavoriteHolder bagFavoriteHolder = (BagFavoriteHolder) holder;
            createFolderItem(bagFavoriteHolder,position);
        }
    }

    private void setMusicInfo(Song musicInfo){
        this.mMusicInfo = musicInfo;
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if(mDocBean != null){
            size = mDocBean.getDetails().size() + mComments.size() + 4;
            if(mDocBean.getCoin() > 0){
                size += mDocBean.getCoinDetails().size() + 1;
            }
            if(mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                size++;
            }
        }
        return size;
    }

    public ArrayList<CommentV2Entity> getmComments(){
        return mComments;
    }

    public Object getItem(int position){
        if(position == 0){
            return "";
        }else if(position == 1){
            return mDocBean.getTitle();
        } else if(position < mDocBean.getDetails().size() + 2){
            return  mDocBean.getDetails().get(position - 2).getTrueData();
        }else if( position ==  mDocBean.getDetails().size() + 2){
            if(mDocBean.getCoin() > 0){
                return "";
            }else if(mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                return mDocBean.getFolderInfo();
            }else {
                return mDocBean.getTags();
            }
        }else if(mDocBean.getCoinDetails().size() > 0 && position > mDocBean.getDetails().size() + 2 && position < mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 3){
            return mDocBean.getCoinDetails().get(position - 3 - mDocBean.getDetails().size()).getTrueData();
        }else if(mDocBean.getCoin() > 0 && position == mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 3){
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
                return mComments.get(position - mDocBean.getDetails().size() - 6 - mDocBean.getCoinDetails().size());
            }else if(mDocBean.getCoin() > 0){
                return mComments.get(position - mDocBean.getDetails().size() - 5 - mDocBean.getCoinDetails().size());
            }else if(mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                return mComments.get(position - mDocBean.getDetails().size() - 5);
            }else {
                return mComments.get(position - mDocBean.getDetails().size() - 4);
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
                mTagsPosition = mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 4;
            }else if(mDocBean.getCoin() > 0){
                mTagsPosition = mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 3;
            }else if(mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                mTagsPosition = mDocBean.getDetails().size() + 3;
            }else {
                mTagsPosition = mDocBean.getDetails().size() + 2;
            }
        }
        notifyDataSetChanged();
    }

    public void setComment(ArrayList<CommentV2Entity> beans){
        if(beans.size() > 0){
            int bgSize = getItemCount() - mComments.size();
            int bfSize = mComments.size();
            this.mComments.clear();
            mComments.addAll(beans);
            int afSize = mComments.size();
            int btSize = afSize - bfSize;
           // notifyItemChanged(mTagsPosition + 1);
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
           // notifyItemChanged(mTagsPosition + 1);
            notifyItemRangeRemoved(bgSize,bfSize);
        }
    }

    public void addComment(ArrayList<CommentV2Entity> beans){
        int bgSize = getItemCount();
        int bfSize = mComments.size();
        this.mComments.addAll(beans);
        int afSize = mComments.size();
        int btSize = afSize - bfSize;
       // notifyItemChanged(mTagsPosition + 1);
        notifyItemRangeInserted(bgSize,btSize);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_CREATOR;
        }else if(position == 1){
            return TYPE_TITLE;
        }else if(position < mDocBean.getDetails().size() + 2){
            String type = mDocBean.getDetails().get(position - 2).getType();
            return NewDocType.getType(type);
        }else if( position ==  mDocBean.getDetails().size() + 2){
            if(mDocBean.getCoin() > 0){
                return TYPE_COIN;
            }else if(mDocBean.getFolderInfo() != null && !TextUtils.isEmpty(mDocBean.getFolderInfo().getFolderId())){
                return TYPE_FOLDER;
            }else {
                return TYPE_LABEL;
            }
        }else if(mDocBean.getCoinDetails().size() > 0 && position > mDocBean.getDetails().size() + 2 && position < mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 3){
            String type = mDocBean.getCoinDetails().get(position - 3 - mDocBean.getDetails().size()).getType();
            return NewDocType.getType(type) + 8;
        }else if(mDocBean.getCoin() > 0 && position == mDocBean.getDetails().size() + mDocBean.getCoinDetails().size() + 3){
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
        ImageView mIvVip;
        ImageView mIvSex;
        TextView mTvCreator;
        TextView tvLevel;
        TextView mTvTime;
        TextView mFollow;
        View rlHuiZhang1;
        TextView tvHuiZhang1;
        View[] huiZhangRoots;
        TextView[] huiZhangTexts;

        CreatorHolder(View itemView) {
            super(itemView);
            mIvCreator = itemView.findViewById(R.id.iv_avatar);
            mIvVip = itemView.findViewById(R.id.iv_vip);
            mIvSex = itemView.findViewById(R.id.iv_sex);
            mTvCreator = itemView.findViewById(R.id.tv_name);
            mTvTime = itemView.findViewById(R.id.tv_time);
            tvLevel = itemView.findViewById(R.id.tv_level);
            tvHuiZhang1 = itemView.findViewById(R.id.tv_huizhang_1);
            mFollow = itemView.findViewById(R.id.tv_follow);
            rlHuiZhang1 = itemView.findViewById(R.id.fl_huizhang_1);
            huiZhangRoots = new View[]{rlHuiZhang1};
            huiZhangTexts = new TextView[]{tvHuiZhang1};
        }
    }

    public void followUserSuccess(boolean isFollow){
        mDocBean.setFollowUser(isFollow);
        notifyItemChanged(0);
    }

    private void createCreator(final CreatorHolder holder){
        int size = (int) mContext.getResources().getDimension(R.dimen.x80);
        Glide.with(mContext)
                .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + mDocBean.getUserIcon(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(holder.mIvCreator);
        if(mDocBean.getUserId().equals(PreferenceUtils.getUUid())){
            holder.mFollow.setVisibility(View.GONE);
        }else {
            holder.mFollow.setVisibility(View.VISIBLE);
        }
        holder.mFollow.setSelected(mDocBean.isFollowUser());
        holder.mFollow.setText(mDocBean.isFollowUser() ? mContext.getString(R.string.label_followed) : mContext.getString(R.string.label_follow));
        holder.mFollow.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ((NewDocDetailActivity)mContext).followUser(mDocBean.getUserId(),mDocBean.isFollowUser());
            }
        });

        if(mDocBean.isVip()){
            holder.mIvVip.setVisibility(View.VISIBLE);
        }else {
            holder.mIvVip.setVisibility(View.GONE);
        }
        holder.mIvSex.setImageResource(mDocBean.getUserSex().equalsIgnoreCase("M")?R.drawable.ic_user_girl:R.drawable.ic_user_boy);
        holder.mTvCreator.setText(mDocBean.getUserName());
        holder.mTvTime.setText(StringUtils.timeFormat(mDocBean.getCreateTime()));
        LevelSpan levelSpan = new LevelSpan(ContextCompat.getColor(mContext,R.color.white),mContext.getResources().getDimension(R.dimen.x12));
        String content = "LV" + mDocBean.getUserLevel();
        String colorStr = "LV";
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(levelSpan, content.indexOf(colorStr), content.indexOf(colorStr) + colorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvLevel.setText(style);
        float radius2 = mContext.getResources().getDimension(R.dimen.y4);
        float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
        RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable();
        shapeDrawable2.setShape(roundRectShape2);
        shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(mDocBean.getUserLevelColor(), ContextCompat.getColor(mContext, R.color.main_cyan)));
        holder.tvLevel.setBackgroundDrawable(shapeDrawable2);
        holder.mIvCreator.setTag(R.id.id_creator_uuid, mDocBean.getUserId());
        holder.mIvCreator.setOnClickListener(mAvatarListener);
        ViewUtils.badge(mContext,holder.huiZhangRoots,holder.huiZhangTexts,mDocBean.getBadgeList());
    }

    private static class TextHolder extends RecyclerView.ViewHolder{

        TextView mTvText;

        TextHolder(View itemView) {
            super(itemView);
            mTvText = itemView.findViewById(R.id.tv_doc_content);
        }
    }

    private void createText(final TextHolder holder,int position){
        String content = (String) getItem(position);
        if(position == 1 && !TextUtils.isEmpty(content)){
            holder.mTvText.setTextColor(ContextCompat.getColor(mContext,R.color.black_1e1e1e));
            holder.mTvText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
            holder.mTvText.getPaint().setFakeBoldText(true);
            holder.mTvText.setText(mDocBean.getTitle());
        }else if(!TextUtils.isEmpty(content)){
            holder.mTvText.setTextColor(ContextCompat.getColor(mContext,R.color.gray_444444));
            holder.mTvText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
            holder.mTvText.getPaint().setFakeBoldText(false);
            holder.mTvText.setText(StringUtils.getUrlClickableText(mContext, StringUtils.buildAtUserToShow(mContext,(String) getItem(position))));
            holder.mTvText.setMovementMethod(LinkMovementMethod.getInstance());
        }else {
            holder.mTvText.setVisibility(View.GONE);
        }
    }

    private static class HideTextHolder  extends RecyclerView.ViewHolder{
        private LinearLayout mRoot;
        private TextView mTvText;

        HideTextHolder(View itemView) {
            super(itemView);
            mRoot = itemView.findViewById(R.id.ll_root);
            mTvText = itemView.findViewById(R.id.tv_doc_content);
        }
    }

    private void createHideText(final HideTextHolder holder,int position){
        holder.mTvText.setText(StringUtils.getUrlClickableText(mContext, StringUtils.buildAtUserToShow(mContext,(String) getItem(position))));
        holder.mTvText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private static class ImageHolder extends RecyclerView.ViewHolder{
        ImageView mIvImage;
        LongImageView mIvLongImage;

        ImageHolder(View itemView) {
            super(itemView);
            mIvImage = itemView.findViewById(R.id.iv_doc_image);
            mIvLongImage = itemView.findViewById(R.id.iv_doc_long_image);
        }
    }

    private void createImage(final ImageHolder holder, final int position, int size){
        final Image image  = (Image) getItem(position);
        if(image.getW() <= 0 || image.getH() <= 0){
            holder.mIvImage.setVisibility(View.GONE);
            holder.mIvLongImage.setVisibility(View.GONE);
        }else {
            final int[] wh = BitmapUtils.getDocIconSizeFromW(image.getW() * 2, image.getH() * 2, DensityUtil.getScreenWidth(mContext) - (int)mContext.getResources().getDimension(R.dimen.x72));
            if(((float)wh[1] / wh[0]) > 16.0f / 9.0f){
                holder.mIvImage.setVisibility(View.GONE);
                holder.mIvLongImage.setVisibility(View.VISIBLE);
                String temp = EncoderUtils.MD5(ApiService.URL_QINIU + image.getPath()) + ".jpg";
                final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
                ViewGroup.LayoutParams layoutParams = holder.mIvLongImage.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                holder.mIvLongImage.setLayoutParams(layoutParams);
                if(longImage.exists()){
                    holder.mIvLongImage.setImage(longImage.getAbsolutePath());
                }else {
                    FileDownloader.getImpl().create(ApiService.URL_QINIU + image.getPath())
                            .setPath(StorageUtils.getGalleryDirPath() + temp)
                            .setCallbackProgressTimes(1)
                            .setListener(new FileDownloadListener() {
                                @Override
                                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                }

                                @Override
                                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                }

                                @Override
                                protected void completed(BaseDownloadTask task) {
                                    BitmapUtils.galleryAddPic(mContext, longImage.getAbsolutePath());
                                    holder.mIvLongImage.setImage(longImage.getAbsolutePath());
                                    notifyItemChanged(position);
                                }

                                @Override
                                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                }

                                @Override
                                protected void error(BaseDownloadTask task, Throwable e) {

                                }

                                @Override
                                protected void warn(BaseDownloadTask task) {

                                }
                            }).start();
                }
            }else {
                holder.mIvImage.setVisibility(View.VISIBLE);
                holder.mIvLongImage.setVisibility(View.GONE);
                if(FileUtil.isGif(image.getPath())){
                    ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
                    layoutParams.width = wh[0];
                    layoutParams.height = wh[1];
                    holder.mIvImage.setLayoutParams(layoutParams);
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
    }

    private static class HideImageHolder extends RecyclerView.ViewHolder{
        private LinearLayout mRoot;
        private ImageView mIvImage;
        private LongImageView mIvLongImage;

        HideImageHolder(View itemView) {
            super(itemView);
            mRoot = itemView.findViewById(R.id.ll_root);
            mIvImage = itemView.findViewById(R.id.iv_doc_image);
            mIvLongImage = itemView.findViewById(R.id.iv_doc_long_image);
        }
    }

    private void createHideImage(final HideImageHolder holder, final int position, int size){
        final Image image  = (Image) getItem(position);
        if(image.getW() <= 0 || image.getH() <= 0){
            holder.mIvImage.setVisibility(View.GONE);
            holder.mIvLongImage.setVisibility(View.GONE);
        }else {
            final int[] wh = BitmapUtils.getDocIconSizeFromW(image.getW() * 2, image.getH() * 2, DensityUtil.getScreenWidth(mContext) - (int)mContext.getResources().getDimension(R.dimen.x144));
            if(wh[1] > 4000){
                holder.mIvImage.setVisibility(View.GONE);
                holder.mIvLongImage.setVisibility(View.VISIBLE);
                String temp = EncoderUtils.MD5(ApiService.URL_QINIU + image.getPath()) + ".jpg";
                final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
                ViewGroup.LayoutParams layoutParams = holder.mIvLongImage.getLayoutParams();
                layoutParams.width = wh[0];
                layoutParams.height = wh[1];
                holder.mIvLongImage.setLayoutParams(layoutParams);
                if(longImage.exists()){
                    holder.mIvLongImage.setImage(longImage.getAbsolutePath());
                }else {
                    FileDownloader.getImpl().create(ApiService.URL_QINIU + image.getPath())
                            .setPath(StorageUtils.getGalleryDirPath() + temp)
                            .setCallbackProgressTimes(1)
                            .setListener(new FileDownloadListener() {
                                @Override
                                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                }

                                @Override
                                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                }

                                @Override
                                protected void completed(BaseDownloadTask task) {
                                    BitmapUtils.galleryAddPic(mContext, longImage.getAbsolutePath());
                                    holder.mIvLongImage.setImage(longImage.getAbsolutePath());
                                    notifyItemChanged(position);
                                }

                                @Override
                                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                }

                                @Override
                                protected void error(BaseDownloadTask task, Throwable e) {

                                }

                                @Override
                                protected void warn(BaseDownloadTask task) {

                                }
                            }).start();
                }
            }else {
                if(FileUtil.isGif(image.getPath())){
                    ViewGroup.LayoutParams layoutParams = holder.mIvImage.getLayoutParams();
                    layoutParams.width = wh[0];
                    layoutParams.height = wh[1];
                    holder.mIvImage.setLayoutParams(layoutParams);
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
            ivMusicCtrl = itemView.findViewById(R.id.iv_music_ctrl);
            tvMusicTitle = itemView.findViewById(R.id.tv_music_name);
            tvMusicTime = itemView.findViewById(R.id.tv_music_seek);
            sbMusicTime = itemView.findViewById(R.id.sb_music);
            musicRoot = itemView.findViewById(R.id.rl_music_root);
            mIvImage = itemView.findViewById(R.id.iv_doc_image);
            mIvLongImage = itemView.findViewById(R.id.iv_doc_long_image);
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
            final Image image = music.getCover();
            if(image.getW() <= 0 || image.getH() <= 0){
                mMusicHolder.mIvImage.setVisibility(View.GONE);
                mMusicHolder.mIvLongImage.setVisibility(View.GONE);
            }else {
                final int[] wh = BitmapUtils.getDocIconSizeFromW(image.getW(), image.getH(), DensityUtil.getScreenWidth(mContext) - (int)mContext.getResources().getDimension(R.dimen.x40));
                if(wh[1] > 4000){
                    mMusicHolder.mIvImage.setVisibility(View.GONE);
                    mMusicHolder.mIvLongImage.setVisibility(View.VISIBLE);
                    String temp = EncoderUtils.MD5(ApiService.URL_QINIU + image.getPath()) + ".jpg";
                    final File longImage = new File(StorageUtils.getGalleryDirPath(), temp);
                    ViewGroup.LayoutParams layoutParams = mMusicHolder.mIvLongImage.getLayoutParams();
                    layoutParams.width = wh[0];
                    layoutParams.height = wh[1];
                    mMusicHolder.mIvLongImage.setLayoutParams(layoutParams);
                    if(longImage.exists()){
                        mMusicHolder.mIvLongImage.setImage(longImage.getAbsolutePath());
                    }else {
                        FileDownloader.getImpl().create(ApiService.URL_QINIU + image.getPath())
                                .setPath(StorageUtils.getGalleryDirPath() + temp)
                                .setCallbackProgressTimes(1)
                                .setListener(new FileDownloadListener() {
                                    @Override
                                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                    }

                                    @Override
                                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                    }

                                    @Override
                                    protected void completed(BaseDownloadTask task) {
                                        BitmapUtils.galleryAddPic(mContext, longImage.getAbsolutePath());
                                        mMusicHolder.mIvLongImage.setImage(longImage.getAbsolutePath());
                                        notifyItemChanged(position);
                                    }

                                    @Override
                                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                    }

                                    @Override
                                    protected void error(BaseDownloadTask task, Throwable e) {

                                    }

                                    @Override
                                    protected void warn(BaseDownloadTask task) {

                                    }
                                }).start();
                    }
                }else {
                    if(FileUtil.isGif(image.getPath())){
                        ViewGroup.LayoutParams layoutParams = mMusicHolder.mIvImage.getLayoutParams();
                        layoutParams.width = wh[0];
                        layoutParams.height = wh[1];
                        mMusicHolder.mIvImage.setLayoutParams(layoutParams);
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
            ivBg = itemView.findViewById(R.id.iv_bg);
            tvNum = itemView.findViewById(R.id.tv_num);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivGot = itemView.findViewById(R.id.tv_got);
            ivGot.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
            layoutParams.height = (int)mContext.getResources().getDimension(R.dimen.y240);
            layoutParams.width = DensityUtil.getScreenWidth(mContext) - (int)mContext.getResources().getDimension(R.dimen.x72);
            ivBg.setLayoutParams(layoutParams);
        }
    }

    private void createFolderItem(BagFavoriteHolder holder,int position){
        BagDirEntity entity = (BagDirEntity) getItem(position);
        Glide.with(mContext)
                .load(StringUtils.getUrl(mContext, ApiService.URL_QINIU + entity.getCover(), DensityUtil.getScreenWidth(mContext) - (int)mContext.getResources().getDimension(R.dimen.x72), (int)mContext.getResources().getDimension(R.dimen.y240), false, true))
                .override(DensityUtil.getScreenWidth(mContext) - (int)mContext.getResources().getDimension(R.dimen.x72), (int)mContext.getResources().getDimension(R.dimen.y240))
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
            ivMusicCtrl = itemView.findViewById(R.id.iv_music_ctrl);
            tvMusicTitle = itemView.findViewById(R.id.tv_music_name);
            tvMusicTime = itemView.findViewById(R.id.tv_music_seek);
            sbMusicTime = itemView.findViewById(R.id.sb_music);
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
                .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + bean.getIcon().getPath(), (int)mContext.getResources().getDimension(R.dimen.y90), (int)mContext.getResources().getDimension(R.dimen.y90), false, true))
                .override((int)mContext.getResources().getDimension(R.dimen.y90), (int)mContext.getResources().getDimension(R.dimen.y90))
                .placeholder(R.drawable.bg_default_circle)
                .error(R.drawable.bg_default_circle)
                .into(holder.ivMusicCtrl);
    }

    private static class ChapterHolder extends RecyclerView.ViewHolder{

        private DocLabelView mDvLabel;

        ChapterHolder(View itemView) {
            super(itemView);
            mDvLabel = itemView.findViewById(R.id.dv_doc_label_root);
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
        TextView tvForward;
        TextView tvComment;
        TextView tvTag;
        View fRoot;
        View cRoot;
        View tRoot;


        LabelHolder(View itemView) {
            super(itemView);
            mDvLabel = itemView.findViewById(R.id.dv_doc_label_root);
            docLabelAdapter = new NewDocLabelAdapter(itemView.getContext(),false);
            tvForward = itemView.findViewById(R.id.tv_forward_num);
            tvComment = itemView.findViewById(R.id.tv_comment_num);
            tvTag = itemView.findViewById(R.id.tv_tag_num);
            fRoot = itemView.findViewById(R.id.fl_forward_root);
            cRoot = itemView.findViewById(R.id.fl_comment_root);
            tRoot = itemView.findViewById(R.id.fl_tag_root);
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

        mLabelHolder.tvForward.setCompoundDrawablePadding(0);
        mLabelHolder.tvComment.setCompoundDrawablePadding(0);
        mLabelHolder.tvTag.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext,R.drawable.btn_feed_tab),null,null,null);
        mLabelHolder.tvTag.setCompoundDrawablePadding(0);
        mLabelHolder.fRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ShareArticleEntity entity = new ShareArticleEntity();
                entity.setDocId(mDocBean.getId());
                entity.setTitle(mDocBean.getShare().getTitle());
                entity.setContent(mDocBean.getShare().getDesc());
                entity.setCover(mDocBean.getCover());
                entity.setCreateTime(mDocBean.getCreateTime());
                UserTopEntity entity2 = new UserTopEntity();
                if(mDocBean.getBadgeList().size() > 0){
                    entity2.setBadge(mDocBean.getBadgeList().get(0));
                }else {
                    entity2.setBadge(null);
                }
                entity2.setHeadPath(mDocBean.getUserIcon());
                entity2.setLevel(mDocBean.getUserLevel());
                entity2.setLevelColor(mDocBean.getUserLevelColor());
                entity2.setSex(mDocBean.getUserSex());
                entity2.setUserId(mDocBean.getUserId());
                entity2.setUserName(mDocBean.getUserName());
                entity.setDocCreateUser(entity2);
                CreateForwardActivity.startActivity(mContext,entity);
            }
        });
        mLabelHolder.cRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                CreateCommentActivity.startActivity(mContext,mDocBean.getId(),false,"",true);
            }
        });
    }

    private void giveCoin(int count){
        if (!NetworkUtils.checkNetworkAndShowError(mContext)){
            return;
        }
        ((NewDocDetailActivity)mContext).giveCoin(count);
    }

    public void onGiveCoin(int coins){
        mDocBean.setCoinPays(mDocBean.getCoinPays() + coins);
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
        TextView mSort;
        CommonTabLayout pageIndicator;

        FloorHolder(View itemView) {
            super(itemView);
            mIvGiveCoin = itemView.findViewById(R.id.iv_give_coin);
            mTvCoinNum = itemView.findViewById(R.id.tv_got_coin);
            mSort = itemView.findViewById(R.id.tv_sort);
            pageIndicator = itemView.findViewById(R.id.indicator_person_data);
        }
    }

    private void createFloor(final FloorHolder holder){
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
                    alertDialogUtil.createEditDialog(mContext, PreferenceUtils.getAuthorInfo().getCoin(),0);
                    alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void CancelOnClick() {
                            alertDialogUtil.dismissDialog();
                        }

                        @Override
                        public void ConfirmOnClick() {
                            if(DialogUtils.checkLoginAndShowDlg(mContext)){
                                String content = alertDialogUtil.getEditTextContent();
                                if(!TextUtils.isEmpty(content) && Integer.valueOf(content) > 0){
                                    giveCoin(Integer.valueOf(content));
                                    alertDialogUtil.dismissDialog();
                                }else {
                                    ToastUtils.showShortToast(mContext,R.string.msg_input_err_coin);
                                }
                            }
                        }
                    });
                    alertDialogUtil.showDialog();
                }
            });
        }
        String[] mTitles = {"转发 " +  StringUtils.getNumberInLengthLimit(mDocBean.getRtNum(), 3),"评论 " + StringUtils.getNumberInLengthLimit(mDocBean.getComments(), 3)};
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        for(String title : mTitles){
            mTabEntities.add(new TabEntity(title, R.drawable.ic_personal_bag,R.drawable.ic_personal_bag));
        }
        holder.pageIndicator.setTabData(mTabEntities);
        holder.pageIndicator.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                commentType = position;
                if(commentType == 0){
                    holder.mSort.setVisibility(View.GONE);
                }else {
                    holder.mSort.setVisibility(View.VISIBLE);
                }
                if(mPreComments.size() == 0){
                    ((NewDocDetailActivity)mContext).requestComment();
                }else {
                    ArrayList<CommentV2Entity> temp = mPreComments;
                    mPreComments = DocRecyclerViewAdapter.this.getmComments();
                    if(commentType == 0){
                        DocRecyclerViewAdapter.this.setShowFavorite(false);
                    }else {
                        DocRecyclerViewAdapter.this.setShowFavorite(true);
                    }
                    int pre = mPrePosition;
                    mPrePosition = ((NewDocDetailActivity)mContext).getPosition();
                    DocRecyclerViewAdapter.this.setComment(temp);
                    if(pre != -1)((NewDocDetailActivity)mContext).scrollToPosition(pre);
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        holder.pageIndicator.setCurrentTab(1);

        holder.mSort.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                sortTime = !sortTime;
                holder.mSort.setText(sortTime?"时间排序":"热门排序");
                ((NewDocDetailActivity)mContext).requestComment();
            }
        });
    }

    public boolean isSortTime() {
        return sortTime;
    }

    public int getCommentType() {
        return commentType;
    }

    public void setCommentType(int commentType) {
        this.commentType = commentType;
    }

    public void setSortTime(boolean sortTime) {
        this.sortTime = sortTime;
    }

    private static class CommentHolder extends RecyclerView.ViewHolder{

        ImageView avatar;
        TextView userName;
        TextView time;
        TextView content;
        TextView level;
        TextView favorite;
        LinearLayout llImg;
        LinearLayout llComment;

        CommentHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.iv_avatar);
            userName = itemView.findViewById(R.id.tv_name);
            level = itemView.findViewById(R.id.tv_level);
            favorite = itemView.findViewById(R.id.tv_favorite);
            content = itemView.findViewById(R.id.tv_comment);
            llImg = itemView.findViewById(R.id.ll_comment_img);
            llComment = itemView.findViewById(R.id.ll_comment_root);
            time = itemView.findViewById(R.id.tv_comment_time);
        }
    }

    private void createComment(final CommentHolder holder, final int position){
        final CommentV2Entity entity = (CommentV2Entity) getItem(position);
        int size = (int) mContext.getResources().getDimension(R.dimen.x72);
        Glide.with(mContext)
                .load(StringUtils.getUrl(mContext,entity.getCreateUser().getHeadPath(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(holder.avatar);
        holder.avatar.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(mContext,entity.getCreateUser().getUserId());
            }
        });
        holder.userName.setText(entity.getCreateUser().getUserName());
        LevelSpan levelSpan = new LevelSpan(ContextCompat.getColor(mContext,R.color.white),mContext.getResources().getDimension(R.dimen.x12));
        final String content = "LV" + entity.getCreateUser().getLevel();
        String colorStr = "LV";
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(levelSpan, content.indexOf(colorStr), content.indexOf(colorStr) + colorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.level.setText(style);
        float radius2 = mContext.getResources().getDimension(R.dimen.y4);
        float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
        RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable();
        shapeDrawable2.setShape(roundRectShape2);
        shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(entity.getCreateUser().getLevelColor(), ContextCompat.getColor(mContext, R.color.main_cyan)));
        holder.level.setBackgroundDrawable(shapeDrawable2);

        if(showFavorite){
            holder.favorite.setVisibility(View.VISIBLE);
            holder.favorite.setSelected(entity.isLike());
            holder.favorite.setText(entity.getLikes() + "");
            holder.favorite.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ((NewDocDetailActivity) mContext).favoriteComment(entity.getCommentId(),entity.isLike(),position);
                }
            });
        }else {
            holder.favorite.setVisibility(View.GONE);
        }

        holder.favorite.setSelected(entity.isLike());
        holder.favorite.setText(entity.getLikes() + "");
        holder.favorite.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ((NewDocDetailActivity) mContext).favoriteComment(entity.getCommentId(),entity.isLike(),position);
            }
        });


        holder.content.setText(TagControl.getInstance().paresToSpann(mContext,entity.getContent()));
        holder.content.setMovementMethod(LinkMovementMethod.getInstance());
        if(entity.getImages().size() > 0){
            holder.llImg.setVisibility(View.VISIBLE);
            holder.llImg.removeAllViews();
            for (int i = 0;i < entity.getImages().size();i++){
                final int pos = i;
                Image image = entity.getImages().get(i);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.topMargin = (int) mContext.getResources().getDimension(R.dimen.y10);
                if(FileUtil.isGif(image.getPath())){
                    ImageView imageView = new ImageView(mContext);
                    setGif(image, imageView,params);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ImageBigSelectActivity.class);
                            intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, entity.getImages());
                            intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                                    pos);
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
                            intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, entity.getImages());
                            intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                                    pos);
                            mContext.startActivity(intent);
                        }
                    });
                    holder.llImg.addView(imageView,holder.llImg.getChildCount(),params);
                }
            }
        }else {
            holder.llImg.setVisibility(View.GONE);
        }
        holder.time.setText(StringUtils.timeFormat(entity.getCreateTime()));
        //sec comment
        if(entity.getHotComments() != null && entity.getHotComments().size() > 0){
            holder.llComment.setVisibility(View.VISIBLE);
            holder.llComment.removeAllViews();
            for(CommentV2SecEntity secEntity : entity.getHotComments()){
                TextView tv = new TextView(mContext);
                tv.setTextColor(ContextCompat.getColor(mContext,R.color.gray_444444));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,mContext.getResources().getDimension(R.dimen.x20));

                String retweetContent = "@" + secEntity.getCreateUser().getUserName();
                if(!TextUtils.isEmpty(secEntity.getCommentTo())){
                    retweetContent += " 回复 " + "@" + secEntity.getCommentToName();
                }
                retweetContent += ": " + secEntity.getContent();
                String retweetColorStr = "@" + secEntity.getCreateUser().getUserName();
                SpannableStringBuilder style1 = new SpannableStringBuilder(retweetContent);
                UserUrlSpan span = new UserUrlSpan(mContext,secEntity.getCreateUser().getUserId(),null);
                style1.setSpan(span, retweetContent.indexOf(retweetColorStr), retweetContent.indexOf(retweetColorStr) + retweetColorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(!TextUtils.isEmpty(secEntity.getCommentTo())){
                    String retweetColorStr1 = "@" + secEntity.getCommentToName();
                    UserUrlSpan span1 = new UserUrlSpan(mContext,secEntity.getCommentTo(),null);
                    style1.setSpan(span1, retweetContent.indexOf(retweetColorStr1), retweetContent.indexOf(retweetColorStr1) + retweetColorStr1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tv.setText(style1);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                holder.llComment.addView(tv);
            }
            if(entity.getComments() > entity.getHotComments().size()){
                TextView tv = new TextView(mContext);
                tv.setTextColor(ContextCompat.getColor(mContext,R.color.main_cyan));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,mContext.getResources().getDimension(R.dimen.x20));
                tv.setText("全部" + StringUtils.getNumberInLengthLimit(entity.getComments(),3) + "条回复");
                tv.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        CommentSecListActivity.startActivity(mContext,entity,mDocBean.getId());
                    }
                });
                holder.llComment.addView(tv);
            }
        }else {
            holder.llComment.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                showMenu(entity, position);
            }
        });
    }

    private void showMenu(final CommentV2Entity bean, final int position){
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item;
        item = new MenuItem(0,mContext.getString(R.string.label_reply));
        items.add(item);

        if(isShowFavorite()){
            item = new MenuItem(5,"点赞");
            items.add(item);
        }

        item = new MenuItem(1,mContext.getString(R.string.label_copy_dust));
        items.add(item);

        item = new MenuItem(2,mContext.getString(R.string.label_jubao));
        items.add(item);

        if(TextUtils.equals(PreferenceUtils.getUUid(), bean.getCreateUser().getUserId()) ){
            item = new MenuItem(3,mContext.getString(R.string.label_delete));
            items.add(item);
        }else if( TextUtils.equals(PreferenceUtils.getUUid(), mDocBean.getUserId())){
            item = new MenuItem(4,mContext.getString(R.string.label_delete));
            items.add(item);
        }

        fragment.setShowTop(true);
        fragment.setTopContent(bean.getContent());
        fragment.setMenuItems(items);
        fragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        fragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == 0) {
                    CreateCommentActivity.startActivity(mContext,bean.getCommentId(),true,"",true);
                } else if (itemId == 2) {
                    Intent intent = new Intent(mContext, JuBaoActivity.class);
                    intent.putExtra(JuBaoActivity.EXTRA_NAME, bean.getCreateUser().getUserName());
                    intent.putExtra(JuBaoActivity.EXTRA_CONTENT, bean.getContent());
                    intent.putExtra(JuBaoActivity.EXTRA_TYPE, 4);
                    intent.putExtra(JuBaoActivity.UUID,bean.getCommentId());
                    intent.putExtra(JuBaoActivity.EXTRA_TARGET, "COMMENT");
                    mContext.startActivity(intent);
                } else if (itemId == 3) {
                    ((NewDocDetailActivity)mContext).deleteComment(mDocBean.getId(),bean.getCommentId(),position);
                }else if(itemId == 1){
                    String content = bean.getContent();
                    ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("回复内容", content);
                    cmb.setPrimaryClip(mClipData);
                    ToastUtils.showShortToast(mContext, mContext.getString(R.string.label_level_copy_success));
                }else if(itemId == 4){
                    ((NewDocDetailActivity)mContext).deleteComment(mDocBean.getId(),bean.getCommentId(),position);
                }else if(itemId == 5){
                    ((NewDocDetailActivity)mContext).favoriteComment(bean.getCommentId(),bean.isLike(),position);
                }
            }
        });
        fragment.show(((BaseAppCompatActivity)mContext).getSupportFragmentManager(),"DocComment");
    }

     private static class CoinHideViewHolder extends RecyclerView.ViewHolder{

         View llTop;
         View llHide;
         TextView hideText;
         View shareRoot;
         TextView lock;
         View shareRoot2;

         CoinHideViewHolder(View itemView) {
             super(itemView);
             llTop = itemView.findViewById(R.id.ll_hide_top);
             llHide = itemView.findViewById(R.id.ll_hide);
             hideText = itemView.findViewById(R.id.tv_hide_text);
             shareRoot = itemView.findViewById(R.id.ll_share_root);
             shareRoot2 = itemView.findViewById(R.id.ll_share_root_2);
             lock = itemView.findViewById(R.id.tv_unlock);
        }
    }

    private void setGif(Image image, ImageView gifImageView, LinearLayout.LayoutParams params){
        final int[] wh = BitmapUtils.getDocIconSizeFromW(image.getW() * 2, image.getH() * 2, DensityUtil.getScreenWidth(mContext) - (int)mContext.getResources().getDimension(R.dimen.x168));
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
        final int[] wh = BitmapUtils.getDocIconSizeFromW(image.getW() * 2, image.getH() * 2, DensityUtil.getScreenWidth(mContext) - (int)mContext.getResources().getDimension(R.dimen.x168));
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

    private View.OnClickListener mAvatarListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String uuid = (String) v.getTag(R.id.id_creator_uuid);
            if (!TextUtils.isEmpty(uuid) && !uuid.equals(PreferenceUtils.getUUid())) {
                Intent i = new Intent(mContext,PersonalV2Activity.class);
                i.putExtra(BaseAppCompatActivity.UUID,uuid);
                mContext.startActivity(i);
            }
        }
    };
}