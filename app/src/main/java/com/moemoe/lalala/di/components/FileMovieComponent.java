package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.FileMovieModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.FileMovieActivity;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = FileMovieModule.class,dependencies = NetComponent.class)
public interface FileMovieComponent {
    void inject(FileMovieActivity activity);
}
