package com.moemoe.lalala.view.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 * Created by yi on 2017/7/21.
 */

public class RejectViewHolder  extends ClickableViewHolder {
    ImageView ivAvatar;
    TextView tvName,tvRemove;

    public RejectViewHolder(View itemView) {
        super(itemView);
        ivAvatar = $(R.id.iv_avatar);
        tvName = $(R.id.tv_name);
        tvRemove = $(R.id.tv_remove);
    }
}
