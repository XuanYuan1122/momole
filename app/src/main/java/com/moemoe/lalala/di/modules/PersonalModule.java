package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.PersonalContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PersonalModule {
    private PersonalContract.View mView;

    public PersonalModule(PersonalContract.View view){
        this.mView = view;
    }

    @Provides
    public PersonalContract.View provideView(){return mView;}
}
