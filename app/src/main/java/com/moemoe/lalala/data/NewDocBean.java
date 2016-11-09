package com.moemoe.lalala.data;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;


import com.app.annotation.Column;
import com.app.annotation.Table;
import com.moemoe.lalala.R;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/4/15 0015.
 */
@Table(name = "doc_1.0")
public class NewDocBean {
    @Column(name = "id",isId = true,autoGen = false)
    public String id;
    @Column(name = "json")
    public String json;
    public String title;
    public String rssId;//订阅ID 不为空则可订阅  为空 不可订阅
    public int comments;
    public String userId;
    public String userName;
    public long userScore;
    public int userLevel;
    public  String userLevelName;
    public int userLevelColor;
    public  String userSex;
    public  Image userIcon;
    public  String createTime;
    public  String updateTime;
    public  ArrayList<DocTag> tags;
    public  ArrayList<DocDetail> details;
    public ArrayList<DocDetail> coinDetails;
    public boolean rssFlag;
    public ShareInfo shareInfo;
    public int coin;
    public int coinPays;
    public boolean favoriteFlag;

    public NewDocBean(){
        userIcon = new Image();
        tags = new ArrayList<>();
        details = new ArrayList<>();
        coinDetails = new ArrayList<>();
    }

    public DocLoadCoinResultBean getCoinResult(Context context,String str){
        DocLoadCoinResultBean bean = new DocLoadCoinResultBean();
        bean.readFromJsonContent(context,str);
        return bean;
    }

    public class DocLoadCoinResultBean {
        public String pay;
        public ArrayList<DocDetail> data;

        public DocLoadCoinResultBean(){
            data = new ArrayList<>();
        }

