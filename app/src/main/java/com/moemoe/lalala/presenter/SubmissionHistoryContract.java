package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.SubmissionItemEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface SubmissionHistoryContract {
    interface Presenter extends BasePresenter{
        void loadSubmissionList(int index);
    }

    interface View extends BaseView{
        void onLoadSubmissionListSuccess(ArrayList<SubmissionItemEntity> entities,boolean isPull);
    }
}
