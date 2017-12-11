package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.GameModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.SanGuoActivity;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = GameModule.class,dependencies = NetComponent.class)
public interface GameComponent {
    void inject(SanGuoActivity activity);
}
