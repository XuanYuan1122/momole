package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.Luntan2Contract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class Luntan2Module {
    private Luntan2Contract.View mView;

    public Luntan2Module(Luntan2Contract.View view){
        this.mView = view;
    }

    @Provides
    public Luntan2Contract.View provideView(){return mView;}
}
