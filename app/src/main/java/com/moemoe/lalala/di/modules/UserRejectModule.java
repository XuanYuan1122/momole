package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.BadgeContract;
import com.moemoe.lalala.presenter.UserRejectContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class UserRejectModule {
    private UserRejectContract.View mView;

    public UserRejectModule(UserRejectContract.View view){
        this.mView = view;
    }

    @Provides
    public UserRejectContract.View provideView(){return mView;}
}
