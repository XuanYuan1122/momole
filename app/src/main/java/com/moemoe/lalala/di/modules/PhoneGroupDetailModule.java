package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.PhoneGroupDetailContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneGroupDetailModule {
    private PhoneGroupDetailContract.View mView;

    public PhoneGroupDetailModule(PhoneGroupDetailContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneGroupDetailContract.View provideView(){return mView;}
}
