package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.PhoneAlbumEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class PhoneAlbumAdapter extends BaseRecyclerViewAdapter<PhoneAlbumEntity,PhoneAlbumHolder> {

    public PhoneAlbumAdapter() {
        super(R.layout.item_phone_album);
    }


    @Override
    protected void convert(PhoneAlbumHolder helper, final PhoneAlbumEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
