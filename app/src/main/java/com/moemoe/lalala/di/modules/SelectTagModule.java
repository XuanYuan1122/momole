package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.SelectTagContract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class SelectTagModule {
    private SelectTagContract.View mView;

    public SelectTagModule(SelectTagContract.View view){
        this.mView = view;
    }

    @Provides
    public SelectTagContract.View provideView(){return mView;}
}
