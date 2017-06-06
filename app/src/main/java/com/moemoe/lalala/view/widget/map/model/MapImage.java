package com.moemoe.lalala.view.widget.map.model;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by yi on 2016/12/13.
 */

public class MapImage extends ImageView {

    protected MapImgLayer parent;

    protected float scale; // Current map scale

    // Pivot point
    protected Point pivotPoint;

    private Object id;

    // Position in map coordinates
    protected Point pos;
    // Position in map coordinates taking the scale into account
    protected Point posScaled;
    private Point realPos;

    private Drawable drawable;

    private boolean isScalable; // Map object is scalable or not (on map zoom)
    private boolean isTouchable; // Shows whether this object should respond to touch events.

    protected Rect touchRect; // Object's touch area.

    public MapImage(Context context) {
        super(context);
    }

    public MapImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MapImage(Context context,Object id,int rx,int ry,int x, int y, int pivotX, int pivotY, boolean isTouchable, boolean isScalable){
        super(context);
        this.id = id;
        realPos = new Point(rx,ry);
        pos = new Point(x, y);
        posScaled = new Point();
        this.pivotPoint = new Point(pivotX, pivotY);

        this.isTouchable = isTouchable;
        this.isScalable = isScalable;

        this.scale = 1.0f;

        this.touchRect = new Rect();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        this.drawable = drawable;
        super.setImageDrawable(this.drawable);
    }

    public Drawable getDrawable()
    {
        return drawable;
    }

    public Object getMapId()
    {
        return id;
    }

    public boolean isTouched(Rect touchRect)
    {
        return Rect.intersects(this.touchRect, touchRect);
    }

    public Point getPosition()
    {
        return pos;
    }

    public Point getRealPos(){return realPos;}

    public boolean isTouchable() {
        return isTouchable;
    }

    public Rect getBounds() {
        if (drawable != null) {
            return drawable.getBounds();
        } else return null;
    }

    protected void recalculateBounds() {
        posScaled.x = (int)(pos.x*scale);
        posScaled.y = (int)(pos.y*scale);

        int width = 0;
        int height = 0;

        if (drawable == null)
            return;

        width = drawable.getIntrinsicWidth();
        height = drawable.getIntrinsicHeight();

        if (!isScalable) {
            //ignore scale
            drawable.setBounds(posScaled.x - pivotPoint.x, posScaled.y-pivotPoint.y, posScaled.x + (int)(width* 0.75) - pivotPoint.x,
                    posScaled.y + (int)(height * 0.75) - pivotPoint.y);
        } else {
            drawable.setBounds(posScaled.x - (int)(pivotPoint.x*scale), posScaled.y-(int)(pivotPoint.y*scale), posScaled.x + (int)(width*scale) - (int)(pivotPoint.x*scale),
                    posScaled.y + (int)(height*scale) - (int)(pivotPoint.y*scale));
        }

        if (isTouchable) {
            touchRect.set(drawable.getBounds());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof MapImage)) {
            return false;
        }

        return ((MapImage)o).id.equals(this.id);
    }

//    @Override
//    public int hashCode()
//    {
//        return id.hashCode();
//    }

    protected void invalidateSelf()
    {
        parent.invalidate(this);
    }

    void setScale(float scale) {
        this.scale = scale;
        recalculateBounds();
    }

    void setParent(MapImgLayer layer)
    {
        this.parent = layer;
    }
}
