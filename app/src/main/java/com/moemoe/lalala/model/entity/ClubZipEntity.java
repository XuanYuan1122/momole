package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public class ClubZipEntity {
    public ApiResult<ArrayList<DocListEntity>> topList;
    public ApiResult<ArrayList<DocListEntity>> hotList;
    public ApiResult<ArrayList<DocListEntity>> docList;

    public ClubZipEntity(ApiResult<ArrayList<DocListEntity>> topList, ApiResult<ArrayList<DocListEntity>> hotList, ApiResult<ArrayList<DocListEntity>> docList) {
        this.topList = topList;
        this.hotList = hotList;
        this.docList = docList;
    }
}
