package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PersonalListModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.NewsDetailActivity;
import com.moemoe.lalala.view.fragment.PersonalDocFragment;
import com.moemoe.lalala.view.fragment.PersonalFavoriteDocFragment;
import com.moemoe.lalala.view.fragment.PersonalFollowFragment;
import com.moemoe.lalala.view.fragment.PersonalMainFragment;
import com.moemoe.lalala.view.fragment.PersonalMsgFragment;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PersonalListModule.class,dependencies = NetComponent.class)
public interface PersonalListComponent {
    void inject(PersonalMainFragment fragment);
    void inject(PersonalDocFragment fragment);
    void inject(PersonalFavoriteDocFragment fragment);
    void inject(PersonalFollowFragment fragment);
    void inject(PersonalMsgFragment fragment);
    void inject(NewsDetailActivity activity);
}
