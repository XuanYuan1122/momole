package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.CommentDetailModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.CommentDetailActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = CommentDetailModule.class,dependencies = NetComponent.class)
public interface CommentDetailComponent {
    void inject(CommentDetailActivity activity);
}
