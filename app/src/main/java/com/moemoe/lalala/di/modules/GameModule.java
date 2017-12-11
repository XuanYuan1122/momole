package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.GameContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class GameModule {
    private GameContract.View mView;

    public GameModule(GameContract.View view){
        this.mView = view;
    }

    @Provides
    public GameContract.View provideView(){return mView;}
}
