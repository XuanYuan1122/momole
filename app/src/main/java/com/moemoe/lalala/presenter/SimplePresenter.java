package com.moemoe.lalala.presenter;

import com.moemoe.lalala.greendao.gen.AuthorInfoDao;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.model.entity.DelCommentEntity;
import com.moemoe.lalala.model.entity.LoginEntity;
import com.moemoe.lalala.model.entity.LoginResultEntity;
import com.moemoe.lalala.model.entity.ReportEntity;
import com.moemoe.lalala.model.entity.ThirdLoginEntity;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.PreferenceUtils;

import javax.inject.Inject;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class SimplePresenter implements SimpleContract.Presenter {

    private SimpleContract.View view;
    private ApiService apiService;

    @Inject
    public SimplePresenter(SimpleContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void doRequest(final Object data, int type) {
        if (type == 0){//find pwd
            apiService.requestCode4ResetPwd((String) data)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            view.onSuccess(null);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 1){//register
            apiService.requestRegisterCode((String) data)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            view.onSuccess(null);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 3){//jubao
            apiService.report((ReportEntity) data)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            view.onSuccess(null);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 4){
            AuthorInfoDao dao = GreenDaoManager.getInstance().getSession().getAuthorInfoDao();
            AuthorInfo info = dao.load((long) 1);
            if(info == null) info = new AuthorInfo();
            if(isThirdParty(info.getPlatform())){
                //三方登录
                ThirdLoginEntity bean = new ThirdLoginEntity(info.getUserName(),info.getOpenId(),info.getPlatform(), (String) data);
                final AuthorInfo finalInfo1 = info;
                apiService.loginThird(bean)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<LoginResultEntity>() {
                            @Override
                            public void onSuccess(LoginResultEntity entity) {
                                finalInfo1.setToken(entity.getToken());
                                finalInfo1.setUserId(entity.getUserId());
                                finalInfo1.setCoin(entity.getCoin());
                                finalInfo1.setUserName(entity.getUserName());
                                finalInfo1.setLevel(entity.getLevel());
                                finalInfo1.setOpenBag(entity.isOpenBag());
                                PreferenceUtils.setAuthorInfo(finalInfo1);
                                view.onSuccess(entity);
                            }

                            @Override
                            public void onFail(int code,String msg) {
                                PreferenceUtils.clearAuthorInfo();
                                view.onFailure(code,msg);
                            }
                        });

            }else {
                //自有登录
                LoginEntity bean = new LoginEntity(info.getPhone(),info.getPassword(), (String) data);
                final AuthorInfo finalInfo = info;
                apiService.login(bean)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<LoginResultEntity>() {
                            @Override
                            public void onSuccess(LoginResultEntity entity) {
                                finalInfo.setToken(entity.getToken());
                                finalInfo.setUserId(entity.getUserId());
                                finalInfo.setCoin(entity.getCoin());
                                finalInfo.setUserName(entity.getUserName());
                                finalInfo.setLevel(entity.getLevel());
                                finalInfo.setOpenBag(entity.isOpenBag());
                                PreferenceUtils.setAuthorInfo(finalInfo);
                                view.onSuccess(entity);
                            }

                            @Override
                            public void onFail(int code,String msg) {
                                PreferenceUtils.clearAuthorInfo();
                                view.onFailure(code,msg);
                            }
                        });
            }
        }else if(type == 5){
            apiService.delCommentByOwner((DelCommentEntity) data)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            view.onSuccess(null);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 6){
            apiService.reportBag((ReportEntity) data)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            view.onSuccess(null);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }
    }

    public boolean isThirdParty(String platform){
        return platform != null && (platform.equals(cn.sharesdk.tencent.qq.QQ.NAME) || platform.equals(Wechat.NAME) || platform.equals(SinaWeibo.NAME));
    }
}
