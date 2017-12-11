package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.JuQingChatModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.MapEventNewActivity;
import com.moemoe.lalala.view.fragment.JuQingChatV2Fragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = JuQingChatModule.class,dependencies = NetComponent.class)
public interface JuQIngChatComponent {
    void inject(JuQingChatV2Fragment activity);
    void inject(MapEventNewActivity activity);
}
