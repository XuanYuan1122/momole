package com.moemoe.lalala.di.components;

import com.moemoe.lalala.di.modules.CalendarModule;
import com.moemoe.lalala.di.scopes.UserScope;
import com.moemoe.lalala.view.activity.NewCalendarActivity;

import dagger.Component;

/**
 * Created by yi on 2016/11/27.
 */
@UserScope
@Component(modules = CalendarModule.class,dependencies = NetComponent.class)
public interface CalendarComponent {
    void inject(NewCalendarActivity activity);
}
