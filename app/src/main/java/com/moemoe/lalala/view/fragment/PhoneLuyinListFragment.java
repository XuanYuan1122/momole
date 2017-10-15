package com.moemoe.lalala.view.fragment;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.SeekBar;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.di.components.DaggerPhoneLuYinComponent;
import com.moemoe.lalala.di.modules.PhoneLuYinModule;
import com.moemoe.lalala.event.MateBackPressEvent;
import com.moemoe.lalala.event.MateLuyinEvent;
import com.moemoe.lalala.event.PhonePlayMusicEvent;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.LuYinEntity;
import com.moemoe.lalala.netamusic.data.model.PlayList;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.netamusic.player.IPlayBack;
import com.moemoe.lalala.netamusic.player.Player;
import com.moemoe.lalala.presenter.PhoneLuYinPresenter;
import com.moemoe.lalala.presenter.PhoneLuyinContract;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.AudioPlayer;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.view.activity.PhoneMainActivity;
import com.moemoe.lalala.view.adapter.PhoneLuYinListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadStatus;

/**
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

//    private Handler mHandler = new Handler();
//    private Runnable mProgressCallback = new Runnable() {
//        @Override
//        public void run() {
//            if (AudioPlayer.getInstance(getContext()).isPlaying()) {
//                if(mAdapter.getPlayingPosition() != -1){
//                    mAdapter.notifyItemChanged(mAdapter.getPlayingPosition());
//                    mHandler.postDelayed(this, 1000);
//                }
//            }else {
//                mHandler.removeCallbacks(this);
//            }
//        }
//    };

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
                    final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
                    dialogUtil.createPromptNormalDialog(getContext(),"是否消耗录音券解锁");
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
        subscribeBackOrChangeEvent();
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    private void subscribeBackOrChangeEvent() {
        Disposable subscription = RxBus.getInstance()
                .toObservable(PhonePlayMusicEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Consumer<PhonePlayMusicEvent>() {
                    @Override
                    public void accept(PhonePlayMusicEvent phonePlayMusicEvent) throws Exception {
                        if(type.equals(phonePlayMusicEvent.getType())){
                            if(phonePlayMusicEvent.isPlay()){
                                int position = mAdapter.getPlayingPosition();
                                mAdapter.setPlayingPosition(phonePlayMusicEvent.getPosition());
                                if(position >= 0){
                                    mAdapter.notifyItemChanged(position);
                                }
                                //检查文件是否存在
                                if(FileUtil.isExists(StorageUtils.getMusicRootPath() + phonePlayMusicEvent.getPath().substring(phonePlayMusicEvent.getPath().lastIndexOf("/") + 1))){
                                    //存在提示更新播放
                                    mAdapter.notifyItemChanged(phonePlayMusicEvent.getPosition());
                                }else {
                                    //不存在 下载  下载成功更新  下载失败提示失败 不更新播放
                                    LuYinEntity entity = mAdapter.getItem(phonePlayMusicEvent.getPosition());
                                    downloadMusic(entity,true);
                                }
                            }else {
                                mAdapter.setPlayingPosition(-1);
                                mAdapter.notifyItemChanged(phonePlayMusicEvent.getPosition());
                            }
                        }else {
                            int position = mAdapter.getPlayingPosition();
                            if(position >= 0){
                                mAdapter.setPlayingPosition(-1);
                                mAdapter.notifyItemChanged(position);
                            }
                        }
                        if("error".equals(phonePlayMusicEvent.getType())){
                            FileUtil.deleteFile(phonePlayMusicEvent.getPath());
                        }
                    }
                }, new Consumer<Throwable>() {

                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        AudioPlayer.getInstance(getContext()).stop();
        RxBus.getInstance().unSubscribe(this);
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
            RxDownload.getInstance(getContext())
                    .download(ApiService.URL_QINIU + entity.getSound(),entity.getSound().substring(entity.getSound().lastIndexOf("/") + 1),StorageUtils.getMusicRootPath())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<DownloadStatus>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull DownloadStatus downloadStatus) {
                            dialog.setMax((int) downloadStatus.getTotalSize());
                            dialog.setProgress((int) downloadStatus.getDownloadSize());
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            ToastUtils.showShortToast(getContext(),"下载失败，请重试");
                            dialog.dismiss();
                            RxDownload.getInstance(getContext()).deleteServiceDownload(ApiService.URL_QINIU +  entity.getSound(),false).subscribe();
                            if(play){
                                mAdapter.setPlayingPosition(-1);
                            }
                        }

                        @Override
                        public void onComplete() {
                            dialog.dismiss();
                            RxDownload.getInstance(getContext()).deleteServiceDownload(ApiService.URL_QINIU +  entity.getSound(),false).subscribe();
                            if(play){
                                mAdapter.notifyItemChanged(mAdapter.getPlayingPosition());
                                //mHandler.postDelayed(mProgressCallback,1000);
                            }
                        }
                    });
        }
    }
}
