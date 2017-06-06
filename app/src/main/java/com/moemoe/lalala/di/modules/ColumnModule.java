package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.ColumnContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class ColumnModule {
    private ColumnContract.View mView;

    public ColumnModule(ColumnContract.View view){
        this.mView = view;
    }

    @Provides
    public ColumnContract.View provideView(){return mView;}
}
