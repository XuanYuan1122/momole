package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.AddAddressModule;
import com.moemoe.lalala.di.modules.Comment24Module;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.presenter.Comment24ListContract;
import com.moemoe.lalala.view.activity.AddAddressActivity;
import com.moemoe.lalala.view.activity.Comment24ListActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = Comment24Module.class,dependencies = NetComponent.class)
public interface Comment24Component {
    void inject(Comment24ListActivity activity);
}
