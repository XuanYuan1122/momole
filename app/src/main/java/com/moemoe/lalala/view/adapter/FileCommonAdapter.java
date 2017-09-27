package com.moemoe.lalala.view.adapter;

import android.content.Context;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.model.entity.CommonFileEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

import zlc.season.rxdownload2.RxDownload;


/**
 *
 * Created by yi on 2017/6/26.
 */

public class FileCommonAdapter extends BaseRecyclerViewAdapter<CommonFileEntity,FileCommonViewHolder> {

    private boolean isSelect;
    private boolean isGrid;
    private RxDownload downloadSub;

    public FileCommonAdapter(String folderType, Context context) {
        super(null);
        if(folderType.equals(FolderType.MH.toString())){
            setLayoutResId(R.layout.item_folder_manhua_2);
        }else if(folderType.equals(FolderType.XS.toString())){
            setLayoutResId(R.layout.item_file_xiaoshuo);
        }else {
            setLayoutResId(R.layout.item_file_common);
        }
        isSelect = false;
        downloadSub = RxDownload.getInstance(context)
                .maxThread(3)
                .maxRetryCount(3)
                .defaultSavePath(StorageUtils.getGalleryDirPath())
                .retrofit(MoeMoeApplication.getInstance().getNetComponent().getRetrofit());
    }


    @Override
    protected void convert(FileCommonViewHolder helper, final CommonFileEntity item, int position) {
        if(isGrid){
            helper.createItem(item,isSelect);
        }else {
            helper.createLinearItem(item,isSelect,downloadSub,this,position);
        }
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public void setGrid(boolean grid) {
        isGrid = grid;
    }
}
