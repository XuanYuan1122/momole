package com.moemoe.lalala.presenter;

import android.text.TextUtils;

import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.greendao.gen.AuthorInfoDao;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.model.entity.CodeEntity;
import com.moemoe.lalala.model.entity.CommentDetailEntity;
import com.moemoe.lalala.model.entity.CommentDetailRqEntity;
import com.moemoe.lalala.model.entity.LoginEntity;
import com.moemoe.lalala.model.entity.LoginResultEntity;
import com.moemoe.lalala.model.entity.SplashEntity;
import com.moemoe.lalala.model.entity.ThirdLoginEntity;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.retrofit.RetrofitUrlManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;

/**
 *
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
            CodeEntity entity = new CodeEntity();
            entity.mobile = (String) data;
            apiService.requestCode4ResetPwd(entity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onSuccess(null);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else if(type == 1){//register
            CodeEntity entity = new CodeEntity();
            entity.mobile = (String) data;
            apiService.requestRegisterCode(entity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onSuccess(null);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else if(type == 4){
            AuthorInfoDao dao = GreenDaoManager.getInstance().getSession().getAuthorInfoDao();
            AuthorInfo info = dao.load((long) 1);
            if(info == null) {
                info = new AuthorInfo();
            }else {
                PreferenceUtils.setAuthorInfo(info);
            }
            if(StringUtils.isThirdParty(info.getPlatform())){
                //三方登录
                ThirdLoginEntity bean = new ThirdLoginEntity(info.getUserName(),info.getOpenId(),StringUtils.convertPlatform(info.getPlatform()), (String) data);
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
                                finalInfo1.setRcToken(entity.getRcToken());
                                finalInfo1.setLevel(entity.getLevel());
                                finalInfo1.setOpenBag(entity.isOpenBag());
                                if(!TextUtils.isEmpty(entity.getVipTime())){
                                    try {
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                        Date date = format.parse(entity.getVipTime());
                                        Date now = new Date();
                                        if(date.before(now)){
                                            finalInfo1.setVipTime("");
                                        }else {
                                            finalInfo1.setVipTime(entity.getVipTime());
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    finalInfo1.setVipTime("");
                                }
                                finalInfo1.setInspector(entity.isInspector());
                                finalInfo1.setInviteNum(entity.getInviteNum());
                                finalInfo1.setDeskMateEntities(entity.getDeskMateList());
                                PreferenceUtils.setAuthorInfo(finalInfo1);
                                if(view != null) view.onSuccess(entity);
                            }

                            @Override
                            public void onFail(int code,String msg) {
                                PreferenceUtils.clearAuthorInfo();
                                if(view != null) view.onFailure(code,msg);
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
                                finalInfo.setRcToken(entity.getRcToken());
                                finalInfo.setLevel(entity.getLevel());
                                finalInfo.setOpenBag(entity.isOpenBag());
                                if(!TextUtils.isEmpty(entity.getVipTime())){
                                    try {
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                        Date date = format.parse(entity.getVipTime());
                                        Date now = new Date();
                                        if(date.before(now)){
                                            finalInfo.setVipTime("");
                                        }else {
                                            finalInfo.setVipTime(entity.getVipTime());
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    finalInfo.setVipTime("");
                                }
                                finalInfo.setInspector(entity.isInspector());
                                finalInfo.setInviteNum(entity.getInviteNum());
                                finalInfo.setDeskMateEntities(entity.getDeskMateList());
                                PreferenceUtils.setAuthorInfo(finalInfo);
                                if(view != null)  view.onSuccess(entity);
                            }

                            @Override
                            public void onFail(int code,String msg) {
                                PreferenceUtils.clearAuthorInfo();
                                if(view != null) view.onFailure(code,msg);
                            }
                        });
            }
        }else if(type == 8){
            apiService.getCommentDetail((CommentDetailRqEntity) data)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<CommentDetailEntity>() {
                        @Override
                        public void onSuccess(CommentDetailEntity commentDetailEntity) {
                            if(view != null) view.onSuccess(commentDetailEntity);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else if(type == 9){
            apiService.saveLive2d((String) data)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFail(int code, String msg) {

                        }
                    });
        }else if(type == 10){
            apiService.checkTxbb()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(new NetResultSubscriber<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            AppSetting.TXBB = aBoolean;
//                            if(!aBoolean){
//                                RetrofitUrlManager.getInstance().setGlobalDomain("http://pgbb.moemoe.la/");
//                            }else {
//                                RetrofitUrlManager.getInstance().setGlobalDomain("http://2333.moemoe.la/");
//                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {

                        }
                    });

        }
    }


    @Override
    public void release() {
        view = null;
    }
}
