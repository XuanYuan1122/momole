package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.FeedNoticeModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.FeedNoticeActivity;
import com.moemoe.lalala.view.fragment.FeedNoticeFragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = FeedNoticeModule.class,dependencies = NetComponent.class)
public interface FeedNoticeComponent {
    void inject(FeedNoticeFragment activity);
    void inject(FeedNoticeActivity activity);
}
