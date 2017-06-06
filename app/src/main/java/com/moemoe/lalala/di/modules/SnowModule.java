package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.SnowContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class SnowModule {
    private SnowContract.View mView;

    public SnowModule(SnowContract.View view){
        this.mView = view;
    }

    @Provides
    public SnowContract.View provideView(){return mView;}
}
