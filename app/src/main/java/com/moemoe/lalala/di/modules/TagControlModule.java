package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.TagControlContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class TagControlModule {
    private TagControlContract.View mView;

    public TagControlModule(TagControlContract.View view){
        this.mView = view;
    }

    @Provides
    public TagControlContract.View provideView(){return mView;}
}
