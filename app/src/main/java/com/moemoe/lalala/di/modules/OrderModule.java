package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.OrderContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class OrderModule {
    private OrderContract.View mView;

    public OrderModule(OrderContract.View view){
        this.mView = view;
    }

    @Provides
    public OrderContract.View provideView(){return mView;}
}
