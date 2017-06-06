package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.FilesContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class FileModule {
    private FilesContract.View mView;

    public FileModule(FilesContract.View view){
        this.mView = view;
    }

    @Provides
    public FilesContract.View provideView(){return mView;}
}
