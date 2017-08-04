package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.FollowMainContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class FollowMainModule {
    private FollowMainContract.View mView;

    public FollowMainModule(FollowMainContract.View view){
        this.mView = view;
    }

    @Provides
    public FollowMainContract.View provideView(){return mView;}
}
