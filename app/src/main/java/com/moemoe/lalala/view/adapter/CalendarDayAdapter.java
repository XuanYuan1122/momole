package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CalendarDayEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.GlideCircleTransform;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.NewPersonalActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yi on 2016/12/1.
 */

public class CalendarDayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    
    private static final int TYPE_DAY = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_IMG = 2;
    private int[] mBackGround = { R.drawable.shape_rect_label_cyan, R.drawable.shape_rect_label_yellow, R.drawable.shape_rect_label_orange, R.drawable.shape_rect_label_pink, R.drawable.shape_rect_border_green_5, R.drawable.shape_rect_label_purple, R.drawable.shape_rect_label_tab_blue};
    private Context mContext;
    private ArrayList<Object> items;
    private LayoutInflater mInflater;
    private HashMap<Integer,CalendarDayEntity.Day> mDayMap;
    private OnItemClickListener onItemClickListener;

    public CalendarDayAdapter(Context context){
        mContext = context;
        items = new ArrayList<>();
        mDayMap = new HashMap<>();
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void addData(CalendarDayEntity bean,boolean setData){
        if(setData) {
            items.clear();
            mDayMap.clear();
        }
        mDayMap.put(items.size(),bean.getDay());
        items.add(bean.getDay());
        items.addAll(bean.getItems());
        notifyDataSetChanged();
    }

    public boolean isTop(int position){
        return mDayMap.containsKey(position);
    }

    public CalendarDayEntity.Day getDay(int position){
        return mDayMap.get(position);
    }

    public CalendarDayEntity.Day getLastDay(int position){
        for (int i = position - 1;i >= 0;i--){
            if(mDayMap.containsKey(i)){
                return mDayMap.get(i);
            }
        }
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_DAY){
            return new DayHolder(mInflater.inflate(R.layout.item_new_cal_time,parent,false));
        } else if(viewType == TYPE_NORMAL){
            return new NormalItemsHolder(mInflater.inflate(R.layout.item_new_cal_normal,parent,false));
        }else if(viewType == TYPE_IMG){
            return new ImgItemsHolder(mInflater.inflate(R.layout.item_new_cal_img,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Object item = getItem(position);
        if(holder instanceof DayHolder){
            createDay((DayHolder) holder, (CalendarDayEntity.Day) item);
        }else if (holder instanceof NormalItemsHolder){
            createNormalItems((NormalItemsHolder) holder, (CalendarDayEntity.Items) item);
        }else if(holder instanceof ImgItemsHolder){
            createImgItems((ImgItemsHolder) holder, (CalendarDayEntity.Items) item);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(view,position);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        Object o = getItem(position);
        if(o instanceof CalendarDayEntity.Day){
            return 0;
        }else if(o instanceof CalendarDayEntity.Items){
            String type = ((CalendarDayEntity.Items) o).getShowType();
            if(type.equals("PICTURE")){
                return 2;
            }else {
                return 1;
            }
        }
        return -1;
    }

    public Object getItem(int position){
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class DayHolder extends RecyclerView.ViewHolder{

        private ImageView ivBg;
        private TextView tvTime,tvWeek;

        DayHolder(View itemView) {
            super(itemView);
            ivBg = (ImageView) itemView.findViewById(R.id.iv_background);
            tvTime = (TextView) itemView.findViewById(R.id.tv_cal_time);
            tvWeek = (TextView) itemView.findViewById(R.id.tv_week);
        }
    }

    private void createDay(final DayHolder holder, CalendarDayEntity.Day day){
        holder.tvTime.setText(day.getDay());
        holder.tvWeek.setText(mContext.getResources().getString(R.string.label_cal_top,day.getWeek(),day.getSize(),day.getReadTime()));
        Glide.with(mContext)
                .load(StringUtils.getUrl(mContext, ApiService.URL_QINIU +  day.getBg().getPath(), DensityUtil.getScreenWidth(mContext), DensityUtil.dip2px(mContext,144), false, true))
                .override(DensityUtil.getScreenWidth(mContext), DensityUtil.dip2px(mContext,144))
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into(holder.ivBg);
    }

    private class NormalItemsHolder extends RecyclerView.ViewHolder{

        private ImageView ivCreator;
        private TextView tvName,tvUiName,tvTitle,tvContent,tvLikes,tvComments;
        private ImageView ivBg,ivCtrl;

        NormalItemsHolder(View itemView) {
            super(itemView);
            ivCreator = (ImageView) itemView.findViewById(R.id.iv_post_creator);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvUiName = (TextView) itemView.findViewById(R.id.tv_label);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvLikes = (TextView) itemView.findViewById(R.id.tv_post_pants_num);
            tvComments = (TextView) itemView.findViewById(R.id.tv_post_comment_num);
            ivBg = (ImageView) itemView.findViewById(R.id.iv_img);
            ivCtrl = (ImageView) itemView.findViewById(R.id.iv_music_ctrl);
        }
    }

    private void createNormalItems(NormalItemsHolder holder, final CalendarDayEntity.Items item){
        if(item.getShowType().equals("MUSIC")){
            holder.ivCtrl.setVisibility(View.VISIBLE);
        }else {
            holder.ivCtrl.setVisibility(View.GONE);
        }
        Glide.with(mContext)
                .load( StringUtils.getUrl(mContext, ApiService.URL_QINIU +  item.getUserIcon().getPath(), DensityUtil.dip2px(mContext,35), DensityUtil.dip2px(mContext,35), false, true))
                .override(DensityUtil.dip2px(mContext,35), DensityUtil.dip2px(mContext,35))
                .placeholder(R.drawable.bg_default_circle)
                .error(R.drawable.bg_default_circle)
                .transform(new GlideCircleTransform(mContext))
                .into(holder.ivCreator);
        holder.ivCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.getUserId().equals(PreferenceUtils.getUUid())){
                    Intent intent = new Intent(mContext, NewPersonalActivity.class);
                    intent.putExtra(BaseAppCompatActivity.UUID, item.getUserId());
                    mContext.startActivity(intent);
                }
            }
        });
        holder.tvName.setText(item.getUserName());
        holder.tvUiName.setText(item.getUiName());
        int index = StringUtils.getHashOfString(item.getUiName(), mBackGround.length);
        holder.tvUiName.setBackgroundResource(mBackGround[index]);
        holder.tvTitle.setText(item.getTitle());
        holder.tvContent.setText(item.getContent());
        holder.tvLikes.setText(StringUtils.getNumberInLengthLimit(item.getLikes(), 3));
        holder.tvComments.setText(StringUtils.getNumberInLengthLimit(item.getComments(), 3));
        Glide.with(mContext)
                .load( StringUtils.getUrl(mContext, ApiService.URL_QINIU +  item.getImage().getPath(), DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,20), DensityUtil.dip2px(mContext,200), false, true))
                .override(DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,20), DensityUtil.dip2px(mContext,200))
                .centerCrop()
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into(holder.ivBg);
    }

    private class ImgItemsHolder extends RecyclerView.ViewHolder{

        private ImageView ivCreator;
        private TextView tvName,tvUiName,tvImgNum,tvTitle,tvLikes,tvComments;
        private ImageView ivBg;

        ImgItemsHolder(View itemView) {
            super(itemView);
            ivCreator = (ImageView) itemView.findViewById(R.id.iv_post_creator);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvUiName = (TextView) itemView.findViewById(R.id.tv_label);
            tvImgNum = (TextView) itemView.findViewById(R.id.tv_img_num);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvLikes = (TextView) itemView.findViewById(R.id.tv_post_pants_num);
            tvComments = (TextView) itemView.findViewById(R.id.tv_post_comment_num);
            ivBg = (ImageView) itemView.findViewById(R.id.iv_img);
        }
    }

    private void createImgItems(ImgItemsHolder holder, CalendarDayEntity.Items item){
        Glide.with(mContext)
                .load( StringUtils.getUrl(mContext, ApiService.URL_QINIU +  item.getUserIcon().getPath(), DensityUtil.dip2px(mContext,35), DensityUtil.dip2px(mContext,35), false, true))
                .override(DensityUtil.dip2px(mContext,35), DensityUtil.dip2px(mContext,35))
                .placeholder(R.drawable.bg_default_circle)
                .error(R.drawable.bg_default_circle)
                .transform(new GlideCircleTransform(mContext))
                .into(holder.ivCreator);
        holder.tvName.setText(item.getUserName());
        holder.tvUiName.setText(item.getUiName());
        int index = StringUtils.getHashOfString(item.getUiName(), mBackGround.length);
        holder.tvTitle.setText(item.getTitle());
        holder.tvUiName.setBackgroundResource(mBackGround[index]);
        holder.tvLikes.setText(StringUtils.getNumberInLengthLimit(item.getLikes(), 3));
        holder.tvComments.setText(StringUtils.getNumberInLengthLimit(item.getComments(), 3));
        if (!TextUtils.isEmpty(item.getMark())){
            holder.tvImgNum.setVisibility(View.VISIBLE);
            holder.tvImgNum.setText(item.getMark());
        }else {
            holder.tvImgNum.setVisibility(View.GONE);
        }
        Glide.with(mContext)
                .load( StringUtils.getUrl(mContext, ApiService.URL_QINIU +  item.getImage().getPath(), DensityUtil.getScreenWidth(mContext), DensityUtil.dip2px(mContext,240), false, true))
                .override(DensityUtil.getScreenWidth(mContext), DensityUtil.dip2px(mContext,240))
                .centerCrop()
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into(holder.ivBg);
    }
}
