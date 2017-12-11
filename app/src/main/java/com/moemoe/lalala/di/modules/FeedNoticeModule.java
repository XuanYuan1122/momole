package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.FeedNoticeContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class FeedNoticeModule {
    private FeedNoticeContract.View mView;

    public FeedNoticeModule(FeedNoticeContract.View view){
        this.mView = view;
    }

    @Provides
    public FeedNoticeContract.View provideView(){return mView;}
}
