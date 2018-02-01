package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.ApplyAdminContract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class ApplyAdminModule {
    private ApplyAdminContract.View mView;

    public ApplyAdminModule(ApplyAdminContract.View view){
        this.mView = view;
    }

    @Provides
    public ApplyAdminContract.View provideView(){return mView;}
}
