package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DownloadEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.DownloadListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadRecord;
import zlc.season.rxdownload2.function.Utils;

/**
 * 下载管理列表
 * Created by yi on 2017/10/11.
 */

public class DownLoadListActivity extends BaseAppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    private DownloadListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setPadding(DensityUtil.dip2px(this,12),0,DensityUtil.dip2px(this,12),0);
        mAdapter = new DownloadListAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.setLoadMoreEnabled(false);
        loadData();
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
        mTitle.setText("下载管理");
    }

    @Override
    protected void initListeners() {
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DownloadEntity entity = mAdapter.getItem(position);
                String type = entity.record.getExtra3();
                if("image".equals(type)){
                    ArrayList<Image> temp = new ArrayList<>();
                    Image image = new Image();
                    String str = entity.record.getUrl();
                    image.setPath(str);
                    temp.add(image);
                    Intent intent = new Intent(DownLoadListActivity.this, ImageBigSelectActivity.class);
                    intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, temp);
                    intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
                            0);
                    startActivity(intent);
                }else if("txt".equals(type)){
                    NewFileXiaoShuo2Activity.startActivity(DownLoadListActivity.this,entity.record.getExtra4());
                }else if("movie".equals(type)){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String type1 = "video/* ";
                    File file = new File(entity.record.getExtra4());
                    Uri uri;
                    // 判断版本大于等于7.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// "com.moemoe.lalala.FileProvider"即是在清单文件中配置的authorities
                        uri = FileProvider.getUriForFile(DownLoadListActivity.this, "com.moemoe.lalala.FileProvider", file);// 给目标应用一个临时授权
                         intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    intent.setDataAndType(uri, type1);
                    startActivity(intent);
                }else {
                    showToast("不支持打开此文件");
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        List<DownloadEntity> list = mAdapter.getList();
        for (DownloadEntity each : list) {
            Utils.dispose(each.disposable);
        }
    }

        private void loadData() {
        RxDownload.getInstance(this).getTotalDownloadRecords()
                .map(new Function<List<DownloadRecord>, List<DownloadEntity>>() {
                    @Override
                    public List<DownloadEntity> apply(List<DownloadRecord> downloadRecords) throws Exception {
                        List<DownloadEntity> result = new ArrayList<>();
                        for (DownloadRecord each : downloadRecords) {
                            if(!TextUtils.isEmpty(each.getExtra1())){
                                DownloadEntity bean = new DownloadEntity();
                                bean.record = each;
                                result.add(bean);
                            }else {
                                RxDownload.getInstance(DownLoadListActivity.this).deleteServiceDownload(each.getUrl(),false).subscribe();
                            }
                        }
                        return result;
                    }
                })
                .subscribe(new Consumer<List<DownloadEntity>>() {
                    @Override
                    public void accept(List<DownloadEntity> downloadBeen) throws Exception {
                        mAdapter.addList((ArrayList<DownloadEntity>) downloadBeen);
                    }
                });
    }
}
