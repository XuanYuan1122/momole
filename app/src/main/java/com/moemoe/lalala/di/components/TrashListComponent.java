package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.TrashListModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.MyTrashFragment;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = TrashListModule.class,dependencies = NetComponent.class)
public interface TrashListComponent {
    void inject(MyTrashFragment fragment);
}
