package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;
import com.moemoe.lalala.model.entity.TrashEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/12/13.
 */

public interface TrashContract {
    interface Presenter extends BasePresenter{
        void getTrashList(int type,int time);
        void operationTrash(String id,boolean fun);
        void favoriteTrash(TrashEntity entity,String type);
        void sendOperationTrash();
        void getTop3List(int type);
        void getTrashDetail(String type,String id);
        void likeTrashTag(TagLikeEntity entity,boolean isLike,int position);
        void createTag(TagSendEntity entity);
    }

    interface View extends BaseView{
        void onLoadListSuccess(ArrayList<TrashEntity> entities);
        void onFavoriteTrashSuccess(TrashEntity entity);
        void onTop3LoadSuccess(ArrayList<TrashEntity> entities);
        void onTop3LoadFail(int code,String msg);
        void onLoadDetailSuccess(ArrayList<DocTagEntity> entities);
        void onLikeTag(boolean like,int position);
        void onCreateTag(String id);
    }
}
