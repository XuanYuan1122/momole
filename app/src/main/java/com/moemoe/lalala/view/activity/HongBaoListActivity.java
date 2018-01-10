package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerHongBaoListComponent;
import com.moemoe.lalala.di.modules.HongBaoListModule;
import com.moemoe.lalala.model.entity.HongBaoEntity;
import com.moemoe.lalala.presenter.HongBaoListContract;
import com.moemoe.lalala.presenter.HongBaoListPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.HongbaoListAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * 红包领取列表
 * Created by yi on 2018/1/4.
 */

public class HongBaoListActivity  extends BaseAppCompatActivity implements HongBaoListContract.View{

    @BindView(R.id.iv_back)
    View mBack;
    @BindView(R.id.list)
    RecyclerView mList;

    @Inject
    HongBaoListPresenter mPresenter;

    private HongbaoListAdapter mAdapter;

    public static void startActivity(Context context,String id,String icon,int totalCoin,int totalNum){
        Intent i = new Intent(context,HongBaoListActivity.class);
        i.putExtra(UUID,id);
        i.putExtra("icon",icon);
        i.putExtra("totalCoin",totalCoin);
        i.putExtra("totalNum",totalNum);
        context.startActivity(i);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ac_one_list_align_top;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerHongBaoListComponent.builder()
                .hongBaoListModule(new HongBaoListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        String icon = getIntent().getStringExtra("icon");
        int coin = getIntent().getIntExtra("totalCoin",0);
        int num = getIntent().getIntExtra("totalNum",0);
        String id = getIntent().getStringExtra(UUID);

        mList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HongbaoListAdapter();
        mList.setAdapter(mAdapter);
        View hongbaoHead = LayoutInflater.from(this).inflate(R.layout.item_hongbao_top, null);
        ImageView avatar = hongbaoHead.findViewById(R.id.iv_avatar);
        TextView totalCoin = hongbaoHead.findViewById(R.id.tv_total_coin);
        TextView totalNum = hongbaoHead.findViewById(R.id.tv_total_hongbao);

        int size = (int) getResources().getDimension(R.dimen.y92);
        Glide.with(this)
                .load(StringUtils.getUrl(this,icon,size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(avatar);
        totalCoin.setText(String.format(getString(R.string.label_hongbao_total_coin),coin));
        totalNum.setText(String.format(getString(R.string.label_hongbao_total),num));
        mAdapter.addHeaderView(hongbaoHead);
        mPresenter.loadHongBaoList(id);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
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

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    public void onLoadHongBaoListSuccess(ArrayList<HongBaoEntity> entities) {
        mAdapter.setList(entities);
    }
}
