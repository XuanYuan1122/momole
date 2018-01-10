package com.moemoe.lalala.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerGameComponent;
import com.moemoe.lalala.di.modules.GameModule;
import com.moemoe.lalala.model.entity.GamePriceInfoEntity;
import com.moemoe.lalala.model.entity.PayReqEntity;
import com.moemoe.lalala.model.entity.PayResEntity;
import com.moemoe.lalala.presenter.GameContract;
import com.moemoe.lalala.presenter.GamePresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IpAdressUtils;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.pingplusplus.android.Pingpp;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 三国游戏
 * Created by yi on 2017/12/8.
 */

public class SanGuoActivity extends BaseAppCompatActivity implements GameContract.View {

    @BindView(R.id.iv_title)
    View mIvTitle;
    @BindView(R.id.iv_continue)
    View mIvContinue;
    @BindView(R.id.ll_role_root)
    View mLlRoleRoot;
    @BindView(R.id.ll_role_root_2)
    View mLlRoleRoot2;
    @BindView(R.id.iv_select_title)
    View mSelectTitle;
    @BindView(R.id.tv_fuhuobi)
    TextView mTvFuhuobi;
    @BindView(R.id.tv_introduce)
    View mTvIntroduce;
    @BindView(R.id.iv_duihuan)
    View mIvDuihuan;
    @BindView(R.id.tv_my_coin)
    TextView mTvCoin;
    @BindView(R.id.iv_start_game)
    View mIvStart;
    @BindView(R.id.rl_len_root)
    View mRlLenRoot;
    @BindView(R.id.rl_sari_root)
    View mRlSariRoot;
    @BindView(R.id.rl_mei_root)
    View mRlMeiRoot;
    @BindView(R.id.rl_xiaozhang_root)
    View mRlXiaozhangRoot;
    @BindView(R.id.rl_fuzi_root)
    View mRlFuziRoot;
    @BindView(R.id.iv_xiaozhang_has)
    View mXiaozhangHas;
    @BindView(R.id.iv_fuzi_has)
    View mFuziHas;

    @Inject
    GamePresenter mPresenter;

    ObjectAnimator alphaContinue;
    private ArrayList<Integer> selectRole;
    private boolean hasFuzi;
    private boolean hasGetVip;
    private boolean hasGetFuzi;
    private int getFuHuoNum;
    private int buyType;//1.vip,2.role,3.ciyuanbi

