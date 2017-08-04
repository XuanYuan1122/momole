package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.DiscovertMainContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class DiscoveryModule {
    private DiscovertMainContract.View mView;

    public DiscoveryModule(DiscovertMainContract.View view){
        this.mView = view;
    }

    @Provides
    public DiscovertMainContract.View provideView(){return mView;}
}
