package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.PhoneMainContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneMainModule {
    private PhoneMainContract.View mView;

    public PhoneMainModule(PhoneMainContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneMainContract.View provideView(){return mView;}
}
