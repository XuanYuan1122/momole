package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.FeedFollowType2Entity;

/**
 * feed流关注页接口
 * Created by yi on 2018/1/11
 */

public interface FeedFollowOtherContract {
    interface Presenter extends BasePresenter{
        void loadData(String id);
    }

    interface View extends BaseView{
        void onLoadListSuccess(FeedFollowType2Entity entity);
    }
}
