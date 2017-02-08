package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.view.adapter.SelectImgAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by yi on 2016/11/30.
 */

public class DocHideAddActivity extends BaseAppCompatActivity {

    private final int CONTENT_LIMIT = 3000;
    private final int REQ_GET_EDIT_VERSION_IMG = 2333;
    private final int REQ_GET_EDIT_VERSION_IMG_2 = 233;
    private final int ICON_NUM_LIMIT = 9;
    private final int RES_OK = 6666;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edt_content)
    EditText mEdtContent;
    @BindView(R.id.rv_img)
    RecyclerView mRvSelectImg;
    @BindView(R.id.tv_menu)
    TextView mTvDone;
    private String mContent;
    private int mContentRemain;
    private SelectImgAdapter mSelectAdapter;
    private ArrayList<String> mIconPaths = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.ac_add_hide_doc;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        LinearLayoutManager selectRvL = new LinearLayoutManager(this);
        selectRvL.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvSelectImg.setLayoutManager(selectRvL);
        mSelectAdapter = new SelectImgAdapter(this);
        mRvSelectImg.setAdapter(mSelectAdapter);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mEdtContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mContentRemain = CONTENT_LIMIT - s.length();
                if (mContentRemain <= -1000) { // 无聊...免得显示4位数不好看
                    mEdtContent.setText(mEdtContent.getText().subSequence(0, 999 + CONTENT_LIMIT));
                }
                mContent = s.toString();
            }
        });
        mTvDone.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                done();
            }
        });
        mSelectAdapter.setOnItemClickListener(new SelectImgAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position == mIconPaths.size()){
                    choosePhoto();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onAllDelete() {

            }
        });
    }

    private void done(){
        if (mContent != null && mContent.length() > CONTENT_LIMIT) {
            showToast(R.string.label_more_doc_content);
        }else {
            Intent i = new Intent();
            i.putExtra("content",mContent);
            i.putStringArrayListExtra("paths",mIconPaths);
            setResult(RES_OK,i);
            finish();
        }
    }

    private void choosePhoto() {
        if (mIconPaths.size() < ICON_NUM_LIMIT) {
            if (AppSetting.IS_EDITOR_VERSION) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                try {
                    startActivityForResult(intent, REQ_GET_EDIT_VERSION_IMG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    DialogUtils.createImgChooseDlg(this, null, this, mIconPaths, ICON_NUM_LIMIT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            showToast(R.string.msg_create_doc_9_jpg);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_GET_EDIT_VERSION_IMG || requestCode == REQ_GET_EDIT_VERSION_IMG_2) {
            if (resultCode == RESULT_OK && data != null) {
                String photoPath = null;
                Uri u = data.getData();
                if (u != null) {
                    String schema = u.getScheme();
                    if ("file".equals(schema)) {
                        photoPath = u.getPath();
                    }else if ("content".equals(schema)) {
                        photoPath = StorageUtils.getTempFile(System.currentTimeMillis() + ".jpg").getAbsolutePath();
                        InputStream is ;
                        FileOutputStream fos ;
                        try {
                            is = getContentResolver().openInputStream(u);
                            fos = new FileOutputStream(new File(photoPath));
                            FileUtil.copyFile(is, fos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (FileUtil.isValidGifFile(photoPath)) {
                            String newFile = StorageUtils.getTempFile(System.currentTimeMillis() + ".gif").getAbsolutePath();
                            FileUtil.copyFile(photoPath, newFile);
                            FileUtil.deleteOneFile(photoPath);
                            photoPath = newFile;
                        }
                    }
                    if(requestCode == REQ_GET_EDIT_VERSION_IMG_2){
                        mIconPaths.clear();
                    }
                    mIconPaths.add(photoPath);
                    if(requestCode == REQ_GET_EDIT_VERSION_IMG){
                        onGetPhotos();
                    }
                }
            }
        }else {
            DialogUtils.handleImgChooseResult(this, requestCode, resultCode, data, new DialogUtils.OnPhotoGetListener() {

                @Override
                public void onPhotoGet(ArrayList<String> photoPaths, boolean override) {
                    if (override) {
                        mIconPaths = photoPaths;
                    } else {
                        mIconPaths.addAll(photoPaths);
                    }
                    onGetPhotos();
                }
            });
        }
    }
    private void onGetPhotos() {
        mSelectAdapter.setData(mIconPaths);
    }

    @Override
    protected void initData() {

    }
}
