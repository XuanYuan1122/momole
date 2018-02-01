package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.FeedFollowType1Entity;
import com.moemoe.lalala.model.entity.UserFollowTagEntity;

import java.util.ArrayList;

/**
 * feed流关注页接口
 * Created by yi on 2018/1/11
 */

public interface FeedFollowAllContract {
    interface Presenter extends BasePresenter{
        void loadList(int index);
    }

    interface View extends BaseView{
        void onLoadListSuccess(ArrayList<FeedFollowType1Entity> entities,boolean isPull);
    }
}
