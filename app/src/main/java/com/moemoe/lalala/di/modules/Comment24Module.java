package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.Comment24ListContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class Comment24Module {
    private Comment24ListContract.View mView;

    public Comment24Module(Comment24ListContract.View view){
        this.mView = view;
    }

    @Provides
    public Comment24ListContract.View provideView(){return mView;}
}
