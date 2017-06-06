/*************************************************************************
* Copyright (c) 2015 Lemberg Solutions
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**************************************************************************/

package com.moemoe.lalala.view.widget.map.model;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Looper;

import com.moemoe.lalala.view.widget.map.MapWidget;
import com.moemoe.lalala.view.widget.map.config.OfflineMapConfig;
import com.moemoe.lalala.view.widget.map.interfaces.Layer;

import java.util.ArrayList;

public class MapImgLayer
	implements Layer, Callback
{
	private long layerId;
	private boolean isVisible;

	//private ArrayList<MapObject> drawables;
	private ArrayList<MapImage> drawables;
	//private ArrayList<MapObject> touchables;
	private ArrayList<MapImage> touchables;

	protected MapWidget parent;

	public MapImgLayer(long theLayerId, MapWidget parent)
	{
		drawables = new ArrayList<>();
		touchables = new ArrayList<>();
		
		//Visible by default
		isVisible = true;
		
		this.layerId = theLayerId;
		this.parent = parent;
	}


	@Override
	public void addMapObject(MapObject mapObject) {

	}

	@Override
	public  void addMapObject(MapImage object)
	{
		if (object == null) {
			throw new IllegalArgumentException();
		}
		
		if (Looper.myLooper() == null) {
			throw new RuntimeException("addMapObject should be called from UI thread.");
		}
		
		object.setParent(this);
		
		object.setScale(parent.getScale());
		
		drawables.add(object);
		
		if (object.isTouchable()) {
			touchables.add(object);
		}
		
		parent.invalidate(object.getBounds());
	}


	@Override
	public MapObject getMapObject(Object id) {
		return null;
	}

	@Override
	public MapImage getMapImgObject(Object id) {
		if (id == null) {
			throw new IllegalArgumentException();
		}

		// TODO: Re-factor this as this is extremely not efficient
		for (MapImage drawable:drawables) {
			if (drawable.getMapId().equals(id)) {
				return drawable;
			}
		}

		return null;
	}


	@Override
	public MapObject getMapObjectByIndex(int index)
	{
		return null;
	}

	@Override
	public MapImage getMapImgObjectByIndex(int index) {
		return drawables.get(index);
	}


	@Override
	public int getMapObjectCount() 
	{
		return drawables != null?drawables.size():0;
	}
	
	
	@Override
	public void removeMapObject(Object id)
	{
		if (id == null) {
			throw new IllegalArgumentException();
		}
		
		if (Looper.myLooper() == null) {
			throw new RuntimeException("removeMapObject should be called from UI thread.");
		}

		MapImage objectToDelete = null;
		for (MapImage mapObject:drawables) {
			if (mapObject.getMapId().equals(id)) {
				objectToDelete = mapObject;
				break;
			}
		}
		
		if (objectToDelete != null) {
			drawables.remove(objectToDelete);
			touchables.remove(objectToDelete);
			
			Rect b = objectToDelete.getDrawable().getBounds();
			parent.postInvalidate(b.left, b.top, b.right, b.bottom);
		}				
	}
	
	
//	public ArrayList<Object> getTouched(int normX, int normY)
//	{
//		ArrayList<Object> result = new ArrayList<Object>();
//		
//		for (MapObject touchable:touchables) {
//			if (touchable.isTouched(normX, normY)) {
//				result.add(touchable.getId());
//			}
//		}
//		
//		return result;
//	}
//	
	
	/**
	 * Returns Ids of map object that were touched. Intended for internal use
	 * @param touchRect
	 * @return
	 */
	public ArrayList<Object> getTouched(Rect touchRect)
	{
		ArrayList<Object> result = new ArrayList<Object>();
		
		for (MapImage touchable:touchables) {
			if (touchable.isTouched(touchRect)) {
				result.add(touchable.getMapId());
			}
		}
		
		return result;
	}
	
	
	public boolean isVisible()
	{
		return isVisible;
	}

	
	public void setVisible(boolean visible)
	{
		isVisible = visible;
	}
	
	
	public void setScale(float scale)
	{
		int size = drawables.size();
		
		for (int i=0; i < size; ++i) {
			MapImage drawable = drawables.get(i);
			drawable.setScale(scale);
		}
	}
	
	
	public void draw(Canvas canvas, Rect drawingRect)
	{
		if (!isVisible)
			return;

    		for (int i=0, size=drawables.size(); i<size; ++i) {
				MapImage object = drawables.get(i);
    		    
    			if (Rect.intersects(object.getBounds(), drawingRect)) {
    				object.draw(canvas);
    			}
    		}
	}


	public void clearAll() 
	{
		if (Looper.myLooper() == null) {
			throw new RuntimeException("clearAll should be called from UI thread.");
		}
		
		drawables.clear();
		touchables.clear();
		drawables = null;
		touchables = null;
		drawables = new ArrayList<>();
		touchables = new ArrayList<>();
	}

	
	public boolean equals(Object o)
	{
	    if (o == null) {
	        return false;
	    }
	    
	    if (!(o instanceof MapImgLayer)) {
	        return false;
	    }
	    
	    MapImgLayer inMapLayer = (MapImgLayer) o;
	    
	    return inMapLayer.layerId == this.layerId;
	}
	
	
	public int hashCode()
	{
	    return (int)(layerId ^ (layerId >>> 32));
	}
	

	// Package access methods
	
	OfflineMapConfig getConfig()
	{
		return parent.getConfig();
	}
	
	
	void invalidate(MapImage object) {
		Rect bounds = object.getBounds();
		parent.postInvalidate(bounds.left, bounds.top, bounds.right, bounds.bottom);
	}
	
	
	public void scheduleDrawable(Drawable who, Runnable what, long when)
	{		
		parent.scheduleDrawable(who, what, when);
	}
	
	
	public void unscheduleDrawable(Drawable who, Runnable what) {
		parent.unscheduleDrawable(who, what);
	}

	
	public void invalidateDrawable(Drawable who) {
		Rect bounds = who.getBounds();
		parent.postInvalidate(bounds.left, bounds.top, bounds.right, bounds.bottom);
	}


	public long getId() 
	{
		return layerId;
	}
}
