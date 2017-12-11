package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PersonalModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.PersonalV2Activity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PersonalModule.class,dependencies = NetComponent.class)
public interface PersonalComponent {
    void inject(PersonalV2Activity activity);
}
