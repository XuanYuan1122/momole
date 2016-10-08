package com.moemoe.lalala.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.Utils;
import com.app.common.Callback;
import com.app.common.util.DensityUtil;
import com.app.http.request.UriRequest;
import com.app.image.ImageOptions;
import com.squareup.picasso.Picasso;
import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.ColumnDetailActivity;
import com.moemoe.lalala.ImageBigSelectActivity;
import com.moemoe.lalala.PersonSubscribeActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.CalendarDayItem;
import com.moemoe.lalala.data.CalendarDayType;
import com.moemoe.lalala.data.CalendarDayUiType;
import com.moemoe.lalala.data.MusicInfo;
import com.moemoe.lalala.music.MusicServiceManager;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.AnimationUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.IConstants;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.DocLabelView;
import com.moemoe.lalala.view.MyRoundedImageView;
import com.moemoe.lalala.view.NoDoubleClickListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Haru on 2016/3/2 0002.
 */
public class CalendarOneDayRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int TYPE_HIDE = -1;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private ArrayList<CalendarDayItem> mOneDayList;
    private AnimationUtil mRefreshAnim;
    private MusicServiceManager mServiceManager;
    private boolean hasTopBanner = false;
    private Map<Integer,DocLabelView.SimpleLabelAdapter> adapters;
    private String mSelectDay;
    private int mPreMusicPosition = -1;

    public CalendarOneDayRecyclerViewAdapter(Context context, MusicServiceManager msm){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mRefreshAnim = new AnimationUtil();
        mServiceManager = msm;
        adapters = new HashMap<>();
        mOneDayList = new ArrayList<>();
    }

    public void resetPreMusicPosition(){
         mPreMusicPosition = -1;
    }

    public void setSelectDay(String day){
        mSelectDay = day;
    }

    public void setData(ArrayList<CalendarDayItem> beans){
       // this.beans = beans;
        //mOneDayList.clear();
        //mServiceManager.clearMusicList();
        mOneDayList = beans;
        notifyDataSetChanged();
    }
    public boolean hasTopBanner(){
        return hasTopBanner;
    }

    public void startAnim(View view){
        mRefreshAnim.rotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,.5f,Animation.RELATIVE_TO_SELF,.5f)
            .setDuration(1000)
            .setLinearInterpolator()
        .setFillAfter(false)
        .startAnimation(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if(i == CalendarDayType.valueOf(CalendarDayType.BANNER_X55)){
            hasTopBanner = true;
            return new BannerViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type0,viewGroup,false));
        }else if(i == CalendarDayType.valueOf(CalendarDayType.BAR)){
            return new TopViewHolder(mLayoutInflater.inflate(R.layout.item_calender_item_top,viewGroup,false));
        }else if(i == CalendarDayType.valueOf(CalendarDayType.DOC_V_1)){
            return new LinearVViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type1_item,viewGroup,false));
        }else if(i== CalendarDayType.valueOf(CalendarDayType.DOC_G_2)){
            return new LinearVViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type2_item,viewGroup,false));
        }else if(i == CalendarDayType.valueOf(CalendarDayType.DOC_H_1)){
            return new LinearHViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type3,viewGroup,false));
        }else if(i == CalendarDayType.valueOf(CalendarDayType.DOC_V_2)){
            return new NewsViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type4_item,viewGroup,false));
        }else if(i == CalendarDayType.valueOf(CalendarDayType.RSS)){
            return new PersonHolder(mLayoutInflater.inflate(R.layout.item_calender_type5,viewGroup,false));
        }else if(i == TYPE_HIDE){
            return new TopViewHolder(mLayoutInflater.inflate(R.layout.item_calender_item_top_hide,viewGroup,false));
        }else if(i == CalendarDayType.valueOf(CalendarDayType.DOC_V_3)){
            return new LinearVViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type2_item,viewGroup,false));
        }
        return new LinearVViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type1_item,viewGroup,false));
    }

    private void requestFresh(String day,final CalendarDayItem.CalendarDayBar bar, final int position){
        if(!NetworkUtils.isNetworkAvailable(mContext)){
            ToastUtil.showCenterToast(mContext,R.string.a_server_msg_connection);
            return;
        }
        Otaku.getCalendarV2().refreshUi(PreferenceManager.getInstance(mContext).getToken(), day, bar.id, bar.refreshPosition, bar.pageSize).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                ArrayList<CalendarDayItem> items = CalendarDayItem.readFromJsonList(mContext, s);
                if (items.size() > 0) {
                    CalendarDayItem item = items.get(0);
                    if (item.type.equals(CalendarDayType.DOC_H_1.value)) {
                        int size = ((CalendarDayItem.CalendarDayDocH1) item.data).docList.size();
                        if (size < bar.pageSize) {
                            bar.refreshPosition = 0;
                        } else {
                            bar.refreshPosition += size;
                        }
                        bar.curIndex = size;
                        mOneDayList.remove(position + 1);
                        mOneDayList.add(position + 1, item);
                        notifyItemChanged(position + 1);
                    } else {
                        if (items.size() < bar.pageSize) {
                            bar.refreshPosition = 0;
                        } else {
                            bar.refreshPosition += items.size();
                        }
                        if (items.size() <= bar.curIndex) {
                            int n = -1;
                            for (int i = 0; i < bar.curIndex; i++) {
                                if (i < items.size()) {
                                    CalendarDayItem dayItem = items.get(i);
                                    mOneDayList.remove(position + 1 + i);
                                    mOneDayList.add(position + 1 + i, dayItem);
                                } else {
                                    if (n == -1) {
                                        n = i;
                                    }
                                    mOneDayList.remove(position + 1 + n);
                                }
                            }
                            notifyItemRangeChanged(position + 1, items.size());
                            notifyItemRangeRemoved(position + 1 + items.size(), bar.curIndex - items.size());
                        } else {
                            for (int i = 0; i < items.size(); i++) {
                                CalendarDayItem dayItem = items.get(i);
                                if (i < bar.curIndex) {
                                    mOneDayList.remove(position + 1 + i);
                                    mOneDayList.add(position + 1 + i, dayItem);
                                } else {
                                    mOneDayList.add(position + 1 + i, dayItem);
                                }
                            }
                            notifyItemRangeChanged(position + 1, bar.curIndex);
                            notifyItemRangeInserted(position + 1 + bar.curIndex, items.size() - bar.curIndex);
                        }
                        bar.curIndex = items.size();
                    }
                } else {
                    bar.refreshPosition = 0;
                }
            }

            @Override
            public void failure(String e) {

            }
        }));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CalendarDayItem.CalendarData bean = getItem(position);
        if(holder instanceof BannerViewHolder){
            CalendarDayItem.CalendarDayBanner banner = (CalendarDayItem.CalendarDayBanner) bean;
            BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
            bannerViewHolder.mViewPager.setAdapter(new BannerAdapter(banner.items));
        } else if(holder instanceof TopViewHolder){
            final CalendarDayItem.CalendarDayBar bar = (CalendarDayItem.CalendarDayBar) bean;
            final TopViewHolder topViewHolder = (TopViewHolder) holder;
            Picasso.with(mContext)
                    .load(StringUtils.getUrl(mContext,bar.icon.path,bar.icon.w,bar.icon.h,false,false))
                    .resize(0, DensityUtil.dip2px(27))
                    .placeholder(R.drawable.ic_default_title)
                    .error(R.drawable.ic_default_title)
                    .into(topViewHolder.mIvTitle);
            Object o = getItem(position + 1);
            if(o instanceof CalendarDayItem.CalendarDayDocH1){
                CalendarDayItem.CalendarDayDocH1 h1 = (CalendarDayItem.CalendarDayDocH1) o;
                bar.curIndex = bar.refreshPosition = h1.docList.size();
            }
            topViewHolder.mIvTitle.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(NetworkUtils.isNetworkAvailable(mContext)){
                        Intent i = new Intent(mContext, ColumnDetailActivity.class);
                        i.putExtra(ColumnDetailActivity.EXTRA_KEY_UUID, bar.id);
                        i.putExtra(ColumnDetailActivity.EXTRA_TITLE, bar.title);
                        mContext.startActivity(i);
                    }else {
                        ToastUtil.showCenterToast(mContext,R.string.a_server_msg_connection);
                    }

                }
            });
            topViewHolder.mTvTitle.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(NetworkUtils.isNetworkAvailable(mContext)){
                        Intent i = new Intent(mContext, ColumnDetailActivity.class);
                        i.putExtra(ColumnDetailActivity.EXTRA_KEY_UUID, bar.id);
                        i.putExtra(ColumnDetailActivity.EXTRA_TITLE, bar.title);
                        mContext.startActivity(i);
                    }else {
                        ToastUtil.showCenterToast(mContext,R.string.a_server_msg_connection);
                    }

                }
            });
            topViewHolder.mTvTitle.setText(bar.title);

            topViewHolder.mRefreshName.setText(bar.refreshName);
            if(bar.refresh){
                topViewHolder.mRefresh.setVisibility(View.VISIBLE);
            }else{
                topViewHolder.mRefresh.setVisibility(View.GONE);
            }
            if(bar.titleVisible){
                topViewHolder.mIvTitle.setVisibility(View.VISIBLE);

            }else {
                topViewHolder.mIvTitle.setVisibility(View.GONE);
                topViewHolder.mTvTitle.setText("");
            }

            topViewHolder.mRefresh.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    startAnim(topViewHolder.mIvRefresh);
                    requestFresh(mSelectDay,bar,position);
                }
            });
        }else if(holder instanceof LinearVViewHolder){
            final CalendarDayItem.CalendarDoc doc = (CalendarDayItem.CalendarDoc) bean;
            final LinearVViewHolder linearVViewHolder = (LinearVViewHolder) holder;
            if(doc.mark != null && !doc.mark.equals("")){
                linearVViewHolder.mTvTag.setVisibility(View.VISIBLE);
                linearVViewHolder.mTvTag.setText(doc.mark);
            }else{
                linearVViewHolder.mTvTag.setVisibility(View.GONE);
            }
            if(getItemViewType(position) == CalendarDayType.valueOf(CalendarDayType.DOC_V_1)){
                Picasso.with(mContext)
                        .load(StringUtils.getUrl(mContext, doc.icon.path,DensityUtil.dip2px(90),DensityUtil.dip2px(90),false,true))
                        .resize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                        .placeholder(R.drawable.ic_default_avatar_l)
                        .error(R.drawable.ic_default_avatar_l)
                        .into(linearVViewHolder.mIvTitle);
            }else {
                Picasso.with(mContext)
                        .load(StringUtils.getUrl(mContext, doc.icon.path,DensityUtil.dip2px(173), DensityUtil.dip2px(110),false,true))
                        .resize(DensityUtil.dip2px(173), DensityUtil.dip2px(110))
                        .placeholder(R.drawable.ic_default_video)
                        .error(R.drawable.ic_default_video)
                        .into(linearVViewHolder.mIvTitle);
            }

            TextPaint tp = linearVViewHolder.mTvTitle.getPaint();
            int style = CalendarDayUiType.getType(doc.ui);
            linearVViewHolder.mRlDocLikePack.setVisibility(View.VISIBLE);
            linearVViewHolder.mRlDocCommentPack.setVisibility(View.VISIBLE);
            linearVViewHolder.mTvLikeNum.setText(StringUtils.getNumberInLengthLimit(doc.likes, 2));
            linearVViewHolder.mTvCommentNum.setText(StringUtils.getNumberInLengthLimit(doc.comments, 2));
            if(style == CalendarDayUiType.valueOf(CalendarDayUiType.NEWS)){
                tp.setFakeBoldText(true);
                linearVViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                if(linearVViewHolder.mDlView != null) {
                    linearVViewHolder.mDlView.setVisibility(View.GONE);
                }
                linearVViewHolder.mSubtitle.setText(doc.content);
            }else{
                tp.setFakeBoldText(false);
                linearVViewHolder.mSubtitle.setVisibility(View.GONE);
                if(linearVViewHolder.mDlView != null){
                    linearVViewHolder.mDlView.setVisibility(View.GONE);
                }
            }
            if(style == CalendarDayUiType.valueOf(CalendarDayUiType.MUSIC)){
                linearVViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                linearVViewHolder.mSubtitle.setText(doc.content);
                MusicInfo musicInfo = mServiceManager.findMusicInfoByUrl(doc.musicUrl);
                if(musicInfo == null){
                    musicInfo = new MusicInfo();
                    musicInfo.musicName = doc.musicName;
                    musicInfo.position = position;
                    musicInfo.url = doc.musicUrl;
                    musicInfo.img = doc.icon.path;
                    mServiceManager.addMusicInfo(musicInfo);
                }else {
                    musicInfo.position = position;
                }
                final MusicInfo cur = musicInfo;
                linearVViewHolder.mIvVideo.setVisibility(View.VISIBLE);
                if(cur.playState == IConstants.MPS_PLAYING || cur.playState == IConstants.MPS_PREPARE){
                    linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_stop);
                }else{
                    linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_play);
                }
                linearVViewHolder.mIvVideo.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(cur.playState == IConstants.MPS_PLAYING || cur.playState == IConstants.MPS_PREPARE){
                            mServiceManager.pause();
                            linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_play);
                        }else{
                            mServiceManager.playByUrl(doc.musicUrl);
                            linearVViewHolder.mIvVideo.setImageResource(R.drawable.icon_video_stop);
                        }
                        if(position != mPreMusicPosition){
                            if(mPreMusicPosition != -1){
                                CalendarDayItem.CalendarDoc doc1 = (CalendarDayItem.CalendarDoc) getItem(mPreMusicPosition);
                                MusicInfo musicInfo = mServiceManager.findMusicInfoByUrl(doc1.musicUrl);
                                musicInfo.playState = IConstants.MPS_NOFILE;
                                notifyItemChanged(mPreMusicPosition);
                            }
                            mPreMusicPosition = position;
                        }
                    }
                });
            }else{
                if(linearVViewHolder.mIvVideo != null){
                    linearVViewHolder.mIvVideo.setVisibility(View.GONE);
                }
            }
            linearVViewHolder.mTvTitle.setText(doc.title);
            linearVViewHolder.mTvTime.setText(StringUtils.timeFormate(doc.updateTime));
            if (linearVViewHolder.mTvName != null){
                linearVViewHolder.mTvName.setText(doc.userName);
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
        }else if(holder instanceof LinearHViewHolder){
            final CalendarDayItem.CalendarDayDocH1 docH1 = (CalendarDayItem.CalendarDayDocH1) bean;
            LinearHViewHolder linearHViewHolder = (LinearHViewHolder) holder;
            linearHViewHolder.recyclerViewAdapter.setData(docH1.docList);
            linearHViewHolder.recyclerViewAdapter.setOnItemClickListener(new CalendarRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    CalendarDayItem.CalendarDoc docBean = docH1.docList.get(position);
                    if(!TextUtils.isEmpty(docBean.id)){
                        Uri uri = Uri.parse(docBean.id);
                        IntentUtils.toActivityFromUri(mContext,uri,view);
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
        }else if(holder instanceof NewsViewHolder){
            final CalendarDayItem.CalendarDoc doc = (CalendarDayItem.CalendarDoc) bean;
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.mTvTitle.setText(doc.title);
            newsViewHolder.mTvName.setText(doc.userName);
            newsViewHolder.mTvTime.setText(StringUtils.timeFormate(doc.updateTime));
            newsViewHolder.mTvSubtitle.setText(doc.content);
            newsViewHolder.mTvIconNum.setVisibility(View.GONE);
            newsViewHolder.mIvIcon1.setTag(R.id.id_filebean, doc);
            newsViewHolder.mIvIcon2.setTag(R.id.id_filebean, doc);
            newsViewHolder. mIvIcon3.setTag(R.id.id_filebean, doc);
            newsViewHolder.mRlDocLikePack.setVisibility(View.VISIBLE);
            newsViewHolder.mRlDocCommentPack.setVisibility(View.VISIBLE);
            newsViewHolder.mTvLikeNum.setText(StringUtils.getNumberInLengthLimit(doc.likes, 2));
            newsViewHolder.mTvCommentNum.setText(StringUtils.getNumberInLengthLimit(doc.comments, 2));
            if(doc.images.size() > 0){
                newsViewHolder.mLlImagePack.setVisibility(View.VISIBLE);
                newsViewHolder.mRlIcon2.setVisibility(View.INVISIBLE);
                newsViewHolder.mRlIcon3.setVisibility(View.INVISIBLE);
                if(FileUtil.isGif(doc.images.get(0).path)){
                    newsViewHolder.mIvGifIcon1.setVisibility(View.VISIBLE);
                }else {
                    newsViewHolder.mIvGifIcon1.setVisibility(View.GONE);
                }
                Picasso.with(mContext)
                        .load(StringUtils.getUrl(mContext,doc.images.get(0).path,(DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3,(DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3,false,true))
                        .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                        .placeholder(R.drawable.ic_default_club_l)
                        .error(R.drawable.ic_default_club_l)
                        .into(newsViewHolder.mIvIcon1);
                newsViewHolder.mIvGifIcon1.setVisibility(View.GONE);
                if(doc.images.size() > 1){
                    newsViewHolder.mRlIcon2.setVisibility(View.VISIBLE);
                    if(FileUtil.isGif(doc.images.get(1).path)){
                        newsViewHolder.mIvGifIcon2.setVisibility(View.VISIBLE);
                    }else {
                        newsViewHolder.mIvGifIcon2.setVisibility(View.GONE);
                    }
                    Picasso.with(mContext)
                            .load(StringUtils.getUrl(mContext,doc.images.get(1).path,(DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3,(DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3,false,true))
                            .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                            .placeholder(R.drawable.ic_default_club_l)
                            .error(R.drawable.ic_default_club_l)
                            .into(newsViewHolder.mIvIcon2);
                }
                if(doc.images.size() > 2){
                    newsViewHolder.mRlIcon3.setVisibility(View.VISIBLE);
                    if(FileUtil.isGif(doc.images.get(2).path)){
                        newsViewHolder.mIvGifIcon3.setVisibility(View.VISIBLE);
                    }else {
                        newsViewHolder.mIvGifIcon3.setVisibility(View.GONE);
                    }
                    Picasso.with(mContext)
                            .load(StringUtils.getUrl(mContext,doc.images.get(2).path,(DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3,(DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3,false,true))
                            .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                            .placeholder(R.drawable.ic_default_club_l)
                            .error(R.drawable.ic_default_club_l)
                            .into(newsViewHolder.mIvIcon3);
                }
                if(doc.images.size() > 3){
                    newsViewHolder.mTvIconNum.setVisibility(View.VISIBLE);
                    newsViewHolder.mTvIconNum.setText(mContext.getString(R.string.label_post_icon_num, doc.images.size()));
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
        }else if(holder instanceof PersonHolder){
            final CalendarDayItem.CalendarDayRss rss = (CalendarDayItem.CalendarDayRss) bean;
            final PersonHolder personHolder = (PersonHolder) holder;
            personHolder.mAdapter.setDate(rss.rssInstances, mContext);
            setListViewHeightBasedOnChildren(personHolder.mLvPerson);
            personHolder.mLvPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 5){
                        PersonSubscribeActivity.startActivity(mContext, mSelectDay, rss.total);
                    }else{
                        if(!TextUtils.isEmpty((rss.rssInstances.get(position).target))){
                            Uri uri = Uri.parse(rss.rssInstances.get(position).target);
                            IntentUtils.toActivityFromUri(mContext,uri,view);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mOneDayList.size();
    }

    public  CalendarDayItem.CalendarData getItem(int position){
        return mOneDayList.get(position).data;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder{

        ViewPager mViewPager;

        public BannerViewHolder(View itemView) {
            super(itemView);
            mViewPager = (ViewPager) itemView.findViewById(R.id.vp_type0);
        }
    }

    public static class TopViewHolder extends RecyclerView.ViewHolder{

        ImageView mIvTitle;
        TextView mTvTitle;
        View mRefresh;
        ImageView mIvRefresh;
        TextView mRefreshName;

        public TopViewHolder(View itemView) {
            super(itemView);
            mIvTitle = (ImageView) itemView.findViewById(R.id.iv_calender_item_top_image);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_calender_item_top_title);
            mRefresh = itemView.findViewById(R.id.ll_calender_item_top_refresh);
            mIvRefresh = (ImageView) itemView.findViewById(R.id.iv_calender_item_refresh);
            mRefreshName = (TextView) itemView.findViewById(R.id.tv_refresh_name);

        }
    }

    public static class LinearVViewHolder extends RecyclerView.ViewHolder{

        TextView mTvTag;
        MyRoundedImageView mIvTitle;
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

        public LinearVViewHolder(View itemView) {
            super(itemView);
            mTvTag = (TextView) itemView.findViewById(R.id.tv_tag);
            mIvTitle = (MyRoundedImageView) itemView.findViewById(R.id.iv_item_image);
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
        }
    }

    public static class LinearHViewHolder extends RecyclerView.ViewHolder{

        RecyclerView mRvList;
        CalendarRecyclerViewAdapter recyclerViewAdapter;

        public LinearHViewHolder(View itemView) {
            super(itemView);
            mRvList = (RecyclerView) itemView.findViewById(R.id.rv_calender_type3);
            recyclerViewAdapter = new CalendarRecyclerViewAdapter(itemView.getContext());
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRvList.setLayoutManager(layoutManager);
            mRvList.setAdapter(recyclerViewAdapter);
        }
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder{

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

        public NewsViewHolder(View itemView) {
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
                final CalendarDayItem.CalendarDoc docBean = (CalendarDayItem.CalendarDoc) v.getTag(R.id.id_filebean);
                Intent intent = new Intent(context, ImageBigSelectActivity.class);
                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, docBean.images);
                intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, index);
                // 以后可选择 有返回数据
                context.startActivity(intent);

            }
        };
    }

    public static class PersonHolder extends RecyclerView.ViewHolder{

        ListView mLvPerson;
        PersonAdapter mAdapter;

        public PersonHolder(View itemView) {
            super(itemView);
            mLvPerson = (ListView) itemView.findViewById(R.id.lv_timetable);
            mAdapter = new PersonAdapter();
            mLvPerson.setAdapter(mAdapter);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int i = CalendarDayType.getType(mOneDayList.get(position).type);
        if(i == CalendarDayType.valueOf(CalendarDayType.BAR)){
            CalendarDayItem.CalendarDayBar bar = (CalendarDayItem.CalendarDayBar)getItem(position);
            if(!bar.titleVisible && !bar.refresh){
                return TYPE_HIDE;
            }
        }
        return i;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
             return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public static class PersonAdapter extends BaseAdapter{

        private static int TYPE_NORMAL = 1;
        private static int TYPE_MORE = 2;

        private  ArrayList<CalendarDayItem.RssInstance> rssItems;
        private Context context;

        public PersonAdapter(){
            rssItems = new ArrayList<>();
        }

        public void setDate( ArrayList<CalendarDayItem.RssInstance> rssItems,Context context){
            this.rssItems.clear();
            this.rssItems.addAll(rssItems);
            this.context = context;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if(rssItems != null) {
                if(rssItems.size() > 5){
                    return 6;
                }else {
                    return rssItems.size();
                }
            }else{
                return 0;
            }
        }

        @Override
        public int getViewTypeCount() {
            if(rssItems != null){
                if(rssItems.size() > 5){
                    return 2;
                }else {
                    return 1;
                }
            }
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 5){
                return TYPE_MORE;
            }else{
                return TYPE_NORMAL;
            }
        }

        @Override
        public CalendarDayItem.RssInstance getItem(int position) {
            return rssItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            NormalHolder holder = null;
            if(convertView == null){
                if(type == TYPE_NORMAL){
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_calender_type5_item,
                            null);
                    holder = new NormalHolder();
                    holder.ivState = (ImageView) convertView.findViewById(R.id.iv_state);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                    holder.tvEventStatus = (TextView) convertView.findViewById(R.id.tv_event_status);
                    holder.tvChapter = (TextView) convertView.findViewById(R.id.tv_event_chapter);
                    convertView.setTag(holder);
                }else if(type == TYPE_MORE){
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_calender_type5_item2,
                            null);
                }
            }
            if(type == TYPE_NORMAL){
                holder = (NormalHolder) convertView.getTag();
                CalendarDayItem.RssInstance rss = getItem(position);
                if(!rss.unread){
                    holder.ivState.setImageResource(R.drawable.icon_timetable_subscibe_hook);
                }else {
                    holder.ivState.setImageResource(R.drawable.icon_timetable_subscibe_circle);
                }
                holder.tvTitle.setText(rss.title);
                if(rss.tip != null && !rss.tip.equals("")){
                    holder.tvEventStatus.setVisibility(View.VISIBLE);
                    holder.tvChapter.setVisibility(View.VISIBLE);
                    holder.tvChapter.setText(rss.tip);
                }else {
                    holder.tvEventStatus.setVisibility(View.GONE);
                    holder.tvChapter.setVisibility(View.GONE);
                }
            }
            return convertView;
        }

        class NormalHolder {
            ImageView ivState;
            TextView tvTitle;
            TextView tvEventStatus;
            TextView tvChapter;
        }
    }

    class BannerAdapter extends PagerAdapter {

        private  ArrayList<CalendarDayItem.CalendarDayBannerItem> images;

        public BannerAdapter(ArrayList<CalendarDayItem.CalendarDayBannerItem> images){
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
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(mContext);
//            Utils.image().bind(imageView, images.get(position).image.real_path,
//                    new ImageOptions.Builder()
//                            .setSize(DensityUtil.getScreenWidth(), DensityUtil.dip2px(75))
//                            .setCrop(true)
//                            .setImageScaleType(ImageView.ScaleType.FIT_CENTER)
//                            .setLoadingDrawableId(R.drawable.ic_default_calendar_banner)
//                            .setFailureDrawableId(R.drawable.ic_default_calendar_banner)
//                            .build()
//            );
            Picasso.with(mContext)
                    .load(images.get(position).image.real_path)
                    .fit()
                    //.resize(DensityUtil.getScreenWidth(), DensityUtil.dip2px(75))
                    .placeholder(R.drawable.ic_default_calendar_banner)
                    .error(R.drawable.ic_default_calendar_banner)
                    .into(imageView);
            imageView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(!TextUtils.isEmpty(images.get(position).targetId)){
                        Uri uri = Uri.parse(images.get(position).targetId);
                        IntentUtils.toActivityFromUri(mContext, uri,v);
                    }
                }
            });
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
