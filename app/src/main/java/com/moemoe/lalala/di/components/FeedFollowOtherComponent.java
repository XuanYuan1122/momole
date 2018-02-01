package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.FeedFollowOtherModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.FeedFollowOtherFragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = FeedFollowOtherModule.class,dependencies = NetComponent.class)
public interface FeedFollowOtherComponent {
    void inject(FeedFollowOtherFragment fragment);
}
