package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PhoneEditGroupModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.PhoneEditGroupV2Fragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PhoneEditGroupModule.class,dependencies = NetComponent.class)
public interface PhoneEditGroupComponent {
    //void inject(PhoneEditGroupFragment activity);
    void inject(PhoneEditGroupV2Fragment activity);
}
