package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.FeedFollowOther2Contract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class FeedFollowOther2Module {
    private FeedFollowOther2Contract.View mView;

    public FeedFollowOther2Module(FeedFollowOther2Contract.View view){
        this.mView = view;
    }

    @Provides
    public FeedFollowOther2Contract.View provideView(){return mView;}
}
