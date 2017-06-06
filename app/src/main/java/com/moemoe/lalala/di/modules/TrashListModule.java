package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.TrashListContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class TrashListModule {
    private TrashListContract.View mView;

    public TrashListModule(TrashListContract.View view){
        this.mView = view;
    }

    @Provides
    public TrashListContract.View provideView(){return mView;}
}
