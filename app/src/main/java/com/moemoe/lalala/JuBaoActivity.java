package com.moemoe.lalala;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;

import org.json.JSONArray;

/**
 * Created by Haru on 2016/5/4 0004.
 */
@ContentView(R.layout.ac_jubao)
public class JuBaoActivity extends BaseActivity {
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_CONTENT = "content";
    public static final String EXTRA_TARGET = "target";

    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTvTitle;
    private String mUuid;
    private String mName;
    private String mContent;
    @FindView(R.id.tv_item_1)
    private TextView mTvContent1;
    @FindView(R.id.tv_item_2)
    private TextView mTvContent2;
    private CheckBox[] mCbItems;
    @FindView(R.id.tv_jubao_go)
    private View mBtnGo;
    @FindView(R.id.edt_content)
    private EditText mEtContent;

    private String mTarget;

    @Override
    protected void initView() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvTitle.setText(R.string.label_jubao);
        mTvTitle.setVisibility(View.VISIBLE);
        if(mIntent == null){
            finish();
        }
        mUuid = mIntent.getStringExtra(EXTRA_KEY_UUID);
        mName = mIntent.getStringExtra(EXTRA_NAME);
        mContent = mIntent.getStringExtra(EXTRA_CONTENT);
        mTarget = mIntent.getStringExtra(EXTRA_TARGET);
        mCbItems = new CheckBox[4];
        int ids[] = new int[]{R.id.cb_anli, R.id.cb_h, R.id.cb_guomindang, R.id.cb_avatar};
        for(int i = 0; i < 4; i++){
            mCbItems[i] = (CheckBox)findViewById(ids[i]);
            mCbItems[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mBtnGo.setEnabled(false);
                    for(int i = 0; i < 4; i++){
                        if(mCbItems[i].isChecked()){
                            mBtnGo.setEnabled(true);
                            break;
                        }
                    }
                }
            });
        }
        mTvContent1.setText(mName);
        mTvContent2.setText(mContent);
        mBtnGo.setEnabled(false);
        mBtnGo.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {

                if (!NetworkUtils.checkNetworkAndShowError(JuBaoActivity.this)){
                    return;
                }
                // start jubao
                JSONArray array = new JSONArray();
                for(int i = 0; i < mCbItems.length; i++){
                    if(mCbItems[i].isChecked()){
                        array.put(mCbItems[i].getText().toString());
                    }
                }
                createDialog();
                Otaku.getCommonV2().report(mPreferMng.getToken(),mTarget,mUuid,array.toString(),mEtContent.getText().toString()).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                    @Override
                    public void success(String token, String s) {
                        finalizeDialog();
                        ToastUtil.showToast(JuBaoActivity.this, R.string.msg_jubao_success);
                        finish();
                    }

                    @Override
                    public void failure(String e) {
                        finalizeDialog();
                        ToastUtil.showToast(JuBaoActivity.this, R.string.msg_jubao_fail);
                    }
                }));
            }
        });
    }
}
