package com.app.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by Haru on 2016/3/28 0028.
 */
public class ViewFinder {
    private View view;
    private Activity activity;

    public ViewFinder(View view){this.view = view;}

    public ViewFinder(Activity activity){this.activity=activity;}

    public View findViewById(int id){
        if(view != null) return view.findViewById(id);
        if(activity != null) return activity.findViewById(id);
        return null;
    }

    public View findViewByInfo(ViewInfo info){return findViewById(info.value,info.parenetId);}

    public View findViewById(int id,int pid){
        View pView = null;
        if(pid > 0){
            pView = this.findViewById(pid);
        }
        View view = null;
        if(pView != null){
            view = pView.findViewById(id);
        }else {
            view = this.findViewById(id);
        }
        return view;
    }

    public Context getContext(){
        if(view != null) return view.getContext();
        if(activity != null) return activity;
        return null;
    }
}
