package com.moemoe.lalala.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.app.Utils;
import com.app.common.Callback;
import com.moemoe.lalala.LoginActivity;
import com.moemoe.lalala.MapActivity;
import com.moemoe.lalala.MultiImageChooseActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AlertDialog;
import com.moemoe.lalala.fragment.BaseFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Haru on 2016/5/2 0002.
 */
public class DialogUtils {

    public static final int REQ_TAKE_PHOTO = 4921;
    public static final int REQ_GET_FROM_GALLERY = 4922;

  //  private static String sPathCamera = StorageUtils.getTempFile("take_photo_temp.jpg").getAbsolutePath();
    private static String sPathCamera;

    private static final String TAG = "DialogUtils";

    /**
     * 成功获取图片的回调方法
     *
     * @author Ben
     *
     */
    public static interface OnPhotoGetListener {
        /**
         * 返回多少张图
         *
         * @param photoPaths
         * @param override 是否覆盖原有图片
         */
        public void onPhotoGet(ArrayList<String> photoPaths, boolean override);
    }

    /**
     * 创建图片选择的对话框
     *
     * @param activity
     * @param fragment
     * @param context
     * @param tmpPath
     * @return
     */
    public static AlertDialog createImgChooseDlg(final Activity activity, final BaseFragment fragment,
                                                 final Context context, final ArrayList<String> selected, final int maxPhotos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.a_dlg_title_take_photo);
        CharSequence[] items = new String[] { context.getString(R.string.label_take_photo),
                context.getString(R.string.label_get_picture) };

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri mTmpAvatar = null;
                try {
                    mTmpAvatar = Uri.fromFile(createImageFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (which == 0) {
                    // 拍照
                    try {
                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
                        i.putExtra(MediaStore.EXTRA_OUTPUT, mTmpAvatar);
                        if (fragment != null) {
                            fragment.startActivityForResult(i, REQ_TAKE_PHOTO);// CAMERA_WITH_DATA
                        } else {
                            activity.startActivityForResult(i, REQ_TAKE_PHOTO);// CAMERA_WITH_DATA
                        }

                    } catch (Exception e) {
                        Toast.makeText(context, context.getString(R.string.msg_no_system_camera), Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    // 从相册选择
                    // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    // intent.setType("image/*");
                    // try {
                    // if (fragment != null) {
                    // fragment.startActivityForResult(intent, REQ_GET_FROM_GALLERY);
                    // } else {
                    // activity.startActivityForResult(intent, REQ_GET_FROM_GALLERY);
                    // }
                    // } catch (Exception e) {
                    // LogUtils.LOGE(TAG, e);
                    // }
                    // 从多选界面选择
                    Intent intent = new Intent(activity, MultiImageChooseActivity.class);
                    intent.putExtra(MultiImageChooseActivity.EXTRA_KEY_MAX_PHOTO, maxPhotos);
                    intent.putExtra(MultiImageChooseActivity.EXTRA_KEY_SELETED_PHOTOS, selected);
                    activity.startActivityForResult(intent, REQ_GET_FROM_GALLERY);
                }
            }
        });
        return builder.create();
    }

    /**
     * 把程序拍摄的照片放到 SD卡的 Pictures目录中 sheguantong 文件夹中
     * 照片的命名规则为：neta_20130125_173729.jpg
     *
     * @return
     * @throws IOException
     */
    @SuppressLint("SimpleDateFormat")
    public static File createImageFile() throws IOException {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = format.format(new Date());
        String imageFileName = "neta_" + timeStamp + ".jpg";

        File image = new File(BitmapUtils.getAlbumDir(), imageFileName);
        sPathCamera = image.getAbsolutePath();
        return image;
    }

