package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.CommentListSendEntity;
import com.moemoe.lalala.model.entity.NewCommentEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface CommentListContract {
    interface Presenter extends BasePresenter{
        void doRequest(int index,String userId);
        void sendComment(CommentListSendEntity entity);
    }

    interface View extends BaseView{
        void onSuccess(ArrayList<NewCommentEntity> entities,boolean pull);
        void onSendSuccess();
    }
}
