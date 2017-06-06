package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.SettingContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class SettingModule {
    private SettingContract.View mView;

    public SettingModule(SettingContract.View view){
        this.mView = view;
    }

    @Provides
    public SettingContract.View provideView(){return mView;}
}
