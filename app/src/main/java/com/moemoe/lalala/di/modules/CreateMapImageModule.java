package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.CreateMapImageContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class CreateMapImageModule {
    private CreateMapImageContract.View mView;

    public CreateMapImageModule(CreateMapImageContract.View view){
        this.mView = view;
    }

    @Provides
    public CreateMapImageContract.View provideView(){return mView;}
}