    @Override
    protected int getLayoutId() {
        return R.layout.ac_sanguo;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerGameComponent.builder()
                .gameModule(new GameModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        selectRole = new ArrayList<>();
        ObjectAnimator scaleTitleX = ObjectAnimator.ofFloat(mIvTitle,"scaleX",0.5f,1.0f);
        scaleTitleX.setDuration(1000);
        ObjectAnimator scaleTitleY = ObjectAnimator.ofFloat(mIvTitle,"scaleY",0.5f,1.0f);
        scaleTitleY.setDuration(1000);
        ObjectAnimator alphaTitle = ObjectAnimator.ofFloat(mIvTitle,"alpha",0.5f,1.0f);
        alphaTitle.setDuration(1000);
        AnimatorSet titleSet = new AnimatorSet();
        titleSet.play(scaleTitleX).with(scaleTitleY);
        titleSet.play(scaleTitleY).with(alphaTitle);
        titleSet.start();

        alphaContinue = ObjectAnimator.ofFloat(mIvContinue,"alpha",0.5f,1.0f);
        alphaContinue.setDuration(500);
        alphaContinue.setRepeatMode(ValueAnimator.REVERSE);
        alphaContinue.setRepeatCount(ValueAnimator.INFINITE);
        alphaContinue.start();
        mPresenter.getPriceInfo();
        mPresenter.loadTicketsNum();
        mPresenter.loadFuHuoNum(PreferenceUtils.getUUid());
        mPresenter.hasRole(PreferenceUtils.getUUid(),"dantou","fuzi");
        selectRole.add(29);
        selectRole.add(2);
        selectRole.add(15);
        if(!TextUtils.isEmpty(PreferenceUtils.getAuthorInfo().getVipTime())){
            mXiaozhangHas.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.rl_root,R.id.rl_len_root,R.id.rl_sari_root,
            R.id.rl_mei_root,R.id.rl_xiaozhang_root,
            R.id.iv_duihuan,R.id.iv_start_game,R.id.rl_fuzi_root})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.rl_root:
                if(mIvTitle.getVisibility() == View.VISIBLE){
                    if(alphaContinue != null && alphaContinue.isRunning()){
                        alphaContinue.cancel();
                        mIvTitle.setVisibility(View.GONE);
                        mIvContinue.setVisibility(View.GONE);
                        mSelectTitle.setVisibility(View.VISIBLE);
                        mLlRoleRoot.setVisibility(View.VISIBLE);
                        mLlRoleRoot2.setVisibility(View.VISIBLE);
                        mTvFuhuobi.setVisibility(View.VISIBLE);
                        mTvIntroduce.setVisibility(View.VISIBLE);
                        mIvDuihuan.setVisibility(View.VISIBLE);
                        mTvCoin.setVisibility(View.VISIBLE);
                        mIvStart.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.rl_len_root:
                if(!selectRole.contains(29)){
                    selectRole.add(29);
                    mRlLenRoot.setBackgroundResource(R.drawable.shape_main_background_white_y8);
                }else {
                    selectRole.remove(Integer.valueOf(29));
                    mRlLenRoot.setBackground(null);
                }

                break;
            case R.id.rl_sari_root:
                if(!selectRole.contains(2)){
                    selectRole.add(2);
                    mRlSariRoot.setBackgroundResource(R.drawable.shape_main_background_white_y8);
                }else {
                    selectRole.remove(Integer.valueOf(2));
                    mRlSariRoot.setBackground(null);
                }

                break;
            case R.id.rl_mei_root:
                if(!selectRole.contains(15)){
                    selectRole.add(15);
                    mRlMeiRoot.setBackgroundResource(R.drawable.shape_main_background_white_y8);
                }else {
                    selectRole.remove(Integer.valueOf(15));
                    mRlMeiRoot.setBackground(null);
                }
                break;
            case R.id.rl_xiaozhang_root:
                if(TextUtils.isEmpty(PreferenceUtils.getAuthorInfo().getVipTime())){
                  //  showToast("vip才能选哦");
                    final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                    alertDialogUtil.createNormalDialog(SanGuoActivity.this,"vip才能选哦,是否购买vip");
                    alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void CancelOnClick() {
                            alertDialogUtil.dismissDialog();
                        }

                        @Override
                        public void ConfirmOnClick() {
                            alertDialogUtil.dismissDialog();
                            buyType = 1;
                            mPresenter.createOrder("b3b952d1-7f31-4014-8048-2e207bcfe53c");
                        }
                    });
                    alertDialogUtil.showDialog();
                }else {
                    if(!selectRole.contains(20)){
                        selectRole.add(20);
                        mRlXiaozhangRoot.setBackgroundResource(R.drawable.shape_main_background_white_y8);
                    }else {
                        selectRole.remove(Integer.valueOf(20));
                        mRlXiaozhangRoot.setBackground(null);
                    }
                }
                break;
            case R.id.rl_fuzi_root:
                if(hasFuzi){
                    if(!selectRole.contains(3)){
                        selectRole.add(3);
                        mRlFuziRoot.setBackgroundResource(R.drawable.shape_main_background_white_y8);
                    }else {
                        selectRole.remove(Integer.valueOf(3));
                        mRlFuziRoot.setBackground(null);
                    }

                }else {
                    final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                    alertDialogUtil.createNormalDialog(SanGuoActivity.this,"确认购买");
                    alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void CancelOnClick() {
                            alertDialogUtil.dismissDialog();
                        }

                        @Override
                        public void ConfirmOnClick() {
                            alertDialogUtil.dismissDialog();
                            buyType = 2;
                            mPresenter.createOrder("3fb9a790-4038-46e4-a719-2253ada9582d");
                        }
                    });
                    alertDialogUtil.showDialog();
                }
                break;
            case R.id.iv_duihuan:
                final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                alertDialogUtil.createNormalDialog(this,"确认兑换？");
                alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        alertDialogUtil.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        alertDialogUtil.dismissDialog();
                        mPresenter.useCiYuanBiGetFuHuo(PreferenceUtils.getUUid(),1);
                    }
                });
                alertDialogUtil.showDialog();
                break;
            case R.id.iv_start_game:
                if(selectRole.size() != 3){
                    showToast("必须选择3个角色");
                    return;
                }
