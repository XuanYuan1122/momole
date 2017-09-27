package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.CreateCommentContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class CreateCommentModule {
    private CreateCommentContract.View mView;

    public CreateCommentModule(CreateCommentContract.View view){
        this.mView = view;
    }

    @Provides
    public CreateCommentContract.View provideView(){return mView;}
}
