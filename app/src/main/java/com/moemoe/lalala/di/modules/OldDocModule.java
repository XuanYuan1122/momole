package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.OldDocContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class OldDocModule {
    private OldDocContract.View mView;

    public OldDocModule(OldDocContract.View view){
        this.mView = view;
    }

    @Provides
    public OldDocContract.View provideView(){return mView;}
}
