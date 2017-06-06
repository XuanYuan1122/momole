package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.ChatContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class ChatModule {
    private ChatContract.View mView;

    public ChatModule(ChatContract.View view){
        this.mView = view;
    }

    @Provides
    public ChatContract.View provideView(){return mView;}
}
