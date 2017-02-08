package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.SettingModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.SecretSettingActivity;
import com.moemoe.lalala.view.activity.SettingActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules =SettingModule.class,dependencies = NetComponent.class)
public interface SettingComponent {
    void inject(SettingActivity activity);
    void inject(SecretSettingActivity activity);
}
