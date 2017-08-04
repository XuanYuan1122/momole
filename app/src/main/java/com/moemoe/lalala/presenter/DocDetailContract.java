package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.CommentSendEntity;
import com.moemoe.lalala.model.entity.DocDetailEntity;
import com.moemoe.lalala.model.entity.GiveCoinEntity;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface DocDetailContract {
    interface Presenter extends BasePresenter{
        void requestDoc(String id);
        void deleteDoc(String docId);
        void requestCommentFloor(String id,long floor,int len,boolean target,boolean isJump,boolean clear,boolean addBefore);
        void favoriteDoc(String id);
        void sendComment(ArrayList<String> paths,CommentSendEntity entity);
        void likeTag(boolean isLike,int position, TagLikeEntity entity);
        void deleteComment(NewCommentEntity entity,int position);
        void createLabel(TagSendEntity entity);
        void getCoinContent(String id);
        void giveCoin(GiveCoinEntity entity);
        void shareDoc();
        void followUser(String id,boolean isFollow);
        void checkEgg(String docId);
        void postOrCancelEgg(String docId, boolean isPost);
    }

    interface View extends BaseView{
        void onDocLoaded(DocDetailEntity entity);
        void onCommentsLoaded(ArrayList<NewCommentEntity> entities,boolean pull,boolean isJump,boolean clear,boolean addBefore);
        void onDeleteDoc();
        void onFavoriteDoc(boolean favorite);
        void onSendComment();
        void onPlusLabel(int position,boolean isLike);
        void onDeleteComment(NewCommentEntity entity,int position);
        void onCreateLabel(String s,String name);
        void onGetCoinContent();
        void onGiveCoin(int coins);
        void onFollowSuccess(boolean isFollow);
        void checkEggSuccess(boolean isThrow);
        void postOrCancelEggSuccess(boolean isPost);
    }
}