    /**
     * 处理相册/拍照完成后的图片
     *
     * @param context
     * @param requestCode
     * @param resultCode
     * @param data
     * @param toJpg
     * @return
     */
    public static boolean handleImgChooseResult(Context context, int requestCode, int resultCode, Intent data,final OnPhotoGetListener listener) {
        boolean res = false;
        if (requestCode == REQ_GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            ArrayList<String> paths = data.getStringArrayListExtra(MultiImageChooseActivity.EXTRA_KEY_SELETED_PHOTOS);
            if (listener != null) {
                listener.onPhotoGet(paths, true);
            }

        } else if (requestCode == REQ_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<String> paths = new ArrayList<String>();
                paths.add(sPathCamera);
                BitmapUtils.galleryAddPic(context, sPathCamera);
                if(listener != null) {
                    listener.onPhotoGet(paths, false);
                }
                res = true;
            }else {
                BitmapUtils.deleteTempFile(sPathCamera);
            }
        }
        return res;
    }

    /**
     * 创建扑通的进度对话框
     *
     * @param context
     * @param mPrgMsg
     * @return
     */
    public static ProgressDialog createNormalPgDlg(Context context, String mPrgMsg) {
        ProgressDialog prgDlg = new ProgressDialog(context);
        prgDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prgDlg.setCancelable(false);
        if (TextUtils.isEmpty(mPrgMsg)) {
            mPrgMsg = context.getString(R.string.a_global_msg_loading);
        }
        prgDlg.setMessage(mPrgMsg);
        return prgDlg;
    }

    public static void checkLoginThenGo2Activity(final Context context, final Class activityClass) {
        checkLoginThenGo2Activity(context, activityClass, null);
    }

    public static void checkLoginThenGo2Activity(final Context context, final Class activityClass,
                                                 final Bundle params) {
        if (PreferenceManager.getInstance(context).isLogin(context)) {
            Intent intent = new Intent(context, activityClass);
            if (params != null) {
                intent.putExtras(params);
            }
            context.startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.a_dlg_title).setMessage(R.string.a_dlg_msg_need_login_first)
                    .setPositiveButton(R.string.a_dlg_go_2_login, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 前往登录界面
                            Intent i = new Intent(context,LoginActivity.class);
                            i.putExtras(params);
                            context.startActivity(i);
                        }
                    }).setNegativeButton(R.string.a_dlg_cancel, null);
            try {
                builder.create().show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 班级界面，前往个人中心的hint dialog
     *
     * @param context
     */
    public static void showAccountCenterLoginHintDlg(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.a_dlg_title).setMessage(R.string.a_dlg_msg_account_center_login_hint)
                .setPositiveButton(R.string.a_dlg_go_2_login, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 前往登录界面
                                Intent i = new Intent(context,MapActivity.class);
                               // i.putExtra(MainActivity.EXTRA_KEY_TO_ACCOUNT_CENTER, false);
                                context.startActivity(i);
                            }
                }).setNegativeButton(R.string.a_dlg_cancel, null);

        try {
            builder.show();
        } catch (Exception e) {
        }

    }

    /**
     * 先检查登录状态，然后做事
     *
     * @param context
     * @param runnable
     */
//    public static void checkLoginAndDo(final Context context, final Runnable runnable) {
//        if (AccountHelper.isLogin(context)) {
//            runnable.run();
//        } else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle(R.string.a_dlg_title).setMessage(R.string.a_dlg_msg_need_login_first)
//                    .setPositiveButton(R.string.a_dlg_go_2_login, new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // 前往登录界面
//                            IntentUtils.go2Login(context, runnable);
//                        }
//                    }).setNegativeButton(R.string.a_dlg_cancel, null);
//            try {
//                builder.create().show();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 检查登录状态，如果成功，return true；如果失败，return false;
     *
     * @param context
     * @return
     */
    public static boolean checkLoginAndShowDlg(final Context context) {
        boolean res = false;
        if (PreferenceManager.getInstance(context).isLogin(context)) {
            res = true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.a_dlg_title).setMessage(R.string.a_dlg_msg_need_login_first)
                    .setPositiveButton(R.string.a_dlg_go_2_login, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 前往登录界面
                         Intent i = new Intent(context, LoginActivity.class);
                            context.startActivity(i);
                        }
                    }).setNegativeButton(R.string.a_dlg_cancel, null);
            try {
                builder.create().show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            res = false;
        }
        return res;
    }

    /**
     * 有变动时，放弃修改
     *
     * @param activity
     */
    public static void showAbandonModifyDlg(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.a_dlg_title).setMessage(R.string.a_dlg_msg_abandon_modify)
                .setPositiveButton(R.string.a_dlg_abandon, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                }).setNegativeButton(R.string.a_dlg_cancel, null);
        try {
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
