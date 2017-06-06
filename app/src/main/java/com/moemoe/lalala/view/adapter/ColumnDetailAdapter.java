package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CalendarDayItemEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.view.DocLabelView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yi on 2016/11/30.
 */

public class ColumnDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Object> mList;
    private Context context;
    int TYPE_SPLIT = 0 ;
    int TYPE_DOC = 1;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public ColumnDetailAdapter(Context context){
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        mList = new ArrayList<>();
    }

    public void setData(ArrayList<CalendarDayItemEntity> calendarDayItemEntities,boolean isFuture){
        int bfSize = mList.size();
        for (CalendarDayItemEntity calendarDayItem : calendarDayItemEntities){
            ArrayList<CalendarDayItemEntity.CalendarData> docList = calendarDayItem.getDocs();
            String day = calendarDayItem.getDay();
            if(!isFuture){
                deleteRepeat(docList,mList);
                if(!hasDay(mList,day)){
                    mList.add(day);
                    mList.addAll(docList);
                }else {
                    mList.addAll(docList);
                }
            }else {
                deleteRepeat(docList,mList);
                if(!hasDay(mList,day)){
                    mList.addAll(0,docList);
                    mList.add(0,day);
                }else {
                    String tempDay = (String)mList.get(0);
                    if(tempDay.equals(day)){
                        mList.add(1,docList);
                    }
                }
            }
        }
        int afSize = mList.size();
        if(!isFuture){
            notifyItemRangeInserted(bfSize,afSize - bfSize);
        }else {
            notifyItemRangeInserted(0,afSize - bfSize);
        }
    }

    private boolean hasDay(ArrayList<Object> list,String day){
        for(Object o : list){
            if(o instanceof String){
                String tmpDay = (String) o;
                if(tmpDay.equals(day)){
                    return true;
                }
            }
        }
        return false;
    }

    private void deleteRepeat(ArrayList<CalendarDayItemEntity.CalendarData> list,ArrayList<Object> docArrayList){
        ArrayList<CalendarDayItemEntity.CalendarData> temp = new ArrayList<>();
        for(CalendarDayItemEntity.CalendarData doc : list){
            for(Object o : docArrayList){
                if(o instanceof CalendarDayItemEntity.CalendarData){
                    CalendarDayItemEntity.CalendarData doc1 = (CalendarDayItemEntity.CalendarData) o;
                    if(doc1.getRefId().equals(doc.getRefId())){
                        temp.add(doc);
                    }
                }
            }
        }
        list.removeAll(temp);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_SPLIT){
            return new SplitViewHolder(mLayoutInflater.inflate(R.layout.item_split,parent,false));
        }else if(viewType == TYPE_DOC){
            return new LinearVViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type1_item,parent,false));
        }
        return new LinearVViewHolder(mLayoutInflater.inflate(R.layout.item_calender_type1_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        if(holder instanceof SplitViewHolder){
            SplitViewHolder viewHolder = (SplitViewHolder) holder;
            viewHolder.mTvSplit.setText((String) getItem(position));
        }else if(holder instanceof LinearVViewHolder){
            final CalendarDayItemEntity.CalendarData doc = (CalendarDayItemEntity.CalendarData) getItem(position);
            final LinearVViewHolder linearVViewHolder = (LinearVViewHolder) holder;
            if(doc.getMark() != null && !doc.getMark().equals("")){
                linearVViewHolder.mTvTag.setVisibility(View.VISIBLE);
                linearVViewHolder.mTvTag.setText(doc.getMark());
            }else{
                linearVViewHolder.mTvTag.setVisibility(View.GONE);
            }
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + doc.getIcon().getPath(), DensityUtil.dip2px(context,90),DensityUtil.dip2px(context,90),false,true))
                    .override(DensityUtil.dip2px(context,90), DensityUtil.dip2px(context,90))
                    .centerCrop()
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .transform(new GlideRoundTransform(context,5,0,0,5))
                    .into(linearVViewHolder.mIvTitle);
            TextPaint tp = linearVViewHolder.mTvTitle.getPaint();
            int style = CalendarDayUiType.getType(doc.getUi());
            linearVViewHolder.mRlDocLikePack.setVisibility(View.VISIBLE);
            linearVViewHolder.mRlDocCommentPack.setVisibility(View.VISIBLE);
            linearVViewHolder.mTvLikeNum.setText(StringUtils.getNumberInLengthLimit(doc.getLikes(), 2));
            linearVViewHolder.mTvCommentNum.setText(StringUtils.getNumberInLengthLimit(doc.getComments(), 2));
            if(style == CalendarDayUiType.valueOf(CalendarDayUiType.NEWS)){
                tp.setFakeBoldText(true);
                linearVViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                if(linearVViewHolder.mDlView != null) {
                    linearVViewHolder.mDlView.setVisibility(View.GONE);
                }
                linearVViewHolder.mSubtitle.setText(doc.getContent());
            }else{
                tp.setFakeBoldText(false);
                linearVViewHolder.mSubtitle.setVisibility(View.GONE);
                if(linearVViewHolder.mDlView != null) {
                    linearVViewHolder.mDlView.setVisibility(View.GONE);
                }
            }
            if(linearVViewHolder.mIvVideo != null){
                linearVViewHolder.mIvVideo.setVisibility(View.GONE);
            }
            linearVViewHolder.mTvTitle.setText(doc.getTitle());
            linearVViewHolder.mTvTime.setText(StringUtils.timeFormate(doc.getUpdateTime()));
            if (linearVViewHolder.mTvName != null){
                linearVViewHolder.mTvName.setText(doc.getUserName());
            }
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

    public Object getItem(int position){
        return mList.get(position);
    }


    @Override
    public int getItemViewType(int position) {
        return mList.get(position) instanceof String ? TYPE_SPLIT : TYPE_DOC;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class LinearVViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_tag)
        TextView mTvTag;
        @BindView(R.id.iv_item_image)
        ImageView mIvTitle;
        @BindView(R.id.iv_video)
        ImageView mIvVideo;
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.tv_creator_name)
        TextView mTvName;
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.tv_subtitle)
        TextView mSubtitle;
        @BindView(R.id.rl_doc_like_pack)
        View mRlDocLikePack;
        @BindView(R.id.rl_doc_comment_pack)
        View mRlDocCommentPack;
        @BindView(R.id.dv_label_root)
        DocLabelView mDlView;
        @BindView(R.id.tv_post_pants_num)
        TextView mTvLikeNum;
        @BindView(R.id.tv_post_comment_num)
        TextView mTvCommentNum;

        LinearVViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class SplitViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_time_table)
        TextView mTvSplit;

        SplitViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
