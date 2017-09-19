package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.PhoneMenuListContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneMenuListModule {
    private PhoneMenuListContract.View mView;

    public PhoneMenuListModule(PhoneMenuListContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneMenuListContract.View provideView(){return mView;}
}
