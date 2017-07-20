package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.FileEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by yi on 2017/1/18.
 */

public class BagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private boolean mNeedAdd;
    private OnItemClickListener onItemClickListener;
    private ArrayList<Object> entities;
    private int mType;
    private boolean isBuy;
    private int selectPosition;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public BagAdapter(Context context,boolean needAdd,int type){
        mContext = context;
        mNeedAdd = needAdd;
        entities = new ArrayList<>();
        mType = type;
        isBuy = false;
        selectPosition = -1;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public ArrayList<Object> getList(){
        return entities;
    }

    public void setData(Collection entities){
        this.entities.clear();
        this.entities.addAll(entities);
        notifyDataSetChanged();
    }

    public void setIsBuy(boolean isBuy){
        this.isBuy = isBuy;
    }

    public boolean isBuy(){
        return isBuy;
    }

    public void addData(Collection entities){
        int bfSize = this.entities.size();
        this.entities.addAll(entities);
        int afSize = this.entities.size();
        if(mNeedAdd){
            notifyItemRangeInserted(bfSize + 1,afSize - bfSize);
        }else {
            notifyItemRangeInserted(bfSize,afSize - bfSize);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mNeedAdd && position == 0){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mType == 0){
            if(viewType == 0){
                View v = LayoutInflater.from(mContext).inflate(R.layout.item_bag_dir_add,parent,false);
                return new AddViewHolder(v);
            }else if(viewType == 1){
                View v = LayoutInflater.from(mContext).inflate(R.layout.item_bag_dir,parent,false);
                return new BagItemViewHolder(v);
            }
        }else if(mType == 1){
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_folder_item,parent,false);
            return new FileViewHolder(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof BagItemViewHolder){
            BagItemViewHolder viewHolder = (BagItemViewHolder) holder;
            BagDirEntity entity = (BagDirEntity) getItem(position);
            String path ;
            if(entity.getCover().startsWith("/")){
                path = entity.getCover();
            }else {
                path = StringUtils.getUrl(mContext, ApiService.URL_QINIU +  entity.getCover(), (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2 ,(DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2, false, true);
            }
            Glide.with(mContext)
                    .load(path)
                    .override((DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2,(DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2)
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .centerCrop()
                    .transform(new GlideRoundTransform(mContext,5))
                    .into(viewHolder.ivBg);
            if (entity.getCoin() > 0){
                viewHolder.tvMark.setBackgroundResource(R.drawable.ic_bag_mask_red);
                viewHolder.tvMark.setText(entity.getCoin() + " 节操");
                viewHolder.tvMark.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
            }else {
                viewHolder.tvMark.setBackgroundResource(R.drawable.ic_bag_mask_green);
                viewHolder.tvMark.setText("无料");
                viewHolder.tvMark.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
            }
            viewHolder.tvNum.setText(entity.getNumber() + "项");
            viewHolder.tvName.setText(entity.getName());
            viewHolder.tvTime.setText(entity.getUpdateTime());
            if(position == selectPosition){
                viewHolder.ivSelected.setVisibility(View.VISIBLE);
            }else {
                viewHolder.ivSelected.setVisibility(View.GONE);
            }
        }else if(holder instanceof FileViewHolder){
            FileViewHolder viewHolder = (FileViewHolder) holder;
            FileEntity entity = (FileEntity) getItem(position);
            if(entity.getType().equals("image")){
                viewHolder.mMusicRoot.setVisibility(View.GONE);
                viewHolder.ivMusic.setVisibility(View.GONE);
                if(!isBuy){
                    Glide.with(mContext)
                            .load(StringUtils.getUrl(mContext, ApiService.URL_QINIU +  entity.getPath(), (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2, false, true))
                            .override((DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2)
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .centerCrop()
                            .bitmapTransform(new BlurTransformation(mContext,10,4))
                            .into(viewHolder.ivImg);
                }else {
                    Glide.with(mContext)
                            .load(StringUtils.getUrl(mContext, ApiService.URL_QINIU +  entity.getPath(), (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2, false, true))
                            .override((DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2)
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .centerCrop()
                            .into(viewHolder.ivImg);
                }
            }else if(entity.getType().equals("music")){
                viewHolder.mMusicRoot.setVisibility(View.VISIBLE);
                viewHolder.ivMusic.setVisibility(View.INVISIBLE);
                Glide.with(mContext)
                        .load(R.drawable.bg_bag_music)
                        .override((DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2)
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .centerCrop()
                        .into(viewHolder.ivImg);
                viewHolder.tvMusicName.setText(entity.getFileName());
                viewHolder.tvMusicTime.setText(getMinute(entity.getAttr().get("timestamp").getAsInt()));
            }else if(entity.getType().equals("txt")){
                viewHolder.mMusicRoot.setVisibility(View.VISIBLE);
                viewHolder.ivMusic.setVisibility(View.INVISIBLE);
                Glide.with(mContext)
                        .load(R.drawable.bg_bag_word)
                        .override((DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2)
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .centerCrop()
                        .into(viewHolder.ivImg);
                viewHolder.tvMusicName.setText(entity.getFileName());
                viewHolder.tvMusicTime.setText(FileUtil.formatFileSizeToString(entity.getAttr().get("size").getAsLong()));
            }else {
                viewHolder.mMusicRoot.setVisibility(View.VISIBLE);
                viewHolder.ivMusic.setVisibility(View.INVISIBLE);
                Glide.with(mContext)
                        .load(R.drawable.bg_bag_unknow)
                        .override((DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2)
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .centerCrop()
                        .into(viewHolder.ivImg);
                viewHolder.tvMusicName.setText(entity.getFileName());
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

    private String getMinute(int time) {
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

    public Object getItem(int position){
        if(position == 0 && mNeedAdd){
            return null;
        }
        return mNeedAdd?entities.get(position - 1) : entities.get(position);
    }

    @Override
    public int getItemCount() {
        return mNeedAdd? 1 + entities.size() : entities.size();
    }

    private class AddViewHolder extends RecyclerView.ViewHolder{
        View root;
        public AddViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.rl_root);
            ViewGroup.MarginLayoutParams layoutParams1 = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
            layoutParams1.height = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2;
            layoutParams1.width = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2;
//            layoutParams1.topMargin = DensityUtil.dip2px(mContext,10);
//            layoutParams1.rightMargin = DensityUtil.dip2px(mContext,10);
            root.setLayoutParams(layoutParams1);
        }
    }

    private class BagItemViewHolder extends RecyclerView.ViewHolder{

        TextView tvMark,tvTime,tvName,tvNum;
        ImageView ivSelected,ivBg;

        View root;

        public BagItemViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.rl_root);
            tvMark = (TextView) itemView.findViewById(R.id.tv_mark);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvNum = (TextView) itemView.findViewById(R.id.tv_num);
            ivBg = (ImageView) itemView.findViewById(R.id.iv_bg);
            ivSelected = (ImageView) itemView.findViewById(R.id.iv_selected);
            ViewGroup.MarginLayoutParams layoutParams1 = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
            layoutParams1.height = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2;
            layoutParams1.width = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2;
            root.setLayoutParams(layoutParams1);
            ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
            layoutParams.height = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2;
            layoutParams.width = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2;
            ivBg.setLayoutParams(layoutParams);
        }
    }

    private class FileViewHolder extends RecyclerView.ViewHolder{

        ImageView ivImg,ivMusic;
        TextView tvMusicName,tvMusicTime;
        View mMusicRoot;
        View root;

        public FileViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.fl_root);
            ivImg = (ImageView) itemView.findViewById(R.id.iv_img);
            ivMusic = (ImageView) itemView.findViewById(R.id.iv_music);
            tvMusicName = (TextView) itemView.findViewById(R.id.tv_music_name);
            tvMusicTime = (TextView) itemView.findViewById(R.id.tv_music_time);
            mMusicRoot = itemView.findViewById(R.id.ll_music_root);
            ViewGroup.MarginLayoutParams layoutParams1 = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
            layoutParams1.height = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2;
            layoutParams1.width = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2;
//            layoutParams1.topMargin = DensityUtil.dip2px(mContext,10);
//            layoutParams1.rightMargin = DensityUtil.dip2px(mContext,10);
            root.setLayoutParams(layoutParams1);
            ViewGroup.LayoutParams layoutParams = ivImg.getLayoutParams();
            layoutParams.height = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2;
            layoutParams.width = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,30))/2;
            ivImg.setLayoutParams(layoutParams);
        }
    }
}
