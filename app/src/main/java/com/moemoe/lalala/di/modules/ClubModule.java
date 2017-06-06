package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.ClubPostContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class ClubModule {
    private ClubPostContract.View mView;

    public ClubModule(ClubPostContract.View view){
        this.mView = view;
    }

    @Provides
    public ClubPostContract.View provideView(){return mView;}
}
