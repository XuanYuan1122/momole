package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.CommonRequest;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface ApplyAdminContract {
    interface Presenter extends BasePresenter{
        void applyAdmin(CommonRequest request);
    }

    interface View extends BaseView{
        void onApplyAdminSuccess();
    }
}
