package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.AddAddressModule;
import com.moemoe.lalala.di.modules.Live2dNormalModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.AddAddressActivity;
import com.moemoe.lalala.view.activity.Live2dNormalActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = Live2dNormalModule.class,dependencies = NetComponent.class)
public interface Live2dNoramlComponent {
    void inject(Live2dNormalActivity activity);
}
