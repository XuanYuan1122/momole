package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.NewFolderContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class NewFolderModule {
    private NewFolderContract.View mView;

    public NewFolderModule(NewFolderContract.View view){
        this.mView = view;
    }

    @Provides
    public NewFolderContract.View provideView(){return mView;}
}
