package com.moemoe.lalala.view.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by yi on 2016/12/1.
 */

public abstract class BaseFragment extends Fragment {

    protected View rootView;
    private Unbinder bind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(getLayoutId(), container, false);
            bind = ButterKnife.bind(this, rootView);
            initViews(savedInstanceState);
        }
        init();
        return rootView;
    }

    protected void init(){

    }

    protected abstract int getLayoutId();
    protected abstract void initViews(Bundle savedInstanceState);


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void release(){
        if(bind != null){
            try {
                bind.unbind();
            }catch (Exception e){

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

//    public boolean onBackPressed(){
//        return true;
//    }
//
//    public void setTitle(String title){
//
//    }
//
//    public String getTitle(){
//        return "";
//    }
//
//    public void setMenu(@DrawableRes int res){
//
//    }
//
//    public void toFragment(Fragment fragment){
//
//    }
//
//    public int getMenu(){
//        return 0;
//    }
//
//    public void onMenuClick(){
//
//    }
}
