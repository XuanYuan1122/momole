package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.moemoe.lalala.R;
import com.moemoe.lalala.view.fragment.PersonalChatFragment;
import com.moemoe.lalala.view.fragment.PersonalDocFragment;
import com.moemoe.lalala.view.fragment.PersonalFavoriteDocFragment;
import com.moemoe.lalala.view.fragment.PersonalFollowFragment;
import com.moemoe.lalala.view.fragment.PersonalMainFragment;
import com.moemoe.lalala.view.fragment.PersonalMsgFragment;

/**
 * Created by yi on 2016/12/15.
 */

public class PersonalPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private boolean mIsSelf;
    private boolean mIsShowFavorite;
    private boolean mIsShowFollow;
    private boolean mIsShowFans;
    private String mUserId;
    private PersonalMainFragment mainFragment;
    private PersonalDocFragment docFragment;
    private PersonalFavoriteDocFragment favoriteDocFragment;
   // private PersonalPropFragment propFragment;
    private PersonalChatFragment chatFragment;
    private PersonalFollowFragment fansFragment;
    private PersonalFollowFragment followFragment;
    private PersonalMsgFragment msgFragment;

    public PersonalPagerAdapter(FragmentManager fm, Context context,boolean isSelf,String id,boolean isShowFavorite,boolean isShowFollow,boolean isShowFans) {
        super(fm);
        this.context = context;
        mIsSelf = isSelf;
        mUserId = id;
        mIsShowFavorite = isShowFavorite;
        mIsShowFollow = isShowFollow;
        mIsShowFans = isShowFans;
        if(mIsSelf){
            mIsShowFavorite = true;
            mIsShowFollow = true;
            mIsShowFans = true;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return context.getResources().getString(R.string.label_home_page);
            case 1:
                return context.getResources().getString(R.string.label_doc);
            case 2:
                return context.getResources().getString(R.string.label_favorite);
            case 3:
                return context.getResources().getString(R.string.label_fans);
            case 4:
                return context.getResources().getString(R.string.label_follow);
            case 5:
                return context.getResources().getString(R.string.label_chat);
            case 6:
                return context.getResources().getString(R.string.label_msg);
            default:
                return "";
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(mainFragment == null)
                    mainFragment = PersonalMainFragment.newInstance(mUserId);
                return mainFragment;
            case 1:
                if(docFragment == null) docFragment = PersonalDocFragment.newInstance(mUserId);
                return docFragment;
            case 2:
                if(favoriteDocFragment == null) favoriteDocFragment = PersonalFavoriteDocFragment.newInstance(mUserId,mIsShowFavorite);
                return favoriteDocFragment;
            case 3:
                if(fansFragment == null) fansFragment = PersonalFollowFragment.newInstance(mUserId,mIsShowFans,"fans");
                return fansFragment;
            case 4:
                if(followFragment == null) followFragment = PersonalFollowFragment.newInstance(mUserId,mIsShowFollow,"follow");
                return followFragment;
            case 5:
//                if(propFragment == null) propFragment = PersonalPropFragment.newInstance(mUserId);
//                return propFragment;
                if(chatFragment == null) chatFragment = PersonalChatFragment.newInstance(mUserId);
                return chatFragment;
            case 6:
                if(msgFragment == null) msgFragment = PersonalMsgFragment.newInstance(mUserId);
                return msgFragment;
            default:
                return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return mIsSelf ? 7 : 5;
    }

    public void release() {
        if(mainFragment != null) mainFragment.release();
        if(docFragment != null) docFragment.release();
        if(favoriteDocFragment != null) favoriteDocFragment.release();
        if(fansFragment != null) fansFragment.release();
        if(followFragment != null) followFragment.release();
        if(msgFragment != null) msgFragment.release();
    }
}
