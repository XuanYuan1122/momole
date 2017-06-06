package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.LoginContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class LoginModule {
    private LoginContract.View mView;

    public LoginModule(LoginContract.View view){
        this.mView = view;
    }

    @Provides
    public LoginContract.View provideView(){return mView;}
}