        public void readFromJsonContent(Context context,String str){
            try {
                JSONObject json = new JSONObject(str);
                pay = json.optString("pay");
                JSONArray coinsArray = json.optJSONArray("coinDetails");
                addJsonToArray(coinsArray,data,context);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class ShareInfo{
        public String icon;
        public String title;
        public String desc;

        public void readFromJsonContent(Context context,JSONObject json){
            if(json != null){
                icon = Otaku.URL_QINIU + json.optString("icon");
                title = json.optString("title");
                desc = json.optString("desc");
            }
        }
    }

    public class DocDetail{
        public    String type;// DOC_TEXT, DOC_IMAGE, DOC_MUSIC, DOC_LINK, DOC_GROUP_LINK, CLUB_DOC (USE OLD COMPOMENT)
        public   DocBase data;
    }

    public class DocBase{

    }

    public class DocText extends DocBase{
        public   String content;

        public void readFromJsonContent(Context context,JSONObject json){
            content = json.optString("content");
        }
    }

    public class DocImage extends DocBase{
        public   Image image;

        public DocImage(){
            image = new Image();
        }

        public void readFromJsonContent(Context context,JSONObject json){
            image.real_path = Otaku.URL_QINIU + json.optString("url");
            image.w = json.optInt("w");
            image.h = json.optInt("h");
            image.path = StringUtils.getUrl(context,json.optString("url"),image.w,image.h);
        }
    }

    public class DocMusic extends DocBase{
        public  String name;
        public  int timestamp;
        public  String url;
        public  Image cover;

        public DocMusic(){
            cover = new Image();
        }

        public void readFromJsonContent(Context context,JSONObject json){
            name = json.optString("name");
            timestamp = json.optInt("timestamp");
            url = json.optString("url");
            cover.path = Otaku.URL_QINIU + json.optString("coverUrl");
            cover.w = json.optInt("coverW");
            cover.h = json.optInt("coverH");
            cover.path = StringUtils.getUrl(context,json.optString("coverUrl"),cover.w,cover.h);
        }
    }

    public class DocLink extends DocBase{
        public  String name;
        public  String url;
        public  Image icon;

        public DocLink(){
            icon = new Image();
        }

        public void readFromJsonContent(Context context,JSONObject json){
            name = json.optString("name");
            url = json.optString("url");
            icon.real_path = Otaku.URL_QINIU + json.optString("icon");
            icon.w = json.optInt("iconW");
            icon.h = json.optInt("iconH");
            icon.path = StringUtils.getUrl(context,json.optString("icon"),icon.w,icon.h);
        }
    }

    public class DocGroupLink extends DocBase{
        public  ArrayList<DocGroupLinkDetail> details;

        public DocGroupLink(){
            details = new ArrayList<>();
        }

        public void readFromJsonContent(Context context,JSONObject json){
            JSONArray jsonArray = json.optJSONArray("details");
            if(jsonArray != null){
                for(int i = 0;i < jsonArray.length();i++){
                    JSONObject object = jsonArray.optJSONObject(i);
                    DocGroupLinkDetail bean = new DocGroupLinkDetail();
                    bean.name = object.optString("name");
                    bean.url = object.optString("url");
                    bean.bgColor = object.optString("bgColor");
                    details.add(bean);
                }
            }
        }

        public class DocGroupLinkDetail{
            public  String name;
            public  String url;
            public  String bgColor;
        }
    }

    /**
     * 解析16进制颜色字符串
     * @param str
     * @param defaultColor
     * @return
     * @author Ben
     */
    protected int readColorStr(String str, int defaultColor) {
        int color = defaultColor;
        if (!TextUtils.isEmpty(str)) {
            try {
                if (!str.startsWith("#")) {
                    str = "#" + str;
                }
                color = Color.parseColor(str);
            } catch (Exception e) {
            }
        }
        return color;
    }

    public void readFromJsonContent(Context context,String jsonStr){
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            id = jsonObject.optString("id");
            title = jsonObject.optString("title");
            rssId = jsonObject.optString("rssId");
            comments = jsonObject.optInt("comments");
            userId = jsonObject.optString("userId");
            userName = jsonObject.optString("username");
            userScore = jsonObject.optInt("userScore");
            userLevel = jsonObject.optInt("userLevel");
            userLevelName = jsonObject.optString("userLevelName");
            userLevelColor = readColorStr(jsonObject.optString("userLevelColor"), context.getResources().getColor(R.color.main_title_cyan));
            userSex = jsonObject.optString("userSex");
            createTime = jsonObject.optString("createTime");
            updateTime = jsonObject.optString("updateTime");
            rssFlag = jsonObject.optBoolean("rssFlag");
            coin = jsonObject.optInt("coin");
            coinPays = jsonObject.optInt("coinPays");
            favoriteFlag = jsonObject.optBoolean("favoriteFlag");

            userIcon.real_path = Otaku.URL_QINIU + jsonObject.optString("userIcon");
            userIcon.w = jsonObject.optInt("userIconW");
            userIcon.h = jsonObject.optInt("userIconH");
            userIcon.path = StringUtils.getUrl(context, jsonObject.optString("userIcon"), userIcon.w, userIcon.h);
            ShareInfo shareInfo1 = new ShareInfo();
            shareInfo1.readFromJsonContent(context,jsonObject.optJSONObject("share"));
            shareInfo = shareInfo1;

            JSONArray jsonArray = jsonObject.optJSONArray("tags");
            if(jsonArray != null){
                for(int i = 0;i < jsonArray.length();i++){
                    JSONObject json = jsonArray.optJSONObject(i);
                    DocTag tag = new DocTag();
                    tag.readFromJsonContent(context,json);
                    tags.add(tag);
                }
            }

            JSONArray detailsArray = jsonObject.optJSONArray("details");
            addJsonToArray(detailsArray,details,context);
            JSONArray coinsArray = jsonObject.optJSONArray("coinDetails");
            addJsonToArray(coinsArray,coinDetails,context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addJsonToArray(JSONArray array,ArrayList<DocDetail> list,Context context){
        if(array != null){
            for(int i = 0;i < array.length();i++){
                JSONObject json = array.optJSONObject(i);
                String type = json.optString("type");
                JSONObject data = json.optJSONObject("data");
                DocDetail docDetail =  new DocDetail();
                docDetail.type = type;
                if(type.equals("DOC_TEXT")){
                    DocText docText = new DocText();
                    docText.readFromJsonContent(context,data);
                    docDetail.data = docText;
                }else if(type.equals("DOC_IMAGE")){
                    DocImage docImage = new DocImage();
                    docImage.readFromJsonContent(context,data);
                    docDetail.data = docImage;
                }else if(type.equals("DOC_MUSIC")){
                    DocMusic docMusic = new DocMusic();
                    docMusic.readFromJsonContent(context,data);
                    docDetail.data = docMusic;
                }else if(type.equals("DOC_LINK")){
                    DocLink docLink = new DocLink();
                    docLink.readFromJsonContent(context,data);
                    docDetail.data = docLink;
                }else if(type.equals("DOC_GROUP_LINK")){
                    DocGroupLink docGroupLink = new DocGroupLink();
                    docGroupLink.readFromJsonContent(context,data);
                    docDetail.data = docGroupLink;
                }else if(type.equals("CLUB_DOC")){

                }
                list.add(docDetail);
            }
        }
    }
}
