package com.moemoe.lalala.data;

import com.app.annotation.Column;
import com.app.annotation.Table;

/**
 * Created by Haru on 2016/5/12 0012.
 */
@Table(name = "tag_new")
public class ClubDbbean {
    @Column(name = "uuid",isId = true,autoGen = false)
    public String uuid;
    @Column(name = "tag_json")
    public String tagJson;
    @Column(name = "doc_json")
    public String docsJson;
    @Column(name = "top_json")
    public String topJson;
    @Column(name = "hot_json")
    public String hotJson;
}
