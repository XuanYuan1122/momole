package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PhoneMsgListModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.PhoneMsgListV2Fragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PhoneMsgListModule.class,dependencies = NetComponent.class)
public interface PhoneMsgListComponent {
    void inject(PhoneMsgListV2Fragment activity);
}
