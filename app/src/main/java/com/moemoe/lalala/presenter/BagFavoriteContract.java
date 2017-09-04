package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yi on 2016/11/29.
 */

public interface BagFavoriteContract {
    interface Presenter extends BasePresenter{
        void getFavoriteList(int index);
        void deleteFavoriteList(HashMap<Integer,ShowFolderEntity> map);
    }

    interface View extends BaseView{
        void loadListSuccess(ArrayList<ShowFolderEntity> entities,boolean isPull);
        void deleteSuccess();
    }
}
