package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.DonationInfoEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface DonationContract {
    interface Presenter{
        void requestDonationInfo();
        void donationCoin(long num);
        void requestDonationBookInfo(int index);
    }

    interface View extends BaseView{
        void donationCoinSuccess();
        void updateDonationView(DonationInfoEntity entity);
        void updateDonationBook(DonationInfoEntity entity,boolean pull);
    }
}
