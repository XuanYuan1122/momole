package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.BagModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.BagActivity;
import com.moemoe.lalala.view.activity.BagEditActivity;
import com.moemoe.lalala.view.activity.FolderActivity;
import com.moemoe.lalala.view.activity.FolderSelectActivity;
import com.moemoe.lalala.view.activity.MangaActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = BagModule.class,dependencies = NetComponent.class)
public interface BagComponent {
    void inject(BagActivity activity);
    void inject(BagEditActivity activity);
    void inject(FolderActivity activity);
    void inject(FolderSelectActivity activity);
    void inject(MangaActivity activity);
}
