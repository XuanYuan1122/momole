package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.CommentSecListContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class CommentSecListModule {
    private CommentSecListContract.View mView;

    public CommentSecListModule(CommentSecListContract.View view){
        this.mView = view;
    }

    @Provides
    public CommentSecListContract.View provideView(){return mView;}
}
