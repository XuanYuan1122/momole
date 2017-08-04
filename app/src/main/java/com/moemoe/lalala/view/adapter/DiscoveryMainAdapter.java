package com.moemoe.lalala.view.adapter;

import android.view.ViewGroup;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.NewDocListEntity;
import com.moemoe.lalala.view.widget.adapter.BaseMultiItemRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import java.util.ArrayList;

/**
 * Created by yi on 2017/7/27.
 */

public class DiscoveryMainAdapter extends BaseMultiItemRecyclerViewAdapter<NewDocListEntity, ClickableViewHolder>{

    private final int TYPE_DOC = 0;
    private final int TYPE_BROADCAST= 1;
    private final int TYPE_EMPTY= 2;

    public DiscoveryMainAdapter(ArrayList<NewDocListEntity> list) {
        super(list);
        addItemType(TYPE_DOC, R.layout.item_doc_main_1);
        addItemType(TYPE_BROADCAST, R.layout.item_doc_main_3);
        addItemType(TYPE_EMPTY, R.layout.item_empty);
    }

    @Override
    protected void convert(ClickableViewHolder helper, NewDocListEntity item, int position) {
        if(helper instanceof MainListDocViewHolder){
            MainListDocViewHolder holder = (MainListDocViewHolder) helper;
            if("DOC".equals(item.getDetail().getType())){
                holder.createFollowDoc(item);
            }else if("FOLLOW_USER_FOLDER".equals(item.getDetail().getType())){
                holder.createFolderDoc(item);
            }else if("FOLLOW_USER_COMMENT".equals(item.getDetail().getType())){
                holder.createCommentDoc(item);
            }else if("FOLLOW_USER_FOLLOW".equals(item.getDetail().getType())){
                holder.createFollowUser(item);
            }
        }else if(helper instanceof MainListBroadcastViewHolder){
            MainListBroadcastViewHolder holder = (MainListBroadcastViewHolder) helper;
            holder.createDoc(item);
        }
    }

    @Override
    protected ClickableViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_DOC){
            return new MainListDocViewHolder(mLayoutInflater.inflate(getLayoutId(viewType),parent,false));
        }else if(viewType == TYPE_BROADCAST){
            return new MainListBroadcastViewHolder(mLayoutInflater.inflate(getLayoutId(viewType),parent,false));
        }else {
            return new EmptyViewHolder(mLayoutInflater.inflate(getLayoutId(viewType),parent,false));
        }
    }

    @Override
    public int getItemType(int position) {
        NewDocListEntity entity = getList().get(position);
        if("DOC".equals(entity.getDetail().getType())
                || "FOLLOW_USER_FOLDER".equals(entity.getDetail().getType())
                || "FOLLOW_USER_COMMENT".equals(entity.getDetail().getType())
                || "FOLLOW_USER_FOLLOW".equals(entity.getDetail().getType())){
            return TYPE_DOC;
        }else if("FOLLOW_BROADCAST".equals(entity.getDetail().getType())){
            return TYPE_BROADCAST;
        }else {
            return TYPE_EMPTY;
        }
    }
}
