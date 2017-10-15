package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.SendSubmissionEntity;
import com.moemoe.lalala.model.entity.SubmissionDepartmentEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface SubmissionContract {
    interface Presenter extends BasePresenter{
        void loadDepartment();
        void submission(SendSubmissionEntity entity);
    }

    interface View extends BaseView{
        void onLoadDepartmentSuccess(ArrayList<SubmissionDepartmentEntity> entities);
        void onSubmissionSuccess();
    }
}
