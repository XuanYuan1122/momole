package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.FeedFollowOtherContract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class FeedFollowOtherModule {
    private FeedFollowOtherContract.View mView;

    public FeedFollowOtherModule(FeedFollowOtherContract.View view){
        this.mView = view;
    }

    @Provides
    public FeedFollowOtherContract.View provideView(){return mView;}
}
