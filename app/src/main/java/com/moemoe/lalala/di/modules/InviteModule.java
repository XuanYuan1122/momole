package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.InviteContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class InviteModule {
    private InviteContract.View mView;

    public InviteModule(InviteContract.View view){
        this.mView = view;
    }

    @Provides
    public InviteContract.View provideView(){return mView;}
}
