package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.AddAddressModule;
import com.moemoe.lalala.di.modules.PhoneMenuListModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.AddAddressActivity;
import com.moemoe.lalala.view.fragment.PhoneMenuListFragment;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PhoneMenuListModule.class,dependencies = NetComponent.class)
public interface PhoneMenuListComponent {
    void inject(PhoneMenuListFragment fragment);
}
