package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFileComponent;
import com.moemoe.lalala.di.modules.FileModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.FileEntity;
import com.moemoe.lalala.model.entity.MoveFileEntity;
import com.moemoe.lalala.presenter.FilesContract;
import com.moemoe.lalala.presenter.FilesPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/1/20.
 */

public class FilesSelectActivity extends BaseAppCompatActivity implements FilesContract.View{

    private static final int REQ_SELECT_FOLDER = 5001;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_menu)
    TextView mTvSave;
    @BindView(R.id.gv_select_photos)
    GridView mGridImages;
    @BindView(R.id.fl_delete_root)
    FrameLayout mFlDeleteRoot;
    @BindView(R.id.fl_move_root)
    FrameLayout mFlMoveRoot;

    @Inject
    FilesPresenter mPresenter;

    private String folderId;
    private ArrayList<FileEntity> mList;
    private HashMap<Integer,FileEntity> mSelectMap;
    private SelectorAdapter mSelectorAdapter;
    private boolean change;
    private int changeNum = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bag_select;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFileComponent.builder()
                .fileModule(new FileModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        folderId = getIntent().getStringExtra("folderId");
        mList = getIntent().getParcelableArrayListExtra("list");
        if(mList == null){
            finish();
            return;
        }
        change = false;
        mTvSave.setVisibility(View.VISIBLE);
        mTvSave.setText("全选");
        mSelectMap = new HashMap<>();
        mSelectorAdapter = new SelectorAdapter();
        mGridImages.setAdapter(mSelectorAdapter);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initListeners() {
        mTvSave.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(mSelectMap.size() != mList.size()){
                    mSelectMap.clear();
                    for (int i = 0;i < mList.size();i++){
                        mSelectMap.put(i,mList.get(i));
                    }
                    mSelectorAdapter.notifyDataSetChanged();
                    mTvSave.setText("反选");
                }else {
                    mSelectMap.clear();
                    mSelectorAdapter.notifyDataSetChanged();
                    mTvSave.setText("全选");
                }
            }
        });
        mFlDeleteRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ArrayList<String> ids = new ArrayList<>();
                for (FileEntity entity : mSelectMap.values()){
                    ids.add(entity.getFileId());
                }
                mPresenter.deleteFiles(folderId,ids);
            }
        });
        mFlMoveRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent i = new Intent(FilesSelectActivity.this,FolderSelectActivity.class);
                i.putExtra("folderId",folderId);
                startActivityForResult(i,REQ_SELECT_FOLDER);
            }
        });
        mGridImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mSelectMap.containsKey(position)){
                    mSelectMap.remove(position);
                }else {
                    mSelectMap.put(position,mList.get(position));
                }
                ((SelectorAdapter.ImageViewHolder) view.getTag()).setChecked(mSelectMap.containsKey(position));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_SELECT_FOLDER && resultCode == RESULT_OK){
            ArrayList<String> ids = new ArrayList<>();
            for (FileEntity entity : mSelectMap.values()){
                ids.add(entity.getFileId());
            }
            MoveFileEntity entity = new MoveFileEntity(ids,folderId);
            mPresenter.moveFiles(data.getStringExtra("folderId"),entity);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("change",change);
        i.putExtra("number",changeNum);
        setResult(RESULT_OK,i);
        finish();
    }

    private boolean isPictureSelected(FileEntity path) {
        return mSelectMap.containsValue(path);
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void deleteFilesSuccess() {
        change = true;
        changeNum += mSelectMap.size();
        for (FileEntity entity : mSelectMap.values()){
            mList.remove(entity);
        }
        mSelectMap.clear();
        mSelectorAdapter.notifyDataSetChanged();
        showToast("删除文件成功");
    }

    @Override
    public void moveFilesSuccess() {
        change = true;
        changeNum += mSelectMap.size();
        for (FileEntity entity : mSelectMap.values()){
            mList.remove(entity);
        }
        mSelectMap.clear();
        mSelectorAdapter.notifyDataSetChanged();
        showToast("移动文件成功");
    }

    @Override
    public void modifyFileSuccess(String name) {

    }

    @Override
    public void copyFileSuccess() {

    }

    public class SelectorAdapter extends BaseAdapter {
        /**
         * 每一张小图的宽度
         */
        private int mItemWidth;

        SelectorAdapter() {
            mItemWidth = DensityUtil.getScreenWidth(FilesSelectActivity.this) / 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(FilesSelectActivity.this)
                        .inflate(R.layout.item_folder_item, parent, false);
                convertView.setLayoutParams(new GridView.LayoutParams(mItemWidth, mItemWidth));
                holder = new ImageViewHolder(convertView);
            } else {
                holder = (ImageViewHolder) convertView.getTag();
            }
            FileEntity entity = mList.get(position);

            holder.loadData(entity, isPictureSelected(entity), mItemWidth);
            return convertView;
        }

        class ImageViewHolder {

            ImageView ivImg,ivMusic;
            TextView tvMusicName,tvMusicTime;
            View mMusicRoot;
            CheckBox cbIndicatorImage;

            ImageViewHolder(View convertView) {
                cbIndicatorImage = (CheckBox) convertView.findViewById(R.id.iv_multi_toggle);
                ivImg = (ImageView) convertView.findViewById(R.id.iv_img);
                ivMusic = (ImageView) convertView.findViewById(R.id.iv_music);
                tvMusicName = (TextView) convertView.findViewById(R.id.tv_music_name);
                tvMusicTime = (TextView) convertView.findViewById(R.id.tv_music_time);
                mMusicRoot = convertView.findViewById(R.id.ll_music_root);
                cbIndicatorImage.setVisibility(View.VISIBLE);
                convertView.setTag(this);
            }

            void loadData(FileEntity entity, final boolean selected, int size) {
                setChecked(selected);
                if(entity.getType().equals("image")){
                    mMusicRoot.setVisibility(View.GONE);
                    ivMusic.setVisibility(View.GONE);
                    Glide.with(FilesSelectActivity.this)
                            .load(StringUtils.getUrl(FilesSelectActivity.this, ApiService.URL_QINIU +  entity.getPath(), size,size, false, true))
                            .override(size,size)
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .centerCrop()
                            .into(ivImg);
                }else if(entity.getType().equals("music")){
                    mMusicRoot.setVisibility(View.VISIBLE);
                    ivMusic.setVisibility(View.VISIBLE);
                    Glide.with(FilesSelectActivity.this)
                            .load(R.drawable.bg_green)
                            .override(size,size)
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .centerCrop()
                            .into(ivImg);
                    tvMusicName.setText(entity.getFileName());
                    tvMusicTime.setText(getMinute(entity.getAttr().get("timestamp").getAsInt()));

                }
            }

            void setChecked(boolean isChecked) {
                cbIndicatorImage.setChecked(isChecked);
            }
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
    private String getMinute(int time) {
        int h = time / (1000 * 60 * 60);
        String minute;
        int sec = (time % (1000 * 60)) / 1000;
        int min = time % (1000 * 60 * 60) / (1000 * 60);
        String hS = h < 10 ? "0" + h : "" + h;
        String secS = sec < 10 ? "0" + sec : "" + sec;
        String minS = min < 10 ? "0" + min : "" + min;
        if (h == 0) {
            minute = minS + ":" + secS;
        } else {
            minute = hS + ":" + minS + ":" + secS;
        }
        return minute;
    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
    }
}
