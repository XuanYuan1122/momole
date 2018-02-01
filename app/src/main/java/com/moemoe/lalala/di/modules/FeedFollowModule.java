package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.FeedFollowContract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class FeedFollowModule {
    private FeedFollowContract.View mView;

    public FeedFollowModule(FeedFollowContract.View view){
        this.mView = view;
    }

    @Provides
    public FeedFollowContract.View provideView(){return mView;}
}
