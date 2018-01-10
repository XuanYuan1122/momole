package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.CreateOrderEntity;
import com.moemoe.lalala.model.entity.GamePriceInfoEntity;
import com.moemoe.lalala.model.entity.InviteUserEntity;
import com.moemoe.lalala.model.entity.OrderEntity;
import com.moemoe.lalala.model.entity.PayReqEntity;
import com.moemoe.lalala.model.entity.PayResEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * 游戏网络
 * Created by yi on 2016/11/29.
 */

public class GamePresenter implements GameContract.Presenter {

    private GameContract.View view;
    private ApiService apiService;

    @Inject
    public GamePresenter(GameContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadTicketsNum() {
        apiService.loadTicketsNum()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        if(view != null) view.onLoadTicketsNumSuccess(integer);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadFuHuoNum(String id) {
        apiService.getFuHuoNum(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        if(view != null) view.onLoadFuHuoNumSuccess(integer);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void useCiYuanBiGetFuHuo(String id, int num) {
        apiService.useCiYuanBiGetFuHuo(id,num)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onUseCiYuanBiSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void hasRole(String userId, String gameId, String roleId) {
        apiService.hasRole(userId, gameId, roleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if(view != null) view.onHasRoleSuccess(aBoolean);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void createOrder(final String id) {
        apiService.createOrderNum(1,id,"game_sanguo")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<CreateOrderEntity>() {
                    @Override
                    public void onSuccess(CreateOrderEntity entity) {
                        if(view!=null) view.onCreateOrderSuccess(entity.getOrderId());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void payOrder(PayReqEntity entity) {
        apiService.payOrder(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<PayResEntity>() {
                    @Override
                    public void onSuccess(PayResEntity entity) {
                        if(view!=null)view.onPayOrderSuccess(entity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void getPriceInfo() {
        apiService.getGamePriceInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<GamePriceInfoEntity>() {
                    @Override
                    public void onSuccess(GamePriceInfoEntity entity) {
                        if(view!=null)view.getPriceInfoSuccess(entity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }
}
