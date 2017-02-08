package com.moemoe.lalala.view.map;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.MapActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToolTipUtils;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.tooltip.Tooltip;
import com.moemoe.lalala.view.tooltip.TooltipAnimation;

import org.xutils.common.util.DensityUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Haru on 2016/7/25 0025.
 */
public class MapMark extends ImageView{
    public final PointF position = new PointF();
    private float mapX;
    private float mapY;
    private String schame;
    private String content;
    private RenderDelegate renderDelegate;
    private int showTime;//0:白天 1:晚上 2:both
    private String startTime;
    private String endTime;
    private String startTime1;
    private String endTime1;

    public MapMark(final Context context,float width,float height){
        super(context);
        //final int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DensityUtil.dip2px(width),DensityUtil.dip2px(height));
        this.setLayoutParams(params);
        this.setClickable(true);
        setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                    if (!TextUtils.isEmpty(schame)) {
                        String temp = schame;
                        if (!TextUtils.isEmpty(content) && content.equals(getContext().getString(R.string.label_lotter))){
                            if (DialogUtils.checkLoginAndShowDlg(context)){
                                AuthorInfo authorInfo =  PreferenceManager.getInstance(getContext()).getAuthorInfo();
                                    try {
                                        temp += "?user_id=" + authorInfo.getUserId()
                                                + "&nickname=" + (TextUtils.isEmpty(authorInfo.getUserName())? "" : URLEncoder.encode(authorInfo.getUserName(),"UTF-8"))
                                                + "&token=" + PreferenceManager.getToken();
                                        Uri uri = Uri.parse(temp);
                                        IntentUtils.haveShareWeb(getContext(), uri, v);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                            }else {
                                return;
                            }
                        }else {
                            if(temp.contains("http://prize.moemoe.la:8000/mt")){
                                AuthorInfo authorInfo =  PreferenceManager.getInstance(getContext()).getAuthorInfo();
                                temp +="?user_id=" + authorInfo.getUserId() + "&nickname="+authorInfo.getUserName();
                            }
                            if(temp.contains("http://prize.moemoe.la:8000/netaopera/chap")){
                                AuthorInfo authorInfo =  PreferenceManager.getInstance(getContext()).getAuthorInfo();
                                temp +="?pass=" + PreferenceManager.getInstance(getContext()).getPassEvent() + "&user_id=" + authorInfo.getUserId();
                            }
                            if(temp.contains("http://neta.facehub.me/")){
                                AuthorInfo authorInfo =  PreferenceManager.getInstance(getContext()).getAuthorInfo();
                                temp +="?open_id=" + authorInfo.getUserId() + "&nickname=" + authorInfo.getUserName() + "&full_screen";
                            }
                            Uri uri = Uri.parse(temp);
                            IntentUtils.toActivityFromUri(getContext(), uri, v);
                        }
                    } else {
                        if(StringUtils.isKillEvent() && !AppSetting.isEnterEventToday){
                            return;
                        }
                        if(matchTime()){
                            TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.tooltip_textview, null);
                            int viewHeight = ((MapLayout)getParent()).getViewHeight();
                            int[] location = new int[2];
                            v.getLocationOnScreen(location);
                            int type;
                            if(location[1] < viewHeight / 2){
                                type = Tooltip.BOTTOM;
                            }else {
                                type = Tooltip.TOP;
                            }
                            if(TextUtils.isEmpty(content)){
                                ArrayList<String> temp = new ArrayList<>();
                                temp.add("听说塔里有个体力值翻倍的道具，有了那个我体育考试就能及格了！！");
                                temp.add("唔…某度说，红血瓶+250体力、蓝血瓶+500体力；红水晶+3攻击、蓝水晶+3防御。");
                                temp.add("注意安全啊莲，尽量加“防御”吧，一定要平安回来！");
                                temp.add("邱枳实学长和千世大小姐已经摸清楚里面的情况了，尽量找他们获取帮助吧，真不愧是Neta的精英！");
                                temp.add("最终之战开启后，就无法回头了…");
                                temp.add("有一种怪物会自爆的说，是很危险的存在，叫做“灰烬…法师”？");
                                temp.add("如果没有实力战胜怪物，也找不到补给的话，就会被的永远困在里面了，好可怕。");
                                temp.add("分享可以赚到100金币，但我连进去的勇气都没有…");
                                Random random = new Random();
                                int i = random.nextInt(8);
                                ToolTipUtils.showTooltip(context, ((MapActivity) context).getRoot(), textView, v, temp.get(i),type, true,
                                        TooltipAnimation.SCALE_AND_FADE,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ContextCompat.getColor(context, R.color.main_title_cyan));
                            }else {
                                ToolTipUtils.showTooltip(context, ((MapActivity) context).getRoot(), textView, v, content,type, true,
                                        TooltipAnimation.SCALE_AND_FADE,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ContextCompat.getColor(context, R.color.main_title_cyan));
                            }
                        }
                    }
            }
        });
    }

    public void setContent(String content){this.content = content;}

    public String getContent(){return content;}

    public float getMapX() {
        return mapX;
    }

    public void setMapX(float mapX) {
        this.mapX = mapX;
    }

    public float getMapY() {
        return mapY;
    }

    public void setMapY(float mapY) {
        this.mapY = mapY;
    }

    public void setSchame(String schame){this.schame = schame;}

    public String getSchame(){ return schame;}

    public void setStartTime(String s){startTime = s;}

    public String getStartTime(){return startTime;}

    public void setEndTime(String s){endTime = s;}

    public String getEndTime(){return endTime;}

    public void setShowTime(int showTime){this.showTime = showTime;}


    public void setStartTime1(String s){startTime1 = s;}

    public String getStartTime1(){return startTime1;}

    public void setEndTime1(String s){endTime1 = s;}

    public String getEndTime1(){return endTime1;}

    public boolean matchTime(){
        return StringUtils.matchCurrentTime(startTime,endTime) || StringUtils.matchCurrentTime(startTime1,endTime1);
    }

    public interface RenderDelegate {
        void onDisplay(View bubbleView);
    }

    public void setRenderDelegate (RenderDelegate renderDelegate) {
        this.renderDelegate = renderDelegate;
    }

    public void show(PointF position){
        if(getDrawable() == null) return;
        setMapMarkViewAtPosition(position);
        if (renderDelegate != null){
            //renderDelegate.onDisplay(view);
        }
    }

    private void setMapMarkViewAtPosition(PointF center){
        float posX = center.x;
        float posY = center.y;
        setMapMarkViewAtPosition(posX, posY);
    }

    private void setMapMarkViewAtPosition(float x, float y){

        // BUG : HTC SDK 2.3.3 界面会被不停的重绘,这个重绘请求是View.onDraw()方法发起的。
        if(position.equals(x,y)) return;
        position.set(position.x + x,position.y + y);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        int left = (int)position.x;
        int top = (int)position.y;
        // HTC SDK 2.3.3 Required
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.TOP;
        params.leftMargin = left;
        params.topMargin = top;
        setLayoutParams(params);
        //}
    }

    public void scaleMark(float scaleCenterX,float scaleCenterY,float scale){
        position.set(scaleByPoint(scaleCenterX,scaleCenterY,scale));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        int left = (int)position.x;
        int top = (int)position.y;
        // HTC SDK 2.3.3 Required
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.TOP;
        params.leftMargin = left;
        params.topMargin = top;
        setLayoutParams(params);
    }

    private PointF scaleByPoint(float scaleCenterX,float scaleCenterY,float scale){
        Matrix matrix = new Matrix();
        matrix.preTranslate(position.x,position.y);
        matrix.postScale(scale,scale,scaleCenterX,scaleCenterY);
        float[] values = new float[9];
        matrix.getValues(values);
        return new PointF(values[Matrix.MTRANS_X],values[Matrix.MTRANS_Y]);
    }
}
