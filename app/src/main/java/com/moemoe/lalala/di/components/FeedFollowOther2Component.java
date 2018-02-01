package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.FeedFollowOther2Module;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.FeedFollowOther2Fragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = FeedFollowOther2Module.class,dependencies = NetComponent.class)
public interface FeedFollowOther2Component {
    void inject(FeedFollowOther2Fragment fragment);
}
