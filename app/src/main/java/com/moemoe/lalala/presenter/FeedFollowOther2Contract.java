package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.DocResponse;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface FeedFollowOther2Contract {
    interface Presenter extends BasePresenter{
        void loadList(String id,int index);
    }

    interface View extends BaseView{
        void onLoadListSuccess(ArrayList<DocResponse> responses, boolean isPull);
    }
}
