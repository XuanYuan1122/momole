package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.CommentListContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class CommentListModule {
    private CommentListContract.View mView;

    public CommentListModule(CommentListContract.View view){
        this.mView = view;
    }

    @Provides
    public CommentListContract.View provideView(){return mView;}
}
