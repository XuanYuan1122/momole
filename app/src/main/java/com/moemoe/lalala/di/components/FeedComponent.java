package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.AddAddressModule;
import com.moemoe.lalala.di.modules.FeedModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.AddAddressActivity;
import com.moemoe.lalala.view.activity.PersonalFavoriteDynamicActivity;
import com.moemoe.lalala.view.fragment.NewDiscoverMainFragment;
import com.moemoe.lalala.view.fragment.NewFollowMainFragment;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = FeedModule.class,dependencies = NetComponent.class)
public interface FeedComponent {
    void inject(NewFollowMainFragment followMainFragment);
    void inject(NewDiscoverMainFragment followMainFragment);
    void inject(PersonalFavoriteDynamicActivity activity);
}
