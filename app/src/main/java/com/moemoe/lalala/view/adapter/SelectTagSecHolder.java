package com.moemoe.lalala.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.moemoe.lalala.R;
import com.moemoe.lalala.event.TagSelectEvent;
import com.moemoe.lalala.model.entity.OfficialTag;
import com.moemoe.lalala.model.entity.UserFollowTagEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;
import com.moemoe.lalala.view.widget.recycler.FlowLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class SelectTagSecHolder extends ClickableViewHolder {

    public SelectTagSecHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final OfficialTag.OfficialTagSec entity, final int pos, final ArrayList<UserFollowTagEntity> userIds){
        setText(R.id.tv_tag,entity.getText());
        final FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        ((RecyclerView)$(R.id.list)).setLayoutManager(flowLayoutManager);
        final TagAdapter adapter = new TagAdapter();
        adapter.setUserIds(userIds);
        ((RecyclerView)$(R.id.list)).setAdapter(adapter);
        adapter.setList(entity.getTagThi());
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(userIds == null){
                    adapter.getItem(position).setSelect(!adapter.getItem(position).isSelect());
                    adapter.notifyDataSetChanged();
                }else {
                    if(adapter.getItem(position).isSelect()){
                        EventBus.getDefault().post(new TagSelectEvent("del",adapter.getItem(position).getId(),adapter.getItem(position).getText(),pos));
                    }else {
                        EventBus.getDefault().post(new TagSelectEvent("add",adapter.getItem(position).getId(),adapter.getItem(position).getText(),pos));
                    }
                    adapter.getItem(position).setSelect(!adapter.getItem(position).isSelect());
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }
}
