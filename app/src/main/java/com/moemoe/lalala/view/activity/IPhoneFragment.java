package com.moemoe.lalala.view.activity;

/**
 * Created by yi on 2017/11/28.
 */

public interface IPhoneFragment {
    String getTitle();
    int getTitleColor();
    int getMenu();
    int getBack();
    boolean onBackPressed();
    void onMenuClick();
}
