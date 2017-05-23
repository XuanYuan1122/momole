package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.DocPut;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface CreateDocContract {
    interface Presenter extends BasePresenter{
        void createDoc(DocPut doc,int type);
        void createUploadDoc(DocPut doc, ArrayList<String> paths,int type,int isQiu,int visibleSize,DocPut.DocPutMusic music);
    }

    interface View extends BaseView{
        void onSendSuccess();
    }
}
