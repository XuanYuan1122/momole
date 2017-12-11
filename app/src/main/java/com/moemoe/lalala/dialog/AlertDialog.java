package com.moemoe.lalala.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.moemoe.lalala.R;

/**
 * 基本对话框
 * Created by yi on 2016/11/28.
 */

public class AlertDialog extends Dialog {
    private ListView mListView;
    private ImageView mIconView ;
    private TextView mTitleView;
    private TextView mMessageView;
    private FrameLayout mCustomView;
    private Button button_negative; // Negative
    private Button button_neutral;  // Neutral
    private Button button_positive; // Positive
    private View mBottomPanel;
    private View mTitlePanel;
    private View mDivider;
    private View mMessagePanel;
    private View mBtnDivider1,mBtnDivider2;
    private View mContentPanel;//中间层，在标题与底层按之间的中间层
    public AlertDialog(Context context, boolean cancelable,
                       OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public AlertDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public AlertDialog(Context context) {
        super(context);
        init();
    }
    private void init() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(getContext(), R.layout.alert_dialog, null);
        init(view);
    }
    public void init(View view){
        mIconView =view.findViewById(R.id.icon_view);
        mTitleView = view.findViewById(R.id.label_title);
        mMessageView= view.findViewById(R.id.message_panel);
        mCustomView = view.findViewById(R.id.custom_panel);
        button_negative = view.findViewById(android.R.id.button1);
        button_neutral = view.findViewById(android.R.id.button2);
        button_positive = view.findViewById(android.R.id.button3);
        mBtnDivider1 = view.findViewById(R.id.divider_1);
        mBtnDivider2 = view.findViewById(R.id.divider_2);
        mBottomPanel = view.findViewById(R.id.bottom_panel);
        mTitlePanel = view.findViewById(R.id.title_panel);
        mDivider = view.findViewById(R.id.divider);
        mMessagePanel = view.findViewById(R.id.scrollView);
        mContentPanel = view.findViewById(R.id.content_panel);
        button_negative.setVisibility(View.GONE);
        button_neutral.setVisibility(View.GONE);
        button_positive.setVisibility(View.GONE);
        mCustomView.setVisibility(View.GONE);
        mBottomPanel.setVisibility(View.GONE);
        mTitlePanel.setVisibility(View.GONE);
        mDivider.setVisibility(View.GONE);
        mMessagePanel.setVisibility(View.GONE);
        setContentView(view);
    }

    private ListView getListView() {
        if (mListView == null)
            mListView = (ListView) findViewById(R.id.list_panel);
        return mListView;
    }


    public void setIcon(Drawable icon) {
        mIconView.setImageDrawable(icon);
        makesureShowTitle();

    }
    public void setIcon(int iconId) {
        mIconView.setImageResource(iconId);
        makesureShowTitle();
    }


    public void setMessage(CharSequence message) {
        mMessageView.setText(message);
        mMessagePanel.setVisibility(View.VISIBLE);
    }

    private void setButton(final int which, CharSequence text, final OnClickListener listener) {
        Button button;
        switch(which){
            case BUTTON_NEGATIVE:
                button = button_negative;
                break;
            case BUTTON_POSITIVE:
                button = button_positive;
                break;
            case BUTTON_NEUTRAL:
                button = button_neutral;
                break;
            default:
                button = button_negative;
        }
        mBottomPanel.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        if(button_negative.getVisibility()==View.VISIBLE&&button_neutral.getVisibility()==View.VISIBLE)
            mBtnDivider1.setVisibility(View.VISIBLE);
        else
            mBtnDivider1.setVisibility(View.GONE);
        if(button_positive.getVisibility()==View.VISIBLE&&(button_negative.getVisibility()==View.VISIBLE||button_neutral.getVisibility()==View.VISIBLE))
            mBtnDivider2.setVisibility(View.VISIBLE);
        else
            mBtnDivider2.setVisibility(View.GONE);

        button.setText(text);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(AlertDialog.this, which);
                boolean dismiss = true;
                switch(which){
                    case BUTTON_NEGATIVE:
                        dismiss = btn_negative_dismiss_after_click;
                        break;
                    case BUTTON_POSITIVE:
                        dismiss = btn_positive_dismiss_after_click;
                        break;
                    case BUTTON_NEUTRAL:
                        dismiss = btn_neutral_dismiss_after_click;
                        break;
                }
                if(dismiss)
                    dismiss();
            }
        });
    }
    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
        makesureShowTitle();
    }
    public void	 setTitle(int titleId){
        mTitleView.setText(titleId);
        makesureShowTitle();
    }

    public void setView(View view){
        mCustomView.setVisibility(View.VISIBLE);
        mCustomView.removeAllViews();
        mCustomView.addView(view);
    }


    private void makesureShowTitle() {
        mTitlePanel.setVisibility(View.VISIBLE);
        mDivider.setVisibility(View.VISIBLE);
    }


    public static class Builder {
        Context context;
        AlertDialog dialog;

        public Builder(Context context) {
            super();
            this.context = context;
            dialog = new AlertDialog(context);
        }

        public Builder(Context context, AlertDialog dialog) {
            super();
            this.context = context;
            this.dialog = dialog;
        }
        public AlertDialog create() {
            return dialog;
        }

        public Builder setAdapter(ListAdapter adapter,
                                  final OnClickListener listener) {
            ListView list = dialog.getListView();
            list.setAdapter(adapter);

            list.setOnItemClickListener(new ListView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> listView, View view,
                                        int position, long id) {
                    if (listener != null)
                        listener.onClick(dialog, position);
                    dialog.dismiss();
                }
            });
            return this;
        }

        public Builder	 setIcon(Drawable icon){
            dialog.setIcon(icon);
            return this;
        }
        public Builder	 setIcon(int iconId){
            dialog.setIcon(iconId);
            return this;
        }

        public Builder	 setItems(CharSequence[] items, OnClickListener listener){
            ListAdapter adapter = new ArrayAdapter<>(context, R.layout.simple_list_item_1,android.R.id.text1,items);
            setAdapter(adapter, listener);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            dialog.setCancelable(cancelable);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            dialog.setTitle(title);
            return this;
        }
        public Builder	 setTitle(int titleId){
            dialog.setTitle(titleId);
            return this;
        }

        public Builder	 setView(View view){
            dialog.setView(view);
            return this;
        }

        public Builder setMessage(CharSequence message ) {
            dialog.setMessage(message)	;
            return this;
        }
        public Builder	 setMessage(int messageId) {
            dialog.setMessage(context.getResources().getText(messageId))	;
            return this;
        }

        public Builder	 setNegativeButton(int textId, OnClickListener listener){
            dialog.setButton(BUTTON_NEGATIVE, context.getResources().getString(textId), listener);
            return this;
        }

        public Builder setPositiveButton(String text, OnClickListener listener){
            dialog.setButton(BUTTON_POSITIVE, text, listener);
            return this;
        }

        public Builder	 setPositiveButton(int textId, OnClickListener listener){
            dialog.setButton(BUTTON_POSITIVE, context.getResources().getString(textId), listener);
            return this;
        }


        public void show(){
            dialog.show();
        }
    }

    private boolean btn_positive_dismiss_after_click = true;
    private boolean btn_negative_dismiss_after_click = true;
    private boolean btn_neutral_dismiss_after_click = true;
}
