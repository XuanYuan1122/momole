package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.BagDynamicModule;
import com.moemoe.lalala.di.modules.BagMyModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.BagMyFragment;
import com.moemoe.lalala.view.fragment.DynamicFragment;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = BagDynamicModule.class,dependencies = NetComponent.class)
public interface BagDynamicComponent {
    void inject(DynamicFragment fragment);
}
