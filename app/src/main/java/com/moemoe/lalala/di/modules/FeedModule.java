package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.FeedContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class FeedModule {
    private FeedContract.View mView;

    public FeedModule(FeedContract.View view){
        this.mView = view;
    }

    @Provides
    public FeedContract.View provideView(){return mView;}
}
