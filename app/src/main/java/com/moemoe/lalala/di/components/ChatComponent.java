package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.ChatModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.ChatActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = ChatModule.class,dependencies = NetComponent.class)
public interface ChatComponent {
    void inject(ChatActivity activity);
}
