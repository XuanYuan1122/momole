package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.NewDocListEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface ClassMainContract {
    interface Presenter extends BasePresenter{
        void loadDocList(int index, boolean change, boolean isPull);
    }

    interface View extends BaseView{
        void onChangeSuccess(ArrayList<DocListEntity> entities);
        void onLoadDocListSuccess(ArrayList<DocListEntity> entities, boolean isPull);
    }
}
