package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.SnowModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.SelectFukuActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = SnowModule.class,dependencies = NetComponent.class)
public interface SnowComponent {
    void inject(SelectFukuActivity activity);
}
