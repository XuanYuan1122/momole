package com.moemoe.lalala;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.data.DonationInfoBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NetworkUtils;

/**
 * Created by yi on 2016/11/7.
 */
@ContentView(R.layout.ac_donation)
public class DonationActivity extends BaseActivity implements View.OnClickListener{

    private String token = "VdoLMxy-03ajJv6LCbkfQ676C3_90LMOooTppdBKNpn9q0TBwXO4fuYZnxVn0UqVmH28l_UrQUWUr66DydEjMsk4658ik_HchEP4WfaDC2jp6cTKtmsl9OvLw5sYUejb32s89HCv7UTMFF-TSQEB5XbeNx6XJPRR1NAtfS6GS5ydY3m3TcuBKCQdCWqwX7Otpyg9PUyqAFIRpJmzjkD1Z8QYOtCZVNgEhKndi51olexyeElwb9HCE1tlksQ0J36VhUVJ0-W0y7582TZBrnEtLH6ccMf5MzGgJn_SWIC6ioN28MF0PGkztAy_QxEU-pt6vfdmxAvOcbRSF3BFg3YDlw==";

    @FindView(R.id.iv_back)
    private ImageView mIvBack;
    @FindView(R.id.tv_my_donation)
    private TextView mTvMyDonation;
    @FindView(R.id.tv_total_donation)
    private TextView mTvTotalDonation;
    @FindView(R.id.iv_donation)
    private ImageView mIvDonation;
    private int[] mTvNames = {R.id.tv_top_1_name,R.id.tv_top_2_name,R.id.tv_top_3_name};
    private int[] mTvNums = {R.id.tv_no1_coin,R.id.tv_no2_coin,R.id.tv_no3_coin};
    @FindView(R.id.tv_donation_coin)
    private TextView mTvDonation;
    private int[] mTvBookNames = {R.id.tv_book_top_1_name,R.id.tv_book_top_2_name,R.id.tv_book_top_3_name,R.id.tv_book_top_4_name
                         ,R.id.tv_book_top_5_name,R.id.tv_book_top_6_name,R.id.tv_book_top_7_name,R.id.tv_book_top_8_name,R.id.tv_book_top_9_name,R.id.tv_book_top_10_name};
    private int[] mTvBookCoins = {R.id.tv_book_no1_coin,R.id.tv_book_no2_coin,R.id.tv_book_no3_coin,R.id.tv_book_no4_coin,R.id.tv_book_no5_coin,R.id.tv_book_no6_coin
                                    ,R.id.tv_book_no7_coin,R.id.tv_book_no8_coin,R.id.tv_book_no9_coin,R.id.tv_book_no10_coin};
    @FindView(R.id.tv_book_my_donation)
    private TextView mTvBookMyDonation;
    @FindView(R.id.tv_exit)
    private TextView mTvExit;
    @FindView(R.id.view_book)
    private View mView;
    @FindView(R.id.fl_book)
    private View mBook;

    @Override
    protected void initView() {
        mIvDonation.setOnClickListener(this);
        mTvDonation.setOnClickListener(this);
        mTvExit.setOnClickListener(this);
        getDonationInfo();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.iv_donation){
            showDonationDialog();
        }else if(id == R.id.tv_donation_coin){
            mView.setVisibility(View.VISIBLE);
            mBook.setVisibility(View.VISIBLE);
            mTvExit.setVisibility(View.VISIBLE);
            mIvDonation.setEnabled(false);
            mTvDonation.setEnabled(false);
            mIvBack.setEnabled(false);
            getDonationBookInfo();
        }else if(id == R.id.tv_exit){
            mView.setVisibility(View.GONE);
            mBook.setVisibility(View.GONE);
            mTvExit.setVisibility(View.GONE);
            mIvDonation.setEnabled(true);
            mTvDonation.setEnabled(true);
            mIvBack.setEnabled(true);
        }else if(id == R.id.iv_back){
            finish();
        }
    }

    private void updateDonationView(DonationInfoBean donationInfoBean){
        mTvMyDonation.setText("" + donationInfoBean.getMyCoin());
        mTvTotalDonation.setText("" + donationInfoBean.getSumCoin());
        if(donationInfoBean.getRankList() != null){
            for(int i = 0; i < donationInfoBean.getRankList().size();i++){
                DonationInfoBean.RankBean bean = donationInfoBean.getRankList().get(i);
                ((TextView)findViewById(mTvNames[i])).setText(bean.getNickName());
                ((TextView)findViewById(mTvNums[i])).setText("" + bean.getCoin());
            }
        }
    }

    private void updateBookView(DonationInfoBean donationInfoBean){
        if(donationInfoBean.getMyRank() == 0){
            mTvBookMyDonation.setVisibility(View.GONE);
        }else {
            mTvBookMyDonation.setVisibility(View.VISIBLE);
            mTvBookMyDonation.setText(getString(R.string.label_donation_book,donationInfoBean.getMyCoin(),donationInfoBean.getMyRank()));
        }
        if(donationInfoBean.getRankList() != null){
            for(int i = 0;i < donationInfoBean.getRankList().size();i++){
                DonationInfoBean.RankBean bean = donationInfoBean.getRankList().get(i);
                ((TextView)findViewById(mTvBookNames[i])).setText(bean.getNickName());
                ((TextView)findViewById(mTvBookCoins[i])).setText("" + bean.getCoin());
            }
        }
    }

    private void showDonationDialog(){
        final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
        dialogUtil.createEditDialog(this,mPreferMng.getThirdPartyLoginMsg().getmCoin(),0);
        dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
            @Override
            public void CancelOnClick() {
                dialogUtil.dismissDialog();
            }

            @Override
            public void ConfirmOnClick() {
                donationCoin(dialogUtil.getEditTextContent());
                dialogUtil.dismissDialog();
            }
        });
        dialogUtil.showDialog();
    }

    private void donationCoin(int num){
        if(NetworkUtils.checkNetworkAndShowError(this)){
            Otaku.getCommonV2().donationCoin(token,num).enqueue(CallbackFactory.getInstance().callback1(new OnNetWorkCallback<String,DonationInfoBean>() {

                @Override
                public void success(String token, DonationInfoBean donationInfoBean) {
                    mTvMyDonation.setText((Integer.valueOf(mTvMyDonation.getText().toString()) + 1 )+ "");
                    mTvTotalDonation.setText((Integer.valueOf(mTvTotalDonation.getText().toString()) + 1 )+ "");
                }

                @Override
                public void failure(int code,String e) {
                    ErrorCodeUtils.showErrorMsgByCode(DonationActivity.this,code);
                }
            }));
        }
    }

    private void getDonationInfo(){
        if(NetworkUtils.checkNetworkAndShowError(this)){
            Otaku.getCommonV2().getDonationInfo(token).enqueue(CallbackFactory.getInstance().callback1(new OnNetWorkCallback<String,DonationInfoBean>() {

                @Override
                public void success(String token, DonationInfoBean donationInfoBean) {
                    updateDonationView(donationInfoBean);
                }

                @Override
                public void failure(int code,String e) {

                }
            }));
        }
    }

    private void getDonationBookInfo(){
        if(NetworkUtils.checkNetworkAndShowError(this)){
            Otaku.getCommonV2().getBookDonationInfo(token).enqueue(CallbackFactory.getInstance().callback1(new OnNetWorkCallback<String,DonationInfoBean>() {

                @Override
                public void success(String token, DonationInfoBean donationInfoBean) {
                    updateBookView(donationInfoBean);
                }

                @Override
                public void failure(int code,String e) {

                }
            }));
        }
    }
}
