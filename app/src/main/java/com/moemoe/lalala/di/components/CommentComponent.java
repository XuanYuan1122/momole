package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.CommentModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.CoinDetailActivity;
import com.moemoe.lalala.view.activity.QiuMingShanActivity;
import com.moemoe.lalala.view.fragment.WallBlockFragment;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = CommentModule.class,dependencies = NetComponent.class)
public interface CommentComponent {
    void inject(WallBlockFragment fragment);
    void inject(QiuMingShanActivity activity);
    void inject(CoinDetailActivity activity);
}
