package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.PhoneStateContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneStateModule {
    private PhoneStateContract.View mView;

    public PhoneStateModule(PhoneStateContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneStateContract.View provideView(){return mView;}
}
