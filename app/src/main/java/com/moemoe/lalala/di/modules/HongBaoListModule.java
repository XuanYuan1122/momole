package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.HongBaoListContract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class HongBaoListModule {
    private HongBaoListContract.View mView;

    public HongBaoListModule(HongBaoListContract.View view){
        this.mView = view;
    }

    @Provides
    public HongBaoListContract.View provideView(){return mView;}
}
