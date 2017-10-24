package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.DepartModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.DepartmentActivity;
import com.moemoe.lalala.view.activity.WenQuanActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/29.
 */
@UserScope
@Component(modules = DepartModule.class,dependencies = NetComponent.class)
public interface DepartComponent {
    void inject(DepartmentActivity activity);
}
