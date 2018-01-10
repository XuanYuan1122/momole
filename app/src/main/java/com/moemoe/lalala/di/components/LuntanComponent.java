package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.LuntanModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.LuntanActivity;
import com.moemoe.lalala.view.fragment.LuntanAllFragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = LuntanModule.class,dependencies = NetComponent.class)
public interface LuntanComponent {
    void inject(LuntanActivity activity);
    void inject(LuntanAllFragment activity);
}
