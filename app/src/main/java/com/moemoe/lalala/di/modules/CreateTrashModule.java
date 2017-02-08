package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.CreateTrashContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class CreateTrashModule {
    private CreateTrashContract.View mView;

    public CreateTrashModule(CreateTrashContract.View view){
        this.mView = view;
    }

    @Provides
    public CreateTrashContract.View provideView(){return mView;}
}
