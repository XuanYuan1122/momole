package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.Live2dShopContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class Live2dShopModule {
    private Live2dShopContract.View mView;

    public Live2dShopModule(Live2dShopContract.View view){
        this.mView = view;
    }

    @Provides
    public Live2dShopContract.View provideView(){return mView;}
}
