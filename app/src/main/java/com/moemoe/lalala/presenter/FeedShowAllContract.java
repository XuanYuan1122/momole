package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.FeedFollowType1Entity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.TagFileDelRequest;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface FeedShowAllContract {
    interface Presenter extends BasePresenter{
        void loadList(String type,String id,int index);
        void delFile(String type, TagFileDelRequest request);
    }

    interface View extends BaseView{
        void onLoadListSuccess(ArrayList<ShowFolderEntity> entities,boolean isPull);
        void onLoadList2Success(ArrayList<FeedFollowType1Entity> entities,boolean isPull);
        void onDelFileSuccess();
    }
}
