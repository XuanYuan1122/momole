package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.CreateTrashModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.CreateTrashActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = CreateTrashModule.class,dependencies = NetComponent.class)
public interface CreateTrashComponent {
    void inject(CreateTrashActivity activity);
}
