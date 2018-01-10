package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;

import butterknife.BindView;

/**
 *
 * Created by yi on 2017/12/28.
 */

public class MotifyTaiciActivity extends BaseAppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvDone;
    @BindView(R.id.et_text)
    EditText mEtText;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_motify_taici;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        mEtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mEtText.getText();
                int len = editable.length();
                if (len > 30) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    mEtText.setText(editable.subSequence(0, 30));
                    editable = mEtText.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvTitle.setText("台词");
        mTvDone.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvDone, (int)getResources().getDimension(R.dimen.x36));
        mTvDone.setText("确认");
        mTvDone.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String taici = mEtText.getText().toString();
                if(!TextUtils.isEmpty(taici)){
                    Intent i = new Intent();
                    i.putExtra("taici",taici);
                    setResult(RESULT_OK,i);
                }
                finish();
            }
        });
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }
}
