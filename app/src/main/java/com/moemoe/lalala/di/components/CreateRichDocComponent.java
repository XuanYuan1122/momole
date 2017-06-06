package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.CreateRichDocModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.CreateRichDocActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/29.
 */
@UserScope
@Component(modules = CreateRichDocModule.class,dependencies = NetComponent.class)
public interface CreateRichDocComponent {
    void inject(CreateRichDocActivity activity);
}
