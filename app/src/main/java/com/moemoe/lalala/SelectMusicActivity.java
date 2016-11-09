package com.moemoe.lalala;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.utils.MusicLoader;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/7/12 0012.
 */
@ContentView(R.layout.ac_one_list_samller)
public class SelectMusicActivity extends BaseActivity{

    public static final String EXTRA_SELECT_MUSIC = "select_music";
    public static final int RES_OK = 2333;
    @FindView(R.id.list)
    private ListView mList;
    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_toolbar_title)
    private TextView mTitle;

    private ArrayList<MusicLoader.MusicInfo> mMusicList;

    @Override
    protected void initView() {
        mMusicList = new ArrayList<>();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText(R.string.label_select_music);
        MusicLoader musicLoader = MusicLoader.instance(getContentResolver());
        mMusicList.addAll(musicLoader.getMusicList());
        mList.setAdapter(new MusicListAdapter());
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.putExtra(EXTRA_SELECT_MUSIC,mMusicList.get(position));
                setResult(RES_OK,i);
                finish();
            }
        });
    }

    private class MusicListAdapter extends BaseAdapter{

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
            ViewHolder viewHolder = null;
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
}
