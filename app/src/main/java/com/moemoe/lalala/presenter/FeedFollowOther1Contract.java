package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.FeedFollowOther1Entity;

/**
 * feed流关注页接口
 * Created by yi on 2018/1/11
 */

public interface FeedFollowOther1Contract {
    interface Presenter extends BasePresenter{
        void loadData(String id);
    }

    interface View extends BaseView{
        void onLoadDataSuccess(FeedFollowOther1Entity entities);
    }
}
