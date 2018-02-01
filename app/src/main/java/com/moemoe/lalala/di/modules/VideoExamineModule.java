package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.VideoExamineContract;

import dagger.Module;
import dagger.Provides;

/**
 *
 * Created by yi on 2016/11/29.
 */
@Module
public class VideoExamineModule {
    private VideoExamineContract.View mView;

    public VideoExamineModule(VideoExamineContract.View view){
        this.mView = view;
    }

    @Provides
    public VideoExamineContract.View provideView(){return mView;}
}
