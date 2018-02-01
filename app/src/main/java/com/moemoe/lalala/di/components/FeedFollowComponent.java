package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.AddAddressModule;
import com.moemoe.lalala.di.modules.FeedFollowModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.AddAddressActivity;
import com.moemoe.lalala.view.fragment.FeedFollowV3Fragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = FeedFollowModule.class,dependencies = NetComponent.class)
public interface FeedFollowComponent {
    void inject(FeedFollowV3Fragment feedFollowV3Fragment);
}
