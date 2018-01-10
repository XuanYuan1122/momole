package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.RecommendTagEntity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface RecommendTagContract {
    interface Presenter extends BasePresenter{
        void loadRecommendTag(String folderType);
        void loadKeyWordTag(String keyWord);
    }

    interface View extends BaseView{
        void onLoadRecommendTagSuccess(ArrayList<RecommendTagEntity> entities);
        void onLoadKeyWordTagSuccess(ArrayList<RecommendTagEntity> entities);
    }
}
