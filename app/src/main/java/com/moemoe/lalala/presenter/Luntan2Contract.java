package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.DepartmentGroupEntity;
import com.moemoe.lalala.model.entity.DocResponse;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface Luntan2Contract {
    interface Presenter extends BasePresenter{
        void loadDepartmentGroup(String id);
        void loadDocList(String id,long timestamp);
        void joinAuthor(String id,String name);
    }

    interface View extends BaseView{
        void onLoadGroupSuccess(ArrayList<DepartmentGroupEntity> entity);
        void onLoadDocListSuccess(ArrayList<DocResponse> responses,boolean isPull);
        void onJoinSuccess(String id,String name);
    }
}
