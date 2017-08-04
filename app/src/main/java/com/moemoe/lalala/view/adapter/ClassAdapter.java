package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.activity.NewPersonalActivity;
import com.moemoe.lalala.view.widget.view.NewDocLabelAdapter;
import com.moemoe.lalala.view.widget.view.DocLabelView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by yi on 2016/12/2.
 */

public class ClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_BANNER = 0;
    private static final int VIEW_TYPE_DOC= 1;
    private static final int VIEW_TYPE_FEATURED= 2;
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<BannerEntity> mBannerBeans;
    private ArrayList<FeaturedEntity> mFeaturedBeans ;
    private ArrayList<DocListEntity> mClassData;
    private BannerViewHolder mBannerViewHolder;
    private ScheduledExecutorService scheduledExecutorService;
    private int mCurrentItem = 0;
    private OnItemClickListener mOnItemClickListener;

    public ClassAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        this.context = context;
        mBannerBeans = new ArrayList<>();
        mFeaturedBeans = new ArrayList<>();
        mClassData = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mBannerViewHolder.viewPager != null) {
                mBannerViewHolder.viewPager.setCurrentItem(mCurrentItem, true);
            }
        }
    };

    private class BannerRunnable implements Runnable {

        @Override
        public void run() {
            if(mBannerViewHolder.viewPager != null){
                synchronized (context) {
                    mCurrentItem = (mBannerViewHolder.viewPager.getCurrentItem() + 1) % mBannerBeans.size();
                    handler.obtainMessage().sendToTarget();
                }
            }
        }
    }

    /**
     * 开始循环banner
     *
     */
    private void startBanner() {
        if(scheduledExecutorService != null) scheduledExecutorService.shutdown();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new BannerRunnable(), 3, 3, TimeUnit.SECONDS);
    }

    public void setBanner(ArrayList<BannerEntity> bannerBeans){
        mBannerBeans.clear();
        mBannerBeans.addAll(bannerBeans);
        notifyItemChanged(0);
    }

    public void setFeaturedBeans(ArrayList<FeaturedEntity> featuredBeans){
        mFeaturedBeans.clear();
        mFeaturedBeans.addAll(featuredBeans);
        notifyItemChanged(1);
    }

    public ArrayList<DocListEntity> getDocList(){
        return mClassData;
    }

    public ArrayList<BannerEntity> getBannerList(){
        return mBannerBeans;
    }

    public ArrayList<FeaturedEntity> getFeaturedList(){
        return mFeaturedBeans;
    }

    public void setDocList(ArrayList<DocListEntity> docList){
        int bfSize = mClassData.size();
        mClassData.clear();
        mClassData.addAll(docList);
        int afSize = mClassData.size();
        if(bfSize == 0){
            notifyItemRangeInserted(2,mClassData.size());
        }else {
            notifyItemRangeChanged(2,mClassData.size());
            if(bfSize - afSize > 0){
                notifyItemRangeRemoved(afSize + 2,bfSize - afSize);
            }
        }
    }

    public void addDocList(ArrayList<DocListEntity> docList){
        int bfSize = mClassData.size();
        mClassData.addAll(docList);
        int afSize = mClassData.size();
        notifyItemRangeInserted(bfSize + 2,afSize - bfSize);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_BANNER){
            return new BannerViewHolder(mInflater.inflate(R.layout.item_banner_class,parent,false));
        } else if(viewType == VIEW_TYPE_DOC){
            return new DocViewHolder(mInflater.inflate(R.layout.item_doc_club_class,parent,false));
        } else if(viewType == VIEW_TYPE_FEATURED){
            return new LinearHViewHolder(mInflater.inflate(R.layout.item_class_featured,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof LinearHViewHolder){
            LinearHViewHolder linearHViewHolder = (LinearHViewHolder) viewHolder;
            if(mFeaturedBeans.size() == 0){
                ViewGroup.LayoutParams params =  linearHViewHolder.itemView.getLayoutParams();
                params.height = 1;
                linearHViewHolder.itemView.setLayoutParams(params);
                return;
            }else {
                ViewGroup.LayoutParams params =  linearHViewHolder.itemView.getLayoutParams();
                params.height = DensityUtil.dip2px(context,110);
                linearHViewHolder.itemView.setLayoutParams(params);
            }
            linearHViewHolder.mRvList.setBackgroundColor(Color.WHITE);
            linearHViewHolder.recyclerViewAdapter.setData(mFeaturedBeans);
            linearHViewHolder.recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    FeaturedEntity docBean = mFeaturedBeans.get(position);
                    if(!TextUtils.isEmpty(docBean.getSchema())){
                        Uri uri = Uri.parse(docBean.getSchema());
                        IntentUtils.toActivityFromUri(context,uri,view);
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
        }else if(viewHolder instanceof BannerViewHolder){
            mBannerViewHolder = (BannerViewHolder) viewHolder;
            if(mBannerBeans.size() == 0){
                ViewGroup.LayoutParams params =  mBannerViewHolder.itemView.getLayoutParams();
                params.height = 1;
                mBannerViewHolder.itemView.setLayoutParams(params);
                return;
            }else {
                ViewGroup.LayoutParams params =  mBannerViewHolder.itemView.getLayoutParams();
                params.height = DensityUtil.dip2px(context,144);
                mBannerViewHolder.itemView.setLayoutParams(params);
                startBanner();
            }
            ArrayList<ImageView> imageViews = new ArrayList<>();
            mBannerViewHolder.llPointGroup.removeAllViews();
            for(int i=0;i<mBannerBeans.size();i++) {
                final BannerEntity bean = mBannerBeans.get(i);
                ImageView image = new ImageView(context);
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(context)
                        .load(StringUtils.getUrl(context, ApiService.URL_QINIU + bean.getBg().getPath(), DensityUtil.getScreenWidth(context), DensityUtil.dip2px(context,150), false, true))
                        .override( DensityUtil.getScreenWidth(context), DensityUtil.dip2px(context,150))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(image);
                imageViews.add(image);
                if(mBannerBeans.size() > 1){
                    View view = new View(context);
                    view.setBackgroundResource(R.drawable.icon_class_banner_switch_white);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(context,10), DensityUtil.dip2px(context,10));
                    if (i > 0) {
                        params.leftMargin = DensityUtil.dip2px(context,4);
                    }
                    view.setLayoutParams(params);
                    mBannerViewHolder.llPointGroup.addView(view);
                }
                image.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(!TextUtils.isEmpty(bean.getSchema())){
                            Uri uri = Uri.parse(bean.getSchema());
                            IntentUtils.toActivityFromUri(context, uri,v);
                        }
                    }
                });
            }
            mBannerViewHolder.viewPager.setAdapter(new BannerAdapter(imageViews));
            if(mBannerBeans.size() > 1){
                mBannerViewHolder.llPointContainer.setVisibility(View.VISIBLE);
                mBannerViewHolder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        int len = (int) (mBannerViewHolder.width * positionOffset) + position * mBannerViewHolder.width;
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBannerViewHolder.pressedPoint.getLayoutParams();
                        params.leftMargin = len;
                        mBannerViewHolder.pressedPoint.setLayoutParams(params);
                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                mBannerViewHolder.llPointGroup.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mBannerViewHolder.llPointGroup.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        mBannerViewHolder.width = mBannerViewHolder.llPointGroup.getChildAt(1).getLeft()
                                - mBannerViewHolder.llPointGroup.getChildAt(0).getLeft();
                    }
                });
            }else {
                mBannerViewHolder.llPointContainer.setVisibility(View.GONE);
            }
        }else if(viewHolder instanceof DocViewHolder){
            Object o = getItem(position);
            if(o instanceof DocListEntity){
                final DocListEntity post = (DocListEntity) o;
                final DocViewHolder holder = (DocViewHolder) viewHolder;

                if(holder.ivClubCreatorFlag != null){
                    holder.ivClubCreatorFlag.setVisibility(View.GONE);
                    holder.tvCreatorName.setSelected(false);
                }
                if(holder.docLabel != null && post.getTags() != null){
                    holder.docLabel.setDocLabelAdapter(holder.docLabelAdapter);
                    holder.docLabelAdapter.setData(post.getTags(),false);
                    if(post.getTags().size()>0) {
                        holder.docLabel.setVisibility(View.VISIBLE);
                        holder.vDocSep.setVisibility(View.VISIBLE);
                    }else{
                        holder.docLabel.setVisibility(View.GONE);
                        holder.vDocSep.setVisibility(View.GONE);
                    }
                }else {
                    if(holder.docLabel != null) holder.docLabel.setVisibility(View.GONE);
                    holder.vDocSep.setVisibility(View.GONE);
                }

                holder.tvCreatorName.setText(post.getUserName());
                holder.tvLevel.setText(String.valueOf(post.getUserLevel()));
                int radius1 = DensityUtil.dip2px(context,5);
                float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
                RoundRectShape roundRectShape1 = new RoundRectShape(outerR1, null, null);
                ShapeDrawable shapeDrawable1 = new ShapeDrawable();
                shapeDrawable1.setShape(roundRectShape1);
                shapeDrawable1.getPaint().setStyle(Paint.Style.FILL);
                shapeDrawable1.getPaint().setColor(StringUtils.readColorStr(post.getUserLevelColor(), ContextCompat.getColor(context, R.color.main_cyan)));
                holder.mLevelRoot.setBackgroundDrawable(shapeDrawable1);
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
                if(post.getBadgeList().size() > 0){
                    int size = 3;
                    if(post.getBadgeList().size() < 3){
                        size = post.getBadgeList().size();
                    }
                    for (int i = 0;i < size;i++){
                        holder.huiZhangTexts[i].setVisibility(View.VISIBLE);
                        holder.huiZhangRoots[i].setVisibility(View.VISIBLE);
                        BadgeEntity badgeEntity = post.getBadgeList().get(i);
                        TextView tv = holder.huiZhangTexts[i];
                        tv.setText(badgeEntity.getTitle());
                        tv.setText(badgeEntity.getTitle());
                        tv.setBackgroundResource(R.drawable.bg_badge_cover);
                        int px = DensityUtil.dip2px(context,4);
                        tv.setPadding(px,0,px,0);
                        int radius2 = DensityUtil.dip2px(context,2);
                        float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
                        RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
                        ShapeDrawable shapeDrawable2 = new ShapeDrawable();
                        shapeDrawable2.setShape(roundRectShape2);
                        shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
                        shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(context, R.color.main_cyan)));
                        holder.huiZhangRoots[i].setBackgroundDrawable(shapeDrawable2);
                    }
                }
                if(holder.ivCreatorAvatar != null){
                    Glide.with(context)
                            .load(StringUtils.getUrl(context,ApiService.URL_QINIU + post.getUserIcon().getPath(), DensityUtil.dip2px(context,44), DensityUtil.dip2px(context,44), false, true))
                            .override(DensityUtil.dip2px(context,44), DensityUtil.dip2px(context,44))
                            .placeholder(R.drawable.bg_default_circle)
                            .error(R.drawable.bg_default_circle)
                            .bitmapTransform(new CropCircleTransformation(context))
                            .into(holder.ivCreatorAvatar);
                    holder.ivCreatorAvatar.setTag(R.id.id_creator_uuid, post.getUserId());
                }
                holder.ivClubCreatorFlag.setVisibility(View.GONE);
                holder.tvCreatorName.setSelected(false);
                holder.ivClubCreatorFlag.setVisibility(View.GONE);
                holder.tvCreatorName.setSelected(false);
                if(TextUtils.isEmpty(post.getDesc().getTitle())){
                    holder.tvPostTitle.setVisibility(View.GONE);
                }else{
                    holder.tvPostTitle.setVisibility(View.VISIBLE);
                    holder. tvPostTitle.setText(post.getDesc().getTitle());
                }
                // 点赞/评论
                holder.tvCommentNum.setText(StringUtils.getNumberInLengthLimit(post.getDesc().getComments(), 3));
                holder.tvPantsNum.setText(StringUtils.getNumberInLengthLimit(post.getDesc().getLikes(), 3));
                // 时间,内容
                holder.tvPostDate.setText(StringUtils.timeFormate(post.getUpdateTime()));
                holder.tvPostBrief.setText(post.getDesc().getContent());
                // 加载特殊帖子样式：投票，视频
                holder.rlSpecialTypePack.setVisibility(View.GONE);
                // 加载图片
                holder.ivIcon1.setImageBitmap(null);
                holder.ivIcon2.setImageBitmap(null);
                holder.ivIcon3.setImageBitmap(null);
                holder.ivIcon1.setTag(R.id.id_filebean, post);
                holder.ivIcon2.setTag(R.id.id_filebean, post);
                holder. ivIcon3.setTag(R.id.id_filebean, post);
                holder.rlIcon1.setVisibility(View.INVISIBLE);
                holder. rlIcon2.setVisibility(View.INVISIBLE);
                if (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE) {
                    holder. rlIcon3.setVisibility(View.GONE);
                } else {
                    holder.rlIcon3.setVisibility(View.INVISIBLE);
                }
                holder.tvIconNum.setVisibility(View.GONE);
                if (post.getDesc().getMusic() != null){
                    holder.rlMusicRoot.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(StringUtils.getUrl(context,ApiService.URL_QINIU + post.getDesc().getMusic().getCover().getPath(), DensityUtil.dip2px(context,90), DensityUtil.dip2px(context,90), false, true))
                            .override(DensityUtil.dip2px(context,90), DensityUtil.dip2px(context,90))
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .centerCrop()
                            .transform(new GlideRoundTransform(context,5,0,0,5))
                            .into(holder.musicImg);
                    holder.musicTitle.setText(post.getDesc().getMusic().getName());
                }else {
                    holder.rlMusicRoot.setVisibility(View.GONE);
                    if (post.getDesc().getImages() != null && post.getDesc().getImages().size() > 0) {
                        holder.llImagePack.setVisibility(View.VISIBLE);
                        holder.rlIcon1.setVisibility(View.VISIBLE);
                        holder.rlIcon1.setVisibility(View.VISIBLE);
                        if (FileUtil.isGif(post.getDesc().getImages().get(0).getPath())) {
                            holder.ivGifIcon1.setVisibility(View.VISIBLE);
                        } else {
                            holder.ivGifIcon1.setVisibility(View.GONE);
                        }
                        Glide.with(context)
                                .load(StringUtils.getUrl(context,ApiService.URL_QINIU + post.getDesc().getImages().get(0).getPath(), (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, false, true))
                                .override((DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3)
                                .placeholder(R.drawable.bg_default_square)
                                .error(R.drawable.bg_default_square)
                                .centerCrop()
                                .into(holder.ivIcon1);
                        if(post.getDesc().getImages().size() > 1){
                            holder.rlIcon2.setVisibility(View.VISIBLE);
                            if (FileUtil.isGif(post.getDesc().getImages().get(1).getPath())) {
                                holder.ivGifIcon2.setVisibility(View.VISIBLE);
                            } else {
                                holder.ivGifIcon2.setVisibility(View.GONE);
                            }
                            Glide.with(context)
                                    .load(StringUtils.getUrl(context,ApiService.URL_QINIU + post.getDesc().getImages().get(1).getPath(), (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, false, true))
                                    .override((DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3)
                                    .placeholder(R.drawable.bg_default_square)
                                    .error(R.drawable.bg_default_square)
                                    .centerCrop()
                                    .into(holder.ivIcon2);
                        }
                        // 是否显示第三张图
                        if(holder.rlSpecialTypePack.getVisibility() != View.VISIBLE && post.getDesc().getImages().size() > 2){
                            holder.rlIcon3.setVisibility(View.VISIBLE);
                            if (FileUtil.isGif(post.getDesc().getImages().get(2).getPath())) {
                                holder.ivGifIcon3.setVisibility(View.VISIBLE);
                            } else {
                                holder.ivGifIcon3.setVisibility(View.GONE);
                            }
                            Glide.with(context)
                                    .load(StringUtils.getUrl(context,ApiService.URL_QINIU + post.getDesc().getImages().get(2).getPath(), (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3,false,true))
                                    .override((DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3)
                                    .placeholder(R.drawable.bg_default_square)
                                    .error(R.drawable.bg_default_square)
                                    .centerCrop()
                                    .into(holder.ivIcon3);
                        }
                        // 是否显示  “共xx张图”
                        if(post.getDesc().getImages().size() > 3 || (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE && post.getDesc().getImages().size() > 2)){
                            holder.tvIconNum.setVisibility(View.VISIBLE);
                            holder.tvIconNum.setText(context.getString(R.string.label_post_icon_num, post.getDesc().getImages().size()));
                        }
                        //  }
                    }else if (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE){
                        // 没有图片，只有投票或者视频
                        holder.llImagePack.setVisibility(View.VISIBLE);
                    } else {
                        holder.llImagePack.setVisibility(View.GONE);
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

                for (int i = 1;i < holder.mainRoot.getChildCount();i++){
                    holder.mainRoot.removeViewAt(i);
                }
                for (int i = 0; i < post.getEggs(); i++){
                    int[] local;
                    if(holder.itemView.getWidth() > 0 && holder.itemView.getHeight() > 0){
                        local = getEggPosition(holder.itemView.getWidth() - 200, holder.itemView.getHeight() - 200);
                    }else {
                        local = getEggPosition(DensityUtil.getScreenWidth(context) - 200,  DensityUtil.dip2px(context, 150) - 200);
                    }
                    ImageView iv = new ImageView(context);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
                    layoutParams.topMargin = local[0] > 0 ? local[0] : 0;
                    layoutParams.leftMargin = local[1] > 0 ? local[1] : 0;
                    iv.setLayoutParams(layoutParams);
                    iv.setImageResource(R.drawable.ic_doclist_egg);
                    holder.mainRoot.addView(iv);
                }
            }
        }
    }

    private int[] getEggPosition(int r, int b){
        Random rand = new Random();
        int x = rand.nextInt(b + 1);
        int y = rand.nextInt(r + 1);
        return new int[]{x, y};
    }

    public Object getItem(int position){
        if(position == 0){
            return mBannerBeans.get(mBannerViewHolder.viewPager.getCurrentItem());
        }else if(position == 1){
            return "";
        }else {
            return mClassData.get(position - 2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type ;
        if (position == 0) {
            type = VIEW_TYPE_BANNER;
        } else if(position == 1){
            type = VIEW_TYPE_FEATURED;
        } else{
            type = VIEW_TYPE_DOC;
        }
        return type;
    }

    @Override
    public int getItemCount() {
        return mClassData.size() + 2;
    }

    class BannerViewHolder extends RecyclerView.ViewHolder{

        ViewPager viewPager;
        LinearLayout llPointGroup;
        View llPointContainer;
        View pressedPoint;
        View root;
        int width;

        BannerViewHolder(View itemView) {
            super(itemView);
            viewPager = (ViewPager) itemView.findViewById(R.id.pager_class_banner);
            llPointGroup = (LinearLayout) itemView.findViewById(R.id.ll_point_group);
            pressedPoint = itemView.findViewById(R.id.view_pressed_point);
            llPointContainer = itemView.findViewById(R.id.ll_point_container);
            root = itemView.findViewById(R.id.rl_root);
            if(scheduledExecutorService != null){
                scheduledExecutorService.shutdown();
            }
        }
    }

    class BannerAdapter extends PagerAdapter {

        private ArrayList<ImageView> images;

        BannerAdapter(ArrayList<ImageView> images){
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(images.get(position));
            return images.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private View.OnClickListener mAvatarListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String uuid = (String) v.getTag(R.id.id_creator_uuid);
            if (!TextUtils.isEmpty(uuid) && !uuid.equals(PreferenceUtils.getUUid())) {
                Intent intent = new Intent(context, NewPersonalActivity.class);
                intent.putExtra(BaseAppCompatActivity.UUID, uuid);
                context.startActivity(intent);
            }
        }
    };

    class LinearHViewHolder extends RecyclerView.ViewHolder{

        View root;
        RecyclerView mRvList;
        ClassRecyclerViewAdapter recyclerViewAdapter;

        LinearHViewHolder(View itemView) {
            super(itemView);
            mRvList = (RecyclerView) itemView.findViewById(R.id.rv_class_featured);
            root = itemView.findViewById(R.id.ll_root);
            recyclerViewAdapter = new ClassRecyclerViewAdapter(itemView.getContext());
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRvList.setLayoutManager(layoutManager);
            mRvList.setAdapter(recyclerViewAdapter);
        }
    }

    class DocViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.rl_post_root)
        RelativeLayout mainRoot;
        @BindView(R.id.iv_post_creator)
        ImageView ivCreatorAvatar;
        @BindView(R.id.tv_post_creator_name)
        TextView tvCreatorName;
        @BindView(R.id.iv_post_owner_flag)
        View ivClubCreatorFlag;
        @BindView(R.id.tv_post_update_time)
        TextView tvPostDate;
        @BindView(R.id.tv_post_title)
        TextView tvPostTitle;
        @BindView(R.id.tv_post_brief)
        TextView tvPostBrief;
        @BindView(R.id.ll_image_3)
        View llImagePack;
        @BindView(R.id.rl_post_special_flag)
        View rlSpecialTypePack;
        @BindView(R.id.rl_post_image_1)
        View rlIcon1;
        @BindView(R.id.rl_post_image_2)
        View rlIcon2;
        @BindView(R.id.rl_post_image_3)
        View rlIcon3;
        @BindView(R.id.iv_post_image_1_gif_flag)
        View ivGifIcon1;
        @BindView(R.id.iv_post_image_2_gif_flag)
        View ivGifIcon2;
        @BindView(R.id.iv_post_image_3_gif_flag)
        View ivGifIcon3;
        @BindView(R.id.iv_post_image_1)
        ImageView ivIcon1;
        @BindView(R.id.iv_post_image_2)
        ImageView ivIcon2;
        @BindView(R.id.iv_post_image_3)
        ImageView ivIcon3;
        @BindView(R.id.tv_post_img_num)
        TextView tvIconNum;
        @BindView(R.id.tv_post_comment_num)
        TextView tvCommentNum;
        @BindView(R.id.tv_post_pants_num)
        TextView tvPantsNum;
        @BindView(R.id.rl_music_root)
        View rlMusicRoot;
        @BindView(R.id.iv_item_image)
        ImageView musicImg;
        @BindView(R.id.tv_music_title)
        TextView musicTitle;
        @BindView(R.id.tv_level)
        TextView tvLevel;
        @BindView(R.id.rl_level_bg)
        View mLevelRoot;
        ImageView ivIconClassOffical;
        //标签
        @BindView(R.id.view_doc_sep)
        View vDocSep;
        @BindView(R.id.dv_doc_label_root)
        DocLabelView docLabel;
        @BindView(R.id.fl_huizhang_1)
        View rlHuiZhang1;
        @BindView(R.id.fl_huizhang_2)
        View rlHuiZhang2;
        @BindView(R.id.fl_huizhang_3)
        View rlHuiZhang3;
        @BindView(R.id.tv_huizhang_1)
        TextView tvHuiZhang1;
        @BindView(R.id.tv_huizhang_2)
        TextView tvHuiZhang2;
        @BindView(R.id.tv_huizhang_3)
        TextView tvHuiZhang3;
        View[] huiZhangRoots;
        TextView[] huiZhangTexts;
        NewDocLabelAdapter docLabelAdapter;

        DocViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            if (ivCreatorAvatar != null) {
                ivCreatorAvatar.setOnClickListener(mAvatarListener);
            }
            docLabelAdapter = new NewDocLabelAdapter(context,true);
            if(AppSetting.SUB_TAG)docLabel.setmMaxLines(3);
            ivIcon1.setOnClickListener(mIconListener);
            ivIcon2.setOnClickListener(mIconListener);
            ivIcon3.setOnClickListener(mIconListener);
            huiZhangRoots = new View[]{rlHuiZhang1,rlHuiZhang2,rlHuiZhang3};
            huiZhangTexts = new TextView[]{tvHuiZhang1,tvHuiZhang2,tvHuiZhang3};
        }

        private View.OnClickListener mIconListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int index = 0;
                if (v.equals(ivIcon1)){
                    index = 0;
                } else if (v.equals(ivIcon2)) {
                    index = 1;
                } else if (v.equals(ivIcon3)) {
                    index = 2;
                }else if(v.equals(ivIconClassOffical)){
                    index = 0;
                }

                Context context = v.getContext();
                final DocListEntity docBean = (DocListEntity) v.getTag(R.id.id_filebean);
                Intent intent = new Intent(context, ImageBigSelectActivity.class);
                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, docBean.getDesc().getImages());
                intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, index);
                // 以后可选择 有返回数据
                context.startActivity(intent);

            }
        };

    }

    public void onDestroy(){
        if(scheduledExecutorService != null) scheduledExecutorService.shutdown();
    }
}
