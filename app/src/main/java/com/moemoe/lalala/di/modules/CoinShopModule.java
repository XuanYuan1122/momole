package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.BadgeContract;
import com.moemoe.lalala.presenter.CoinShopContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class CoinShopModule {
    private CoinShopContract.View mView;

    public CoinShopModule(CoinShopContract.View view){
        this.mView = view;
    }

    @Provides
    public CoinShopContract.View provideView(){return mView;}
}
