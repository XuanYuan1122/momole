package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.Comment24Entity;
import com.moemoe.lalala.model.entity.DiscoverEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface FeedContract {
    interface Presenter extends BasePresenter{
        void loadList(long time,String type,String id);
        void loadList(int index);
        void requestBannerData(String room);
        void loadXianChongList();
        void loadFolder();
        void loadComment();
        void likeDynamic(String id,boolean isLike,int position);
        void loadDiscoverList(String type,long minIdx,long maxIdx,boolean isPull);
    }

    interface View extends BaseView{
        void onLoadListSuccess(ArrayList<NewDynamicEntity> resList,boolean isPull);
        void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities);
        void onLoadXianChongSuccess(ArrayList<XianChongEntity> entities);
        void onLoadFolderSuccess(ArrayList<ShowFolderEntity> entities);
        void onLoadCommentSuccess(Comment24Entity entity);
        void onLikeDynamicSuccess(boolean isLike,int position);
        void onLoadDiscoverListSuccess(ArrayList<DiscoverEntity> entities,boolean isPull);
    }
}
