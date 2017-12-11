package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.FeedNoticeEntity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface FeedNoticeContract {
    interface Presenter extends BasePresenter{
        void loadFeedNoticeList(long timestamp);
    }

    interface View extends BaseView{
        void onLoadFeedNoticeListSuccess(ArrayList<FeedNoticeEntity> entities,boolean isPull);
    }
}
