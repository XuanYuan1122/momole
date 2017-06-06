package com.moemoe.lalala.view.widget.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

;

/**
 * Created by Haru on 2016/7/5 0005.
 */
public class DragDetailView extends FrameLayout {
    private Point mBeginPoint;
    private Point mChangePoint;
    /**
     * size of view, performance by unit size
     */
    private int mWidthNum;
    private int mHeightNum;
    /**
     * view showed in freesizedraggablelayout
     */
    private String imgPath;
    private ImageView image;
    private View mRootView;
    private TextView text;
    private int position;
    private String name;
    private DraggableLayout parent;
    private ObjectAnimator scaleAnimator;
    private boolean hasSetChangePosition = false;
    private AtomicInteger index;
    private ObjectAnimator a;
    private ObjectAnimator b;
    private Context mContext;

    private int[] mBGColor = { R.color.pink_fb7ba2, R.color.green_93d856, R.color.orange_ed853e, R.color.blue_39d8d8, R.color.yellow_f2cc2c, R.color.purple_cd8add, R.color.blue_4fc3f7};

    public DragDetailView(Context context){
        this(context, null);
    }

    public DragDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragDetailView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.drag_item, this);
        mContext = context;
        image = (ImageView) findViewById(R.id.drag_item_imageview);
        text = (TextView) findViewById(R.id.drag_item_text);
        text.setVisibility(GONE);
        mRootView = findViewById(R.id.drag_root);
        index = new AtomicInteger(0);
        setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                Toast.makeText(context, text.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fillImageView( ) {
        text.setText(name);
        if(TextUtils.isEmpty(imgPath)){
            int index = StringUtils.getHashOfString(name, mBGColor.length);
            image.setVisibility(GONE);
            mRootView.setBackgroundColor(mContext.getResources().getColor(mBGColor[index]));
        }else {
            int w = getLayoutParams().width;
            int h = getLayoutParams().height;
            Glide.with(mContext)
                    .load(StringUtils.getUrl(mContext, ApiService.URL_QINIU + imgPath, w,h,false,false))
                    .override(w, h)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            image.setImageDrawable(resource);
                        }
                    });
        }
    }

    public void setPoint(Point p) {
        mBeginPoint = p;
    }

    public Point getPoint() {
        return mBeginPoint;
    }

    public void setWidthNum(int i) {
        mWidthNum = i;
    }

    public int getWidthNum() {
        return mWidthNum;
    }

    public void setHeightNum(int i) {
        mHeightNum = i;
    }

    public int getHeightNum() {
        return mHeightNum;
    }

    public void setImgPath(String path){ imgPath = path;}

    public String getImgPath(){ return imgPath;}

    public void setPosition(int pos){ position = pos;}

    public int getPosition(){ return position;}

    public void setChangePoint(Point p){ mChangePoint = p;}

    public Point getChangePoint(){ return mChangePoint;}

    public void setHasSetChangePosition(boolean hasSetChangePosition){ this.hasSetChangePosition = hasSetChangePosition;}

    public boolean isHasSetChangePosition(){ return hasSetChangePosition;}

    public void setParent(DraggableLayout v){ parent = v;}

    public DraggableLayout getmParent(){ return parent;}

    public void setName(String name){ this.name = name;}
    public String getName(){ return name;}
}
