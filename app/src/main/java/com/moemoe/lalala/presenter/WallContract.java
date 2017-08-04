package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface WallContract {
    interface Presenter extends BasePresenter{
        void createLabel(TagSendEntity entity);
    }

    interface View extends BaseView{
        void onCreateLabel(String s,String name);
    }
}
