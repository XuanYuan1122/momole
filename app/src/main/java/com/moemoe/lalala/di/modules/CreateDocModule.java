package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.CreateDocContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class CreateDocModule {
    private CreateDocContract.View mView;

    public CreateDocModule(CreateDocContract.View view){
        this.mView = view;
    }

    @Provides
    public CreateDocContract.View provideView(){return mView;}
}
