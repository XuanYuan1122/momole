package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.MapHistoryEntity;
import com.moemoe.lalala.model.entity.MapRoleBase;
import com.moemoe.lalala.model.entity.MapUserImageEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class MapSelectImageAdapter<T extends MapRoleBase> extends BaseRecyclerViewAdapter<T,MapSelectImageHolder> {

    private int selectPosition;
    private boolean isSingle;
    private String useId;

    public MapSelectImageAdapter() {
        super(R.layout.item_map_select);
        selectPosition = -1;
        this.isSingle = true;
    }

    public MapSelectImageAdapter(String useId) {
        super(R.layout.item_map_select);
        selectPosition = -1;
        this.isSingle = false;
        this.useId = useId;
    }

    @Override
    protected void convert(MapSelectImageHolder helper, final MapRoleBase item, int position) {
        if(!isSingle){
            helper.createItem((MapHistoryEntity)item,useId,position == selectPosition);
        }else {
            helper.createItem((MapUserImageEntity)item,position == selectPosition);
        }

    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }
}
