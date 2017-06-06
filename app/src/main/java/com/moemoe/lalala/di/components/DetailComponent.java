package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.DetailModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = DetailModule.class,dependencies = NetComponent.class)
public interface DetailComponent {
    void inject(NewDocDetailActivity activity);
}
