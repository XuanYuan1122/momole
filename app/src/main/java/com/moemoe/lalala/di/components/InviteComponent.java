package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.AddAddressModule;
import com.moemoe.lalala.di.modules.InviteModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.AddAddressActivity;
import com.moemoe.lalala.view.activity.InviteActivity;
import com.moemoe.lalala.view.activity.InviteAddActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = InviteModule.class,dependencies = NetComponent.class)
public interface InviteComponent {
    void inject(InviteActivity activity);
    void inject(InviteAddActivity activity);
}
