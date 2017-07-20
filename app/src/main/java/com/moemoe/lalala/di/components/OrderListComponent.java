package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.BadgeModule;
import com.moemoe.lalala.di.modules.OrderListModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.BadgeActivity;
import com.moemoe.lalala.view.activity.OrderListActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = OrderListModule.class,dependencies = NetComponent.class)
public interface OrderListComponent {
    void inject(OrderListActivity activity);
}
