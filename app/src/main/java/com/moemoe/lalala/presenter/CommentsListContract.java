package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.CommentV2SecEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface CommentsListContract {
    interface Presenter extends BasePresenter{
        void loadCommentsList(String id, boolean sortTime, int index);
        void deleteComment(String id,String commentId,int position);
        void favoriteComment(String id, String commentId, boolean isFavorite, int position);
    }

    interface View extends BaseView{
        void onLoadCommentsSuccess(ArrayList<CommentV2Entity> commentV2Entities, boolean isPull);
        void onDeleteCommentSuccess(int position);
        void favoriteCommentSuccess(boolean isFavorite, int position);
    }
}
