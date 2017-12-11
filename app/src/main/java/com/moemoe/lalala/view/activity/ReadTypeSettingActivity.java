package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2016/12/16.
 */

public class ReadTypeSettingActivity extends BaseAppCompatActivity{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvDone;

    private ImageView mIvNormal;
    private ImageView mIvImg;
    private ImageView mIvRead;
    private String mReadType;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_read_type_setting;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        ImmersionBar.with(this)
//                .statusBarView(R.id.top_view)
//                .statusBarDarkFont(true,0.2f)
//                .transparentNavigationBar()
//                .init();
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        if(getIntent() == null){
            finish();
            return;
        }
        mReadType = getIntent().getStringExtra("read_type");
        mTvDone.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvDone, (int)getResources().getDimension(R.dimen.x36));
        mTvDone.getPaint().setFakeBoldText(true);
        mTvDone.setText(R.string.label_done);
        initStyleView(R.id.set_normal);
        initStyleView(R.id.set_img);
        initStyleView(R.id.set_read);

    }

    private void initStyleView(int resId){
        View v = findViewById(resId);
        TextView name = getFunNameTv(v);
        TextView detail = getFunDetailTv(v);
        if(resId == R.id.set_normal){
            name.setText(R.string.label_normal_type);
            detail.setVisibility(View.GONE);
            mIvNormal = getFunIndicateIv(v);
            if(TextUtils.isEmpty(mReadType)){
                mIvNormal.setSelected(true);
            }
        }else if(resId == R.id.set_img){
            name.setText(R.string.label_img_type);
            detail.setVisibility(View.VISIBLE);
            detail.setText(R.string.label_img_type_detail);
            mIvImg = getFunIndicateIv(v);
            if(mReadType.equalsIgnoreCase("IMAGE")){
                mIvImg.setSelected(true);
            }
        }else if(resId == R.id.set_read){
            name.setText(R.string.label_read_type);
            detail.setVisibility(View.VISIBLE);
            detail.setText(R.string.label_read_type_detail);
            mIvRead = getFunIndicateIv(v);
            if(mReadType.equalsIgnoreCase("TEXT")){
                mIvRead.setSelected(true);
            }
        }
    }

    private TextView getFunNameTv(View v){
        return (TextView) v.findViewById(R.id.tv_function_name);
    }

    private TextView getFunDetailTv(View v){
        return (TextView) v.findViewById(R.id.tv_function_detail);
    }

    private ImageView getFunIndicateIv(View v){
        return (ImageView) v.findViewById(R.id.iv_select_img);
    }

    @OnClick({R.id.set_normal,R.id.set_img,R.id.set_read,R.id.tv_menu})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.set_normal:
                mReadType = "";
                mIvNormal.setSelected(true);
                mIvImg.setSelected(false);
                mIvRead.setSelected(false);
                break;
            case R.id.set_img:
                mReadType = "IMAGE";
                mIvNormal.setSelected(false);
                mIvImg.setSelected(true);
                mIvRead.setSelected(false);
                break;
            case R.id.set_read:
                mReadType = "TEXT";
                mIvNormal.setSelected(false);
                mIvImg.setSelected(false);
                mIvRead.setSelected(true);
                break;
            case R.id.tv_menu:
                Intent i = new Intent();
                i.putExtra("read_type",mReadType);
                setResult(RESULT_OK,i);
                finish();
                break;
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

}
