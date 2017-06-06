package com.moemoe.lalala.di.modules;

import com.moemoe.lalala.presenter.MapContract;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yi on 2016/11/27.
 */
@Module
public class MapModule {
    private final MapContract.View mView;

    public MapModule(MapContract.View view){this.mView = view;}

    @Provides
    public MapContract.View provideMapView(){return mView;}
}
