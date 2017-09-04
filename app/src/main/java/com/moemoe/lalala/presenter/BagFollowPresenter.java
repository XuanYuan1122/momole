//package com.moemoe.lalala.presenter;
//
//import com.moemoe.lalala.model.api.ApiService;
//import com.moemoe.lalala.model.api.NetResultSubscriber;
//import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
//import com.moemoe.lalala.model.entity.BagDirEntity;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import javax.inject.Inject;
//
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
///**
// * Created by yi on 2016/11/29.
// */
//
//public class BagFollowPresenter implements BagFollowContract.Presenter {
//
//    private BagFollowContract.View view;
//    private ApiService apiService;
//
//    @Inject
//    public BagFollowPresenter(BagFollowContract.View view, ApiService apiService) {
//        this.view = view;
//        this.apiService = apiService;
//    }
//
//    @Override
//    public void getFollowList(final int index) {
//        apiService.getBagFollowList(ApiService.LENGHT,index)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new NetResultSubscriber<ArrayList<BagDirEntity>>() {
//                    @Override
//                    public void onSuccess(ArrayList<BagDirEntity> entities) {
//                        if(view != null) view.loadListSuccess(entities,index == 0);
//                    }
//
//                    @Override
//                    public void onFail(int code, String msg) {
//                        if(view != null) view.onFailure(code, msg);
//                    }
//                });
//    }
//
//    @Override
//    public void deleteFollowList(HashMap<Integer, BagDirEntity> map) {
//        ArrayList<String> ids = new ArrayList<>();
//        for(BagDirEntity id : map.values()){
//            ids.add(id.getFolderId());
//        }
//        apiService.deleteBagFollowList(ids)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new NetSimpleResultSubscriber() {
//                    @Override
//                    public void onSuccess() {
//                        if(view != null) view.deleteSuccess();
//                    }
//
//                    @Override
//                    public void onFail(int code, String msg) {
//                        if(view != null) view.onFailure(code, msg);
//                    }
//                });
//    }
//
//    @Override
//    public void release() {
//        view = null;
//    }
//}
