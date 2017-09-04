package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.NewFolderItemContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class NewFileModule {
    private NewFolderItemContract.View mView;

    public NewFileModule(NewFolderItemContract.View view){
        this.mView = view;
    }

    @Provides
    public NewFolderItemContract.View provideView(){return mView;}
}
