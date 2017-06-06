package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.ChangePwdModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.ChangePasswordActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/29.
 */
@UserScope
@Component(modules = ChangePwdModule.class,dependencies = NetComponent.class)
public interface ChangePwdComponent {
    void inject(ChangePasswordActivity activity);
}
