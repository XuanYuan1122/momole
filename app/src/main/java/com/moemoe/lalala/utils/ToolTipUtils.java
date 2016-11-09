package com.moemoe.lalala.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.common.util.DensityUtil;
import com.moemoe.lalala.MapActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.view.tooltip.Tooltip;
import com.moemoe.lalala.view.tooltip.TooltipAnimation;

/**
 * Created by Haru on 2016/9/9.
 */
public class ToolTipUtils {

    public static void showTooltip(Context context,ViewGroup viewGroup,TextView textView,@NonNull View anchor, @StringRes int resId,
                             @Tooltip.Position int position, boolean autoAdjust,
                             @TooltipAnimation.Type int type,
                             int width, int height,int tooltipColor) {
        //TextView textView = (TextView) LayoutInflater.inflate(R.layout.tooltip_textview, null);
        textView.setText(resId);
        textView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        showTooltip(context,viewGroup,anchor, textView, position, autoAdjust, type, tooltipColor);
    }

    public static void showTooltip(Context context,ViewGroup viewGroup,TextView textView,@NonNull View anchor, String resId,
                                   @Tooltip.Position int position, boolean autoAdjust,
                                   @TooltipAnimation.Type int type,
                                   int width, int height,int tooltipColor) {
        //TextView textView = (TextView) LayoutInflater.inflate(R.layout.tooltip_textview, null);
        textView.setText(resId);
        textView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        showTooltip(context,viewGroup,anchor, textView, position, autoAdjust, type, tooltipColor);
    }

    private  static void showTooltip(Context context,ViewGroup viewGroup,@NonNull View anchor, @NonNull View content,
                             @Tooltip.Position int position, boolean autoAdjust,
                             @TooltipAnimation.Type int type,
                             int tipColor) {

        new Tooltip.Builder(context)
                .anchor(anchor, position)
                .animate(new TooltipAnimation(type, 300))
                .autoAdjust(autoAdjust)
                .autoCancel(3000)
                .content(content)
                .withTip(new Tooltip.Tip(DensityUtil.dip2px(16), DensityUtil.dip2px(16), tipColor))
                .into(viewGroup)
                .debug(false)
                .show();
    }
}
