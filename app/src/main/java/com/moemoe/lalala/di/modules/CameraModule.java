package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.CameraContract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class CameraModule {
    private CameraContract.View mView;

    public CameraModule(CameraContract.View view){
        this.mView = view;
    }

    @Provides
    public CameraContract.View provideView(){return mView;}
}
