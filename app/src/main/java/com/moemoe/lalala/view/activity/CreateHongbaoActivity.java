package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.SoftKeyboardUtils;
import com.moemoe.lalala.utils.ViewUtils;

import butterknife.BindView;

/**
 * 红包创建
 * Created by yi on 2018/1/4.
 */

public class CreateHongbaoActivity extends BaseAppCompatActivity {

    @BindView(R.id.include_toolbar)
    View mToolBarRoot;
    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.et_coin_num)
    EditText mEtCoin;
    @BindView(R.id.et_hongbao_num)
    EditText mEtNum;
    @BindView(R.id.tv_save_hongbao)
    View mSave;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_create_hongbao;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        mSave.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                saveHongbao();
            }
        });
    }

    private void saveHongbao(){
        SoftKeyboardUtils.dismissSoftKeyboard(CreateHongbaoActivity.this);
        try{
            String coin = mEtCoin.getText().toString();
            String num = mEtNum.getText().toString();
            if(TextUtils.isEmpty(coin) || Integer.valueOf(coin) > 9999){
                showToast("节操数必须在0-9999之间");
            }else if(TextUtils.isEmpty(num) || Integer.valueOf(num) > Integer.valueOf(coin) * 100){
                showToast("红包数必须大于0,小于" + (Integer.valueOf(coin) * 100));
            }else {
                Intent i = new Intent();
                i.putExtra("coin",Integer.valueOf(coin));
                i.putExtra("num",Integer.valueOf(num));
                setResult(RESULT_OK,i);
                finish();
            }
        }catch (Exception e){
            showToast("请输入正确的数值");
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mToolBarRoot.setBackgroundColor(Color.TRANSPARENT);
        mTvMenuLeft.setVisibility(View.VISIBLE);
        ViewUtils.setLeftMargins(mTvMenuLeft, (int) getResources().getDimension(R.dimen.x36));
        mTvMenuLeft.setTextColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
        mTvMenuLeft.setText(getString(R.string.label_give_up));
        mTvMenuLeft.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                SoftKeyboardUtils.dismissSoftKeyboard(CreateHongbaoActivity.this);
                finish();
            }
        });
        mTvTitle.setText("节操红包");
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }
}
