package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.CreateRichDocContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class CreateRichDocModule {
    private CreateRichDocContract.View mView;

    public CreateRichDocModule(CreateRichDocContract.View view){
        this.mView = view;
    }

    @Provides
    public CreateRichDocContract.View provideView(){return mView;}
}
