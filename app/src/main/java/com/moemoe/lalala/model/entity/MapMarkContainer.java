package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/12/6.
 */

public class MapMarkContainer {
    private ArrayList<MapMarkEntity> container;

    public MapMarkContainer(){
        container = new ArrayList<>();
    }

    public void addMark(MapMarkEntity entity){container.add(entity);}

    public MapMarkEntity getMark(int index){return container.get(index);}

    public MapMarkEntity getMarkById(String id){
        for (MapMarkEntity entity : container){
            if (entity.getId().equals(id)){
                return entity;
            }
        }
        return null;
    }

    public int size(){return container.size();}
}
