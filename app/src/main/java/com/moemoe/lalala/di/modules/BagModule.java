package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.BagContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class BagModule {
    private BagContract.View mView;

    public BagModule(BagContract.View view){
        this.mView = view;
    }

    @Provides
    public BagContract.View provideView(){return mView;}
}
