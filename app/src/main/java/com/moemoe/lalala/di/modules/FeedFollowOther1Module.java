package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.FeedFollowOther1Contract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class FeedFollowOther1Module {
    private FeedFollowOther1Contract.View mView;

    public FeedFollowOther1Module(FeedFollowOther1Contract.View view){
        this.mView = view;
    }

    @Provides
    public FeedFollowOther1Contract.View provideView(){return mView;}
}
