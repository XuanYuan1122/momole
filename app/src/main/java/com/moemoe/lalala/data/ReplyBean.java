package com.moemoe.lalala.data;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Haru on 2016/5/12 0012.
 */
@Table(name = "t_reply_v1.0")
public class ReplyBean {
    @Column(name ="id",isId = true,autoGen = false)
    public String uuid;
    @Column(name = "json")
    public String json;

    @SerializedName("content")
    private String content;
    @SerializedName("createTime")
    private String createTime;
    @SerializedName("fromIcon")
    private Image fromIcon;
    @SerializedName("fromName")
    private String fromName;
    @SerializedName("schema")
    private String schema;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Image getFromIcon() {
        return fromIcon;
    }

    public void setFromIcon(Image fromIcon) {
        this.fromIcon = fromIcon;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
