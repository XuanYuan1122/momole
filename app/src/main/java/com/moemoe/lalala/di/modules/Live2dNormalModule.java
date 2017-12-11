package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.LIve2dNormalContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class Live2dNormalModule {
    private LIve2dNormalContract.View mView;

    public Live2dNormalModule(LIve2dNormalContract.View view){
        this.mView = view;
    }

    @Provides
    public LIve2dNormalContract.View provideView(){return mView;}
}
