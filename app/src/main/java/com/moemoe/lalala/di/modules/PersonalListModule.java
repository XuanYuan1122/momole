package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.PersonalListContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class PersonalListModule {
    private PersonalListContract.View mView;

    public PersonalListModule(PersonalListContract.View view){
        this.mView = view;
    }

    @Provides
    public PersonalListContract.View provideView(){return mView;}
}
