package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.JuQIngChatContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class JuQingChatModule {
    private JuQIngChatContract.View mView;

    public JuQingChatModule(JuQIngChatContract.View view){
        this.mView = view;
    }

    @Provides
    public JuQIngChatContract.View provideView(){return mView;}
}
