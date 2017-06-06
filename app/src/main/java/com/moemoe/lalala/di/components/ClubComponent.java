package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.ClubModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.ClubPostListActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/29.
 */
@UserScope
@Component(modules = ClubModule.class,dependencies = NetComponent.class)
public interface ClubComponent {
    void inject(ClubPostListActivity activity);
}
