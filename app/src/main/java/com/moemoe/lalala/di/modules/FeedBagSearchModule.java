package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.FeedBagSearchContract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class FeedBagSearchModule {
    private FeedBagSearchContract.View mView;

    public FeedBagSearchModule(FeedBagSearchContract.View view){
        this.mView = view;
    }

    @Provides
    public FeedBagSearchContract.View provideView(){return mView;}
}
