package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface FeedBagContract {
    interface Presenter extends BasePresenter{
        void loadHotBag(int index);
        void loadFeedBagList(int index);
    }

    interface View extends BaseView{
        void onLoadHotBagSuccess(ArrayList<ShowFolderEntity> entities);
        void onLoadFeedBagListSuccess(ArrayList<ShowFolderEntity> entities,boolean isPull);
    }
}
