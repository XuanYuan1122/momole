package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.BadgeModule;
import com.moemoe.lalala.di.modules.UserRejectModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.BadgeActivity;
import com.moemoe.lalala.view.activity.UserRejectListActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = UserRejectModule.class,dependencies = NetComponent.class)
public interface UserRejectComponent {
    void inject(UserRejectListActivity activity);
}
