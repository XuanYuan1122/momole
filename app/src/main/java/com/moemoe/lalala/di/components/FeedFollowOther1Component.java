package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.FeedFollowOther1Module;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.FeedFollowOther1Fragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = FeedFollowOther1Module.class,dependencies = NetComponent.class)
public interface FeedFollowOther1Component {
    void inject(FeedFollowOther1Fragment fragment);
}
