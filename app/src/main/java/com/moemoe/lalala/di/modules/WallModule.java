package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.WallContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class WallModule {
    private WallContract.View mView;

    public WallModule(WallContract.View view){
        this.mView = view;
    }

    @Provides
    public WallContract.View provideView(){return mView;}
}
