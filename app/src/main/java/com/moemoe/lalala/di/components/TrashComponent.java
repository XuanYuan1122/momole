package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.TrashModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.ImageTrashActivity;
import com.moemoe.lalala.view.activity.TrashActivity;
import com.moemoe.lalala.view.activity.TrashDetailActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = TrashModule.class,dependencies = NetComponent.class)
public interface TrashComponent {
    void inject(TrashActivity activity);
    void inject(ImageTrashActivity activity);
    void inject(TrashDetailActivity activity);
}
