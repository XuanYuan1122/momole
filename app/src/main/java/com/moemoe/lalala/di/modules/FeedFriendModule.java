package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.FeedFriendContract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class FeedFriendModule {
    private FeedFriendContract.View mView;

    public FeedFriendModule(FeedFriendContract.View view){
        this.mView = view;
    }

    @Provides
    public FeedFriendContract.View provideView(){return mView;}
}
