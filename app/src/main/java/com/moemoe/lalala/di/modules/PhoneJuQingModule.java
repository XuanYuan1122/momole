package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.PhoneJuQingListContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneJuQingModule {
    private PhoneJuQingListContract.View mView;

    public PhoneJuQingModule(PhoneJuQingListContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneJuQingListContract.View provideView(){return mView;}
}
