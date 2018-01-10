package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.BadgeModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.BadgeActivity;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = BadgeModule.class,dependencies = NetComponent.class)
public interface BadgeComponent {
    void inject(BadgeActivity activity);
}
