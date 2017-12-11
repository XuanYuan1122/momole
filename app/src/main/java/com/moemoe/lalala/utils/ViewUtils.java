package com.moemoe.lalala.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.DeskMateEntity;
import com.moemoe.lalala.view.activity.PersonalV2Activity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

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

    public static void setTopMargins (View v, int l) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.topMargin = l;
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

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
     * 可以用来判断是否为Flyme用户
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     *
     */
    private static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     *
     */
    private static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if(dark){
                    extraFlagField.invoke(window,darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
                }else{
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result=true;
            }catch (Exception e){

            }
        }
        return result;
    }

    private static boolean setStatusBarIconDark(Window window, boolean dark){
        boolean result = false;
        try{
            Class<?> cls = window.getClass();
            Method method = cls.getDeclaredMethod("setStatusBarIconDark",boolean.class);
            method.invoke(window,dark);
            result = true;
        } catch(Exception e){

        }
        return result;
    }

    public static void setStatusBarLight(Window window, View view){
        if(!FlymeSetStatusBarLightMode(window, true)){
            if(!MIUISetStatusBarLightMode(window, true)){
                if (!setStatusBarIconDark(window, true)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.white));
                        window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                               // | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
                    }else {
                        if(view != null){
                            view.setAlpha(0.2f);
                        }
                    }
                }
            }
        }
    }

    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    /**
     * 用户栏徽章显示
     * @param context
     * @param huiZhangRoots
     * @param huiZhangTexts
     * @param list
     */
    public static void badge(Context context,final View[] huiZhangRoots, final TextView[] huiZhangTexts, ArrayList<BadgeEntity> list){
        Observable.range(0,huiZhangRoots.length)
                .subscribe(new Observer<Integer>() {

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(Integer i) {
                        huiZhangTexts[i].setVisibility(View.INVISIBLE);
                        huiZhangRoots[i].setVisibility(View.INVISIBLE);
                    }
                });
        if(list.size() > 0){
            int size = huiZhangRoots.length;
            if(list.size() < huiZhangRoots.length){
                size = list.size();
            }
            for (int i = 0;i < size;i++){
                huiZhangTexts[i].setVisibility(View.VISIBLE);
                huiZhangRoots[i].setVisibility(View.VISIBLE);
                BadgeEntity badgeEntity = list.get(i);
                TextView tv = huiZhangTexts[i];
                tv.setText(badgeEntity.getTitle());
                tv.setText(badgeEntity.getTitle());
                tv.setBackgroundResource(R.drawable.bg_badge_cover);
                int px = (int)context.getResources().getDimension(R.dimen.x8);
                tv.setPadding(px,0,px,0);
                int radius2 = (int)context.getResources().getDimension(R.dimen.y4);
                float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
                RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
                ShapeDrawable shapeDrawable2 = new ShapeDrawable();
                shapeDrawable2.setShape(roundRectShape2);
                shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
                shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(context, R.color.main_cyan)));
                huiZhangRoots[i].setBackgroundDrawable(shapeDrawable2);
            }
        }
    }

    public static void toPersonal(Context context,String uuid){
        if(!uuid.equals(PreferenceUtils.getUUid())){
            Intent i = new Intent(context, PersonalV2Activity.class);
            i.putExtra("uuid",uuid);
            context.startActivity(i);
        }
    }

    public static void setRoleButton(ImageView ivRole,TextView tv){
        if(StringUtils.isyoru()){
            if(PreferenceUtils.isLogin()){
                ArrayList<DeskMateEntity> list = PreferenceUtils.getAuthorInfo().getDeskMateEntities();
                String mate = "";
                if(list != null){
                    for (DeskMateEntity entity : list){
                        if(entity.isDeskmate()){
                            mate = entity.getRoleOf();
                            break;
                        }
                    }
                    if(!TextUtils.isEmpty(mate)){
                        if("len".equals(mate)){
                            ivRole.setImageResource(R.drawable.btn_len_sleep);
                            tv.setText("现在暂时没有需要跑腿或代练游戏的任务，你要做什么都和我没关系……要去隔壁社团看看吗？");
                        }
                        if("mei".equals(mate)){
                            ivRole.setImageResource(R.drawable.btn_mei_sleep);
                            tv.setText("学园里的事情就交给美藤吧！小哥哥有想做的事要跟我说哦？我想现在有不少学部需要你呐！");
                        }
                        if("sari".equals(mate)){
                            ivRole.setImageResource(R.drawable.btn_sari_sleep);
                            tv.setText("少年，你是哪位？开玩笑的啦。说好了要和你一起逛逛的，接下来去哪里好？我可是很期待呢。");
                        }
                    }else {
                        ivRole.setImageResource(R.drawable.btn_len_sleep);
                    }
                }else {
                    ivRole.setImageResource(R.drawable.btn_len_sleep);
                }
            }else {
                ivRole.setImageResource(R.drawable.btn_len_sleep);
                tv.setText("现在暂时没有需要跑腿或代练游戏的任务，你要做什么都和我没关系……要去隔壁社团看看吗？");
            }
        }else {
            if(PreferenceUtils.isLogin()){
                ArrayList<DeskMateEntity> list = PreferenceUtils.getAuthorInfo().getDeskMateEntities();
                DeskMateEntity mate = null;
                if(list != null){
                    for (DeskMateEntity entity : list){
                        if(entity.isDeskmate()){
                            mate = entity;
                            break;
                        }
                    }
                    if(mate != null){
                        if("len".equals(mate.getRoleOf())){
                            tv.setText("现在暂时没有需要跑腿或代练游戏的任务，你要做什么都和我没关系……要去隔壁社团看看吗？");
                            if(mate.getClothesId().equals("d20c51a4-f84e-4404-b520-a9bf6f52f603")){
                                ivRole.setImageResource(R.drawable.btn_len_normal);
                            }else if(mate.getClothesId().equals("aa8466da-423a-4dff-9320-907e075ee507")){
                                ivRole.setImageResource(R.drawable.btn_len_swim);
                            }else if(mate.getClothesId().equals("63fcac63-dcae-4a7f-9d59-5211c86f2a42")){
                                ivRole.setImageResource(R.drawable.btn_len_impact);
                            }else if(mate.getClothesId().equals("1e8c16d8-e0e2-4d45-bc42-bfed079522ac")){
                                ivRole.setImageResource(R.drawable.btn_len_space);
                            }else if(mate.getClothesId().equals("63682d29-3069-4b2c-a6e3-3b5f2c29a04e")){
                                ivRole.setImageResource(R.drawable.btn_len_xmas);
                            }else if(mate.getClothesId().equals("6ceba098-1f53-4e67-b61d-ad6fdc59d06a")){
                                ivRole.setImageResource(R.drawable.btn_len_kimono);
                            }else if(mate.getClothesId().equals("b940dcb6-e52c-4093-8075-09da6a29794e")){
                                ivRole.setImageResource(R.drawable.btn_len_halloween);
                            }else {
                                ivRole.setImageResource(R.drawable.btn_len_normal);
                            }
                        }
                        if("mei".equals(mate.getRoleOf())){
                            tv.setText("学园里的事情就交给美藤吧！小哥哥有想做的事要跟我说哦？我想现在有不少学部需要你呐！");
                            if(mate.getClothesId().equals("1d6beb00-ea83-4e32-8dbd-8e0b7ee9ec5f")){
                                ivRole.setImageResource(R.drawable.btn_mei_normal);
                            }else if(mate.getClothesId().equals("fc745524-df5c-43e6-b6db-3a1ee05c283c")){
                                ivRole.setImageResource(R.drawable.btn_mei_kimono);
                            }else if(mate.getClothesId().equals("88d05ace-aefb-4b5a-8997-929294232805")){
                                ivRole.setImageResource(R.drawable.btn_mei_halloween);
                            }else if(mate.getClothesId().equals("0e8993f7-dcfd-4568-867e-bd8f64e38dd8")){
                                ivRole.setImageResource(R.drawable.btn_mei_swim);
                            }else {
                                ivRole.setImageResource(R.drawable.btn_mei_normal);
                            }
                        }
                        if("sari".equals(mate.getRoleOf())){
                            tv.setText("少年，你是哪位？开玩笑的啦。说好了要和你一起逛逛的，接下来去哪里好？我可是很期待呢。");
                            if(mate.getClothesId().equals("59588a90-7667-4052-8ce4-17a0a013ac29")){
                                ivRole.setImageResource(R.drawable.btn_sari_normal);
                            }else if(mate.getClothesId().equals("83e2b3cc-6831-4405-a3b5-6da7fd51cd5d")){
                                ivRole.setImageResource(R.drawable.btn_sari_kimono);
                            }else if(mate.getClothesId().equals("e0e88782-7615-4222-919e-d8ad14d2a8f4")){
                                ivRole.setImageResource(R.drawable.btn_sari_halloween);
                            }else if(mate.getClothesId().equals("1e7245fc-a05d-498f-aa75-af2533aa35df")){
                                ivRole.setImageResource(R.drawable.btn_sari_swim);
                            }else {
                                ivRole.setImageResource(R.drawable.btn_sari_normal);
                            }
                        }
                    }else {
                        ivRole.setImageResource(R.drawable.btn_len_normal);
                    }
                }else {
                    ivRole.setImageResource(R.drawable.btn_len_normal);
                }
            }else {
                ivRole.setImageResource(R.drawable.btn_len_normal);
                tv.setText("现在暂时没有需要跑腿或代练游戏的任务，你要做什么都和我没关系……要去隔壁社团看看吗？");
            }
        }
    }

//    public static void setRoleButton(ImageView ivRole){
//        setRoleButton(ivRole,null);
//    }
}
