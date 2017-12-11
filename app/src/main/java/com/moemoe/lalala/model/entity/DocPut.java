package com.moemoe.lalala.model.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Haru on 2016/7/11 0011.
 */
public class DocPut {
    public String bagFolderId;
    public DocPutCoin coin;
    public ArrayList<DocDetail> details;
    public String title;
    public ArrayList<String> tags;
    public String docType;
    public String docTypeSchema;
    public boolean coinComment;
    public Set<String> userIds;
    public String cover;
    public String departmentId;

    public DocPut(){
        tags = new ArrayList<>();
        details = new ArrayList<>();
        coin = new DocPutCoin();
        userIds = new HashSet<>();
    }

    public class DocPutCoin{
        public int coin;
        public ArrayList<DocDetail> details;

        public DocPutCoin(){
            details = new ArrayList<>();
        }
    }

    public static class DocDetail<T>{
        public String type;
        public T data;

        public DocDetail(String type, T data){
            this.type = type;
            this.data = data;
        }
    }

    public static class DocPutText{
        public String text;
    }

    public static class DocPutImage{
        public String path;
        public int w;
        public int h;
        public long size;
    }

    public static class DocPutMusic{
        public String name;
        public int timestamp;
        public String url;
        public Image cover;

        public DocPutMusic(){
            cover = new Image();
        }
    }
}
