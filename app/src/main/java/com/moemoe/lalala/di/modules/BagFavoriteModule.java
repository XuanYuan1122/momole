package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.BagFavoriteContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class BagFavoriteModule {
    private BagFavoriteContract.View mView;

    public BagFavoriteModule(BagFavoriteContract.View view){
        this.mView = view;
    }

    @Provides
    public BagFavoriteContract.View provideView(){return mView;}
}
