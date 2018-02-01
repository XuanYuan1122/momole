package com.moemoe.lalala.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;

import com.moemoe.lalala.di.components.DaggerPhoneLuYinComponent;
import com.moemoe.lalala.di.modules.PhoneLuYinModule;
import com.moemoe.lalala.event.PhonePlayMusicEvent;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.LuYinEntity;
import com.moemoe.lalala.presenter.PhoneLuYinPresenter;
import com.moemoe.lalala.presenter.PhoneLuyinContract;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.AudioPlayer;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.PhoneMainV2Activity;
import com.moemoe.lalala.view.adapter.PhoneLuYinListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class PhoneLuyinListFragment extends BaseFragment implements PhoneLuyinContract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @Inject
    PhoneLuYinPresenter mPresenter;
    private PhoneLuYinListAdapter mAdapter;
    private boolean isLoading = false;
    private String mate;
    private String type;

    public static PhoneLuyinListFragment newInstance(String type,String mate){
        PhoneLuyinListFragment fragment = new PhoneLuyinListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        bundle.putString("mate",mate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerPhoneLuYinComponent.builder()
                .phoneLuYinModule(new PhoneLuYinModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        type = getArguments().getString("type");
        mate = getArguments().getString("mate");
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter= new PhoneLuYinListAdapter(type);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final LuYinEntity entity = mAdapter.getItem(position);
                if(!entity.isFlag()){
                    if(PreferenceUtils.getAuthorInfo().getTicketNum() > 0){
                        final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
                        dialogUtil.createPromptNormalDialog(getContext(),"是否消耗次元币解锁");
                        dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                            @Override
                            public void CancelOnClick() {
                                dialogUtil.dismissDialog();
                            }

                            @Override
                            public void ConfirmOnClick() {
                                mPresenter.unlockLuYin(entity.getId(),position);
                                dialogUtil.dismissDialog();
                            }
                        });
                        dialogUtil.showDialog();
                    }else {
                        ToastUtils.showShortToast(getContext(),"没有足够的次元币了，快去商店看看吧");
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
                mPresenter.loadLuYinList(type,mate,mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
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
        mPresenter.loadLuYinList(type,mate,0);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void phonePlayMusicEvent(PhonePlayMusicEvent event){
        if(type.equals(event.getType())){
            if(event.isPlay()){
                int position = mAdapter.getPlayingPosition();
                mAdapter.setPlayingPosition(event.getPosition());
                if(position >= 0){
                    mAdapter.notifyItemChanged(position);
                }
                //检查文件是否存在
                if(FileUtil.isExists(StorageUtils.getMusicRootPath() + event.getPath().substring(event.getPath().lastIndexOf("/") + 1))){
                    //存在提示更新播放
                    mAdapter.notifyItemChanged(event.getPosition());
                }else {
                    //不存在 下载  下载成功更新  下载失败提示失败 不更新播放
                    LuYinEntity entity = mAdapter.getItem(event.getPosition());
                    downloadMusic(entity,true);
                }
            }else {
                mAdapter.setPlayingPosition(-1);
                mAdapter.notifyItemChanged(event.getPosition());
            }
        }else {
            int position = mAdapter.getPlayingPosition();
            if(position >= 0){
                mAdapter.setPlayingPosition(-1);
                mAdapter.notifyItemChanged(position);
            }
        }
        if("error".equals(event.getType())){
            FileUtil.deleteFile(event.getPath());
        }
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        AudioPlayer.getInstance(getContext()).stop();
        EventBus.getDefault().unregister(this);
        super.release();
    }

    @Override
    public void onLoadLuYinListSuccess(ArrayList<LuYinEntity> luYinEntities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if (luYinEntities.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
        }
        if(isPull){
            mAdapter.setList(luYinEntities);
        }else {
            mAdapter.addList(luYinEntities);
        }
    }

    @Override
    public void onUnlockSuccess(int position) {
        PreferenceUtils.getAuthorInfo().setTicketNum(PreferenceUtils.getAuthorInfo().getTicketNum()-1);
        ((PhoneMainV2Activity)getContext()).setLuyinMenu("次元币: " +  PreferenceUtils.getAuthorInfo().getTicketNum());
        mAdapter.getItem(position).setFlag(true);
        mAdapter.notifyItemChanged(position);
        LuYinEntity entity = mAdapter.getItem(position);
        downloadMusic(entity,false);
    }

    private void downloadMusic(final LuYinEntity entity, final boolean play){
        if(!FileUtil.isExists(StorageUtils.getMusicRootPath() + entity.getSound().substring(entity.getSound().lastIndexOf("/") + 1))){
            final ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setIcon(R.drawable.ic_launcher);
            dialog.setTitle("下载中");
            FileDownloader.getImpl().create(ApiService.URL_QINIU + entity.getSound())
                    .setPath(StorageUtils.getMusicRootPath() + entity.getSound().substring(entity.getSound().lastIndexOf("/") + 1))
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
                            if(play){
                                mAdapter.notifyItemChanged(mAdapter.getPlayingPosition());
                            }
                        }

                        @Override
                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {
                            ToastUtils.showShortToast(getContext(),"下载失败，请重试");
                            dialog.dismiss();
                            if(play){
                                mAdapter.setPlayingPosition(-1);
                            }
                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {

                        }
                    }).start();
        }
    }
}
