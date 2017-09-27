package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.DynamicContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class DynamicModule {
    private DynamicContract.View mView;

    public DynamicModule(DynamicContract.View view){
        this.mView = view;
    }

    @Provides
    public DynamicContract.View provideView(){return mView;}
}
