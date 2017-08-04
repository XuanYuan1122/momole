package com.moemoe.lalala.view.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerOrderComponent;
import com.moemoe.lalala.di.modules.OrderModule;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.OrderEntity;
import com.moemoe.lalala.model.entity.PayReqEntity;
import com.moemoe.lalala.model.entity.PayResEntity;
import com.moemoe.lalala.presenter.OrderContract;
import com.moemoe.lalala.presenter.OrderPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IpAdressUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.pingplusplus.android.Pingpp;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2017/7/14.
 */

public class OrderActivity extends BaseAppCompatActivity implements OrderContract.View{

    private static final String TYPE_RECHARGE = "recharge";
    private static final String TYPE_VIRTUAL = "virtual";
    private static final String TYPE_ACTUALS = "actuals";
    private static final int REQ_ADDRESS = 10000;

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.et_order_mark)
    EditText mEtMark;

    @Inject
    OrderPresenter mPresenter;

    private String orderNo;
    private boolean mIsDone;
    private OrderEntity order;
    private BottomMenuFragment bottomFragment;
    private int mPosition;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_order;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        ImmersionBar.with(this)
//                .statusBarView(R.id.top_view)
//                .statusBarDarkFont(true,0.2f)
//                .transparentNavigationBar()
//                .init();
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerOrderComponent.builder()
                .orderModule(new OrderModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        order = getIntent().getParcelableExtra("order");
        boolean showTop = getIntent().getBooleanExtra("show_top",false);
        boolean showStatus = getIntent().getBooleanExtra("show_status",false);
        mPosition = getIntent().getIntExtra("position", -1);
        mIsDone = order.getStatus() == 2;
        mEtMark.setEnabled(!mIsDone);
        if(showTop){
            TextView orderTime = $(R.id.tv_order_time);
            orderTime.setVisibility(View.VISIBLE);
            orderTime.setText("订单有效期至:" + order.getEndTime());
            $(R.id.ll_order_bottom_root).setVisibility(View.VISIBLE);
        }
        if(showStatus){
            $(R.id.ll_order_state).setVisibility(View.VISIBLE);
            if(order.getStatus() == 1){
                $(R.id.tv_order_state).setSelected(true);
                ((TextView)$(R.id.tv_order_state)).setText("等待支付");
            }else if(order.getStatus() == 2){
                $(R.id.tv_order_state).setSelected(false);
                ((TextView)$(R.id.tv_order_state)).setText("购买成功");
            }else {
                $(R.id.ll_order_state).setVisibility(View.INVISIBLE);
            }
        }
        Glide.with(this)
                .load(StringUtils.getUrl(this, order.getIcon(), DensityUtil.dip2px(this,110),DensityUtil.dip2px(this,110),false,true))
                .override(DensityUtil.dip2px(this,110),DensityUtil.dip2px(this,110))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .into((ImageView) $(R.id.iv_commodity));
        ((TextView)$(R.id.tv_commodity_title)).setText(order.getProductName());
        ((TextView)$(R.id.tv_commodity_desc)).setText(order.getDesc());
        StringBuilder price = new StringBuilder();
        if(order.getRmb() > 0){
            if(order.getRmb()%100 != 0){
                price.append((float)order.getRmb()/100);
            }else {
                price.append(order.getRmb()/100);
            }
            price.append( "元");
        }
        if(order.getRmb() > 0 && order.getCoin() > 0) price.append(" + ");
        if(order.getCoin() > 0){
            price.append(order.getCoin()).append("节操");
        }
        ((TextView)$(R.id.tv_commodity_price)).setText(price);
        ((TextView)$(R.id.tv_order_num)).setText(order.getOrderNo());
        orderNo = order.getOrderNo();
        if(TYPE_RECHARGE.equals(order.getOrderType())){
            $(R.id.ll_order_mark).setVisibility(View.VISIBLE);
            $(R.id.tv_order_instruction).setVisibility(View.VISIBLE);
            ((TextView)$(R.id.tv_order_instruction)).setText("此类商品为Neta官方代充，请在备注中填写您需要充值的账号");
            mEtMark.setHint("需要充值的手机号或者账号");
            if(!TextUtils.isEmpty(order.getLastRemark())){
                mEtMark.setText(order.getLastRemark());
            }
        }else if(TYPE_VIRTUAL.equals(order.getOrderType())){
            $(R.id.tv_order_instruction).setVisibility(View.VISIBLE);
            ((TextView)$(R.id.tv_order_instruction)).setText("购买后等待官方发放");
        }else if(TYPE_ACTUALS.equals(order.getOrderType())){
            if(!TextUtils.isEmpty(order.getAddress().getAddress())){
                $(R.id.ll_order_address_done).setVisibility(View.VISIBLE);
                ((TextView)$(R.id.tv_user_name)).setText(order.getAddress().getUserName());
                ((TextView)$(R.id.tv_phone)).setText(order.getAddress().getPhone());
                ((TextView)$(R.id.tv_address)).setText(order.getAddress().getAddress());
            }else {
                $(R.id.ll_order_address).setVisibility(View.VISIBLE);
            }
            $(R.id.ll_order_mark).setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(order.getLastRemark())){
                mEtMark.setText(order.getLastRemark());
            }
        }else {
            showToast("当前版本不支持该类商品，请升级至最新版本");
            finish();
        }
        initPayMenu();
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
        mTitle.setText(getString(R.string.label_order));
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    private void initPayMenu(){
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(0, getString(R.string.label_alipay));
        items.add(item);
        item = new MenuItem(1,getString(R.string.label_wx));
        items.add(item);
        item = new MenuItem(2,getString(R.string.label_qpay));
        items.add(item);
        bottomFragment = new BottomMenuFragment();
        bottomFragment.setShowTop(true);
        bottomFragment.setTopContent("选择支付方式");
        bottomFragment.setMenuItems(items);
        bottomFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                createDialog();
                String payType = "";
                if (itemId == 0) {
                    payType = "alipay";
                } else if (itemId == 1) {
                    payType = "wx";
                } else if (itemId == 2) {
                    payType = "qpay";
                }
                PayReqEntity entity = new PayReqEntity(order.getAddress().getAddress(),
                        payType,
                        IpAdressUtils.getIpAdress(OrderActivity.this),
                        order.getOrderId(),
                        order.getAddress().getPhone(),
                        mEtMark.getText().toString(),
                        order.getAddress().getUserName());
                mPresenter.payOrder(entity);
            }
        });
    }


    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @OnClick({R.id.tv_copy,R.id.ll_order_address,R.id.ll_order_address_done,R.id.tv_cancel_order,R.id.tv_done_order})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_copy:
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("订单号", orderNo);
                cmb.setPrimaryClip(mClipData);
                ToastUtils.showShortToast(this, getString(R.string.label_level_copy_success));
                break;
            case R.id.ll_order_address:
                if(!mIsDone){
                    Intent i = new Intent(OrderActivity.this, AddAddressActivity.class);
                    startActivityForResult(i, REQ_ADDRESS);
                }
                break;
            case R.id.ll_order_address_done:
                if(!mIsDone){
                    Intent i1 = new Intent(OrderActivity.this, AddAddressActivity.class);
                    String name = ((TextView)$(R.id.tv_user_name)).getText().toString();
                    String phone = ((TextView)$(R.id.tv_phone)).getText().toString();
                    String address = ((TextView)$(R.id.tv_address)).getText().toString();
                    AddressEntity entity = new AddressEntity(address, phone, name);
                    i1.putExtra("address",entity);
                    startActivityForResult(i1, REQ_ADDRESS);
                }
                break;
            case R.id.tv_cancel_order:
                final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
                dialogUtil.createPromptNormalDialog(OrderActivity.this, "确定取消订单");
                dialogUtil.setButtonText(getString(R.string.label_confirm), getString(R.string.label_cancel),0);
                dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        dialogUtil.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        mPresenter.cancelOrder(order.getOrderId());
                        dialogUtil.dismissDialog();
                    }
                });
                dialogUtil.showDialog();
                break;
            case R.id.tv_done_order:
                if(TYPE_RECHARGE.equals(order.getOrderType())){
                    String mark = mEtMark.getText().toString();
                    if(TextUtils.isEmpty(mark)){
                        showToast("备注不能为空");
                        return;
                    }
                }else if(TYPE_VIRTUAL.equals(order.getOrderType())){

                }else if(TYPE_ACTUALS.equals(order.getOrderType())) {
                    AddressEntity entity = order.getAddress();
                    if(TextUtils.isEmpty(entity.getAddress())){
                        showToast("地址信息不完整");
                        return;
                    }
                }
                final AlertDialogUtil dialogUtil1 = AlertDialogUtil.getInstance();
                dialogUtil1.createPromptNormalDialog(OrderActivity.this, "确定购买");
                dialogUtil1.setButtonText(getString(R.string.label_confirm), getString(R.string.label_cancel),0);
                dialogUtil1.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        dialogUtil1.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        dialogUtil1.dismissDialog();
                        if(order.getRmb() > 0){
                            bottomFragment.show(getSupportFragmentManager(), "payMenu");
                        }else {
                            createDialog();
                            PayReqEntity entity = new PayReqEntity(order.getAddress().getAddress(),
                                    "",
                                    IpAdressUtils.getIpAdress(OrderActivity.this),
                                    order.getOrderId(),
                                    order.getAddress().getPhone(),
                                    mEtMark.getText().toString(),
                                    order.getAddress().getUserName());
                            mPresenter.payOrder(entity);
                        }
                    }
                });
                dialogUtil1.showDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_ADDRESS && resultCode == RESULT_OK){
            AddressEntity entity = data.getParcelableExtra("address");
            if(!TextUtils.isEmpty(entity.getAddress())){
                $(R.id.ll_order_address_done).setVisibility(View.VISIBLE);
                $(R.id.ll_order_address).setVisibility(View.GONE);
                ((TextView)$(R.id.tv_user_name)).setText("收货人：" + entity.getUserName());
                ((TextView)$(R.id.tv_phone)).setText(entity.getPhone());
                ((TextView)$(R.id.tv_address)).setText("收货地址：" + entity.getAddress());
                order.setAddress(entity);
            }else {
                $(R.id.ll_order_address_done).setVisibility(View.GONE);
                $(R.id.ll_order_address).setVisibility(View.VISIBLE);
            }
        } else if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                finalizeDialog();
                if("success".equals(result)){
                    showToast("支付成功");
                    Intent i = new Intent();
                    i.putExtra("position", mPosition);
                    i.putExtra("type", "pay");
                    setResult(RESULT_OK, i);
                    finish();
                }else {
                    showToast(errorMsg);
                }
            }
        }
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this, code, msg);
    }

    @Override
    public void onCancelOrderSuccess() {
        finalizeDialog();
        showToast("取消订单成功");
        Intent i = new Intent();
        i.putExtra("position", mPosition);
        i.putExtra("type", "cancel");
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onPayOrderSuccess(PayResEntity entity) {
        if(entity.isSuccess()){
            finalizeDialog();
            showToast("支付成功");
            Intent i = new Intent();
            i.putExtra("position", mPosition);
            i.putExtra("type", "pay");
            setResult(RESULT_OK, i);
            finish();
        }else {
            if(entity.getCharge() != null){
                if("qpay".equals(entity.getCharge().get("channel"))){
                    Pingpp.createPayment(OrderActivity.this, entity.getCharge().toString(),"qwallet1104765197");
                }else {
                    Pingpp.createPayment(OrderActivity.this, entity.getCharge().toString());
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(mDialog != null && mDialog.isShowing()){
            finalizeDialog();
            return;
        }
        super.onBackPressed();
    }
}
