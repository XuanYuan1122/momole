package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.AnimationUtil;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by yi on 2016/11/30.
 */

public class MultiImageChooseActivity extends BaseAppCompatActivity implements View.OnClickListener{

    /**
     * 最多可选择多少张图
     */
    public static final String EXTRA_KEY_MAX_PHOTO = "max_image";
    public static final String EXTRA_KEY_SELETED_PHOTOS = "selected_image";
    private final int PREVIEW_IMAGE_REQ = 4000;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_menu)
    TextView mTvSave;
    @BindView(R.id.gv_select_photos)
    GridView mGridImages;
    @BindView(R.id.tv_preview_ar)
    TextView mTvPreview;
    @BindView(R.id.tv_album_ar)
    TextView mTvAlbum;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.ll_select_album)
    RelativeLayout mRlAlbums;

    private ImageSelectorAdapter mImageSelectorAdapter;
    private AlbumAdapter mAlbumAdapter;

    /**
     * 选中的图片路径
     */
    private ArrayList<String> mSelected;
    /**
     * 当前显示的图片列表
     */
    private ArrayList<String> mImages;
    /**
     * 相册列表
     */
    private ArrayList<AlbumModel> mAlbums;
    /**
     * 最多可选择多少图
     */
    private int mMaxNumSelected;
    /**
     * a_label_recent_photos
     */
    private String mLabelRecent;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_multi_image_choose;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        ImmersionBar.with(this)
