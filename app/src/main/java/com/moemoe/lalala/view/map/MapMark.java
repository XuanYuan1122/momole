package com.moemoe.lalala.view.map;

import android.content.Context;
import android.content.Intent;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

    public MapMark(final Context context){
        super(context);
        final int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(wrapContent,wrapContent);
        this.setLayoutParams(params);
        this.setClickable(true);

        setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                    if (!TextUtils.isEmpty(schame)) {
                        String temp = schame;
                        if (!TextUtils.isEmpty(content) && content.equals(getContext().getString(R.string.label_lotter))){
                            if (DialogUtils.checkLoginAndShowDlg(context)){
                                AuthorInfo authorInfo =  PreferenceManager.getInstance(getContext()).getThirdPartyLoginMsg();
                                try {
                                    temp += "?user_id=" + authorInfo.getmUUid()
                                            + "&nickname=" + URLEncoder.encode(authorInfo.getmUserName(),"UTF-8")
                                            + "&token=" + authorInfo.getmToken();
                                    Uri uri = Uri.parse(temp);
                                    IntentUtils.haveShareWeb(getContext(), uri, v);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                return;
                            }
                        }else {
                            Uri uri = Uri.parse(temp);
                            IntentUtils.toActivityFromUri(getContext(), uri, v);
                        }
                    } else {
                        if(StringUtils.isKillEvent() && !AppSetting.isEnterEventToday){
                            return;
                        }
                        if(showTime == 2 || (showTime == 1 && StringUtils.isyoru()) || (showTime == 0 && !StringUtils.isyoru())){
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
                            ToolTipUtils.showTooltip(context, ((MapActivity) context).getRoot(), textView, v, content,type, true,
                                    TooltipAnimation.SCALE_AND_FADE,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ContextCompat.getColor(context, R.color.main_title_cyan));
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

    public void setShowTime(int showTime){this.showTime = showTime;}
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

        //if(IS_API_11_LATER){
        //   view.setX(x);
        //  view.setY(y);
        // }else{
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
        // move the matrix to target position
        // then, scaleBy with the given point and scaleBy.
        matrix.preTranslate(position.x,position.y);
        matrix.postScale(scale,scale,scaleCenterX,scaleCenterY);
        float[] values = new float[9];
        matrix.getValues(values);
        return new PointF(values[Matrix.MTRANS_X],values[Matrix.MTRANS_Y]);
    }
}
