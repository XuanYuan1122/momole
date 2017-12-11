package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.Live2dMusicEntity;
import com.moemoe.lalala.model.entity.ShareLive2dEntity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface Live2dContract {
    interface Presenter extends BasePresenter{
        void loadMusicList();
        void loadShareLive2dList();
    }

    interface View extends BaseView{
        void onLoadMusicListSuccess(ArrayList<Live2dMusicEntity> entities);
        void onLoadShareListSuccess(ArrayList<ShareLive2dEntity> entities);
    }
}
