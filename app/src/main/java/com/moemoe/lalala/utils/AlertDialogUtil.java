package com.moemoe.lalala.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.moemoe.lalala.R;

/**
 * Created by Haru on 2016/5/12 0012.
 */
public class AlertDialogUtil {

    private static AlertDialogUtil alertDialogUtil = null;
    private Dialog dialog = null;
    private View view;
    private OnClickListener onClickListener;
    private Button confirm, cancel;
    private Context context;

    private AlertDialogUtil() {
    }

    public static AlertDialogUtil getInstance() {
        if (alertDialogUtil == null) {
            alertDialogUtil = new AlertDialogUtil();
        }
        return alertDialogUtil;
    }

    public void createPromptDialog(Context context, String title, String content) {
        this.context = context;
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
            this.dialog = null;
        }
        view = LayoutInflater.from(context).inflate(
                R.layout.general_dialog, null);
        this.dialog = new AlertDialog.Builder(context).setView(view).create();
        if (!TextUtils.isEmpty(title))
            ((TextView) view.findViewById(R.id.general_dialog_title)).setText(title);
        else
            ((TextView) view.findViewById(R.id.general_dialog_title)).setText(context.getString(R.string.label_alert));
        if (!TextUtils.isEmpty(content))
            ((TextView) view.findViewById(R.id.general_dialog_content)).setText(content);
        else
            ((TextView) view.findViewById(R.id.general_dialog_content)).setText("");
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        dialog.setCancelable(false);
        confirm = (Button) view.findViewById(R.id.general_dialog_btn_confirm);
        cancel = (Button) view.findViewById(R.id.general_dialog_btn_cancel);
    }

    public void createNoticeDialog(Context context, String title, String content) {
        this.context = context;
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
            this.dialog = null;
        }
        view = LayoutInflater.from(context).inflate(
                R.layout.general_dialog, null);
        this.dialog = new AlertDialog.Builder(context).setView(view).create();
        if (!TextUtils.isEmpty(title))
            ((TextView) view.findViewById(R.id.general_dialog_title)).setText(title);
        if (!TextUtils.isEmpty(content))
            ((TextView) view.findViewById(R.id.general_dialog_content)).setText(content);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        confirm = (Button) view.findViewById(R.id.general_dialog_btn_confirm);
        cancel = (Button) view.findViewById(R.id.general_dialog_btn_cancel);
        cancel.setVisibility(View.GONE);
    }

    public void createNormalDialog(Context context,String content){
        this.context = context;
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
            this.dialog = null;
        }
//        view = LayoutInflater.from(context).inflate(
//                R.layout.dialog_normal, null);
        View contentView = View.inflate(context,R.layout.dialog_normal,null);
        //this.dialog = new AlertDialog.Builder(context).setView(view).create();
        this.dialog = new Dialog(context,R.style.NetaDialog);
        this.dialog.setContentView(contentView);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        TextView tv = ((TextView) contentView.findViewById(R.id.tv_content));
        View v = contentView.findViewById(R.id.ll_normal);
        if (!TextUtils.isEmpty(content)){
            tv.setText(content);
            tv.setVisibility(View.VISIBLE);
            v.setVisibility(View.GONE);
        }else {
            tv.setVisibility(View.GONE);
            v.setVisibility(View.VISIBLE);
        }
        dialog.setCancelable(false);
        cancel = (Button) contentView.findViewById(R.id.cancel);
        confirm = (Button) contentView.findViewById(R.id.confirm);
    }

    public void setButtonText(String comfirmBtn, String cancelBtn,int status) {
        if (cancel != null && confirm != null) {
            if (!TextUtils.isEmpty(comfirmBtn))
                confirm.setText(comfirmBtn);
            else
                confirm.setText(context.getString(R.string.label_confirm));
            if (!TextUtils.isEmpty(cancelBtn))
                cancel.setText(cancelBtn);
            else
                cancel.setText(context.getString(R.string.label_dlg_cancel));
            if(status == 2){
                cancel.setVisibility(View.GONE);
            }
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    private void setOnClickListener() {
        if (cancel != null && confirm != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.CancelOnClick();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.ConfirmOnClick();
                }
            });
        }
    }

    public void showDialog() {
        if (this.dialog != null) {
            setOnClickListener();
            this.dialog.show();
        }
    }

    public void dismissDialog() {
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
            this.dialog = null;
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener){
        if(dialog!= null)
            dialog.setOnDismissListener(onDismissListener);
    }

    public interface OnClickListener {
        void CancelOnClick();

        void ConfirmOnClick();
    }
}
