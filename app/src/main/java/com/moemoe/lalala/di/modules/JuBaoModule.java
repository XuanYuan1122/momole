package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.JuBaoContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class JuBaoModule {
    private JuBaoContract.View mView;

    public JuBaoModule(JuBaoContract.View view){
        this.mView = view;
    }

    @Provides
    public JuBaoContract.View provideView(){return mView;}
}
