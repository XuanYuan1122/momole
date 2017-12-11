package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerCreateMapImageComponent;
import com.moemoe.lalala.di.modules.CreateMapImageModule;
import com.moemoe.lalala.model.entity.MapAddressEntity;
import com.moemoe.lalala.presenter.CreateMapImageContract;
import com.moemoe.lalala.presenter.CreateMapImagePresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.AndroidBug5497Workaround;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

import static com.moemoe.lalala.utils.StartActivityConstant.REQ_SELECT_MAP_IMAGE;

/**
 *
 * Created by yi on 2017/11/1.
 */

public class CreateMapImageActivity extends BaseAppCompatActivity implements CreateMapImageContract.View {

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tv_menu)
    TextView mTvMenuRight;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.rl_select_root)
    View mSelectRoot;
    @BindView(R.id.iv_select_img)
    ImageView mIvSelect;
    @BindView(R.id.iv_map_img)
    ImageView mIvMapShow;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.ll_address_root)
    View mAddressRoot;

    @Inject
    CreateMapImagePresenter mPresenter;

    private String addressId;
    private String mapCover;
    private String mapOrCover;
    private boolean needCheck;
    private BottomMenuFragment bottomMenuFragment;
    private String url = "";

    @Override
    protected int getLayoutId() {
        return R.layout.ac_create_map_iamge;
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        AndroidBug5497Workaround.assistActivity(this);
        DaggerCreateMapImageComponent.builder()
                .createMapImageModule(new CreateMapImageModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mEtName.getText();
                int len = editable.length();
                if (len > 6) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    mEtName.setText(editable.subSequence(0, 6));
                    editable = mEtName.getText();
                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPresenter.loadAddressList();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvTitle.setText(getString(R.string.label_user_map));
        mTvMenuRight.setVisibility(View.VISIBLE);
        ViewUtils.setRightMargins(mTvMenuRight,(int)getResources().getDimension(R.dimen.x36));
        mTvMenuRight.setText(getString(R.string.label_save));
    }

    @Override
    protected void initListeners() {
        mTvMenuRight.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                //保存用户地图 信息
                if(!TextUtils.isEmpty(mapCover) && !TextUtils.isEmpty(addressId)){
                    createDialog();
                    mPresenter.saveUserMapImage(mapCover,mapOrCover,addressId,needCheck);
                }else {
                    showToast("图片或位置不能为空");
                }
            }
        });
        mSelectRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String name = mEtName.getText().toString().replace(" ","");
                if(!TextUtils.isEmpty(name)){
                  showSelect();
                }else {
                    showToast("请先填写名称");
                }
            }
        });
        mAddressRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(bottomMenuFragment != null) bottomMenuFragment.show(getSupportFragmentManager(),"createMapImageActivity");
            }
        });
    }

    @Override
    protected void initData() {

    }

    private void  loadImage(String url){
        final int x = (int) getResources().getDimension(R.dimen.x180);
        final int y = (int) getResources().getDimension(R.dimen.y220);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(x,y);
        mIvSelect.setLayoutParams(lp);
        Glide.with(CreateMapImageActivity.this)
                .load(url)
                .asBitmap()
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Bitmap select = getScaleBitmap(resource,x,y);
                        mIvSelect.setImageBitmap(select);
                        mapOrCover =  saveBitmap(select);
                        Bitmap showMap = createMapShow(select);
                        mIvMapShow.setImageBitmap(showMap);
                        mapCover = saveBitmap(showMap);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_SELECT_MAP_IMAGE){
            if(resultCode == RESULT_OK && data != null){
                String url = data.getStringExtra("url");
                if(!TextUtils.isEmpty(url) && !this.url.equals(url)){
                    this.url = url;
                    loadImage(StringUtils.getUrl(url));
                }
            }
        }else {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                    loadImage(photoPaths.get(0));
                }
            });
        }
    }

    private Bitmap getScaleBitmap(Bitmap source,int mWidth,int mHeight){
        Bitmap.Config config =
                source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, config);
        float scaleX = (float) mWidth / source.getWidth();
        float scaleY = (float) mHeight / source.getHeight();
        float scale = Math.max(scaleX, scaleY);

        float scaledWidth = scale * source.getWidth();
        float scaledHeight = scale * source.getHeight();
        float left = (mWidth - scaledWidth) / 2;
        float top = (mHeight - scaledHeight) / 2;
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source, null, targetRect, null);

        return bitmap;
    }

    private Bitmap createMapShow(Bitmap source){
        int bitmapWidth = source.getWidth();
        int bitmapHeight = source.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(bitmapWidth,
                bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        paint.setAntiAlias(true);
        canvas.drawBitmap(source,0,0,paint);

        int radius2 = (int) getResources().getDimension(R.dimen.y18);
        float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
        RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable();
        shapeDrawable2.setShape(roundRectShape2);
        shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable2.getPaint().setColor(ContextCompat.getColor(this, R.color.pink_fb7ba2_80));

        Rect bounds = new Rect();
        paint.setTextSize(20);
        paint.getTextBounds(mEtName.getText().toString(),0,mEtName.getText().toString().length(),bounds);

        int padding = (int) getResources().getDimension(R.dimen.x24);

        int w = bounds.width() + padding * 2;
        int h = (int) getResources().getDimension(R.dimen.y36);
        int x = ((int) getResources().getDimension(R.dimen.x180) - w) / 2;
        Bitmap topBitmap = drawableToBitmap(shapeDrawable2,w,h);
        canvas.drawBitmap(topBitmap,x,0,paint);
        Rect targetRect = new Rect(x, 0, x + w, h);
        paint.setColor(ContextCompat.getColor(this,R.color.white));
        paint.setTextSize(20);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        paint.setTextAlign(Paint.Align.CENTER);
       // int y = (int) getResources().getDimension(R.dimen.y8) + bounds.height() / 2;
        int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        // 以下这行是实现水平居中。drawText相应改为传入targetRect.centerX()
        int textX = (int) getResources().getDimension(R.dimen.x180) / 2;
        canvas.drawText(mEtName.getText().toString(), textX, baseline, paint);
        return newBitmap;
    }

    public Bitmap drawableToBitmap(Drawable drawable,int w,int h) {
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }

    private String generateFileName() {
        return java.util.UUID.randomUUID().toString();
    }

    public String saveBitmap(Bitmap mBitmap) {
        File filePic;
        try {
            filePic = new File(StorageUtils.getGalleryDirPath() + generateFileName() + ".png");
            if (!filePic.exists()) {
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return filePic.getAbsolutePath();
    }

    @Override
    public void onFailure(int code, String msg) {
        finalizeDialog();
        ErrorCodeUtils.showErrorMsgByCode(this,code,msg);
    }

    private void showSelect(){
        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(1,"选择官方形象");
        items.add(item);
        item = new MenuItem(2,"自定义形象");
        items.add(item);
        item = new MenuItem(3,"使用过的形象");
        items.add(item);
        bottomMenuFragment.setShowTop(false);
        bottomMenuFragment.setShowCancel(false);
        bottomMenuFragment.setMenuItems(items);
        bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 1){
                    needCheck = false;
                    Intent i = new Intent(CreateMapImageActivity.this,SelectMapImageActivity.class);
                    i.putExtra(SelectMapImageActivity.SELECT_TYPE,SelectMapImageActivity.IS_OFFICIAL);
                    startActivityForResult(i,REQ_SELECT_MAP_IMAGE);
                }
                if(itemId == 2){
                    final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                    alertDialogUtil.createKiraNoticeDialog(CreateMapImageActivity.this,getString(R.string.label_create_map_notice),"选择图片");
                    alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void CancelOnClick() {
                            alertDialogUtil.dismissDialog();
                        }

                        @Override
                        public void ConfirmOnClick() {
                            alertDialogUtil.dismissDialog();
                            try {
                                needCheck = true;
                                ArrayList<String> arrayList = new ArrayList<>();
                                DialogUtils.createImgChooseDlg(CreateMapImageActivity.this, null,CreateMapImageActivity.this, arrayList, 1).show();
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    alertDialogUtil.showDialog();
                }
                if (itemId == 3){
                    final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                    alertDialogUtil.createKiraNoticeDialog(CreateMapImageActivity.this,getString(R.string.label_create_map_notice),"选择图片");
                    alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                        @Override
                        public void CancelOnClick() {
                            alertDialogUtil.dismissDialog();
                        }

                        @Override
                        public void ConfirmOnClick() {
                            alertDialogUtil.dismissDialog();
                            needCheck = false;
                            Intent i = new Intent(CreateMapImageActivity.this,SelectMapImageActivity.class);
                            i.putExtra(SelectMapImageActivity.SELECT_TYPE,SelectMapImageActivity.IS_HISTORY_SELECT);
                            i.putExtra("uuid", PreferenceUtils.getUUid());
                            i.putExtra("use_id","");
                            startActivityForResult(i,REQ_SELECT_MAP_IMAGE);
                        }
                    });
                    alertDialogUtil.showDialog();
                }
            }
        });
        bottomMenuFragment.show(getSupportFragmentManager(),"selectMapImageActivity");
    }

    @Override
    public void onLoadAddressListSuccess(final ArrayList<MapAddressEntity> entities) {
        finalizeDialog();
        if(entities.size() > 0){
            MapAddressEntity entity = entities.get(0);
            addressId = entity.getId();
            mTvAddress.setText(entity.getName());
            bottomMenuFragment = new BottomMenuFragment();
            ArrayList<MenuItem> items = new ArrayList<>();
            for(int i = 0;i < entities.size();i++){
                MapAddressEntity e = entities.get(i);
                MenuItem item = new MenuItem(i,e.getName());
                items.add(item);
            }
            bottomMenuFragment.setShowTop(false);
            bottomMenuFragment.setShowCancel(false);
            bottomMenuFragment.setCancelable(false);
            bottomMenuFragment.setMenuItems(items);
            bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
            bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
                @Override
                public void OnMenuItemClick(int itemId) {
                    MapAddressEntity e = entities.get(itemId);
                    addressId = e.getId();
                    mTvAddress.setText(e.getName());
                }
            });
        }else {
            showToast("没有可选择的位置");
            finish();
        }
    }

    @Override
    public void onSaveSuccess() {
        finalizeDialog();
        showToast("上传成功");
        finish();
    }
}
