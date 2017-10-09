package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yi on 2017/9/11.
 */
@Entity
public class AlarmClockEntity  implements Parcelable{

    @Id(autoincrement = true)
    private Long id;//闹钟id
    private int hour;
    private int minute;
    private String roleName;
    private String roleId;
    private String repeat;
    private String weeks;
    private String tag;//备注
    private String ringName;//铃声
    private int ringUrl;//铃声地址
    private boolean onOff;

    @Override
    public int describeContents() {
        return 0;
    }

    public AlarmClockEntity(){

    }

    @Generated(hash = 41083824)
    public AlarmClockEntity(Long id, int hour, int minute, String roleName, String roleId,
            String repeat, String weeks, String tag, String ringName, int ringUrl, boolean onOff) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.roleName = roleName;
        this.roleId = roleId;
        this.repeat = repeat;
        this.weeks = weeks;
        this.tag = tag;
        this.ringName = ringName;
        this.ringUrl = ringUrl;
        this.onOff = onOff;
    }

    public static final Parcelable.Creator<AlarmClockEntity> CREATOR = new Creator<AlarmClockEntity>() {

        @Override
        public AlarmClockEntity createFromParcel(Parcel in) {
            AlarmClockEntity entity = new AlarmClockEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            entity.id = bundle.getLong("id");
            entity.hour = bundle.getInt("hour");
            entity.minute = bundle.getInt("minute");
            entity.ringUrl = bundle.getInt("ringUrl");
            entity.roleName = bundle.getString("roleName");
            entity.roleId = bundle.getString("roleId");
            entity.repeat = bundle.getString("repeat");
            entity.weeks = bundle.getString("weeks");
            entity.tag = bundle.getString("tag");
            entity.ringName = bundle.getString("ringName");
            entity.onOff = bundle.getBoolean("onOff");
            return entity;
        }

        @Override
        public AlarmClockEntity[] newArray(int size) {

            return new AlarmClockEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putInt("hour", hour);
        bundle.putInt("minute", minute);
        bundle.putInt("ringUrl", ringUrl);
        bundle.putString("roleName", roleName);
        bundle.putString("roleId",roleId);
        bundle.putString("repeat",repeat);
        bundle.putString("weeks",weeks);
        bundle.putString("tag",tag);
        bundle.putString("ringName",ringName);
        bundle.putBoolean("onOff",onOff);
        out.writeBundle(bundle);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRingName() {
        return ringName;
    }

    public void setRingName(String ringName) {
        this.ringName = ringName;
    }

    public int getRingUrl() {
        return ringUrl;
    }

    public void setRingUrl(int ringUrl) {
        this.ringUrl = ringUrl;
    }

    public boolean isOnOff() {
        return onOff;
    }

    public void setOnOff(boolean onOff) {
        this.onOff = onOff;
    }

    public boolean getOnOff() {
        return this.onOff;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
