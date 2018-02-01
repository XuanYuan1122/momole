package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.OfficialTag;
import com.moemoe.lalala.model.entity.SimpleListSend;
import com.moemoe.lalala.model.entity.SimpleRequestEntity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface SelectTagContract {
    interface Presenter extends BasePresenter{
        void loadOfficialTags();
        void saveUserTags(SimpleListSend entity);
    }

    interface View extends BaseView{
        void onLoadOfficialTags(ArrayList<OfficialTag> tags);
        void onSaveUserTagsSuccess();
    }
}
