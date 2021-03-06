package com.moemoe.lalala.presenter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.BagMyEntity;
import com.moemoe.lalala.model.entity.BagMyShowEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2016/11/29.
 */

public class BagMyPresenter implements BagMyContract.Presenter {

    private BagMyContract.View view;
    private ApiService apiService;

    @Inject
    public BagMyPresenter(BagMyContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }


    @Override
    public void loadContent(String type,String userId) {
        if("my".equals(type)){
            apiService.loadBagMy(userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<BagMyEntity>() {
                        @Override
                        public void onSuccess(BagMyEntity entity) {
                            if(view != null){
                                //转换显示model
                                ArrayList<BagMyShowEntity> entities = new ArrayList<>();
                                BagMyShowEntity show0 = new BagMyShowEntity(R.drawable.shape_rect_zonghe,"综合",entity.getSynthesizeNum(),entity.getSynthesizeList());
                                BagMyShowEntity show1 = new BagMyShowEntity(R.drawable.shape_rect_tuji,"图集",entity.getImageNum(),entity.getImageList());
                                BagMyShowEntity show2 = new BagMyShowEntity(R.drawable.shape_rect_manhua,"漫画",entity.getCartoonNum(),entity.getCartoonList());
                                BagMyShowEntity show3 = new BagMyShowEntity(R.drawable.shape_rect_xiaoshuo,"小说",entity.getFictionNum(),entity.getFictionList());
                                BagMyShowEntity show4 = new BagMyShowEntity(R.drawable.shape_rect_xiaoshuo,"视频",entity.getVideoNum(),entity.getVideoList());
                                BagMyShowEntity show5 = new BagMyShowEntity(R.drawable.shape_rect_xiaoshuo,"音乐",entity.getMusicNum(),entity.getMusicList());
                                BagMyShowEntity show6 = new BagMyShowEntity(R.drawable.shape_rect_zonghe,"文章",entity.getArticleNum(),entity.getArticleList());
                                entities.add(show0);
                                entities.add(show1);
                                entities.add(show2);
                                entities.add(show3);
                                entities.add(show4);
                                entities.add(show5);
                                entities.add(show6);
                                view.onLoadSuccess(entities);
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else {
            apiService.loadBagMyFollow()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<BagMyEntity>() {
                        @Override
                        public void onSuccess(BagMyEntity entity) {
                            if(view != null){
                                //转换显示model
                                ArrayList<BagMyShowEntity> entities = new ArrayList<>();
                                BagMyShowEntity show0 = new BagMyShowEntity(R.drawable.shape_rect_zonghe,"收藏",entity.getSynthesizeNum(),entity.getFollowList());
                                BagMyShowEntity show1 = new BagMyShowEntity(R.drawable.shape_rect_zonghe,"综合",entity.getSynthesizeNum(),entity.getSynthesizeList());
                                BagMyShowEntity show2 = new BagMyShowEntity(R.drawable.shape_rect_tuji,"图集",entity.getImageNum(),entity.getImageList());
                                BagMyShowEntity show3 = new BagMyShowEntity(R.drawable.shape_rect_manhua,"漫画",entity.getCartoonNum(),entity.getCartoonList());
                                BagMyShowEntity show4 = new BagMyShowEntity(R.drawable.shape_rect_xiaoshuo,"小说",entity.getFictionNum(),entity.getFictionList());
                                BagMyShowEntity show5 = new BagMyShowEntity(R.drawable.shape_rect_xiaoshuo,"视频",entity.getVideoNum(),entity.getVideoList());
                                BagMyShowEntity show6 = new BagMyShowEntity(R.drawable.shape_rect_xiaoshuo,"音乐",entity.getMusicNum(),entity.getMusicList());
                                BagMyShowEntity show7 = new BagMyShowEntity(R.drawable.shape_rect_zonghe,"文章",entity.getArticleNum(),entity.getArticleList());
                                entities.add(show0);
                                entities.add(show1);
                                entities.add(show2);
                                entities.add(show3);
                                entities.add(show4);
                                entities.add(show5);
                                entities.add(show6);
                                entities.add(show7);
                                view.onLoadSuccess(entities);
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }
    }
}
