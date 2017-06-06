package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
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
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.CalendarDayType;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.widget.view.DocLabelView;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yi on 2016/11/30.
 */

public class DepartmentListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int VIEW_TYPE_BANNER = 0;
    private final int VIEW_TYPE_FEATURED = 1;

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<BannerEntity> mBannerBeans;
    private ArrayList<FeaturedEntity> mFeaturedBeans ;
    private OnItemClickListener mOnItemClickListener;
    private ArrayList<DepartmentEntity.DepartmentDoc> mDepartmentList ;
    private int[] mTagId = {R.drawable.btn_class_from_orange,R.drawable.btn_class_from_blue,
            R.drawable.btn_class_from_green,R.drawable.btn_class_from_pink,R.drawable.btn_class_from_yellow};
    private BannerViewHolder mBannerViewHolder;
    private ScheduledExecutorService scheduledExecutorService;
    private int mCurrentItem = 0;

    public DepartmentListAdapter(Context context){
        this.context = context;
        mInflater = LayoutInflater.from(context);
        mBannerBeans = new ArrayList<>();
        mFeaturedBeans = new ArrayList<>();
        mDepartmentList = new ArrayList<>();
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

    public void setDocList(ArrayList<DepartmentEntity.DepartmentDoc> departmentList){
        int bfSize = mDepartmentList.size();
        mDepartmentList.clear();
        mDepartmentList.addAll(departmentList);
        int afSize = mDepartmentList.size();
        if(bfSize == 0){
            notifyItemRangeInserted(2,mDepartmentList.size());
        }else {
            notifyItemRangeChanged(2,mDepartmentList.size());
            if(bfSize - afSize > 0){
                notifyItemRangeRemoved(afSize + 2,bfSize - afSize);
            }
        }
    }

    public void addDocList(ArrayList<DepartmentEntity.DepartmentDoc> departmentList){
        int bfSize = mDepartmentList.size();
        mDepartmentList.addAll(departmentList);
        int afSize = mDepartmentList.size();
        notifyItemRangeInserted(bfSize + 2,afSize - bfSize);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_BANNER){
            return new BannerViewHolder(mInflater.inflate(R.layout.item_banner_class,parent,false));
        }else if(viewType == VIEW_TYPE_FEATURED){
            return new FeaturedLinearHViewHolder(mInflater.inflate(R.layout.item_class_featured,parent,false));
        }else if(viewType == CalendarDayType.valueOf(CalendarDayType.DOC_V_1)){
            return new LinearVViewHolder(mInflater.inflate(R.layout.item_calender_type1_item,parent,false));
        }else if(viewType== CalendarDayType.valueOf(CalendarDayType.DOC_G_2)){
            return new LinearVViewHolder(mInflater.inflate(R.layout.item_calender_type2_item,parent,false));
        }else if(viewType == CalendarDayType.valueOf(CalendarDayType.DOC_H_1)){
            return new LinearVViewHolder(mInflater.inflate(R.layout.item_calender_type1_item,parent,false));
        }else if(viewType == CalendarDayType.valueOf(CalendarDayType.DOC_V_2)){
            return new NewsViewHolder(mInflater.inflate(R.layout.item_calender_type4_item,parent,false));
        }else if(viewType == CalendarDayType.valueOf(CalendarDayType.DOC_V_3)){
            return new LinearVViewHolder(mInflater.inflate(R.layout.item_calender_type2_item,parent,false));
        }
        return new LinearVViewHolder(mInflater.inflate(R.layout.item_calender_type1_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if(viewHolder instanceof FeaturedLinearHViewHolder){
            FeaturedLinearHViewHolder linearHViewHolder = (FeaturedLinearHViewHolder) viewHolder;
            if(mFeaturedBeans.size() == 0){
                ViewGroup.LayoutParams params =  linearHViewHolder.mRvList.getLayoutParams();
                params.height = 0;
                linearHViewHolder.mRvList.setLayoutParams(params);
                return;
            }else {
                ViewGroup.LayoutParams params =  linearHViewHolder.mRvList.getLayoutParams();
                params.height = DensityUtil.dip2px(context,110);
                linearHViewHolder.mRvList.setLayoutParams(params);
            }
            linearHViewHolder.mRvList.setBackgroundColor(Color.WHITE);
            linearHViewHolder.recyclerViewAdapter.setData(mFeaturedBeans);
            linearHViewHolder.recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    FeaturedEntity docBean = mFeaturedBeans.get(position);
                    if(!TextUtils.isEmpty(docBean.getSchema())){
                        Uri uri = Uri.parse(docBean.getSchema());
                        IntentUtils.toActivityFromUri(context, uri,view);
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
        }else if(viewHolder instanceof BannerViewHolder){
            mBannerViewHolder = (BannerViewHolder) viewHolder;
            if(mBannerBeans.size() == 0){
                ViewGroup.LayoutParams params =  mBannerViewHolder.viewPager.getLayoutParams();
                params.height = 0;
                mBannerViewHolder.viewPager.setLayoutParams(params);
                return;
            }else {
                ViewGroup.LayoutParams params =  mBannerViewHolder.viewPager.getLayoutParams();
                params.height = DensityUtil.dip2px(context,144);
                mBannerViewHolder.viewPager.setLayoutParams(params);
                startBanner();
            }
            ArrayList<ImageView> imageViews = new ArrayList<>();
            mBannerViewHolder.llPointGroup.removeAllViews();
            for(int i=0;i<mBannerBeans.size();i++) {
                final BannerEntity bean = mBannerBeans.get(i);
                ImageView image = new ImageView(context);
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(context)
                        .load(StringUtils.getUrl(context, ApiService.URL_QINIU + bean.getBg().getPath(), DensityUtil.getScreenWidth(context), DensityUtil.dip2px(context,150), false, false))
                        .override(DensityUtil.getScreenWidth(context), DensityUtil.dip2px(context,150))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .centerCrop()
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
        }else if(viewHolder instanceof LinearVViewHolder){
            Object beanTmp = getItem(position);
            final DepartmentEntity.DepartmentDoc docBean = (DepartmentEntity.DepartmentDoc) beanTmp;
            final LinearVViewHolder linearVViewHolder = (LinearVViewHolder) viewHolder;
            if(docBean.getMark() != null && !docBean.getMark().equals("")){
                linearVViewHolder.mTvTag.setVisibility(View.VISIBLE);
                linearVViewHolder.mTvTag.setText(docBean.getMark());
            }else{
                linearVViewHolder.mTvTag.setVisibility(View.GONE);
            }
            if(getItemViewType(position) == CalendarDayType.valueOf(CalendarDayType.DOC_V_1)){
                Glide.with(context)
                        .load(StringUtils.getUrl(context,ApiService.URL_QINIU + docBean.getIcon().getPath(), DensityUtil.dip2px(context,90), DensityUtil.dip2px(context,90), false, true))
                        .override(DensityUtil.dip2px(context,90), DensityUtil.dip2px(context,90))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .transform(new GlideRoundTransform(context,5,0,0,5))
                        .centerCrop()
                        .into(linearVViewHolder.mIvTitle);
            }else {
                Glide.with(context)
                        .load(StringUtils.getUrl(context,ApiService.URL_QINIU + docBean.getIcon().getPath(), DensityUtil.getScreenWidth(context), DensityUtil.dip2px(context,150), false, true))
                        .override(DensityUtil.getScreenWidth(context), DensityUtil.dip2px(context,150))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .centerCrop()
                        .transform(new GlideRoundTransform(context,5,5,0,0))
                        .into(linearVViewHolder.mIvTitle);
            }

            TextPaint tp = linearVViewHolder.mTvTitle.getPaint();
            String uiTmp = docBean.getUi();
            String[] strs = uiTmp.split("#");
            String ui = strs[1];
            int style = CalendarDayUiType.getType(ui);
            if(!TextUtils.isEmpty(docBean.getUiTitle())){
                linearVViewHolder.mTvFromName.setVisibility(View.VISIBLE);
                linearVViewHolder.mTvFromName.setText(docBean.getUiTitle());
                linearVViewHolder.mTvFromName.setBackgroundResource(mTagId[StringUtils.getHashOfString(docBean.getUiTitle(), mTagId.length)]);
            }else {
                linearVViewHolder.mTvFromName.setVisibility(View.GONE);
            }
            linearVViewHolder.mRlDocLikePack.setVisibility(View.VISIBLE);
            linearVViewHolder.mRlDocCommentPack.setVisibility(View.VISIBLE);
            linearVViewHolder.mTvLikeNum.setText(StringUtils.getNumberInLengthLimit(docBean.getLikes(), 2));
            linearVViewHolder.mTvCommentNum.setText(StringUtils.getNumberInLengthLimit(docBean.getComments(), 2));
            if(style == CalendarDayUiType.valueOf(CalendarDayUiType.NEWS)){
                tp.setFakeBoldText(true);
                linearVViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                if(linearVViewHolder.mDlView != null) {
                    linearVViewHolder.mDlView.setVisibility(View.GONE);
                }
                linearVViewHolder.mSubtitle.setText(docBean.getContent());
            }else{
                tp.setFakeBoldText(false);
                linearVViewHolder.mSubtitle.setVisibility(View.GONE);
                if(linearVViewHolder.mDlView != null){
                    linearVViewHolder.mDlView.setVisibility(View.GONE);
                }
            }
            if(style == CalendarDayUiType.valueOf(CalendarDayUiType.MUSIC)){
                linearVViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                linearVViewHolder.mSubtitle.setText(docBean.getContent());
                linearVViewHolder.mIvVideo.setVisibility(View.VISIBLE);
                linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_play);
            }else{
                if(linearVViewHolder.mIvVideo != null){
                    linearVViewHolder.mIvVideo.setVisibility(View.GONE);
                }
            }
            linearVViewHolder.mTvTitle.setText(docBean.getTitle());
            linearVViewHolder.mTvTime.setText(StringUtils.timeFormate(docBean.getUpdateTime()));
            if (linearVViewHolder.mTvName != null){
                linearVViewHolder.mTvName.setText(docBean.getUsername());
            }
            linearVViewHolder.itemView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    int pos = linearVViewHolder.getLayoutPosition();
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(linearVViewHolder.itemView, pos);
                    }
                }
            });
            linearVViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = linearVViewHolder.getLayoutPosition();
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemLongClick(linearVViewHolder.itemView, pos);
                    }
                    return false;
                }
            });
        }else if(viewHolder instanceof NewsViewHolder){
            Object beanTmp = getItem(position);
            DepartmentEntity.DepartmentDoc doc = (DepartmentEntity.DepartmentDoc) beanTmp;
            final NewsViewHolder newsViewHolder = (NewsViewHolder) viewHolder;
            newsViewHolder.mTvTitle.setText(doc.getTitle());
            newsViewHolder.mTvName.setText(doc.getUsername());
            newsViewHolder.mTvTime.setText(StringUtils.timeFormate(doc.getUpdateTime()));
            newsViewHolder.mTvSubtitle.setText(doc.getContent());
            newsViewHolder.mTvIconNum.setVisibility(View.GONE);
            newsViewHolder.mIvIcon1.setTag(R.id.id_filebean, doc);
            newsViewHolder.mIvIcon2.setTag(R.id.id_filebean, doc);
            newsViewHolder. mIvIcon3.setTag(R.id.id_filebean, doc);
            newsViewHolder.mRlDocLikePack.setVisibility(View.VISIBLE);
            newsViewHolder.mRlDocCommentPack.setVisibility(View.VISIBLE);
            newsViewHolder.mTvLikeNum.setText(StringUtils.getNumberInLengthLimit(doc.getLikes(), 2));
            newsViewHolder.mTvCommentNum.setText(StringUtils.getNumberInLengthLimit(doc.getComments(), 2));
            if(!TextUtils.isEmpty(doc.getUiTitle())){
                newsViewHolder.mTvFromName.setVisibility(View.VISIBLE);
                newsViewHolder.mTvFromName.setText(doc.getUiTitle());
                newsViewHolder.mTvFromName.setBackgroundResource(mTagId[StringUtils.getHashOfString(doc.getUiTitle(), mTagId.length)]);
            }else {
                newsViewHolder.mTvFromName.setVisibility(View.GONE);
            }
            if(doc.getImages().size() > 0){
                newsViewHolder.mLlImagePack.setVisibility(View.VISIBLE);
                newsViewHolder.mRlIcon2.setVisibility(View.INVISIBLE);
                newsViewHolder.mRlIcon3.setVisibility(View.INVISIBLE);
                if(FileUtil.isGif(doc.getImages().get(0).getPath())){
                    newsViewHolder.mIvGifIcon1.setVisibility(View.VISIBLE);
                }else {
                    newsViewHolder.mIvGifIcon1.setVisibility(View.GONE);
                }
                Glide.with(context)
                        .load(StringUtils.getUrl(context,ApiService.URL_QINIU + doc.getImages().get(0).getPath(), (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, false, true))
                        .override((DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3)
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .centerCrop()
                        .into(newsViewHolder.mIvIcon1);
                newsViewHolder.mIvGifIcon1.setVisibility(View.GONE);
                if(doc.getImages().size() > 1){
                    newsViewHolder.mRlIcon2.setVisibility(View.VISIBLE);
                    if(FileUtil.isGif(doc.getImages().get(1).getPath())){
                        newsViewHolder.mIvGifIcon2.setVisibility(View.VISIBLE);
                    }else {
                        newsViewHolder.mIvGifIcon2.setVisibility(View.GONE);
                    }
                    Glide.with(context)
                            .load(StringUtils.getUrl(context,ApiService.URL_QINIU + doc.getImages().get(1).getPath(), (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, false, true))
                            .override((DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3)
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .centerCrop()
                            .into(newsViewHolder.mIvIcon2);
                }
                if(doc.getImages().size() > 2){
                    newsViewHolder.mRlIcon3.setVisibility(View.VISIBLE);
                    if(FileUtil.isGif(doc.getImages().get(2).getPath())){
                        newsViewHolder.mIvGifIcon3.setVisibility(View.VISIBLE);
                    }else {
                        newsViewHolder.mIvGifIcon3.setVisibility(View.GONE);
                    }
                    Glide.with(context)
                            .load(StringUtils.getUrl(context,ApiService.URL_QINIU + doc.getImages().get(2).getPath(),(DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3,(DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3,false,true))
                            .override((DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3, (DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,56)) / 3)
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .centerCrop()
                            .into(newsViewHolder.mIvIcon3);
                }
                if(doc.getImages().size() > 3){
                    newsViewHolder.mTvIconNum.setVisibility(View.VISIBLE);
                    newsViewHolder.mTvIconNum.setText(context.getString(R.string.label_post_icon_num, doc.getImages().size()));
                }
            }else{
                newsViewHolder.mLlImagePack.setVisibility(View.GONE);
            }
            newsViewHolder.itemView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    int pos = newsViewHolder.getLayoutPosition();
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(newsViewHolder.itemView, pos);
                    }
                }
            });
        }


    }

    public Object getItem(int position){
        if(position == 0){
            return mBannerBeans.get(mBannerViewHolder.viewPager.getCurrentItem());
        }else if(position == 1){
            return "";
        }else {
            return mDepartmentList.get(position - 2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type ;
        if (position == 0 ) {
            type = VIEW_TYPE_BANNER;
        } else if(position == 1){
            type = VIEW_TYPE_FEATURED;
        }else {
            String uiTmp = ((DepartmentEntity.DepartmentDoc)getItem(position)).getUi();
            String[] strs = uiTmp.split("#");
            String ui = strs[0];
            type = CalendarDayType.getType(ui);
        }
        return type;
    }

    @Override
    public int getItemCount() {
        return mDepartmentList.size() + 2;
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

    class FeaturedLinearHViewHolder extends RecyclerView.ViewHolder{

        View root;
        RecyclerView mRvList;
        ClassRecyclerViewAdapter recyclerViewAdapter;

        FeaturedLinearHViewHolder(View itemView) {
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

    class LinearVViewHolder extends RecyclerView.ViewHolder{

        TextView mTvTag;
        ImageView mIvTitle;
        ImageView mIvVideo;
        TextView mTvTitle;
        TextView mTvName;
        TextView mTvTime;
        TextView mSubtitle;
        View mRlDocLikePack;
        View mRlDocCommentPack;
        DocLabelView mDlView;
        TextView mTvLikeNum;
        TextView mTvCommentNum;
        TextView mTvFromName;

        LinearVViewHolder(View itemView) {
            super(itemView);
            mTvTag = (TextView) itemView.findViewById(R.id.tv_tag);
            mIvTitle = (ImageView) itemView.findViewById(R.id.iv_item_image);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvName = (TextView) itemView.findViewById(R.id.tv_creator_name);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mSubtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            mRlDocLikePack = itemView.findViewById(R.id.rl_doc_like_pack);
            mRlDocCommentPack = itemView.findViewById(R.id.rl_doc_comment_pack);
            mDlView = (DocLabelView) itemView.findViewById(R.id.dv_label_root);
            mIvVideo = (ImageView) itemView.findViewById(R.id.iv_video);
            mTvLikeNum = (TextView) itemView.findViewById(R.id.tv_post_pants_num);
            mTvCommentNum = (TextView) itemView.findViewById(R.id.tv_post_comment_num);
            mTvFromName = (TextView) itemView.findViewById(R.id.tv_post_from_name);
        }
    }

    class NewsViewHolder extends RecyclerView.ViewHolder{

        TextView mTvTitle;
        TextView mTvName;
        TextView mTvTime;
        TextView mTvSubtitle;
        View mLlImagePack;
        View mRlSpecialTypePack;
        ImageView mIvSpecialTypeIcon;
        TextView mTvVoteBrief;
        View mRlIcon1;
        View mRlIcon2;
        View mRlIcon3;
        View mIvGifIcon1;
        View mIvGifIcon2;
        View mIvGifIcon3;
        ImageView mIvIcon1;
        ImageView mIvIcon2;
        ImageView mIvIcon3;
        TextView mTvIconNum;
        View mRlDocLikePack;
        View mRlDocCommentPack;
        TextView mTvLikeNum;
        TextView mTvCommentNum;
        TextView mTvFromName;

        NewsViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvSubtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            mLlImagePack = itemView.findViewById(R.id.ll_image_3);
            mRlSpecialTypePack = itemView.findViewById(R.id.rl_post_special_flag);
            mIvSpecialTypeIcon = (ImageView) itemView.findViewById(R.id.iv_post_flag_icon);
            mTvVoteBrief = (TextView) itemView.findViewById(R.id.tv_post_vote_brief);
            mRlDocLikePack = itemView.findViewById(R.id.rl_doc_like_pack);
            mRlDocCommentPack = itemView.findViewById(R.id.rl_doc_comment_pack);
            mTvLikeNum = (TextView) itemView.findViewById(R.id.tv_post_pants_num);
            mTvCommentNum = (TextView) itemView.findViewById(R.id.tv_post_comment_num);
            mRlIcon1 = itemView.findViewById(R.id.rl_post_image_1);
            mRlIcon2 = itemView.findViewById(R.id.rl_post_image_2);
            mRlIcon3 = itemView.findViewById(R.id.rl_post_image_3);

            mIvGifIcon1 = itemView.findViewById(R.id.iv_post_image_1_gif_flag);
            mIvGifIcon2 = itemView.findViewById(R.id.iv_post_image_2_gif_flag);
            mIvGifIcon3 = itemView.findViewById(R.id.iv_post_image_3_gif_flag);

            mIvIcon1 = (ImageView)itemView.findViewById(R.id.iv_post_image_1);
            mIvIcon2 = (ImageView)itemView.findViewById(R.id.iv_post_image_2);
            mIvIcon3 = (ImageView)itemView.findViewById(R.id.iv_post_image_3);
            mTvIconNum = (TextView)itemView.findViewById(R.id.tv_post_img_num);
            mTvFromName = (TextView) itemView.findViewById(R.id.tv_post_from_name);
            mIvIcon1.setOnClickListener(mIconListener);
            mIvIcon2.setOnClickListener(mIconListener);
            mIvIcon3.setOnClickListener(mIconListener);
        }


        View.OnClickListener mIconListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int index = 0;
                if (v.equals(mIvIcon1)){
                    index = 0;
                } else if (v.equals(mIvIcon2)) {
                    index = 1;
                } else if (v.equals(mIvIcon3)) {
                    index = 2;
                }

                Context context = v.getContext();
                final DepartmentEntity.DepartmentDoc docBean = (DepartmentEntity.DepartmentDoc) v.getTag(R.id.id_filebean);
                Intent intent = new Intent(context, ImageBigSelectActivity.class);
                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, docBean.getImages());
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
