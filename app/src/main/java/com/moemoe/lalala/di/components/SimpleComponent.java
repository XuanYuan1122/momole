package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.SimpleModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.FindPasswordActivity;
import com.moemoe.lalala.view.activity.JuBaoActivity;
import com.moemoe.lalala.view.activity.PhoneRegisterActivity;
import com.moemoe.lalala.view.activity.SplashActivity;
import com.moemoe.lalala.view.activity.TagControlActivity;
import com.moemoe.lalala.view.activity.WebViewActivity;
import com.moemoe.lalala.view.fragment.WebViewFragment;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = SimpleModule.class,dependencies = NetComponent.class)
public interface SimpleComponent {
    void inject(PhoneRegisterActivity activity);
    void inject(FindPasswordActivity activity);
    void inject(SplashActivity activity);
    void inject(WebViewActivity activity);
}
