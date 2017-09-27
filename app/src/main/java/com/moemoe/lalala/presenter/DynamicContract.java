package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface DynamicContract {
    interface Presenter extends BasePresenter{
        void loadTags(String id);
        void deleteDynamic(String id,String type);
        void followUser(String id,boolean isFollow);
        void favoriteDynamic(String id,boolean isFavorite);
        void sendTag(TagSendEntity bean);
        void plusTag(boolean isLike,int position, TagLikeEntity entity);
        void giveCoin(String id,int coin);
        void loadCommentsList(String id,int type,boolean sortTime,int index);
        void deleteComment(String id,String commentId,int position);
        void favoriteComment(String id,String commentId,boolean isFavorite,int position);
    }

    interface View extends BaseView{
        void onLoadTagsSuccess(ArrayList<DocTagEntity> tagEntities);
        void onDeleteDynamicSuccess();
        void onFollowUserSuccess(boolean isFollow);
        void onFavoriteDynamicSuccess(boolean isFavorite);
        void onSendTagSuccess(String s,String name);
        void onGiveCoinSuccess(int coins);
        void onLoadCommentsSuccess(ArrayList<CommentV2Entity> commentV2Entities,boolean isPull);
        void onDeleteCommentSuccess(int position);
        void favoriteCommentSuccess(boolean isFavorite,int position);
        void onPlusTagSuccess(int position,boolean isLike);
    }
}
