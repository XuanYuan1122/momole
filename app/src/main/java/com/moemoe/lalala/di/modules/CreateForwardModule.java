package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.CreateForwardContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class CreateForwardModule {
    private CreateForwardContract.View mView;

    public CreateForwardModule(CreateForwardContract.View view){
        this.mView = view;
    }

    @Provides
    public CreateForwardContract.View provideView(){return mView;}
}
