package com.moemoe.lalala.data;

import com.app.annotation.Column;
import com.app.annotation.Table;

/**
 * Created by Haru on 2016/5/11 0011.
 */
@Table(name = "friend_doc_old")
public class FriendDocBean {
    @Column(name = "uuid",isId = true,autoGen = false)
    public String uuid;
    @Column(name = "doc_json")
    public String docJson;
    @Column(name = "club_json")
    public String clubJson;
}
