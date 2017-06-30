package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.FukuContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class FukuModule {
    private FukuContract.View mView;

    public FukuModule(FukuContract.View view){
        this.mView = view;
    }

    @Provides
    public FukuContract.View provideView(){return mView;}
}
