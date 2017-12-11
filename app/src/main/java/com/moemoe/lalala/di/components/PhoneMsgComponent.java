package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PhoneMsgModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.fragment.KiraConversationListFragment;

import dagger.Component;

/**
 *
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PhoneMsgModule.class,dependencies = NetComponent.class)
public interface PhoneMsgComponent {
    void inject(KiraConversationListFragment fragment);
}
