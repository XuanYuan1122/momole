package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PhoneTicketModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.PhoneTicketV2Fragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PhoneTicketModule.class,dependencies = NetComponent.class)
public interface PhoneTicketComponent {
    void inject(PhoneTicketV2Fragment fragment);
}
