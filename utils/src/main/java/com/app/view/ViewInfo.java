package com.app.view;

/**
 * Created by Haru on 2016/3/28 0028.
 */
final class ViewInfo {
    public int value;
    public int parenetId;

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ViewInfo viewInfo = (ViewInfo) o;
        if(value != viewInfo.value) return false;
        return parenetId == viewInfo.parenetId;
    }

    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + parenetId;
        return result;
    }
}
