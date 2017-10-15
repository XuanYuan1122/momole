package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.SubmissionContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class SubmissionModule {
    private SubmissionContract.View mView;

    public SubmissionModule(SubmissionContract.View view){
        this.mView = view;
    }

    @Provides
    public SubmissionContract.View provideView(){return mView;}
}
