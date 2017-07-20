package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.BadgeContract;
import com.moemoe.lalala.presenter.OrderListContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class OrderListModule {
    private OrderListContract.View mView;

    public OrderListModule(OrderListContract.View view){
        this.mView = view;
    }

    @Provides
    public OrderListContract.View provideView(){return mView;}
}
