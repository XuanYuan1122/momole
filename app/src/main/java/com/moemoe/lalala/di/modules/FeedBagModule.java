package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.FeedBagContract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class FeedBagModule {
    private FeedBagContract.View mView;

    public FeedBagModule(FeedBagContract.View view){
        this.mView = view;
    }

    @Provides
    public FeedBagContract.View provideView(){return mView;}
}
