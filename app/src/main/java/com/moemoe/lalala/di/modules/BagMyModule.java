package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.BagMyContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class BagMyModule {
    private BagMyContract.View mView;

    public BagMyModule(BagMyContract.View view){
        this.mView = view;
    }

    @Provides
    public BagMyContract.View provideView(){return mView;}
}
