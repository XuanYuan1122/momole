package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.AddressContract;
import com.moemoe.lalala.presenter.SubmissionHistoryContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/29.
 */
@Module
public class SubmissionHistoryModule {
    private SubmissionHistoryContract.View mView;

    public SubmissionHistoryModule(SubmissionHistoryContract.View view){
        this.mView = view;
    }

    @Provides
    public SubmissionHistoryContract.View provideView(){return mView;}
}
