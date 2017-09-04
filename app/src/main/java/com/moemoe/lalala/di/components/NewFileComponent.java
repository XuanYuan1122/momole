package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.AddAddressModule;
import com.moemoe.lalala.di.modules.NewFileModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.AddAddressActivity;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHua2Activity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = NewFileModule.class,dependencies = NetComponent.class)
public interface NewFileComponent {
    void inject(NewFileCommonActivity activity);
    void inject(NewFileManHua2Activity activity);
    void inject(NewFileManHuaActivity activity);
    void inject(NewFileXiaoshuoActivity activity);
}
