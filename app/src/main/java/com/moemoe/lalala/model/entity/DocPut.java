package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/7/11 0011.
 */
public class DocPut {
    public String title;
    public ArrayList<String> tags;
    public ArrayList<DocDetail> details;
    public DocPutCoin coin;
    public String bagFolderId;

    public DocPut(){
        tags = new ArrayList<>();
        details = new ArrayList<>();
        coin = new DocPutCoin();
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
