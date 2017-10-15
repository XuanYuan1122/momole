package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.CommentSendV2Entity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface CreateCommentContract {
    interface Presenter extends BasePresenter{
        void createComment(boolean isSec, String id, CommentSendV2Entity entity, ArrayList<String> path,boolean isDoc);
    }

    interface View extends BaseView{
        void onCreateCommentSuccess();
    }
}
