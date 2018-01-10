package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.FeedBagModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.FeedBagFragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = FeedBagModule.class,dependencies = NetComponent.class)
public interface FeedBagComponent {
    void inject(FeedBagFragment feedBagFragment);
}
