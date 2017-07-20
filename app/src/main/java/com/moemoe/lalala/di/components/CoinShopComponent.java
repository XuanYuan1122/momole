package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.BadgeModule;
import com.moemoe.lalala.di.modules.CoinShopModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.BadgeActivity;
import com.moemoe.lalala.view.activity.CoinShopActivity;
import com.moemoe.lalala.view.activity.ShopDetailActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = CoinShopModule.class,dependencies = NetComponent.class)
public interface CoinShopComponent {
    void inject(CoinShopActivity activity);
    void inject(ShopDetailActivity activity);
}
