package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.CommentDetailEntity;
import com.moemoe.lalala.model.entity.CommentDetailRqEntity;
import com.moemoe.lalala.model.entity.CommentSendEntity;
import com.moemoe.lalala.model.entity.NewCommentEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface CommentDetailContract {
    interface Presenter{
        void requestCommentDetail(CommentDetailRqEntity data);
        void deleteComment(NewCommentEntity entity);
        void sendComment(ArrayList<String> paths, CommentSendEntity entity);
    }

    interface View extends BaseView{
        void onGetDetailSuccess(CommentDetailEntity o);
        void onDeleteComment();
        void onSendComment();
    }
}
