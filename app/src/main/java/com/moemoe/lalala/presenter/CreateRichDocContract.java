package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.DocPut;

/**
 * Created by yi on 2016/11/29.
 */

public interface CreateRichDocContract {
    interface Presenter extends BasePresenter{
        void createDoc(DocPut doc, int type);
    }

    interface View extends BaseView{
        void onSendSuccess();
    }
}
