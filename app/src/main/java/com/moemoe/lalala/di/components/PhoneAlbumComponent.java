package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.AddAddressModule;
import com.moemoe.lalala.di.modules.PhoneAlbumModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.AddAddressActivity;
import com.moemoe.lalala.view.fragment.PhoneAlbumFragment;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PhoneAlbumModule.class,dependencies = NetComponent.class)
public interface PhoneAlbumComponent {
    void inject(PhoneAlbumFragment fragment);
}
