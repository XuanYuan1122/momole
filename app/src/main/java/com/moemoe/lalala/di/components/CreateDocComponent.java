package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.CreateDocModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.CreateNormalDocActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/29.
 */
@UserScope
@Component(modules = CreateDocModule.class,dependencies = NetComponent.class)
public interface CreateDocComponent {
    void inject(CreateNormalDocActivity activity);
}
