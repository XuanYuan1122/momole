package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.MapModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.MapActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = MapModule.class,dependencies = NetComponent.class)
public interface MapComponent {
    void inject(MapActivity activity);
}
