package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.PhoneGroupEditContract;
import com.moemoe.lalala.presenter.PhoneGroupListContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PhoneEditGroupModule {
    private PhoneGroupEditContract.View mView;

    public PhoneEditGroupModule(PhoneGroupEditContract.View view){
        this.mView = view;
    }

    @Provides
    public PhoneGroupEditContract.View provideView(){return mView;}
}
