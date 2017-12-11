package com.moemoe.lalala.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerGameComponent;
import com.moemoe.lalala.di.modules.GameModule;
import com.moemoe.lalala.kira.game.MapGameActivity;
import com.moemoe.lalala.presenter.GameContract;
import com.moemoe.lalala.presenter.GamePresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *
 * Created by yi on 2017/12/8.
 */

public class SanGuoActivity extends BaseAppCompatActivity implements GameContract.View {

    @BindView(R.id.rl_root)
    View mRlRoot;
    @BindView(R.id.iv_title)
    View mIvTitle;
    @BindView(R.id.iv_continue)
    View mIvContinue;
    @BindView(R.id.ll_role_root)
    View mLlRoleRoot;
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
    @BindView(R.id.iv_len_close)
    View mIvLenClose;
    @BindView(R.id.iv_sari_close)
    View mIvSariClose;
    @BindView(R.id.iv_mei_close)
    View mIvMeiClose;
    @BindView(R.id.iv_xiaozhang_close)
    View mIvXiaozhangClose;
    @BindView(R.id.rl_len_root)
    View mRlLenRoot;
    @BindView(R.id.rl_sari_root)
    View mRlSariRoot;
    @BindView(R.id.rl_mei_root)
    View mRlMeiRoot;
    @BindView(R.id.rl_xiaozhang_root)
    View mRlXiaozhangRoot;

    @Inject
    GamePresenter mPresenter;

    ObjectAnimator alphaContinue;
    private ArrayList<String> selectRole;

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
        mPresenter.loadTicketsNum();
        mPresenter.loadFuHuoNum(PreferenceUtils.getUUid());
        selectRole.add("len");
        selectRole.add("sari");
        selectRole.add("mei");

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

    @OnClick({R.id.rl_root,R.id.rl_len_root,R.id.iv_len_close,R.id.rl_sari_root,R.id.iv_sari_close,R.id.rl_mei_root,R.id.iv_mei_close,R.id.rl_xiaozhang_root,R.id.iv_xiaozhang_close,R.id.iv_duihuan,R.id.iv_start_game})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.rl_root:
                if(mIvTitle.getVisibility() == View.VISIBLE){
                    if(alphaContinue != null && alphaContinue.isRunning()){
                        alphaContinue.cancel();
                        mRlRoot.setBackgroundResource(R.drawable.bg_sanguo_start);
                        mIvTitle.setVisibility(View.GONE);
                        mIvContinue.setVisibility(View.GONE);
                        mLlRoleRoot.setVisibility(View.VISIBLE);
                        mTvFuhuobi.setVisibility(View.VISIBLE);
                        mTvIntroduce.setVisibility(View.VISIBLE);
                        mIvDuihuan.setVisibility(View.VISIBLE);
                        mTvCoin.setVisibility(View.VISIBLE);
                        mIvStart.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.rl_len_root:
                if(!selectRole.contains("len")){
                    selectRole.add("len");
                }
                mIvLenClose.setVisibility(View.VISIBLE);
                mRlLenRoot.setBackgroundResource(R.drawable.shape_rect_border_main_background_white_y8);
                break;
            case R.id.iv_len_close:
                if(selectRole.contains("len")){
                    selectRole.remove("len");
                }
                mIvLenClose.setVisibility(View.INVISIBLE);
                mRlLenRoot.setBackground(null);
                break;
            case R.id.rl_sari_root:
                if(!selectRole.contains("sari")){
                    selectRole.add("sari");
                }
                mIvSariClose.setVisibility(View.VISIBLE);
                mRlSariRoot.setBackgroundResource(R.drawable.shape_rect_border_main_background_white_y8);
                break;
            case R.id.iv_sari_close:
                if(selectRole.contains("sari")){
                    selectRole.remove("sari");
                }
                mIvSariClose.setVisibility(View.INVISIBLE);
                mRlSariRoot.setBackground(null);
                break;
            case R.id.rl_mei_root:
                if(!selectRole.contains("mei")){
                    selectRole.add("mei");
                }
                mIvMeiClose.setVisibility(View.VISIBLE);
                mRlMeiRoot.setBackgroundResource(R.drawable.shape_rect_border_main_background_white_y8);
                break;
            case R.id.iv_mei_close:
                if(selectRole.contains("mei")){
                    selectRole.remove("mei");
                }
                mIvMeiClose.setVisibility(View.INVISIBLE);
                mRlMeiRoot.setBackground(null);
                break;
            case R.id.rl_xiaozhang_root:
                if(TextUtils.isEmpty(PreferenceUtils.getAuthorInfo().getVipTime())){
                    return;
                }
                if(!selectRole.contains("xiaozhang")){
                    selectRole.add("xiaozhang");
                }
                mIvXiaozhangClose.setVisibility(View.VISIBLE);
                mRlXiaozhangRoot.setBackgroundResource(R.drawable.shape_rect_border_main_background_white_y8);
                break;
            case R.id.iv_xiaozhang_close:
                if(selectRole.contains("xiaozhang")){
                    selectRole.remove("xiaozhang");
                }
                mIvXiaozhangClose.setVisibility(View.INVISIBLE);
                mRlXiaozhangRoot.setBackground(null);
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
                try {
                    Intent i = new Intent(this, MapGameActivity.class);
                    JSONObject res = new JSONObject();
                    res.put("userId",PreferenceUtils.getUUid());
                    res.put("userName",PreferenceUtils.getAuthorInfo().getUserName());
                    res.put("returnCoin",Integer.valueOf(mTvFuhuobi.getText().toString().replace("剩余复活币: ","")));
                    JSONArray array = new JSONArray();
                    for(String name : selectRole){
                        array.put(name);
                    }
                    res.put("roles",array);
                    Bundle bundle = new Bundle();
                    bundle.putString("res",res.toString());
                    i.putExtra("res",bundle);
                    startActivity(i);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                    break;
        }
    }

    @Override
    public void onFailure(int code, String msg) {

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
        int fuhuo = Integer.valueOf(mTvFuhuobi.getText().toString().replace("剩余复活币: ","")) + 10;
        int coin = Integer.valueOf(mTvCoin.getText().toString().replace("我的次元币: ","")) - 1;
        mTvFuhuobi.setText("剩余复活币: " + fuhuo);
        mTvCoin.setText("我的次元币: " + coin);
    }
}
