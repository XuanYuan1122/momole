package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.NewFolderEditContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class NewFolderEditModule {
    private NewFolderEditContract.View mView;

    public NewFolderEditModule(NewFolderEditContract.View view){
        this.mView = view;
    }

    @Provides
    public NewFolderEditContract.View provideView(){return mView;}
}
