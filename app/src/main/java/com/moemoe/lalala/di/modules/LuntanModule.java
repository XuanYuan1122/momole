package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.LuntanContract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class LuntanModule {
    private LuntanContract.View mView;

    public LuntanModule(LuntanContract.View view){
        this.mView = view;
    }

    @Provides
    public LuntanContract.View provideView(){return mView;}
}
