package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PhoneGroupDetailModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.PhoneGroupDetailV2Fragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PhoneGroupDetailModule.class,dependencies = NetComponent.class)
public interface PhoneGroupDetailComponent {
    void inject(PhoneGroupDetailV2Fragment activity);
}
