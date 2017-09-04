package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.BagMyModule;
import com.moemoe.lalala.di.modules.NewBagModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.NewBagActivity;
import com.moemoe.lalala.view.fragment.BagMyFragment;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = NewBagModule.class,dependencies = NetComponent.class)
public interface NewBagComponent {
    void inject(NewBagActivity fragment);
}
