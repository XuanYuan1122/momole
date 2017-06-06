package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.ColumnModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.ColumnDetailActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/29.
 */
@UserScope
@Component(modules = ColumnModule.class,dependencies = NetComponent.class)
public interface ColumnComponent {
    void inject(ColumnDetailActivity activity);
}
