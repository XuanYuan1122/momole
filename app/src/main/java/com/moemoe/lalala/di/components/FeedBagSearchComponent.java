package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.FeedBagSearchModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.FeedBagSearchFragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = FeedBagSearchModule.class,dependencies = NetComponent.class)
public interface FeedBagSearchComponent {
    void inject(FeedBagSearchFragment activity);
}
