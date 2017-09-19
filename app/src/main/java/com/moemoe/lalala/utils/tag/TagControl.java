package com.moemoe.lalala.utils.tag;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.moemoe.lalala.model.entity.tag.BaseTag;
import com.moemoe.lalala.model.entity.tag.ImageUrlSpan;
import com.moemoe.lalala.model.entity.tag.UserUrlSpan;
import com.moemoe.lalala.utils.BaseUrlSpan;
import com.moemoe.lalala.utils.CustomUrlSpan;
import com.moemoe.lalala.utils.DoubleKeyValueMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yi on 2017/9/18.
 */

public class TagControl {

    private static TagControl instance;
   // private static Map<String,String> tags;
    private static ArrayList<BaseTag> tags;

//    static {
//        tags = new HashMap<>();
//        tags.put("at_user","user_id");
//        tags.put("kira_img","url");
//    }

    private TagControl(){}

    public static TagControl getInstance(){
        if(instance == null){
            synchronized (TagControl.class){
                if(instance == null){
                    instance = new TagControl();
                }
            }
        }
        return instance;
    }

    public void init(Context context){
        tags = new ArrayList<>();
        BaseTag tag = new BaseTag();
        tag.setTag("at_user");
        tag.setSpan(new UserUrlSpan(context,tag));
        tags.add(tag);
        tag = new BaseTag();
        tag.setTag("kira_img");
        tag.setSpan(new ImageUrlSpan(context,tag));
        tags.add(tag);
    }

    private void checkTag(BaseTag tag){
        if(tags == null) tags = new ArrayList<>();
        BaseTag temp = null;
        for(BaseTag tag1 : tags){
            if(tag1.getTag().equals(tag.getTag())){
                temp = tags.remove(tags.indexOf(tag1));
                break;
            }
        }
        if(temp == null){
            tags.add(tag);
        }else {
            temp.setSpan(tag.getSpan());
            temp.setAttrs(tag.getAttrs());
            tags.add(temp);
        }
    }

    public String paresToString(CharSequence charSequence){
        String res;
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(charSequence);
        BaseUrlSpan[] spen = stringBuilder.getSpans(0,stringBuilder.length(),BaseUrlSpan.class);
        res = stringBuilder.toString();
        if(spen.length > 0){
            int step = 0;
            for (BaseUrlSpan span : spen) {
                int before = res.length();
                String beginStr = res.substring(0,stringBuilder.getSpanStart(span) + step);
                String str = res.substring(stringBuilder.getSpanStart(span) + step,stringBuilder.getSpanEnd(span) + step);
                String endStr = res.substring(stringBuilder.getSpanEnd(span) + step);
                String tmp = "<" + span.getmTag().getTag();
                for(String attr : span.getmTag().getAttrs().keySet()){
                    tmp += " " + attr + "=" + span.getmTag().getAttrs().get(attr);
                }
                tmp += ">" + str.replace(" ","") + "</" + span.getmTag().getTag() + ">";
                res = beginStr + tmp + endStr;
                step += res.length() - before;
            }
        }
        return res;
    }

//    public String paresToString(CharSequence charSequence){
//        String res = "";
//        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(charSequence);
//        BaseUrlSpan[] spen = stringBuilder.getSpans(0,stringBuilder.length(),BaseUrlSpan.class);
//        res = stringBuilder.toString();
//        if(spen.length > 0){
//            int step = 0;
//            for (BaseUrlSpan span : spen) {
//                int before = res.length();
//                String beginStr = res.substring(0,stringBuilder.getSpanStart(span) + step);
//                String str = res.substring(stringBuilder.getSpanStart(span) + step,stringBuilder.getSpanEnd(span) + step);
//                String endStr = res.substring(stringBuilder.getSpanEnd(span) + step);
//                //TODO 根据不同的span
//                str = "<at_user user_id="+ span.getmUrl() + ">" + str + "</at_user>";
//                res = beginStr + str + endStr;
//                step += res.length() - before;
//            }
//        }
//        return res;
//    }

