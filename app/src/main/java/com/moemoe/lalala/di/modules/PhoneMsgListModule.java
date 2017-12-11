package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.PhoneMsgListContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneMsgListModule {
    private PhoneMsgListContract.View mView;

    public PhoneMsgListModule(PhoneMsgListContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneMsgListContract.View provideView(){return mView;}
}
