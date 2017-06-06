package com.moemoe.lalala.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.MusicLoader;
import com.moemoe.lalala.utils.NoDoubleClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yi on 2017/5/16.
 */

public class AddMusicActivity extends BaseAppCompatActivity {

    private final int REQ_GET_FROM_GALLERY = 1002;
    private final int REQ_GET_FROM_SELECT_MUSIC = 1003;

    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvMenuRight;
    @BindView(R.id.iv_select_music)
    ImageView mIvMusic;
    @BindView(R.id.tv_select_music)
    TextView mTvMusic;
    @BindView(R.id.iv_select_img)
    ImageView mIvImg;
    @BindView(R.id.tv_select_img)
    TextView mTvImg;
    private ArrayList<String> mIconPaths;
    private MusicLoader.MusicInfo mMusicInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_add_music;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent i = getIntent();
        if(i == null){
            finish();
            return;
        }
        mMusicInfo = i.getParcelableExtra("music_info");
        String path = i.getStringExtra("music_cover");
        mIconPaths = new ArrayList<>();
        if(!TextUtils.isEmpty(path)) mIconPaths.add(path);
        mTvMenuLeft.setVisibility(View.VISIBLE);
        mTvMenuLeft.setText(getString(R.string.label_cancel));
        mTvMenuLeft.setTextColor(ContextCompat.getColor(this,R.color.black_1e1e1e));
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(getString(R.string.label_hide_area));
        mTvMenuRight.setVisibility(View.VISIBLE);
        mTvMenuRight.setText(getString(R.string.label_done));
        mTvMenuRight.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mTvMenuLeft.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        if(mMusicInfo != null){
            mTvMusic.setText(mMusicInfo.getTitle());
            mIvMusic.setBackgroundResource(R.drawable.btn_select_music_finish);
        }
        if(mIconPaths.size() == 1){
            mTvImg.setText(R.string.label_select_img_finish);
            Glide.with(this)
                    .load( "file://" + mIconPaths.get(0))
                    .override(DensityUtil.dip2px(this,115), DensityUtil.dip2px(this,115))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .centerCrop()
                    .into(mIvImg);
        }
    }

    @OnClick({R.id.ll_select_music,R.id.ll_select_img,R.id.tv_menu})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ll_select_music:
                Intent i = new Intent(AddMusicActivity.this, SelectMusicActivity.class);
                startActivityForResult(i,REQ_GET_FROM_SELECT_MUSIC);
                break;
            case R.id.ll_select_img:
                Intent intent = new Intent(AddMusicActivity.this, MultiImageChooseActivity.class);
                intent.putExtra(MultiImageChooseActivity.EXTRA_KEY_MAX_PHOTO, 1);
                startActivityForResult(intent, REQ_GET_FROM_GALLERY);
                break;
            case R.id.tv_menu:
                done();
                break;
        }
    }

    private void done(){
        if(mMusicInfo != null && mIconPaths.size() == 1){
            Intent i = new Intent();
            i.putExtra("music_info",mMusicInfo);
            i.putExtra("music_cover",mIconPaths.get(0));
            setResult(RESULT_OK,i);
            finish();
        }else {
            showToast(R.string.msg_need_one_music);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            // file
            if (data != null) {
                ArrayList<String> paths = data
                        .getStringArrayListExtra(MultiImageChooseActivity.EXTRA_KEY_SELETED_PHOTOS);
                if (paths != null && paths.size() == 1) {
                    mIconPaths = paths;
                    mTvImg.setText(R.string.label_select_img_finish);
                    Glide.with(this)
                            .load( "file://" + mIconPaths.get(0))
                            .override(DensityUtil.dip2px(this,115), DensityUtil.dip2px(this,115))
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .centerCrop()
                            .into(mIvImg);
                }
            }
        } else if (requestCode == REQ_GET_FROM_SELECT_MUSIC && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mMusicInfo = data.getParcelableExtra(SelectMusicActivity.EXTRA_SELECT_MUSIC);
                mTvMusic.setText(mMusicInfo.getTitle());
                mIvMusic.setBackgroundResource(R.drawable.btn_select_music_finish);
            }
        }
    }
}
