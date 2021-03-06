package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerCoinShopComponent;
import com.moemoe.lalala.di.modules.CoinShopModule;
import com.moemoe.lalala.model.entity.CoinShopEntity;
import com.moemoe.lalala.model.entity.OrderEntity;
import com.moemoe.lalala.presenter.CoinShopContract;
import com.moemoe.lalala.presenter.CoinShopPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.GlideImageLoader;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * 商品详情页
 * Created by yi on 2017/7/18.
 */

public class ShopDetailActivity extends BaseAppCompatActivity implements CoinShopContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;

    @Inject
    CoinShopPresenter mPresenter;

    private CoinShopEntity mShopEntity;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_commodity_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerCoinShopComponent.builder()
                .coinShopModule(new CoinShopModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mShopEntity = getIntent().getParcelableExtra("shop_detail");
        if(mShopEntity == null) {
            String id = getIntent().getStringExtra(UUID);
            if(!TextUtils.isEmpty(id)){
                mPresenter.loadShopDetail(id);
            }else {
                finish();
            }
        }else {
            init();
        }
    }

    private void init(){
        mTitle.setText(mShopEntity.getProductName());
        Banner topRoot = $(R.id.banner_top);
        topRoot.setLayoutParams(new LinearLayout.LayoutParams(DensityUtil.getScreenWidth(this), DensityUtil.getScreenWidth(this)));
        ArrayList<String> titles = new ArrayList<>();
        for(int i = 0;i < mShopEntity.getImages().size();i++){
            titles.add(mShopEntity.getDesc());
        }
        topRoot.setImages(mShopEntity.getImages())
                .setBannerTitles(titles)
                .setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE)
                .setImageLoader(new GlideImageLoader())
                .start();
        ((TextView)$(R.id.tv_commodity_num)).setText("库存:" + (mShopEntity.getStock() - mShopEntity.getFreeze() < 0 ? 0 : mShopEntity.getStock() - mShopEntity.getFreeze()));
        ((TextView)$(R.id.tv_refresh_time)).setText(mShopEntity.getStockDesc());
        TextView tvBuy = $(R.id.tv_done);
        if(mShopEntity.getStock() - mShopEntity.getFreeze() <= 0){
            tvBuy.setText("已售罄");
            tvBuy.setSelected(true);
            tvBuy.setOnClickListener(null);
        }else {
            StringBuilder price = new StringBuilder();
            if(mShopEntity.getRmb() > 0){
                if(mShopEntity.getRmb()%100 != 0){
                    price.append((float)mShopEntity.getRmb()/100);
                }else {
                    price.append(mShopEntity.getRmb()/100);
                }
                price.append( "元");
            }
            if(mShopEntity.getRmb() > 0 && mShopEntity.getCoin() > 0) price.append(" + ");
            if(mShopEntity.getCoin() > 0){
                price.append(mShopEntity.getCoin()).append("节操");
            }
            tvBuy.setText(price);
            tvBuy.setSelected(false);
            tvBuy.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(mShopEntity.getBuyLimit() == 1){
                        mPresenter.createOrder(mShopEntity);
                    }else {
                        final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
                        dialogUtil.createEditDialog(ShopDetailActivity.this, mShopEntity.getBuyLimit(),2);
                        dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                            @Override
                            public void CancelOnClick() {
                                dialogUtil.dismissDialog();
                            }

                            @Override
                            public void ConfirmOnClick() {
                                String content = dialogUtil.getEditTextContent();
                                try {
                                    if(!TextUtils.isEmpty(content) && Integer.valueOf(content) > 0){
                                        if(Integer.valueOf(content) > mShopEntity.getBuyLimit()){
                                            showToast("超过购买限制");
                                        }else {
                                            mPresenter.createOrder(mShopEntity,Integer.valueOf(content));
                                            dialogUtil.dismissDialog();
                                        }
                                    }else {
                                        showToast(R.string.msg_input_err_coin);
                                    }
                                }catch (Exception e){
                                    showToast(R.string.msg_input_err_coin);
                                }
                            }
                        });
                        dialogUtil.showDialog();
                    }
                }
            });
        }
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
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onLoadShopListSuccess(ArrayList<CoinShopEntity> list, boolean isPull) {

    }

    @Override
    public void onCreateOrderSuccess(OrderEntity entity) {
        Intent i = new Intent(this,OrderActivity.class);
        i.putExtra("order",entity);
        i.putExtra("show_top",true);
        i.putExtra("show_status",false);
        startActivity(i);
        finish();
    }

    @Override
    public void onCreateOrderListSuccess(ArrayList<JsonObject> jsonObjects) {

    }

    @Override
    public void onLoadShopDetailSuccess(CoinShopEntity entity) {
        mShopEntity = entity;
        init();
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }
}
