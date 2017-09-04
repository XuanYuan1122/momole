package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;

import butterknife.BindView;

import static com.moemoe.lalala.utils.StartActivityConstant.REQ_GET_BAG;

/**
 * Created by yi on 2017/8/22.
 */

public class BagOpenActivity extends BaseAppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_get_bag)
    TextView mTvOpen;

    @Override
    protected int getLayoutId() {
        return R.layout.view_bag_open_root;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvOpen.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent(BagOpenActivity.this,BagEditActivity.class);
                i.putExtra("bg","");
                i.putExtra("read_type","");
                i.putExtra(BagEditActivity.EXTRA_TYPE,BagEditActivity.TYPE_BAG_OPEN);
                startActivityForResult(i,REQ_GET_BAG);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_GET_BAG && resultCode == RESULT_OK){
            PreferenceUtils.getAuthorInfo().setOpenBag(true);
            Intent i = new Intent(this,NewBagActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }
}
