package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.SelectMapImageContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class SelectMapImageModule {
    private SelectMapImageContract.View mView;

    public SelectMapImageModule(SelectMapImageContract.View view){
        this.mView = view;
    }

    @Provides
    public SelectMapImageContract.View provideView(){return mView;}
}
