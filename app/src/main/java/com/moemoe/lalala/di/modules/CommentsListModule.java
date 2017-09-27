package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.CommentSecListContract;
import com.moemoe.lalala.presenter.CommentsListContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class CommentsListModule {
    private CommentsListContract.View mView;

    public CommentsListModule(CommentsListContract.View view){
        this.mView = view;
    }

    @Provides
    public CommentsListContract.View provideView(){return mView;}
}
