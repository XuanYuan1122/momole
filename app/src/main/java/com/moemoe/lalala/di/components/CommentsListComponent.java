package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.CommentSecListModule;
import com.moemoe.lalala.di.modules.CommentsListModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.CommentListActivity;
import com.moemoe.lalala.view.activity.CommentSecListActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = CommentsListModule.class,dependencies = NetComponent.class)
public interface CommentsListComponent {
    void inject(CommentListActivity activity);
}
