package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.PhoneAlbumContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneAlbumModule {
    private PhoneAlbumContract.View mView;

    public PhoneAlbumModule(PhoneAlbumContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneAlbumContract.View provideView(){return mView;}
}
