package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.Luntan2Module;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.LuntanFragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = Luntan2Module.class,dependencies = NetComponent.class)
public interface Luntan2Component {
    void inject(LuntanFragment activity);
}
