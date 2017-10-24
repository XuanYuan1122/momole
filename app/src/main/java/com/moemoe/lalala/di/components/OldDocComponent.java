package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.AddAddressModule;
import com.moemoe.lalala.di.modules.OldDocModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.AddAddressActivity;
import com.moemoe.lalala.view.activity.OldDocActivity;
import com.moemoe.lalala.view.activity.WenQuanActivity;
import com.moemoe.lalala.view.fragment.OldDocFragment;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = OldDocModule.class,dependencies = NetComponent.class)
public interface OldDocComponent {
    void inject(WenQuanActivity activity);
    void inject(OldDocActivity activity);
    void inject(OldDocFragment activity);
}
