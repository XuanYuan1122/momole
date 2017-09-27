package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PhoneJuQingModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.PhoneJuQingListFragment;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PhoneJuQingModule.class,dependencies = NetComponent.class)
public interface PhoneJuQingComponent {
    void inject(PhoneJuQingListFragment fragment);
}
