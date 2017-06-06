package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.EditAccountContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class EditAccountModule {
    private EditAccountContract.View mView;

    public EditAccountModule(EditAccountContract.View view){
        this.mView = view;
    }

    @Provides
    public EditAccountContract.View provideView(){return mView;}
}
