package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DonationInfoEntity;
import com.moemoe.lalala.model.entity.SnowInfo;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by yi on 2016/12/1.
 */

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.LargeHolder>{

    private Context context;
    private ArrayList mRankList;
    private int mType;

    public DonationAdapter(Context context,int type){
        this.context = context;
        mRankList = new ArrayList<>();
        mType = type;
    }

    @Override
    public LargeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0){
            return new LargeHolder(LayoutInflater.from(context).inflate(R.layout.item_donation_large,parent,false));
        }else if(viewType == 1){
            return new LargeHolder(LayoutInflater.from(context).inflate(R.layout.item_donation_small,parent,false));
        }
        return null;
    }

    public void setRankList(Collection rankList){
        mRankList.clear();
        mRankList.addAll(rankList);
        notifyDataSetChanged();
    }

    public void addRankList(Collection rankList){
        mRankList.addAll(rankList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(LargeHolder holder, int position) {
        Object o = mRankList.get(position);
        int no;
        String name;
        int coin;
        if(mType == 0){
            DonationInfoEntity.RankBean bean = (DonationInfoEntity.RankBean) o;
            no = bean.getIndex();
            name = bean.getNickName();
            coin = bean.getCoin();
        }else {
            SnowInfo.RankInfo info = (SnowInfo.RankInfo) o;
            no = info.getIndex();
            name = info.getNickName();
            coin = info.getNumber();
        }
        holder.tvNo.setText(context.getString(R.string.label_no_num,no));
        holder.tvName.setText(name);
        holder.tvCoin.setText(context.getString(R.string.label_book_coin,coin));
    }

    @Override
    public int getItemCount() {
        return mRankList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position < 3 ? 0 : 1;
    }

    class LargeHolder extends RecyclerView.ViewHolder{
        private TextView tvNo,tvName,tvCoin;

        public LargeHolder(View itemView) {
            super(itemView);
            tvNo = (TextView) itemView.findViewById(R.id.tv_no);
            tvName = (TextView) itemView.findViewById(R.id.tv_book_top_name);
            tvCoin = (TextView) itemView.findViewById(R.id.tv_book_no_coin);
        }
    }
}
