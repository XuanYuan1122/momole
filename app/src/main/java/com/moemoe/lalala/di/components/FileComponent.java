package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.FileModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.FileDetailActivity;
import com.moemoe.lalala.view.activity.ReadActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = FileModule.class,dependencies = NetComponent.class)
public interface FileComponent {
    void inject(FileDetailActivity activity);
    void inject(ReadActivity activity);
}
