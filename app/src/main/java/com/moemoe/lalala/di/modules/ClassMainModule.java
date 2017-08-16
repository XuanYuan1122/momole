package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.BadgeContract;
import com.moemoe.lalala.presenter.ClassMainContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class ClassMainModule {
    private ClassMainContract.View mView;

    public ClassMainModule(ClassMainContract.View view){
        this.mView = view;
    }

    @Provides
    public ClassMainContract.View provideView(){return mView;}
}
