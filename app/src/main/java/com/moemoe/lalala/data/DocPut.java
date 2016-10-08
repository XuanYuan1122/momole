package com.moemoe.lalala.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/7/11 0011.
 */
public class DocPut {
    public String title;
    public ArrayList<String> tags;
    public ArrayList<DocDetail> details;
    public DocPutCoin coin;

    public DocPut(){
        tags = new ArrayList<>();
        details = new ArrayList<>();
        coin = new DocPutCoin();
    }

    public class DocPutCoin{
        public int coin;
        public ArrayList<DocDetail> data;

        public DocPutCoin(){
            data = new ArrayList<>();
        }
    }

    public static class DocDetail{
        public String type;
        public DocBase data;

        public DocDetail(String type,DocBase data){
            this.type = type;
            this.data = data;
        }
    }

    public static class DocBase{}

    public static class DocPutText extends DocBase{
        public String content;
    }

    public static class DocPutImage extends DocBase{
        public String url;
        public int w;
        public int h;
    }

    public static class DocPutMusic extends DocBase{
        public String name;
        public int timestamp;
        public String url;
        public String coverUrl;
        public int coverW;
        public int coverH;
    }

    public class DocPutLink extends DocBase{
        public String name;
        public String url;
        public int iconW;
        public int iconH;
    }

    public class DocPutGroupLink extends DocBase{
        public String title;
        public ArrayList<DocPutGroupLinkDetail> details;
    }

    public class DocPutGroupLinkDetail{
        public String name;
        public String url;
        public String bgColor;
    }

    public String toJsonString(){
        try {
            JSONObject res = new JSONObject();
            res.put("title",title);
            JSONArray array1 = new JSONArray();
            for(String tag : tags){
                array1.put(tag);
            }
            res.put("tags",array1);
            JSONArray array = new JSONArray();
            addDataToJsonArray(array,details);
            res.put("details",array);
            JSONObject json = new JSONObject();
            json.put("coin",coin.coin);
            JSONArray coinArray = new JSONArray();
            addDataToJsonArray(coinArray,coin.data);
            json.put("details",coinArray);
            res.put("coin",json);
            String temp = res.toString();
            return res.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void addDataToJsonArray(JSONArray array,ArrayList<DocDetail> docDetails){
        try {
            for (DocDetail detail : docDetails){
                JSONObject json = new JSONObject();
                json.put("type",detail.type);
                JSONObject doc = new JSONObject();
                if(detail.data instanceof DocPutText){
                    doc.put("content",((DocPutText)detail.data).content);
                }else if(detail.data instanceof DocPutImage){
                    doc.put("url",((DocPutImage)detail.data).url);
                    doc.put("w",((DocPutImage)detail.data).w);
                    doc.put("h",((DocPutImage)detail.data).h);
                }else if(detail.data instanceof DocPutMusic){
                    doc.put("name",((DocPutMusic)detail.data).name);
                    doc.put("timestamp",((DocPutMusic)detail.data).timestamp);
                    doc.put("url",((DocPutMusic)detail.data).url);
                    doc.put("coverUrl",((DocPutMusic)detail.data).coverUrl);
                    doc.put("coverW",((DocPutMusic)detail.data).coverW);
                    doc.put("coverH",((DocPutMusic)detail.data).coverH);
                }else if(detail.data instanceof DocPutLink){

                }else if(detail.data instanceof DocPutGroupLink){

                }
                json.put("data",doc);
                array.put(json);
            }
        }catch (Exception e){

        }

    }
}
