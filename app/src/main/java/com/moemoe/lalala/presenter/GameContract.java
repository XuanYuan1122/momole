package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.GamePriceInfoEntity;
import com.moemoe.lalala.model.entity.PayReqEntity;
import com.moemoe.lalala.model.entity.PayResEntity;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface GameContract {
    interface Presenter extends BasePresenter{
        void loadTicketsNum();
        void loadFuHuoNum(String id);
        void useCiYuanBiGetFuHuo(String id,int num);
        void hasRole(String userId,String gameId,String roleId);
        void createOrder(String id);
        void payOrder(PayReqEntity entity);
        void getPriceInfo();
    }

    interface View extends BaseView{
        void onLoadTicketsNumSuccess(int num);
        void onLoadFuHuoNumSuccess(int num);
        void onUseCiYuanBiSuccess();
        void onHasRoleSuccess(boolean has);
        void onCreateOrderSuccess(String id);
        void onPayOrderSuccess(PayResEntity entity);
        void getPriceInfoSuccess(GamePriceInfoEntity entity);
    }
}
