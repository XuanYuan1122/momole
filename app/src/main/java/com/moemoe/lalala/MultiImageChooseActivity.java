package com.moemoe.lalala;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
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

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.app.image.ImageOptions;
import com.moemoe.lalala.utils.AnimationUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;

import java.util.ArrayList;

;

/**
 * Created by Haru on 2016/4/30 0030.
 */
@ContentView(R.layout.ac_multi_image_choose)
public class MultiImageChooseActivity extends BaseActivity implements View.OnClickListener{

    /**
     * 最多可选择多少张图
     */
    public static final String EXTRA_KEY_MAX_PHOTO = "max_image";
    public static final String EXTRA_KEY_SELETED_PHOTOS = "selected_image";

    private static final int PREVIEW_IMAGE_REQ = 4000;
    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_sava)
    private TextView mTvSave;
    @FindView(R.id.gv_select_photos)
    private GridView mGridImages;
    private ListView mListAblum;
    @FindView(R.id.tv_preview_ar)
    private TextView mTvPreview;
    @FindView(R.id.tv_album_ar)
    private TextView mTvAlbum;
    @FindView(R.id.ll_select_album)
    private RelativeLayout mRlAlbums;

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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }

    };

    @Override
    protected void initView() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mLabelRecent = getResources().getString(R.string.label_recent_photos);
        mTvSave.setOnClickListener(this);
        mSelected = new ArrayList<>();
        if(mIntent != null){
            mMaxNumSelected = mIntent.getIntExtra(EXTRA_KEY_MAX_PHOTO, 9);
            ArrayList<String> selected = mIntent.getStringArrayListExtra(EXTRA_KEY_SELETED_PHOTOS);
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
                            ToastUtil.showToast(MultiImageChooseActivity.this,
                                    String.format(getString(R.string.msg_image_count_limit), mMaxNumSelected));
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
        mGridImages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO
                showPriview(mImages, position);
                return true;
            }
        });
    }

    private void initListAblum() {
        mListAblum = (ListView) findViewById(R.id.lv_select_ablum);
        mAlbumAdapter = new AlbumAdapter();
        mListAblum.setAdapter(mAlbumAdapter);
        mListAblum.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlbumModel current = (AlbumModel) parent.getItemAtPosition(position);
                for (int i = 0; i < mAlbums.size(); i++) {
                    AlbumModel album = mAlbums.get(i);
                    if (i == position)
                        album.isCheck = true;
                    else
                        album.isCheck = false;
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
        if (id == R.id.tv_sava) {
            finishSelect();
        } else if (id == R.id.tv_preview_ar) {
            showPriview(mSelected, 0);
        } else if (id == R.id.tv_album_ar) {
            toggleAlbums();
        }
    }

    private void showPriview(ArrayList<String> images, int position) {
        // TODO
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

    /**
     * 清空选择 预留
     */
    private void resetImageSelectState() {
        mSelected.clear();
        mTvSave.setText(getString(R.string.label_confirm) + "(" + 0 + ")");
        mTvPreview.setEnabled(false);
        mImageSelectorAdapter.notifyDataSetChanged();
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
       // Utils.image().clearMemCache();
    }

    /**
     * 当前图片是否被选中
     *
     * @param path
     * @return
     */
    private boolean isPictureSelected(String path) {
        if (mSelected.contains(path)) {
            return true;
        } else {
            return false;
        }
    }

    public class AlbumAdapter extends BaseAdapter {

        private int mImageWidth;

        public AlbumAdapter() {
            mImageWidth = getResources().getDimensionPixelSize(R.dimen.img_width_album_thumb);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AlbumViewHolder albumItem = null;
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

            public AlbumViewHolder(View convertView) {
                mIvAlbum = (ImageView) convertView.findViewById(R.id.iv_album_image);
                mIvSelectState = (ImageView) convertView.findViewById(R.id.iv_album_select_state);
                mTvName = (TextView) convertView.findViewById(R.id.tv_album_name);
                mTvCount = (TextView) convertView.findViewById(R.id.tv_album_count);
                convertView.setTag(this);
            }

            public void loadData(AlbumModel album, Context context) {
                Utils.image().bind(mIvAlbum, "file://" + album.thumbPath, new ImageOptions.Builder()
                        .setSize(mImageWidth, mImageWidth)
                        .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                        .setFailureDrawableId(R.drawable.ic_default_club_l)
                        .setLoadingDrawableId(R.drawable.ic_default_club_l)
                        .setUseMemCache(false)
                        .build());
//                Glide.with(MultiImageChooseActivity.this)
//                        .load("file://"+album.thumbPath)
//                        .override(mImageWidth, mImageWidth)
//                        .placeholder(R.drawable.ic_default_club_l)
//                        .error(R.drawable.ic_default_club_l)
//                        .skipMemoryCache(true)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .into(mIvAlbum);
//                Picasso.with(MultiImageChooseActivity.this)
//                        .load(new File(album.thumbPath))
//                        .resize(mImageWidth, mImageWidth)
//                        .placeholder(R.drawable.ic_default_club_l)
//                        .error(R.drawable.ic_default_club_l)
//                        .centerInside()
//                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                        .into(mIvAlbum);
                mTvName.setText(album.name);
                mTvCount.setText(album.count + context.getString(R.string.label_zhang));
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
        public String thumbPath;
        public boolean isCheck;

        public AlbumModel() {
            super();
        }

        public AlbumModel(String name) {
            this.name = name;
        }

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

        public ImageSelectorAdapter() {
            mItemWidth = DensityUtil.getScreenWidth() / 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageViewHolder holder = null;
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

            public ImageView ivImage;
            public CheckBox cbIndicatorImage;

            public ImageViewHolder(View convertView) {
                cbIndicatorImage = (CheckBox) convertView.findViewById(R.id.iv_multi_toggle);
                ivImage = (ImageView) convertView.findViewById(R.id.iv_multi_image);
                convertView.setTag(this);
            }

            public void loadData(final String path, final boolean selected, int size) {
                setChecked(selected);
                //cbIndicatorImage.setChecked(selected);
                Utils.image().bind(ivImage,"file://" + path, new ImageOptions.Builder()
                        .setSize(size, size)
                        .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                        .setFailureDrawableId(R.drawable.ic_default_club_l)
                        .setLoadingDrawableId(R.drawable.ic_default_club_l)
                        .setUseMemCache(false)
                        .build());
//                Glide.with(MultiImageChooseActivity.this)
//                        .load("file://" + path)
//                        .override(size, size)
//                        .placeholder(R.drawable.ic_default_club_l)
//                        .error(R.drawable.ic_default_club_l)
//                        .skipMemoryCache(true)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .into(ivImage);
//                Picasso.with(MultiImageChooseActivity.this)
//                        .load(new File(path))
//                        .resize(size, size)
//                        .config(Bitmap.Config.RGB_565)
//                        .placeholder(R.drawable.ic_default_club_l)
//                        .error(R.drawable.ic_default_club_l)
//                        .centerCrop()
//                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                        .into(ivImage);
            }

            public void setChecked(boolean isChecked) {
                cbIndicatorImage.setChecked(isChecked);
//                if (isChecked) {
//                    ivImage.setDrawingCacheEnabled(true);
//                    ivImage.buildDrawingCache();
//                    ivImage.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
//                } else {
//                    ivImage.clearColorFilter();
//                }
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
