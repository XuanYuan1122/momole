package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.BadgeContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class AddAddressModule {
    private AddressContract.View mView;

    public AddAddressModule(AddressContract.View view){
        this.mView = view;
    }

    @Provides
    public AddressContract.View provideView(){return mView;}
}
