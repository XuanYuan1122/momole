package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.PhoneMsgContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneMsgModule {
    private PhoneMsgContract.View mView;

    public PhoneMsgModule(PhoneMsgContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneMsgContract.View provideView(){return mView;}
}
