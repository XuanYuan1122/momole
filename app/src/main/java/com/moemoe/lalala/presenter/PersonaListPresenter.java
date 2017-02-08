package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.PersonDocEntity;
import com.moemoe.lalala.model.entity.PersonFollowEntity;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.ReplyEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class PersonaListPresenter implements PersonalListContract.Presenter {

    private PersonalListContract.View view;
    private ApiService apiService;

    @Inject
    public PersonaListPresenter(PersonalListContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void doRequest(String id, final int index, int type) {
        if(type == 0){//main
            apiService.getPersonalMain(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<PersonalMainEntity>() {
                        @Override
                        public void onSuccess(PersonalMainEntity personalMainEntity) {
                            view.onSuccess(personalMainEntity,index == 0);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 1){//doc
            apiService.requestUserTagDocListV2(index,ApiService.LENGHT,id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<PersonDocEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<PersonDocEntity> personDocEntities) {
                            view.onSuccess(personDocEntities,index == 0);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 2){//favorite
            apiService.requestFavoriteDocList(id,index,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<PersonDocEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<PersonDocEntity> personDocEntities) {
                            view.onSuccess(personDocEntities,index == 0);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 3){//follow
            apiService.getUserFollowList(id,index,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<PersonFollowEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<PersonFollowEntity> personFollowEntities) {
                            view.onSuccess(personFollowEntities,index == 0);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 4){//comment
            apiService.requestCommentFromOther(index,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<ReplyEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<ReplyEntity> replyEntities) {
                            view.onSuccess(replyEntities,index == 0);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 5){//fans
            apiService.getUserFansList(id,index,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<PersonFollowEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<PersonFollowEntity> personFollowEntities) {
                            view.onSuccess(personFollowEntities,index == 0);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }
    }
}
