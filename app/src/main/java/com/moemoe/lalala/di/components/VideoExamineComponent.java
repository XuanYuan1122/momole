package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.VideoExamineModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.VideoExamineActivity;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = VideoExamineModule.class,dependencies = NetComponent.class)
public interface VideoExamineComponent {
    void inject(VideoExamineActivity activity);
}
