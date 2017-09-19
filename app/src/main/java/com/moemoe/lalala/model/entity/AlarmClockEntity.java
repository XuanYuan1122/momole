package com.moemoe.lalala.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yi on 2017/9/11.
 */
@Entity
public class AlarmClockEntity  implements Parcelable{

    @Id
    private long id;//闹钟id
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

    private AlarmClockEntity(Parcel in){
        id = in.readLong();
        hour = in.readInt();
        minute = in.readInt();
        roleName = in.readString();
        roleId = in.readString();
        repeat = in.readString();
        weeks = in.readString();
        tag = in.readString();
        ringName = in.readString();
        ringUrl = in.readInt();
        onOff = in.readByte() != 0;
    }

    @Generated(hash = 581119664)
    public AlarmClockEntity(long id, int hour, int minute, String roleName, String roleId,
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
            return new AlarmClockEntity(in);
        }

        @Override
        public AlarmClockEntity[] newArray(int size) {

            return new AlarmClockEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeInt(hour);
        out.writeInt(minute);
        out.writeString(roleName);
        out.writeString(roleId);
        out.writeString(repeat);
        out.writeString(weeks);
        out.writeString(tag);
        out.writeString(ringName);
        out.writeInt(ringUrl);
        out.writeByte((byte) (onOff ? 1 : 0));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
