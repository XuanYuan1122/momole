package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.PhoneGroupListContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneGroupListModule {
    private PhoneGroupListContract.View mView;

    public PhoneGroupListModule(PhoneGroupListContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneGroupListContract.View provideView(){return mView;}
}
