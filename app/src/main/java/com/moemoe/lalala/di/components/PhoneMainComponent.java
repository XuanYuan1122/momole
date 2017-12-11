package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PhoneMainModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PhoneMainModule.class,dependencies = NetComponent.class)
public interface PhoneMainComponent {
    void inject(PhoneMainV2Activity activity);
}
