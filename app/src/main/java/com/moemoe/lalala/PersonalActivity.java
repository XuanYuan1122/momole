package com.moemoe.lalala;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.adapter.PassportViewHolder;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.data.PersonBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.view.NoDoubleClickListener;

/**
 * Created by Haru on 2016/7/21 0021.
 */
@ContentView(R.layout.frag_my_person_data)
public class PersonalActivity extends BaseActivity implements View.OnClickListener {

    @FindView(R.id.rl_passport_content)
    private View mPassportPanel;
    private PassportViewHolder mPassportViewHolder;
    @FindView(R.id.rl_unlogin_pack)
    private View mRlUnloginPack;
    /**
     * 是否有抽奖机会
     */
    @FindView(R.id.tv_login)
    private View mTvLogin;
    @FindView(R.id.iv_back)
    private ImageView mIvBack;
    @FindView(R.id.list_info)
    private ListView mList;
    private InfoAdapter mAdapter;

    //------------------- fields ---------------------------
    /**
     * 是否登录完成
     */
    private boolean mIsLogin;

    private PersonBean mMyself;
    private AuthorInfo mAuthorInfo;

    @Override
    protected void initView() {
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mPassportViewHolder = new PassportViewHolder(this, mPassportPanel, PassportViewHolder.TYPE_SELF);
        mAdapter = new InfoAdapter();
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent i = new Intent(PersonalActivity.this,MyPostActivity.class);
                        i.putExtra(MyPostActivity.EXTRA_USER_ID,mPreferMng.getUUid());
                        startActivity(i);
                        break;
                    case 1:
                        Intent i1 = new Intent(PersonalActivity.this,MyFavoriteActivity.class);
                        i1.putExtra(EXTRA_KEY_UUID,mPreferMng.getUUid());
                        startActivity(i1);
                        break;
                    case 2:
                        Intent i2 = new Intent(PersonalActivity.this,SettingActivity.class);
                        startActivityForResult(i2, SettingActivity.REQUEST_SETTING_LOGOUT);
                        break;
                    default:
                        break;
                }
            }
        });
        mRlUnloginPack.setOnClickListener(this);
        mTvLogin.setOnClickListener(this);
        loadData();
    }

    /**
     * 请求用户数据
     */
    private void requestSelfData() {
        Otaku.getAccountV2().requestSelfData(mPreferMng.getToken()).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                mMyself = new PersonBean();
                mMyself.readFromJsonContent(PersonalActivity.this,s);
                mAuthorInfo.setmUUid(mMyself.uuid);
                mAuthorInfo.setmUserName(mMyself.name);
                mAuthorInfo.setmGender(mMyself.sex_str);
                mAuthorInfo.setNice_num(mMyself.nice_num);
                mAuthorInfo.setSlogan(mMyself.slogan);
                mAuthorInfo.setmHeadPath(mMyself.icon.path);
                mAuthorInfo.setRegister_time(mMyself.register_time);
                mAuthorInfo.setBirthday(mMyself.birthday);
                mAuthorInfo.setLevel_name(mMyself.level_name);
                mAuthorInfo.setScore(mMyself.score);
                mAuthorInfo.setLevel(mMyself.level);
                mAuthorInfo.setLevel_score_end(mMyself.level_score_end);
                mAuthorInfo.setLevel_score_start(mMyself.level_score_start);
                mAuthorInfo.setLevel_color(mMyself.level_color);
                mAuthorInfo.setmCoin(mMyself.coin);
                mPreferMng.saveThirdPartyLoginMsg(mAuthorInfo);
                updateView();
            }

            @Override
            public void failure(String e) {

            }
        }));
    }

    private void loadData() {
        mIsLogin = mPreferMng.isLogin(this);
        if (mIsLogin) {
            mAuthorInfo = mPreferMng.getThirdPartyLoginMsg();
            requestSelfData();
        } else {
            mMyself = null;
            updateView();
        }
    }

    private void updateView() {
        if (mIsLogin) {
            if (mMyself != null) {
                mPassportViewHolder.setPersonBean(mMyself);
                mRlUnloginPack.setVisibility(View.GONE);
            } else {
                requestSelfData();
            }
        } else {
            goLogin();
        }
    }

    private void goLogin(){
        Intent i = new Intent(PersonalActivity.this, LoginActivity.class);
        startActivityForResult(i, LoginActivity.RESPONSE_LOGIN_SUCCESS);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_login) {
            // 前往登录界面
            goLogin();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == LoginActivity.RESPONSE_LOGIN_SUCCESS){
            loadData();
        }else if(resultCode == SettingActivity.REQUEST_SETTING_LOGOUT){
            //loadData();
            finish();
        }
    }

    private class InfoAdapter extends BaseAdapter{

        private String[] titles = {getString(R.string.label_my_post),getString(R.string.label_my_favorite),getString(R.string.settings)};
        private int[] images = {R.drawable.icon_person_mydoc,R.drawable.icon_person_favour,R.drawable.icon_person_set};

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = View.inflate(PersonalActivity.this,R.layout.item_person_info,null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.iv_image);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.image.setImageResource(images[position]);
            holder.textView.setText(titles[position]);
            return convertView;
        }

        class ViewHolder{
            ImageView image;
            TextView textView;
        }
    }

}
