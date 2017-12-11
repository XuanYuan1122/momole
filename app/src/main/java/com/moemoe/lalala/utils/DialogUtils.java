package com.moemoe.lalala.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.moemoe.lalala.R;
import com.moemoe.lalala.dialog.AlertDialog;
import com.moemoe.lalala.view.activity.DownLoadListActivity;
import com.moemoe.lalala.view.activity.LoginActivity;
import com.moemoe.lalala.view.activity.MultiImageChooseActivity;
import com.moemoe.lalala.view.fragment.BaseFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;

/**
 * Created by yi on 2016/11/28.
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
     * @param
     * @return
     */
    public static AlertDialog createImgChooseDlg(final Activity activity, final Fragment fragment,
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// "com.moemoe.lalala.FileProvider"即是在清单文件中配置的authorities
                        mTmpAvatar = FileProvider.getUriForFile(context, "com.moemoe.lalala.FileProvider", createImageFile());// 给目标应用一个临时授权
                    } else {
                        mTmpAvatar = Uri.fromFile(createImageFile());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (which == 0) {
                    // 拍照
                    try {
                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
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
                    // 从多选界面选择
                    Intent intent = new Intent(activity, MultiImageChooseActivity.class);
                    intent.putExtra(MultiImageChooseActivity.EXTRA_KEY_MAX_PHOTO, maxPhotos);
                    intent.putExtra(MultiImageChooseActivity.EXTRA_KEY_SELETED_PHOTOS, selected);
                    if (fragment != null) {
                        fragment.startActivityForResult(intent, REQ_GET_FROM_GALLERY);
                    }else {
                        activity.startActivityForResult(intent, REQ_GET_FROM_GALLERY);
                    }

                }
            }
        });
        return builder.create();
    }

    public static AlertDialog createImgChooseDlg(final RongExtension rongExtension, final Fragment fragment,
                                                 final IPluginModule pluginModule, final ArrayList<String> selected, final int maxPhotos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        builder.setTitle(R.string.a_dlg_title_take_photo);
        CharSequence[] items = new String[] { fragment.getContext().getString(R.string.label_take_photo),
                fragment.getContext().getString(R.string.label_get_picture) };

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri mTmpAvatar = null;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// "com.moemoe.lalala.FileProvider"即是在清单文件中配置的authorities
                        mTmpAvatar = FileProvider.getUriForFile(fragment.getContext(), "com.moemoe.lalala.FileProvider", createImageFile());// 给目标应用一个临时授权
                    } else {
                        mTmpAvatar = Uri.fromFile(createImageFile());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (which == 0) {
                    // 拍照
                    try {
                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        i.putExtra(MediaStore.EXTRA_OUTPUT, mTmpAvatar);
                        rongExtension.startActivityForPluginResult(i, 22, pluginModule);
                    } catch (Exception e) {
                        Toast.makeText(fragment.getContext(), fragment.getContext().getString(R.string.msg_no_system_camera), Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    // 从多选界面选择
                    Intent intent = new Intent(fragment.getContext(), MultiImageChooseActivity.class);
                    intent.putExtra(MultiImageChooseActivity.EXTRA_KEY_MAX_PHOTO, maxPhotos);
                    intent.putExtra(MultiImageChooseActivity.EXTRA_KEY_SELETED_PHOTOS, selected);
                    rongExtension.startActivityForPluginResult(intent, 23, pluginModule);
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
     * @param
     * @return
     */
    public static boolean handleImgChooseResult(Context context, int requestCode, int resultCode, Intent data,final OnPhotoGetListener listener) {
        boolean res = false;
        if ((requestCode == REQ_GET_FROM_GALLERY || requestCode == 23) && resultCode == Activity.RESULT_OK) {
            ArrayList<String> paths = data.getStringArrayListExtra(MultiImageChooseActivity.EXTRA_KEY_SELETED_PHOTOS);
            if (listener != null) {
                listener.onPhotoGet(paths, true);
            }

        } else if (requestCode == REQ_TAKE_PHOTO || requestCode == 22) {
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
     * 检查登录状态，如果成功，return true；如果失败，return false;
     *
     * @param context
     * @return
     */
    public static boolean checkLoginAndShowDlg(final Context context) {
        boolean res = false;
        if (PreferenceUtils.isLogin()) {
            res = true;
        } else {
            final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
            alertDialogUtil.createPromptNormalDialog(context,context.getString(R.string.a_dlg_msg_need_login_first));
            alertDialogUtil.setButtonText(context.getString(R.string.a_dlg_go_2_login),context.getString(R.string.label_cancel),0);
            alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                @Override
                public void CancelOnClick() {
                    alertDialogUtil.dismissDialog();
                }

                @Override
                public void ConfirmOnClick() {
                    // 前往登录界面
                    Intent i = new Intent(context, LoginActivity.class);
                    context.startActivity(i);
                    alertDialogUtil.dismissDialog();
                }
            });
            alertDialogUtil.showDialog();
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle(R.string.a_dlg_title).setMessage(R.string.a_dlg_msg_need_login_first)
//                    .setPositiveButton(R.string.a_dlg_go_2_login, new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // 前往登录界面
//                            Intent i = new Intent(context, LoginActivity.class);
//                            context.startActivity(i);
//                        }
//                    }).setNegativeButton(R.string.label_cancel, null);
//            try {
//                builder.create().show();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

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
                }).setNegativeButton(R.string.label_cancel, null);
        try {
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
