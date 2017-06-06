package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.EditAccountModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.NewEditAccountActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/29.
 */
@UserScope
@Component(modules = EditAccountModule.class,dependencies = NetComponent.class)
public interface EditAccountComponent {
    void inject(NewEditAccountActivity activity);
}
