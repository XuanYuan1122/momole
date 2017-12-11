package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.GroupEntity;
import com.moemoe.lalala.model.entity.GroupMemberDelEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by yi on 2016/11/29.
 */

public class PhoneGroupMemberPresenter implements PhoneGroupMemberContract.Presenter {

    private PhoneGroupMemberContract.View view;
    private ApiService apiService;

    @Inject
    public PhoneGroupMemberPresenter(PhoneGroupMemberContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadMemberList(String groupId, final int index) {
        apiService.loadGroupMemberList(groupId,index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<UserTopEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<UserTopEntity> entities) {
                        if(view != null) view.onLoadMemberListSuccess(entities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void delMembers(GroupMemberDelEntity entity) {
        apiService.delGroupMember(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onDelMembersSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
