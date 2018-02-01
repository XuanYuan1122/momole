package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.ApplyAdminModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.ApplyAdminActivity;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = ApplyAdminModule.class,dependencies = NetComponent.class)
public interface ApplyAdminComponent {
    void inject(ApplyAdminActivity activity);
}
