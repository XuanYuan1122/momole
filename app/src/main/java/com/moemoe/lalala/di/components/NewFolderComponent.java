package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.AddAddressModule;
import com.moemoe.lalala.di.modules.NewFolderEditModule;
import com.moemoe.lalala.di.modules.NewFolderModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.AddAddressActivity;
import com.moemoe.lalala.view.activity.NewFolderActivity;
import com.moemoe.lalala.view.activity.NewFolderWenZhangActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = NewFolderModule.class,dependencies = NetComponent.class)
public interface NewFolderComponent {
    void inject(NewFolderActivity activity);
    void inject(NewFolderWenZhangActivity activity);
}
