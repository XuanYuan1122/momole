package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.PhoneGroupMemberContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneGroupMemberModule {
    private PhoneGroupMemberContract.View mView;

    public PhoneGroupMemberModule(PhoneGroupMemberContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneGroupMemberContract.View provideView(){return mView;}
}
