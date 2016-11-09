package com.moemoe.lalala.view.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haru on 2016/7/25 0025.
 */
public class MapLayout extends FrameLayout {
    private TouchImageView touchImageView ;
    private List<MapMark> marks = new ArrayList<>();

    public MapLayout(Context context) {
        this(context, null);
    }

    public MapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialImageView(context);
    }

    public MapLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialImageView(context);
    }

    private void initialImageView(Context context){
        touchImageView = new TouchImageView(context);
        touchImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(touchImageView, params);
    }

    public void setOnImageClickLietener(OnClickListener lietener){
        touchImageView.setOnClickListener(lietener);
    }
    
    public void addMapMarkView(int markView,float x,float y,String schame,String content,String start,String end,String start1,String end1,MapMark.RenderDelegate renderDelegate){
        if(markView <= 0){
            throw new IllegalArgumentException("View for bubble cannot be null !");
        }
        MapMark mark = new MapMark(getContext());
        mark.setRenderDelegate(renderDelegate);
        mark.setMapX(x);
        mark.setMapY(y);
        mark.setImageResource(markView);
        mark.setSchame(schame);
        mark.setContent(content);
       // mark.setShowTime(showTime);
        mark.setStartTime(start);
        mark.setStartTime1(start1);
        mark.setEndTime(end);
        mark.setEndTime1(end1);
        marks.add(mark);
        addView(mark);
        touchImageView.addMapMark(mark);
    }

    public int getViewHeight(){
        return touchImageView.getViewHeight();
    }

    public void removeAllMarkView(boolean isChange){
        removeViews(1, marks.size());
        if(isChange){
            marks.clear();
        }
        touchImageView.removeAllMark(isChange);
    }

    public void rebuildMarks(){
        for(MapMark mapMark : marks){
            addView(mapMark);
        }
        touchImageView.postInvalidate();
    }

    public void setMapBitmap(Bitmap bitmap){
        touchImageView.setImageBitmap(bitmap);
    }
    public void setMapResource(int bitmap){
        touchImageView.setImageResource(bitmap);
    }

    public void setIsDialogCause(boolean isDialogCause){
        touchImageView.setIsDialogCause(isDialogCause);
    }
}
