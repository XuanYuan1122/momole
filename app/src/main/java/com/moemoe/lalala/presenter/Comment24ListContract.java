package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.Comment24Entity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface Comment24ListContract {
    interface Presenter extends BasePresenter{
        void loadCommentList(int page);
    }

    interface View extends BaseView{
        void onLoadCommentSuccess(ArrayList<Comment24Entity> entities,boolean isPull);
    }
}
