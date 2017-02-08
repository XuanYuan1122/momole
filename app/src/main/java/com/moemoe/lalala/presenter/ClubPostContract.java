package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.ClubZipEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.TagNodeEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface ClubPostContract {
    interface Presenter{
        void requestClubData(String tagId);
        void requestDocList(int index);
    }

    interface View extends BaseView{
        void bindClubViewData(TagNodeEntity entity);
        void bindListViewData(ClubZipEntity entity);
        void onLoadDocList(ArrayList<DocListEntity> docListEntities,boolean isPull);
    }
}
