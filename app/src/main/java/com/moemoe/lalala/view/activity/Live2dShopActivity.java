package com.moemoe.lalala.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerLive2dShopComponent;
import com.moemoe.lalala.di.modules.CoinShopModule;
import com.moemoe.lalala.di.modules.Live2dShopModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CoinShopEntity;
import com.moemoe.lalala.model.entity.Live2dShopEntity;
import com.moemoe.lalala.model.entity.OrderEntity;
import com.moemoe.lalala.presenter.CoinShopContract;
import com.moemoe.lalala.presenter.CoinShopPresenter;
import com.moemoe.lalala.presenter.Live2dShopContract;
import com.moemoe.lalala.presenter.Live2dShopPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.CoinShopAdapter;
import com.moemoe.lalala.view.adapter.Live2dShopAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/6/26.
 */

public class Live2dShopActivity extends BaseAppCompatActivity implements Live2dShopContract.View{

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    @Inject
    Live2dShopPresenter mPresenter;

    private Live2dShopAdapter mAdapter;
    private boolean isLoading = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerLive2dShopComponent.builder()
                .live2dShopModule(new Live2dShopModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mAdapter = new Live2dShopAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.setLoadMoreEnabled(false);
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        super.onDestroy();
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
        mTitle.setText("Live2D壁纸");
    }

    @Override
    protected void initListeners() {

    }

    private void downloadLive2d(final Live2dShopEntity item){
        final ProgressDialog dialog = new ProgressDialog(Live2dShopActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
        dialog.setTitle("下载中");
        FileDownloader.getImpl().create(ApiService.URL_QINIU + item.getPackageUrl())
                .setPath(StorageUtils.getLive2dRootPath() + item.getId() + ".zip")
                .setCallbackProgressTimes(1)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        dialog.setMax(totalBytes);
                        dialog.setProgress(soFarBytes);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        dialog.dismiss();
                        File file = new File(StorageUtils.getLive2dRootPath() + item.getId() + ".zip");
                        String md5 = item.getMd5();
                        if(md5.length() < 32){
                            int n = 32 - md5.length();
                            for(int i = 0;i < n;i++){
                                md5 = "0" + md5;
                            }
                        }
                        if(!md5.equals(StringUtils.getFileMD5(file))){
                            FileUtil.deleteDir(StorageUtils.getLive2dRootPath() + item.getId() + ".zip");
                            showToast("下载失败,请重试");
                        }else {
                            Intent i = new Intent(Live2dShopActivity.this,Live2dNormalActivity.class);
                            i.putExtra(UUID,item.getId());
                            i.putExtra("show_ping_fen",item.getMyScore() < 0);
                            startActivity(i);
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        dialog.dismiss();
                        FileUtil.deleteDir(StorageUtils.getLive2dRootPath() + item.getId() + ".zip");
                        showToast("下载失败,请重试");
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();
        dialog.show();
    }

    @Override
    protected void initData() {

        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final Live2dShopEntity item = mAdapter.getItem(position);
                if (item.isVip() && !TextUtils.isEmpty(PreferenceUtils.getAuthorInfo().getVipTime())) {
                    FileUtil.deleteFile(StorageUtils.getLive2dRootPath() + item.getId() + ".zip");
                    downloadLive2d(item);

//                    if(FileUtil.isExists(StorageUtils.getLive2dRootPath() + item.getId() + ".zip")){
//                        //存在 直接跳转
//                        Intent i = new Intent(Live2dShopActivity.this,Live2dNormalActivity.class);
//                        i.putExtra(UUID,item.getId());
//                        i.putExtra("show_ping_fen",item.getMyScore() < 0);
//                        startActivity(i);
//                    }else {
//                        //不存在 下载成功后跳转
//                        downloadLive2d(item);
//                    }
                } else {
                    if (item.isHave()) {
                        FileUtil.deleteFile(StorageUtils.getLive2dRootPath() + item.getId() + ".zip");
                        downloadLive2d(item);
//                        if(FileUtil.isExists(StorageUtils.getLive2dRootPath() + item.getId() + ".zip")){
//                            //存在 直接跳转
//                            Intent i = new Intent(Live2dShopActivity.this,Live2dNormalActivity.class);
//                            i.putExtra(UUID,item.getId());
//                            i.putExtra("show_ping_fen",item.getMyScore() < 0);
//                            startActivity(i);
//                        }else {
//                            //不存在 下载成功后跳转
//                            downloadLive2d(item);
//                        }
                    } else {
                        final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                        alertDialogUtil.createNormalDialog(Live2dShopActivity.this, "确认购买？");
                        alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                            @Override
                            public void CancelOnClick() {
                                alertDialogUtil.dismissDialog();
                            }

                            @Override
                            public void ConfirmOnClick() {
                                alertDialogUtil.dismissDialog();
                                mPresenter.buyLive2d(item.getId(), position);
                            }
                        });
                        alertDialogUtil.showDialog();
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadLive2dList(mAdapter.getList().size());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadLive2dList(0);
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        mPresenter.loadLive2dList(0);
    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this, code, msg);
    }

    @Override
    public void onBuyLive2dSuccess(int position) {
        mAdapter.getItem(position).setHave(true);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onLoadListSuccess(ArrayList<Live2dShopEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if(entities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
        }
        if(isPull){
            mAdapter.setList(entities);
        }else {
            mAdapter.addList(entities);
        }
    }
}
