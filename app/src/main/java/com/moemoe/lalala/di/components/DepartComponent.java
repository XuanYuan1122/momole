package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.DepartModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.DepartmentV3Activity;
import com.moemoe.lalala.view.fragment.DepartmentFragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/29.
 */
@UserScope
@Component(modules = DepartModule.class,dependencies = NetComponent.class)
public interface DepartComponent {
   // void inject(DepartmentV2Activity activity);
    void inject(DepartmentV3Activity activity);
    void inject(DepartmentFragment activity);
}