//                .statusBarView(R.id.top_view)
//                .statusBarDarkFont(true,0.2f)
//                .transparentNavigationBar()
//                .init();
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        mLabelRecent = getResources().getString(R.string.label_recent_photos);
        mTvTitle.setText(getString(R.string.label_recent_photos));
        mTvSave.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvSave,DensityUtil.dip2px(this,18));
        mTvSave.setOnClickListener(this);
        mSelected = new ArrayList<>();
        if(getIntent() != null){
            mMaxNumSelected = getIntent().getIntExtra(EXTRA_KEY_MAX_PHOTO, 9);
            ArrayList<String> selected = getIntent().getStringArrayListExtra(EXTRA_KEY_SELETED_PHOTOS);
            if (selected != null && selected.size() > 0) {
                mSelected.addAll(selected);
            }
        }
        if(mSelected.size() > 0){
            mTvSave.setText(getString(R.string.label_confirm) + "(" + mSelected.size() + ")");
        }
        mAlbums = FileUtil.getAlbumList(this);
        mImages = FileUtil.getOneAlbumPhotoList(this, null);
        mTvAlbum.setOnClickListener(this);
        mTvPreview.setOnClickListener(this);
        initGridPhoto();
        initListAblum();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    private void initGridPhoto() {
        mImageSelectorAdapter = new ImageSelectorAdapter();
        mGridImages.setAdapter(mImageSelectorAdapter);
        mGridImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = mImages.get(position);

                if (mMaxNumSelected == 1) {
                    // 直接选择成功
                    mSelected.add(path);
                    ((ImageSelectorAdapter.ImageViewHolder) view.getTag()).setChecked(true);
                    finishSelect();
                } else {
                    if (mSelected.contains(path)) {
                        mSelected.remove(path);
                    } else {
                        if (mSelected.size() >= mMaxNumSelected) {
                            showToast( String.format(getString(R.string.msg_image_count_limit), mMaxNumSelected));
                            return;
                        } else {
                            mSelected.add(path);
                        }
                    }
                    updateBtnPreview();
                    ((ImageSelectorAdapter.ImageViewHolder) view.getTag()).setChecked(mSelected.contains(path));
                    mTvSave.setText(getString(R.string.label_confirm) + "(" + mSelected.size() + ")");
                }
            }
        });
    }

    private void initListAblum() {
        ListView mListAblum = (ListView) findViewById(R.id.lv_select_ablum);
        mAlbumAdapter = new AlbumAdapter();
        mListAblum.setAdapter(mAlbumAdapter);
        mListAblum.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlbumModel current = (AlbumModel) parent.getItemAtPosition(position);
                for (int i = 0; i < mAlbums.size(); i++) {
                    AlbumModel album = mAlbums.get(i);
                    album.isCheck = i == position;
                }
                mAlbumAdapter.notifyDataSetChanged();
                hideAlbum();
                mTvAlbum.setText(current.name);
                if (current.name.equals(mLabelRecent)) {
                    mImages = FileUtil.getOneAlbumPhotoList(MultiImageChooseActivity.this, null);
                } else {
                    mImages = FileUtil.getOneAlbumPhotoList(MultiImageChooseActivity.this, current.name);
                }
                mImageSelectorAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateBtnPreview() {
        if (mSelected.size() > 0) {
            mTvPreview.setEnabled(true);
        } else {
            mTvPreview.setEnabled(false);
        }
    }

    private void finishSelect() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_KEY_SELETED_PHOTOS, mSelected);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_menu) {
            finishSelect();
        } else if (id == R.id.tv_preview_ar) {
            showPriview(mSelected, 0);
        } else if (id == R.id.tv_album_ar) {
            toggleAlbums();
        }
    }

    private void showPriview(ArrayList<String> images, int position) {
        Intent intent = new Intent(MultiImageChooseActivity.this, ImageBigSelectActivity.class);
        ArrayList<String> img = new ArrayList<>();
        for(String image : images){
            image =  "file://" + image;
            img.add(image);
        }
        intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_PREVIEW_PHOTO, img);
        intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, position);
        intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_CAN_SELECT, false);// 预留 以后为true
        intent.putExtra(ImageBigSelectActivity.EXTRA_FROM_MUL,true);
        startActivityForResult(intent, PREVIEW_IMAGE_REQ);// 以后可选择 有返回数据
    }

    /**
     * 切换相册的显示消失状态
     */
    private void toggleAlbums() {
        if (mRlAlbums.getVisibility() == View.GONE) {
            popAlbum();
        } else {
            hideAlbum();
        }
    }

    private void popAlbum() {
        mRlAlbums.setVisibility(View.VISIBLE);
        new AnimationUtil(this, R.anim.album_translate_up).setLinearInterpolator().startAnimation(mRlAlbums);
    }

    private void hideAlbum() {
        new AnimationUtil(this, R.anim.album_translate_down).setLinearInterpolator().startAnimation(mRlAlbums);
        mRlAlbums.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (mRlAlbums.getVisibility() == View.VISIBLE) {
            hideAlbum();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 当前图片是否被选中
     *
     */
    private boolean isPictureSelected(String path) {
        return mSelected.contains(path);
    }

    public class AlbumAdapter extends BaseAdapter {

        private int mImageWidth;

        AlbumAdapter() {
            mImageWidth = getResources().getDimensionPixelSize(R.dimen.size_80);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AlbumViewHolder albumItem;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
                albumItem = new AlbumViewHolder(convertView);
            } else {
                albumItem = (AlbumViewHolder) convertView.getTag();
            }
            albumItem.loadData(mAlbums.get(position), parent.getContext());
            return convertView;
        }

        private class AlbumViewHolder {
            private ImageView mIvAlbum;
            private ImageView mIvSelectState;
            private TextView mTvName;
            private TextView mTvCount;

            AlbumViewHolder(View convertView) {
                mIvAlbum = (ImageView) convertView.findViewById(R.id.iv_album_image);
                mIvSelectState = (ImageView) convertView.findViewById(R.id.iv_album_select_state);
                mTvName = (TextView) convertView.findViewById(R.id.tv_album_name);
                mTvCount = (TextView) convertView.findViewById(R.id.tv_album_count);
                convertView.setTag(this);
            }

            void loadData(AlbumModel album, Context context) {
                Glide.with(MultiImageChooseActivity.this)
                        .load("file://" + album.thumbPath)
                        .override(mImageWidth, mImageWidth)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(mIvAlbum);
                mTvName.setText(album.name);
                String temp = album.count + context.getString(R.string.label_zhang);
                mTvCount.setText(temp);
                if (album.isCheck) {
                    mIvSelectState.setVisibility(View.VISIBLE);
                } else {
                    mIvSelectState.setVisibility(View.GONE);
                }

            }
        }

        @Override
        public int getCount() {
            return mAlbums.size();
        }

        @Override
        public Object getItem(int position) {
            return mAlbums.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    /**
     * 相册数据
     *
     * @author Ben
     *
     */
    public static class AlbumModel {

        public String name;
        public int count;
        String thumbPath;
        boolean isCheck;

        public AlbumModel(String name, int count, String thumbPath) {
            super();
            this.name = name;
            this.count = count;
            this.thumbPath = thumbPath;
        }

        public AlbumModel(String name, int count, String thumbPath, boolean isCheck) {
            super();
            this.name = name;
            this.count = count;
            this.thumbPath = thumbPath;
            this.isCheck = isCheck;
        }

    }


    public class ImageSelectorAdapter extends BaseAdapter {
        /**
         * 每一张小图的宽度
         */
        private int mItemWidth;

        ImageSelectorAdapter() {
            mItemWidth = DensityUtil.getScreenWidth(MultiImageChooseActivity.this) / 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MultiImageChooseActivity.this)
                        .inflate(R.layout.item_multi_select_image, parent, false);
                convertView.setLayoutParams(new GridView.LayoutParams(mItemWidth, mItemWidth));
                holder = new ImageViewHolder(convertView);
            } else {
                holder = (ImageViewHolder) convertView.getTag();
            }
            String path = mImages.get(position);

            holder.loadData(path, isPictureSelected(path), mItemWidth);
            return convertView;
        }

        class ImageViewHolder {

            ImageView ivImage;
            CheckBox cbIndicatorImage;

            ImageViewHolder(View convertView) {
                cbIndicatorImage = (CheckBox) convertView.findViewById(R.id.iv_multi_toggle);
                ivImage = (ImageView) convertView.findViewById(R.id.iv_multi_image);
                convertView.setTag(this);
            }

            void loadData(final String path, final boolean selected, int size) {
                setChecked(selected);
                Glide.with(MultiImageChooseActivity.this)
                        .load("file://" + path)
                        .override(size, size)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .into(ivImage);
            }

            void setChecked(boolean isChecked) {
                cbIndicatorImage.setChecked(isChecked);
            }
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public Object getItem(int position) {
            return mImages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}
