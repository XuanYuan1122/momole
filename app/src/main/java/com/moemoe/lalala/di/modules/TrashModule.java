package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.TrashContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class TrashModule {
    private TrashContract.View mView;

    public TrashModule(TrashContract.View view){
        this.mView = view;
    }

    @Provides
    public TrashContract.View provideView(){return mView;}
}
