package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.RecommendTagContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class RecommendTagModule {
    private RecommendTagContract.View mView;

    public RecommendTagModule(RecommendTagContract.View view){
        this.mView = view;
    }

    @Provides
    public RecommendTagContract.View provideView(){return mView;}
}
