package com.moemoe.lalala.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.moemoe.lalala.R;

/**
 * Created by yi on 2016/11/28.
 */

public class AlertDialogUtil {
    private static AlertDialogUtil alertDialogUtil = null;
    private Dialog dialog = null;
    private View view;
    private OnClickListener onClickListener;
    private OnItemClickListener onItemClickListener;
    private Button confirm, cancel;
    private Button item1,item2,item3,item4;
    private EditText editText;
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

    public void createPromptNormalDialog(Context context, String content) {
        this.context = context;
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
            this.dialog = null;
        }
        View contentView = View.inflate(context,R.layout.dialog_normal_notice,null);
        this.dialog = new Dialog(context,R.style.NetaDialog);
        this.dialog.setContentView(contentView);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        TextView tv = ((TextView) contentView.findViewById(R.id.tv_content));
        tv.setText(content);
        dialog.setCancelable(false);
        cancel = (Button) contentView.findViewById(R.id.cancel);
        confirm = (Button) contentView.findViewById(R.id.confirm);

        if (!TextUtils.isEmpty(content))
            ((TextView) contentView.findViewById(R.id.tv_content)).setText(content);
        else
            ((TextView) contentView.findViewById(R.id.tv_content)).setText("");

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


    public void createSelectDialog(Context context){
        this.context = context;
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
            this.dialog = null;
        }
        View contentView = View.inflate(context,R.layout.dialog_select,null);
        this.dialog = new Dialog(context,R.style.NetaDialog2);
        this.dialog.setContentView(contentView);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = DensityUtil.dip2px(context,256);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        dialog.setCancelable(false);
        item1 = (Button) contentView.findViewById(R.id.btn_item_1);
        item2 = (Button) contentView.findViewById(R.id.btn_item_2);
        item3 = (Button) contentView.findViewById(R.id.btn_item_3);
        item4 = (Button) contentView.findViewById(R.id.btn_item_4);
    }

    public void createNormalDialog(Context context,String content){
        this.context = context;
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
            this.dialog = null;
        }
        View contentView = View.inflate(context,R.layout.dialog_normal,null);
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

    public void createEditDialog(Context context, int total,int type){
        this.context = context;
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
            this.dialog = null;
        }
        View contentView = View.inflate(context,R.layout.dialog_jump_floor,null);
        this.dialog = new Dialog(context,R.style.NetaDialog);
        this.dialog.setContentView(contentView);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        TextView tv = (TextView) contentView.findViewById(R.id.tv_floor_num);
        TextView tv1 = (TextView) contentView.findViewById(R.id.tv_text);
        TextView tv2 = (TextView) contentView.findViewById(R.id.tv_text2);
        if(type == 1){
            tv.setText(context.getString(R.string.label_total_floor,total));
            tv1.setText("跳转到");
            tv2.setText("楼");
        }else {
            tv.setText(context.getString(R.string.label_total_coin,total));
            tv1.setText("献上");
            tv2.setText("枚节操");
        }
        editText = (EditText) contentView.findViewById(R.id.et_floor);
        dialog.setCancelable(false);
        cancel = (Button) contentView.findViewById(R.id.cancel);
        confirm = (Button) contentView.findViewById(R.id.confirm);
    }

    public void createAddLabelDialog(Context context){
        this.context = context;
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
            this.dialog = null;
        }
        View contentView = View.inflate(context,R.layout.dialog_add_label,null);
        this.dialog = new Dialog(context,R.style.NetaDialog);
        this.dialog.setContentView(contentView);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        editText = (EditText) contentView.findViewById(R.id.et_content);
        dialog.setCancelable(false);
        cancel = (Button) contentView.findViewById(R.id.cancel);
        confirm = (Button) contentView.findViewById(R.id.confirm);
    }

    public String getEditTextContent(){
        String res = "";
        if(editText != null){
            res = editText.getText().toString();
        }
        return res;
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
                cancel.setText(context.getString(R.string.label_cancel));
            if(status == 2){
                cancel.setVisibility(View.GONE);
            }
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
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
        if(item1 != null){
            item1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(0);
                }
            });
        }
        if(item2 != null){
            item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(1);
                }
            });
        }
        if(item3 != null){
            item3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(2);
                }
            });
        }
        if(item4 != null){
            item4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(3);
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
            this.context = null;
            this.dialog = null;
            alertDialogUtil = null;
            dialog = null;
            view = null;
            onClickListener = null;
            onItemClickListener = null;
            confirm = null;
            cancel = null;
            item1 = null;
            item2 = null;
            item3 = null;
            item4 = null;
            editText = null;
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener){
        if(dialog!= null)
            dialog.setOnDismissListener(onDismissListener);
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnClickListener {
        void CancelOnClick();

        void ConfirmOnClick();
    }
}
