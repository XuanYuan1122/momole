package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2017/11/8.
 */

public class NearUserEntity {

    private ArrayList<Point> positionList;
    private ArrayList<MapEntity> users;

    public NearUserEntity() {
        positionList = new ArrayList<>();
        users = new ArrayList<>();
    }

    public ArrayList<Point> getPositionList() {
        return positionList;
    }

    public void setPositionList(ArrayList<Point> positionList) {
        this.positionList = positionList;
    }

    public ArrayList<MapEntity> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<MapEntity> users) {
        this.users = users;
    }

    public class Point{
        private int x;
        private int y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
