package com.moemoe.lalala.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yi on 2017/6/7.
 */

public class ViewUtils {

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static void setLeftMargins (View v, int l) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.leftMargin = l;
            v.requestLayout();
        }
    }

    public static void setRightMargins (View v, int r) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.rightMargin = r;
            v.requestLayout();
        }
    }

    public static void setBottomMargins (View v, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.bottomMargin = b;
            v.requestLayout();
        }
    }
}
