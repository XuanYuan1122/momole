package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.DelTagEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface TagControlContract {
    interface Presenter extends BasePresenter{
        void deleteTags(DelTagEntity entity,boolean isNew);
    }

    interface View extends BaseView{
        void onDeleteTagsSuccess();
    }
}
