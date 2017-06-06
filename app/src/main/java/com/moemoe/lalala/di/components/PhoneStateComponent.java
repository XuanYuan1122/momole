package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PhoneStateModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.PhoneStateCheckActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PhoneStateModule.class,dependencies = NetComponent.class)
public interface PhoneStateComponent {
    void inject(PhoneStateCheckActivity activity);
}
