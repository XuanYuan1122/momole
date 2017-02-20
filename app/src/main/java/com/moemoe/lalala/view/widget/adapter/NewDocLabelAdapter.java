package com.moemoe.lalala.view.widget.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by Haru on 2015/12/24 0024.
 */
public class NewDocLabelAdapter extends BaseAdapter {
    private static final int TYPE_LABEL = 1;
    private static final int TYPE_ADD = 2;
    private Context mContext;
    private ArrayList<DocTagEntity> mTags;
    private boolean mIsNeedAdd;
    private boolean mNeedShow;

    private int[] mBackGround = { R.drawable.shape_rect_label_cyan, R.drawable.shape_rect_label_yellow, R.drawable.shape_rect_label_orange, R.drawable.shape_rect_label_pink, R.drawable.shape_rect_label_green, R.drawable.shape_rect_label_purple, R.drawable.shape_rect_label_tab_blue};

    public NewDocLabelAdapter(Context context, boolean needShow){
        mContext = context;
        mNeedShow = needShow;
    }

    public NewDocLabelAdapter(Context context, ArrayList<DocTagEntity> beans, boolean needAdd){
        mContext = context;
        mTags = beans;
        mIsNeedAdd = needAdd;
    }

    public void setData(ArrayList<DocTagEntity> beans, boolean needAdd){
        mTags = beans;
        mIsNeedAdd = needAdd;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(mTags != null){
            if(mIsNeedAdd){
                return mTags.size() + 1;
            }
            return mTags.size();
        }else{
            return 0;
        }
    }

    public ArrayList<DocTagEntity> getTags(){
        return mTags;
    }

    @Override
    public DocTagEntity getItem(int i) {
        if(i < mTags.size()){
            return mTags.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position < mTags.size()){
            return TYPE_LABEL;
        }
        return TYPE_ADD;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        int type = getItemViewType(position);
        if(convertView == null){
            if (type == TYPE_LABEL) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_doc_label, viewGroup,false);
                holder = new ViewHolder();
                holder.labelRoot = (LinearLayout) convertView.findViewById(R.id.ll_label_root);
                holder.labelContent = (TextView) convertView.findViewById(R.id.tv_item_label_content);
                holder.labelFollowNum = (TextView) convertView.findViewById(R.id.tv_item_label_num);
                convertView.setTag(holder);
            } else if (type == TYPE_ADD) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_doc_label_add, viewGroup,false);
            }
        }
        if (type == TYPE_LABEL) {
            holder = (ViewHolder) convertView.getTag();
            DocTagEntity b = getItem(position);
            String content = b.getName();
            if(b.isLiked() || mNeedShow){
                int index = StringUtils.getHashOfString(content, mBackGround.length);
                holder.labelRoot.setBackgroundResource(mBackGround[index]);
                holder.labelContent.setTextColor(Color.WHITE);
                holder.labelFollowNum.setTextColor(Color.WHITE);
            }else{
                holder.labelRoot.setBackgroundResource(R.drawable.btn_follow_label);
                holder.labelContent.setTextColor(Color.BLACK);
                holder.labelFollowNum.setTextColor(Color.BLACK);
            }
            holder.labelContent.setText(content);
            holder.labelFollowNum.setText(StringUtils.getNumberInLengthLimit((int) b.getLikes(),2));
        }

        return convertView;
    }

    class ViewHolder{
        LinearLayout labelRoot;
        TextView labelContent;
        TextView labelFollowNum;
    }
}