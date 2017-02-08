package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.LoginModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.LoginActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = LoginModule.class,dependencies = NetComponent.class)
public interface LoginComponent {
    void inject(LoginActivity activity);
}
