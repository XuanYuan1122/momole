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
import com.moemoe.lalala.utils.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    private static ArrayList<BaseTag> tags;

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
        HashMap<String,String> attrs = new HashMap<>();
        attrs.put("user_id","");
        tag.setAttrs(attrs);
        tags.add(tag);

        tag = new BaseTag();
        tag.setTag("kira_img");
        tag.setSpan(new ImageUrlSpan(context,tag));
        attrs = new HashMap<>();
        attrs.put("path","");
        attrs.put("w","");
        attrs.put("h","");
        tag.setAttrs(attrs);
        tags.add(tag);

        tag = new BaseTag();
        tag.setTag("kira_font");
        //TODO 添加新的span
        attrs = new HashMap<>();
        attrs.put("color","");
        tag.setAttrs(attrs);
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

    private final class CompareStart implements Comparator<BaseUrlSpan> {
        SpannableStringBuilder str;

        public CompareStart(SpannableStringBuilder str) {
            this.str = str;
        }

        @Override
        public int compare(BaseUrlSpan o1, BaseUrlSpan o2) {
            return str.getSpanStart(o1) - str.getSpanStart(o2);
        }
    }

    public String paresToString(CharSequence charSequence){
        String res;
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(charSequence);
        BaseUrlSpan[] spen = stringBuilder.getSpans(0,stringBuilder.length(),BaseUrlSpan.class);
        res = stringBuilder.toString();
        if(spen.length > 0){
            int step = 0;
            ArrayList<BaseUrlSpan> spanList = new ArrayList<>();
            spanList.addAll(Arrays.asList(spen));
            Collections.sort(spanList, new CompareStart(stringBuilder));
            for (BaseUrlSpan span : spanList) {
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

        for(BaseTag tag : tags){
            res = res.replace("<" + tag.getTag(),"%" + tag.getTag()).replace("</" + tag.getTag() + ">","%/" + tag.getTag() + ">");
        }
        res = res.replace("<","%lt%");
        for(BaseTag tag : tags){
            res = res.replace("%" + tag.getTag(),"<" + tag.getTag()).replace("%/" + tag.getTag() + ">","</" + tag.getTag() + ">");
        }
        return res;
    }

    public SpannableStringBuilder paresToSpann(Context context,String s){
        Document document = Jsoup.parse(s);
        s = s.replace("%lt%","<");
        Map<BaseTag,DoubleKeyValueMap<HashMap<String,String>,Integer,Integer>> resMap = new HashMap<>();
        try {
            for (BaseTag tag : tags){
                Elements elements = document.select(tag.getTag());
                DoubleKeyValueMap<HashMap<String,String>,Integer,Integer> map = new DoubleKeyValueMap<>();
                for(Element element : elements){
                    int before = s.length();
                    String text = element.text().replace("%lt%","<");
                    String all = element.toString().replace("\n","");
                    String value = "";
                    HashMap<String,String> attrMap = new HashMap<>();
                    for(String attr : tag.getAttrs().keySet()){
                        value = element.attr(attr);
                        all = all.replace("\"" + value + "\"",value);
                        attrMap.put(attr,value);
                    }
                    all = all.replace(" " + text,text);
                    String beginStr = s.substring(0,s.indexOf(all));
                    String endStr = s.substring(s.indexOf(all) + all.length());
                    if(!TextUtils.isEmpty(value)){
                        map.put(attrMap,s.indexOf(all),s.indexOf(all) + text.length());
                    }
                    int startTemp = s.indexOf(all);
                    s = beginStr + text + endStr;
                    int step = before - s.length();
                    for (BaseTag tagTemp : resMap.keySet()){
                        DoubleKeyValueMap<HashMap<String,String>,Integer,Integer> changeMap = new DoubleKeyValueMap<>();
                        DoubleKeyValueMap<HashMap<String,String>,Integer,Integer> mapD = resMap.get(tagTemp);
                        for(HashMap<String,String> tempMap : mapD.getFirstKeys()){
                            ConcurrentHashMap<Integer, Integer> concurrentHashMap = mapD.get(tempMap);
                            for(Integer start : concurrentHashMap.keySet()){
                                if(start > startTemp){
                                   // mapD.remove(tempMap);
                                    int end = concurrentHashMap.get(start);
                                   // mapD.put(tempMap,start - step,end - step);
                                    changeMap.put(tempMap,start - step,end - step);
                                }
                            }
                        }
                        if(changeMap.size() > 0){
                            for(HashMap<String,String> tempMap : changeMap.getFirstKeys()){
                                mapD.remove(tempMap);
                                ConcurrentHashMap<Integer, Integer> concurrentHashMap = changeMap.get(tempMap);
                                for(Integer start : concurrentHashMap.keySet()){
                                    int end = concurrentHashMap.get(start);
                                    mapD.put(tempMap,start,end);
                                }
                            }
                        }
                    }
                }
                resMap.put(tag,map);
            }
        }catch (Exception e){
            resMap = new HashMap<>();
        }
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(s);
        for (BaseTag tag : resMap.keySet()){
            DoubleKeyValueMap<HashMap<String,String>,Integer,Integer> map = resMap.get(tag);
            for(HashMap<String,String> attrMap : map.getFirstKeys()){
                ConcurrentHashMap<Integer, Integer> concurrentHashMap = map.get(attrMap);
                for(Integer begin : concurrentHashMap.keySet()){
                    int end = concurrentHashMap.get(begin);
                    BaseUrlSpan span;
                    BaseTag tag1 = new BaseTag();
                    tag1.setTag(tag.getTag());
                    tag1.setAttrs(attrMap);
                    if(tag.getSpan() instanceof UserUrlSpan){
                        span = new UserUrlSpan(context,attrMap.get("user_id"),tag1);
                        tag1.setSpan(span);
                    }else {
                        span = new ImageUrlSpan(context,attrMap.get("path"),tag1);
                        tag1.setSpan(span);
                    }
                    stringBuilder.setSpan(span,begin,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return StringUtils.getUrlClickableText(context,stringBuilder);
       // return stringBuilder;
    }

    /**
     * 获取指定tag的attrs
     * @param tag
     * @param sequence
     * @return
     */
    public Set<HashMap<String,String>> getAttr(String tag,CharSequence sequence){
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
