package com.moemoe.lalala.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.StringUtils;
import com.squareup.picasso.Picasso;

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

    private int[] mBGColor = { R.color.label_pink, R.color.label_green, R.color.label_orange, R.color.label_blue, R.color.label_yellow, R.color.label_purple, R.color.label_tab_blue};

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
       // ImageLoader.getInstance().displayImage(imgPath, image);
        if(TextUtils.isEmpty(imgPath)){
            int index = StringUtils.getHashOfString(name, mBGColor.length);
            // image.setBackgroundColor(mContext.getResources().getColor(mBGColor[index]));
            image.setVisibility(GONE);
            mRootView.setBackgroundColor(mContext.getResources().getColor(mBGColor[index]));
        }else {
            int w = getLayoutParams().width;
            int h = getLayoutParams().height;
            Picasso.with(mContext)
                    .load(StringUtils.getUrl(mContext, imgPath, w,h,false,false))
                    .resize(w, h)
                    .config(Bitmap.Config.RGB_565)
                    .into(image);
        }
    }

    public void changePositionImg(final DragDetailView toDragDetailView){
        if (position == toDragDetailView.getPosition()){
            return;
        }
        final Point tcp = toDragDetailView.getChangePoint();
        int l = getLayoutParams().width / mWidthNum / 2;
        int sw = toDragDetailView.getWidthNum() - mWidthNum;
        int sh = toDragDetailView.getHeightNum() - mHeightNum;

        final float sx = (float)toDragDetailView.getWidthNum() / mWidthNum;
        final float sy = (float)toDragDetailView.getHeightNum() / mHeightNum;

        final PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("x",mChangePoint.x,tcp.x + sw * l);
        final PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("y",mChangePoint.y,tcp.y + sh * l);
        final PropertyValuesHolder spvhX = PropertyValuesHolder.ofFloat("scaleX",1.0f,sx);
        final PropertyValuesHolder spvhY = PropertyValuesHolder.ofFloat("scaleY",1.0f,sy);

        a = ObjectAnimator.ofPropertyValuesHolder(this, pvhX, pvhY, spvhX, spvhY).setDuration(300);
        a.setInterpolator(new OvershootInterpolator());

        final PropertyValuesHolder pvhX1 = PropertyValuesHolder.ofFloat("x",tcp.x,mChangePoint.x - sw * l);
        final PropertyValuesHolder pvhY1 = PropertyValuesHolder.ofFloat("y",tcp.y,mChangePoint.y - sh * l);
        final PropertyValuesHolder spvhX1 = PropertyValuesHolder.ofFloat("scaleX",1.0f,1/sx);
        final PropertyValuesHolder spvhY1 = PropertyValuesHolder.ofFloat("scaleY",1.0f,1/sy);
        b = ObjectAnimator.ofPropertyValuesHolder(toDragDetailView, pvhX1, pvhY1, spvhX1, spvhY1).setDuration(300);
        b.setInterpolator(new OvershootInterpolator());
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(a).with(b);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                Point p = toDragDetailView.getPoint();
                toDragDetailView.setPoint(new Point(mBeginPoint.x, mBeginPoint.y));
                mBeginPoint = new Point(p.x, p.y);

                int tmpWidth = toDragDetailView.getWidthNum();
                toDragDetailView.setWidthNum(mWidthNum);
                setWidthNum(tmpWidth);

                int tmpHeight = toDragDetailView.getHeightNum();
                toDragDetailView.setHeightNum(mHeightNum);
                setHeightNum(tmpHeight);

                int cx = tcp.x;
                int cy = tcp.y;
                toDragDetailView.setChangePoint(new Point(mChangePoint.x, mChangePoint.y));
                setChangePoint(new Point(cx, cy));
                parent.childOnLayout(DragDetailView.this);
                toDragDetailView.parent.childOnLayout(toDragDetailView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
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
