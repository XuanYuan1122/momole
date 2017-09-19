package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.PhoneMateContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneMateModule {
    private PhoneMateContract.View mView;

    public PhoneMateModule(PhoneMateContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneMateContract.View provideView(){return mView;}
}
