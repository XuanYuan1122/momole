package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface GameContract {
    interface Presenter extends BasePresenter{
        void loadTicketsNum();
        void loadFuHuoNum(String id);
        void useCiYuanBiGetFuHuo(String id,int num);
    }

    interface View extends BaseView{
        void onLoadTicketsNumSuccess(int num);
        void onLoadFuHuoNumSuccess(int num);
        void onUseCiYuanBiSuccess();
    }
}
