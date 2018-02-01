package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.RecommendTagModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.FeedBagSearchActivity;
import com.moemoe.lalala.view.activity.RecommendTagActivity;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = RecommendTagModule.class,dependencies = NetComponent.class)
public interface RecommendTagComponent {
    void inject(RecommendTagActivity activity);
    void inject(FeedBagSearchActivity activity);
}