    public SpannableStringBuilder paresToSpann(Context context,String s){
        Document document = Jsoup.parse(s);
        Map<BaseTag,DoubleKeyValueMap<String,Integer,Integer>> resMap = new HashMap<>();
        for (BaseTag tag : tags){
            Elements elements = document.select(tag.getTag());
            DoubleKeyValueMap<String,Integer,Integer> map = new DoubleKeyValueMap<>();
            for(Element element : elements){
                String text = element.text();
                String all = "";
                String value = "";
                for(String attr : tag.getAttrs().keySet()){
                    value = element.attr(attr);
                    all = element.toString().replace("\n","").replace("\"" + value + "\"",value).replace(" " + text,text);
                }
                String beginStr = s.substring(0,s.indexOf(all));
                String endStr = s.substring(s.indexOf(all) + all.length());
                if(!TextUtils.isEmpty(value)){
                    map.put(value,s.indexOf(all),s.indexOf(all) + text.length());
                }
                s = beginStr + text + endStr;
            }
            resMap.put(tag,map);
        }
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(s);
        for (BaseTag tag : resMap.keySet()){
            DoubleKeyValueMap<String,Integer,Integer> map = resMap.get(tag);
            for(String id : map.getFirstKeys()){
                ConcurrentHashMap<Integer, Integer> concurrentHashMap = map.get(id);
                for(Integer begin : concurrentHashMap.keySet()){
                    int end = concurrentHashMap.get(begin);
                    tag.getSpan().setmUrl(id);
                    stringBuilder.setSpan(tag.getSpan(),begin,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return stringBuilder;
    }

//    public SpannableStringBuilder paresToSpann(Context context,String s){
//        s = s.replace(" ","&nbsp;");
//        for (String tag : tags.keySet()){
//            s = s.replace("<" + tag + "&nbsp;","<" + tag + " ");
//        }
//        Document document = Jsoup.parse(s);
//        Map<String,DoubleKeyValueMap<String,Integer,Integer>> resMap = new HashMap<>();
//        for (String tag : tags.keySet()){
//            Elements elements = document.select(tag);
//            DoubleKeyValueMap<String,Integer,Integer> map = new DoubleKeyValueMap<>();
//            for(Element element : elements){
//                String text = element.text();
//                String id = element.attr(tags.get(tag));
//                String all = element.toString().replace("\n","")
//                        .replace("\"" + id + "\"",id)
//                        .replace(" ","")
//                        .replace("&nbsp;"," ");
//                if(!all.contains("<" + tag + ">")){
//                    all = all.replace("<" + tag,"<" + tag + " ");
//                }
//                s = s.replace("&nbsp;"," ");
//                String beginStr = s.substring(0,s.indexOf(all));
//                String endStr = s.substring(s.indexOf(all) + all.length());
//                map.put(id,s.indexOf(all),s.indexOf(all) + text.length());
//                s = beginStr + text + endStr;
//                s = s.replace(" ","&nbsp;").replace("<" + tag + "&nbsp;","<" + tag + " ");
//            }
//            resMap.put(tag,map);
//        }
//        s = s.replace("&nbsp;"," ");
//        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(s);
//        for (String tag : resMap.keySet()){
//            DoubleKeyValueMap<String,Integer,Integer> map = resMap.get(tag);
//            for(String id : map.getFirstKeys()){
//                ConcurrentHashMap<Integer, Integer> concurrentHashMap = map.get(id);
//                for(Integer begin : concurrentHashMap.keySet()){
//                    int end = concurrentHashMap.get(begin);
//                    //TODO 不同的tag不同的点击事件
//                    CustomUrlSpan span = new CustomUrlSpan(context,null,id);
//                    stringBuilder.setSpan(span,begin,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                }
//            }
//        }
//        return stringBuilder;
//    }

    /**
     * 获取指定tag的attrs
     * @param tag
     * @param sequence
     * @return
     */
    public static Set<HashMap<String,String>> getAttr(String tag,CharSequence sequence){
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(sequence);
        BaseUrlSpan[] spen = stringBuilder.getSpans(0,stringBuilder.length(),BaseUrlSpan.class);
        Set<HashMap<String,String>> res = new HashSet<>();
        for (BaseUrlSpan span : spen) {
            if(span.getmTag().getTag().equals(tag)){
                HashMap<String,String> value = new HashMap<>();
                value.putAll(span.getmTag().getAttrs());
                res.add(value);
            }
        }
        return res;
    }
}
