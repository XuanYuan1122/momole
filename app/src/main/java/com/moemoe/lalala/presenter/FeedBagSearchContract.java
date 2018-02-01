package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.ShowFolderEntity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface FeedBagSearchContract {
    interface Presenter extends BasePresenter{
        void loadList(String type,String name,int page);
    }

    interface View extends BaseView{
        void onLoadListSuccess(ArrayList<ShowFolderEntity> entities,boolean isPull);
    }
}
