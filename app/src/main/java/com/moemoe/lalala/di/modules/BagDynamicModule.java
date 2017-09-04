package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.BagDynamicContract;
import com.moemoe.lalala.presenter.BagMyContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class BagDynamicModule {
    private BagDynamicContract.View mView;

    public BagDynamicModule(BagDynamicContract.View view){
        this.mView = view;
    }

    @Provides
    public BagDynamicContract.View provideView(){return mView;}
}
