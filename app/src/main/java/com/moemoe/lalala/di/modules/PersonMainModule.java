package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.PersonMainContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PersonMainModule {
    private PersonMainContract.View mView;

    public PersonMainModule(PersonMainContract.View view){
        this.mView = view;
    }

    @Provides
    public PersonMainContract.View provideView(){return mView;}
}
