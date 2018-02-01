package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.FeedFollowAllModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.FeedFollowAllFragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = FeedFollowAllModule.class,dependencies = NetComponent.class)
public interface FeedFollowAllComponent {
    void inject(FeedFollowAllFragment fragment);
}
