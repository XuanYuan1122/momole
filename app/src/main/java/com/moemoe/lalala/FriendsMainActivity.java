package com.moemoe.lalala;

import android.content.Intent;
import android.text.TextUtils;
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
import com.moemoe.lalala.data.PersonBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.view.NoDoubleClickListener;

/**
 * Created by Haru on 2016/5/2 0002.
 */
@ContentView(R.layout.frag_my_person_data)
public class FriendsMainActivity extends BaseActivity {
    private BaseActivity mActivity;
    @FindView(R.id.iv_back)
    private ImageView mIvback;
    @FindView(R.id.tv_title)
    private TextView mTvTitle;

    @FindView(R.id.rl_passport_content)
    private View mPassportPanel;
    private PassportViewHolder mPassportViewHolder;
    @FindView(R.id.list_info)
    private ListView mList;
    private InfoAdapter mAdapter;

    private String mFriendUuid;
    private PersonBean mMyself;

    @Override
    protected void initView() {
        mActivity = this;
        mIvback.setVisibility(View.VISIBLE);
        mIvback.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mFriendUuid = mIntent.getStringExtra(EXTRA_KEY_UUID);
        if (TextUtils.isEmpty(mFriendUuid)) {
            finish();
        }
        //mMyself =  从数据库读取
        if(mMyself != null){
            mTvTitle.setText(mMyself.name);
        }
        requestSelfData();
        mPassportViewHolder = new PassportViewHolder(this, mPassportPanel,PassportViewHolder.TYPE_FRIEND);
        mAdapter = new InfoAdapter();
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent i = new Intent(FriendsMainActivity.this,MyPostActivity.class);
                        i.putExtra(MyPostActivity.EXTRA_USER_ID,mFriendUuid);
                        startActivity(i);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void loadViewData(){
        mPassportViewHolder.setPersonBean(mMyself);
        mTvTitle.setText(mMyself.name);
    }

    private void requestSelfData(){
        if(!NetworkUtils.checkNetworkAndShowError(this)){
            return;
        }
        Otaku.getCommonV2().requestPerson(mPreferMng.getToken(), mFriendUuid).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                mMyself = new PersonBean();
                mMyself.readFromJsonContent(FriendsMainActivity.this, s);
                loadViewData();
            }

            @Override
            public void failure(String e) {

            }
        }));
    }

    private class InfoAdapter extends BaseAdapter {

        private String[] titles = {getString(R.string.label_friend_post)};
        private int[] images = {R.drawable.icon_person_mydoc};

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
                convertView = View.inflate(FriendsMainActivity.this,R.layout.item_person_info,null);
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
