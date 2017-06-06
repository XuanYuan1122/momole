package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.SimpleContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class SimpleModule {
    private SimpleContract.View mView;

    public SimpleModule(SimpleContract.View view){
        this.mView = view;
    }

    @Provides
    public SimpleContract.View provideView(){return mView;}
}
