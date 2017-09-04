package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.BagMyContract;
import com.moemoe.lalala.presenter.NewBagContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class NewBagModule {
    private NewBagContract.View mView;

    public NewBagModule(NewBagContract.View view){
        this.mView = view;
    }

    @Provides
    public NewBagContract.View provideView(){return mView;}
}
