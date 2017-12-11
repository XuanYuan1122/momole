package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PhoneMateModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.PhoneMateSelectV2Fragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PhoneMateModule.class,dependencies = NetComponent.class)
public interface PhoneMateComponent {
    void inject(PhoneMateSelectV2Fragment fragment);
}
