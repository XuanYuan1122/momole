package com.moemoe.lalala.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplicationLike;
import com.moemoe.lalala.di.components.DaggerDonationComponent;
import com.moemoe.lalala.di.modules.DonationModule;
import com.moemoe.lalala.model.entity.DonationInfoEntity;
import com.moemoe.lalala.presenter.DonationContract;
import com.moemoe.lalala.presenter.DonationPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.adapter.DonationAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2016/11/30.
 */

public class DonationActivity extends BaseAppCompatActivity implements DonationContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_my_donation)
    TextView mTvMyDonation;
    @BindView(R.id.tv_total_donation)
    TextView mTvTotalDonation;
    @BindView(R.id.iv_donation)
    ImageView mIvDonation;
    private int[] mTvNames = {R.id.tv_top_1_name,R.id.tv_top_2_name,R.id.tv_top_3_name};
    private int[] mTvNums = {R.id.tv_no1_coin,R.id.tv_no2_coin,R.id.tv_no3_coin};
    @BindView(R.id.tv_donation_coin)
    TextView mTvDonation;
    @BindView(R.id.pl_list)
    PullAndLoadView mRvList;
    private DonationAdapter mAdapter;
    @BindView(R.id.tv_book_my_donation)
    TextView mTvBookMyDonation;
    @BindView(R.id.tv_book_my_rank)
    TextView tvBookMyRank;
    @BindView(R.id.tv_exit)
    TextView mTvExit;
    @BindView(R.id.view_book)
    View mView;
    @BindView(R.id.fl_book)
    View mBook;
    @BindView(R.id.tv_guize)
    TextView mTvGuize;
    @Inject
    DonationPresenter mPresenter;
    private boolean mIsLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_donation;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerDonationComponent.builder()
                .donationModule(new DonationModule(this))
                .netComponent(MoeMoeApplicationLike.getInstance().getNetComponent())
                .build()
                .inject(this);
        mRvList.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new DonationAdapter(this,0);
        mRvList.getRecyclerView().setAdapter(mAdapter);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mRvList.isLoadMoreEnabled(false);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mRvList.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;
                mPresenter.requestDonationBookInfo(mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                mIsLoading = true;
                mPresenter.requestDonationBookInfo(0);
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        mPresenter.requestDonationInfo();
    }

    @OnClick({R.id.iv_donation,R.id.tv_donation_coin,R.id.tv_exit,R.id.iv_back,R.id.tv_guize})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_donation:
                showDonationDialog();
                break;
            case R.id.tv_donation_coin:
                mView.setVisibility(View.VISIBLE);
                mBook.setVisibility(View.VISIBLE);
                mTvExit.setVisibility(View.VISIBLE);
                mIvDonation.setEnabled(false);
                mTvDonation.setEnabled(false);
                mIvBack.setEnabled(false);
                mPresenter.requestDonationBookInfo(0);
                break;
            case R.id.tv_exit:
                mView.setVisibility(View.GONE);
                mBook.setVisibility(View.GONE);
                mTvExit.setVisibility(View.GONE);
                mIvDonation.setEnabled(true);
                mTvDonation.setEnabled(true);
                mIvBack.setEnabled(true);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_guize:
                String temp = "neta://com.moemoe.lalala/doc_1.0?00dc5fa0-ac7a-11e6-9224-525400761152";
                Uri uri = Uri.parse(temp);
                IntentUtils.toActivityFromUri(this, uri, v);
                break;
        }
    }

    @Override
    public void donationCoinSuccess() {
        finalizeDialog();
        mPresenter.requestDonationInfo();
    }

    @Override
    public void updateDonationView(DonationInfoEntity entity) {
        mTvMyDonation.setText(String.valueOf(entity.getMyCoin()));
        mTvTotalDonation.setText(String.valueOf(entity.getSumCoin()));
        if(entity.getRankList() != null){
            for(int i = 0; i < entity.getRankList().size();i++){
                DonationInfoEntity.RankBean bean = entity.getRankList().get(i);
                ((TextView)findViewById(mTvNames[i])).setText(bean.getNickName());
                ((TextView)findViewById(mTvNums[i])).setText(String.valueOf(bean.getCoin()));
            }
        }
    }

    private void showDonationDialog(){
        if(DialogUtils.checkLoginAndShowDlg(this)){
            final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
            dialogUtil.createEditDialog(this, PreferenceUtils.getAuthorInfo().getCoin(),0);
            dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                @Override
                public void CancelOnClick() {
                    dialogUtil.dismissDialog();
                }

                @Override
                public void ConfirmOnClick() {
                    String content = dialogUtil.getEditTextContent();
                    if(!TextUtils.isEmpty(content)){
                        if (Long.valueOf(content) < 1){
                            showToast("请输入正确的节操数");
                            return;
                        }
                        createDialog();
                        mPresenter.donationCoin(Long.valueOf(content));
                        dialogUtil.dismissDialog();
                    }else {
                        showToast(R.string.msg_can_not_empty);
                    }
                }
            });
            dialogUtil.showDialog();
        }
    }

    @Override
    public void updateDonationBook(DonationInfoEntity entity,boolean pull) {
        mIsLoading = false;
        mRvList.setComplete();
        mRvList.isLoadMoreEnabled(true);
        if (entity.getMyRank() == 0) {
            mTvBookMyDonation.setVisibility(View.GONE);
            tvBookMyRank.setVisibility(View.GONE);
        } else {
            mTvBookMyDonation.setVisibility(View.VISIBLE);
            tvBookMyRank.setVisibility(View.VISIBLE);
            mTvBookMyDonation.setText(getString(R.string.label_donation_book, entity.getMyCoin()));
            tvBookMyRank.setText(getString(R.string.label_donation_book_my_rank, entity.getMyRank()));
        }
        if (pull){
            mAdapter.setRankList(entity.getRankList());
        }else {
            mAdapter.addRankList(entity.getRankList());
        }
    }

    @Override
    public void onFailure(int code,String msg) {
        finalizeDialog();
        mIsLoading = false;
        mRvList.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(DonationActivity.this,code,msg);
    }
}
