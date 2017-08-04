package com.moemoe.lalala.view.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 * Created by yi on 2017/7/21.
 */

public class FukuItemViewHolder extends ClickableViewHolder {
    TextView name,info,req;
    ImageView fuku,select;
    RelativeLayout root;
    public FukuItemViewHolder(View itemView) {
        super(itemView);
        name = $(R.id.tv_fuku_name);
        info = $(R.id.tv_fuku_info);
        req = $(R.id.tv_fuku_req);
        fuku = $(R.id.iv_fuku);
        select = $(R.id.iv_select);
        root = $(R.id.rl_root);
    }
}
