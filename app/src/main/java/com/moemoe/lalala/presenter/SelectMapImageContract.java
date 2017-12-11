package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.MapHistoryEntity;
import com.moemoe.lalala.model.entity.MapRoleBase;
import com.moemoe.lalala.model.entity.MapUserImageEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface SelectMapImageContract {
    interface Presenter extends BasePresenter{
        void loadMapSelectList();
        void loadMapHistoryList(String id,int index);
        void deleteHistoryMapRole(ArrayList<String> ids);
        void likeMapRole(boolean isLike,String id,int postion);
    }

    interface View extends BaseView{
        void onLoadListSuccess(ArrayList<MapUserImageEntity> entities);
        void onLoadHistoryListSuccess(ArrayList<MapHistoryEntity> entities,boolean isPull);
        void onDeleteSuccess();
        void onLikeSuccess(boolean isLie,int position);
    }
}
