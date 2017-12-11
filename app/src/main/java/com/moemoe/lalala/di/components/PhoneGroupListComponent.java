package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PhoneGroupListModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.PhoneGroupListV2Fragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PhoneGroupListModule.class,dependencies = NetComponent.class)
public interface PhoneGroupListComponent {
    void inject(PhoneGroupListV2Fragment activity);
}
