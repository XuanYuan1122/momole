package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.FeedShowAllContract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class FeedShowAllModule {
    private FeedShowAllContract.View mView;

    public FeedShowAllModule(FeedShowAllContract.View view){
        this.mView = view;
    }

    @Provides
    public FeedShowAllContract.View provideView(){return mView;}
}
