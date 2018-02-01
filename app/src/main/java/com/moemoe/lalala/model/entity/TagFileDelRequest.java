package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * 标签删除请求
 * Created by yi on 2018/1/30.
 */

public class TagFileDelRequest {
    private String tagId;
    private ArrayList<RemoveId> removeIds;

    public TagFileDelRequest(){
        removeIds = new ArrayList<>();
    }

    public void addRemove(String id,String targetId){
        removeIds.add(new RemoveId(id,targetId));
    }

    public class RemoveId{
        private String parentId;
        private String targetId;

        public RemoveId(String parentId, String targetId) {
            this.parentId = parentId;
            this.targetId = targetId;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public ArrayList<RemoveId> getRemoveIds() {
        return removeIds;
    }

    public void setRemoveIds(ArrayList<RemoveId> removeIds) {
        this.removeIds = removeIds;
    }
}