//                try {
//                    Intent i = new Intent(this, MapGameActivity.class);
//                    JSONObject res = new JSONObject();
//                    res.put("userId",PreferenceUtils.getUUid());
//                    res.put("userName",PreferenceUtils.getAuthorInfo().getUserName());
//                    res.put("returnCoin",Integer.valueOf(mTvFuhuobi.getText().toString().replace("剩余复活币: ","")));
//                    JSONArray array = new JSONArray();
//                    for(Integer name : selectRole){
//                        array.put(name);
//                    }
//                    res.put("roles",array);
//                    if(hasGetVip && infoEntity != null){
//                        res.put("buyVIP",infoEntity.getBuyVIP());
//                    }
//                    if(hasGetFuzi && infoEntity != null){
//                        GamePriceInfoEntity.BuyRole role = getBuyRoleById(3);
//                        if(role != null){
//                            JSONArray roleArry = new JSONArray();
//                            JSONObject object = new JSONObject();
//                            object.put("id",3);
//                            object.put("price",role.getPrice());
//                            array.put(object);
//                            res.put("buyRoles",roleArry);
//                        }
//                    }
//                    JSONArray fuhuoArry = new JSONArray();
//                    fuhuoArry.put(getFuHuoNum);
//                    DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
//                    String p = decimalFormat.format((float)getFuHuoNum / 10);//format 返回的是字符串
//                    fuhuoArry.put(Float.valueOf(p));
//                    res.put("buyRevivalCoins",fuhuoArry);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("res",res.toString());
//                    i.putExtra("res",bundle);
//                    startActivity(i);
//                    finish();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
                break;
            default:
                    break;
        }
    }

    private GamePriceInfoEntity.BuyRole getBuyRoleById(int id){
        if(infoEntity.getBuyRoles() != null){
            for(GamePriceInfoEntity.BuyRole role : infoEntity.getBuyRoles()){
                if(id == role.getId()){
                    return role;
                }
            }
        }
        return null;
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
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
                    if(buyType == 1){
                        hasGetVip = true;
                    }else if(buyType == 2){
                        hasGetFuzi = true;
                    }
                }else {
                    showToast("支付失败");
                }
            }
        }
    }

    @Override
    public void onLoadTicketsNumSuccess(int num) {
        mTvCoin.setText("我的次元币: " + num);
    }

    @Override
    public void onLoadFuHuoNumSuccess(int num) {
        mTvFuhuobi.setText("剩余复活币: " + num);
    }

    @Override
    public void onUseCiYuanBiSuccess() {
        getFuHuoNum += 10;
        int fuhuo = Integer.valueOf(mTvFuhuobi.getText().toString().replace("剩余复活币: ","")) + 10;
        int coin = Integer.valueOf(mTvCoin.getText().toString().replace("我的次元币: ","")) - 1;
        mTvFuhuobi.setText("剩余复活币: " + fuhuo);
        mTvCoin.setText("我的次元币: " + coin);
    }

    @Override
    public void onHasRoleSuccess(boolean has) {
        hasFuzi = has;
        if(has){
            mFuziHas.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOrderSuccess(String id) {
        showMenu(id);
    }

    @Override
    public void onPayOrderSuccess(PayResEntity entity) {
        if(entity.isSuccess()){
            finalizeDialog();
            showToast("支付成功");
            finish();
        }else {
            if(entity.getCharge() != null){
                if("qpay".equals(entity.getCharge().get("channel"))){
                    Pingpp.createPayment(SanGuoActivity.this, entity.getCharge().toString(),"qwallet1104765197");
                }else {
                    Pingpp.createPayment(SanGuoActivity.this, entity.getCharge().toString());
                }
            }
        }
    }

    private GamePriceInfoEntity infoEntity;

    @Override
    public void getPriceInfoSuccess(GamePriceInfoEntity entity) {
        infoEntity = entity;
    }

    private void showMenu(final String id){
        BottomMenuFragment fragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(0, getString(R.string.label_alipay));
        items.add(item);
        item = new MenuItem(1,getString(R.string.label_wx));
        items.add(item);
        item = new MenuItem(2,getString(R.string.label_qpay));
        items.add(item);
        fragment.setShowTop(true);
        fragment.setTopContent("选择支付方式");
        fragment.setMenuItems(items);
        fragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        fragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
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
                PayReqEntity entity = new PayReqEntity("",
                        payType,
                        IpAdressUtils.getIpAdress(SanGuoActivity.this),
                        id,
                        "",
                        "",
                        "");
                mPresenter.payOrder(entity);
            }
        });
        fragment.show(getSupportFragmentManager(),"sanguo");
    }


}
