package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.PersonalListModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.DocHistoryActivity;
import com.moemoe.lalala.view.activity.NewsDetailActivity;
import com.moemoe.lalala.view.activity.PersonalMsgActivity;
import com.moemoe.lalala.view.fragment.PersonalMainFragment;
import com.moemoe.lalala.view.fragment.SearchBagFragment;
import com.moemoe.lalala.view.fragment.SearchDocFragment;
import com.moemoe.lalala.view.fragment.SearchUserFragment;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = PersonalListModule.class,dependencies = NetComponent.class)
public interface PersonalListComponent {
    void inject(PersonalMainFragment fragment);
    void inject(PersonalMsgActivity fragment);
    void inject(SearchDocFragment fragment);
    void inject(SearchBagFragment fragment);
    void inject(SearchUserFragment fragment);
    void inject(NewsDetailActivity activity);
    void inject(DocHistoryActivity activity);
}
