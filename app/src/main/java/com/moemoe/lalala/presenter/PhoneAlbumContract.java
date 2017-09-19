package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.PhoneAlbumEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneAlbumContract {
    interface Presenter extends BasePresenter{
        void loadAlbumList(int index);
        void loadAlbumItemList(String typeId,int index);
        void loadAlbumCount();
        void loadAlbumItemCount(String typeId);
    }

    interface View extends BaseView{
        void onLoadAlbumListSuccess(ArrayList<PhoneAlbumEntity> entities,boolean isPull);
        void onLoadAlbumItemListSuccess(ArrayList<PhoneAlbumEntity> entities,boolean isPull);
        void onLoadAlbumCountSuccess(int count);
        void onLoadAlbumItemCountSuccess(int count);
    }
}
