package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.MusicLoader;
import com.moemoe.lalala.utils.ViewUtils;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by yi on 2016/11/30.
 */

public class SelectMusicActivity extends BaseAppCompatActivity {

    public static final String EXTRA_SELECT_MUSIC = "select_music";

    @BindView(R.id.list)
    ListView mList;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;

    private ArrayList<MusicLoader.MusicInfo> mMusicList;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_one_list_samller;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        ImmersionBar.with(this)
//                .statusBarView(R.id.top_view)
//                .statusBarDarkFont(true,0.2f)
//                .transparentNavigationBar()
//                .init();
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        mMusicList = new ArrayList<>();
        mTitle.setText(R.string.label_select_music);
        MusicLoader musicLoader = MusicLoader.instance(getContentResolver());
        mMusicList.addAll(musicLoader.getMusicList());
        mList.setAdapter(new MusicListAdapter());
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.putExtra(EXTRA_SELECT_MUSIC,mMusicList.get(position));
                setResult(RESULT_OK,i);
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    private class MusicListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMusicList.size();
        }

        @Override
        public MusicLoader.MusicInfo getItem(int position) {
            return mMusicList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = LayoutInflater.from(SelectMusicActivity.this).inflate(R.layout.item_select_music,null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_select_music);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_select_music);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            MusicLoader.MusicInfo musicInfo = getItem(position);
            viewHolder.textView.setText(musicInfo.getTitle());
            return convertView;
        }


        class ViewHolder{
            ImageView imageView;
            TextView textView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
