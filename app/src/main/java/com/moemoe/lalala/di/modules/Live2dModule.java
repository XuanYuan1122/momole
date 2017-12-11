package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.Live2dContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class Live2dModule {
    private Live2dContract.View mView;

    public Live2dModule(Live2dContract.View view){
        this.mView = view;
    }

    @Provides
    public Live2dContract.View provideView(){return mView;}
}
