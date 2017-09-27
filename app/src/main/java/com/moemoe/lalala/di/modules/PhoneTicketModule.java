package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.PhoneTicketContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneTicketModule {
    private PhoneTicketContract.View mView;

    public PhoneTicketModule(PhoneTicketContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneTicketContract.View provideView(){return mView;}
}
