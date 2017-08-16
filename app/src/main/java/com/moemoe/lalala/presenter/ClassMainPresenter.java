package com.moemoe.lalala.presenter;

import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.NewDocListEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class ClassMainPresenter implements ClassMainContract.Presenter{

    private ClassMainContract.View view;
    private ApiService apiService;

    @Inject
    public ClassMainPresenter(ClassMainContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void loadDocList(int index, final boolean change, final boolean isPull) {
        apiService.requestTagDocList(index,ApiService.LENGHT,"", AppSetting.SUB_TAG)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<DocListEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<DocListEntity> docListEntities) {
                        if(change){
                            if(view != null) view.onChangeSuccess(docListEntities);
                        }else {
                            if(view != null)  view.onLoadDocListSuccess(docListEntities,isPull);
                        }
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void release() {
        view = null;
    }
}
