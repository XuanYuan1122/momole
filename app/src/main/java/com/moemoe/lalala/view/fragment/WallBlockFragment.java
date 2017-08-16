//package com.moemoe.lalala.view.fragment;
//
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//
//import com.moemoe.lalala.R;
//import com.moemoe.lalala.app.MoeMoeApplication;
//import com.moemoe.lalala.di.components.DaggerCommentComponent;
//import com.moemoe.lalala.di.modules.CommentModule;
//import com.moemoe.lalala.model.entity.WallBlock;
//import com.moemoe.lalala.presenter.CommentContract;
//import com.moemoe.lalala.presenter.CommentPresenter;
//import com.moemoe.lalala.utils.IntentUtils;
//import com.moemoe.lalala.utils.ToastUtils;
//import com.moemoe.lalala.view.widget.view.DraggableLayout;
//import com.moemoe.lalala.view.widget.view.PullAndLoadLayout;
//import com.moemoe.lalala.view.widget.recycler.PullCallback;
//
//import java.util.ArrayList;
//
//import javax.inject.Inject;
//
//import butterknife.BindView;
//
///**
// * Created by yi on 2016/12/2.
// */
//
//public class WallBlockFragment extends BaseFragment implements CommentContract.View{
//
//    @BindView(R.id.scroll_root)
//    PullAndLoadLayout mList;
//    private boolean mIsLoading = false;
//    private boolean mIsHasLoadedAll = false;
//    @Inject
//    CommentPresenter mPresenter;
//    private int mCurPage = 2;
//    @Override
//    protected int getLayoutId() {
//        return R.layout.frag_wall;
//    }
//
//    @Override
//    protected void initViews(Bundle savedInstanceState) {
//        DaggerCommentComponent.builder()
//                .commentModule(new CommentModule(this))
//                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
//                .build()
//                .inject(this);
//        mList.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
//        mList.getFsdLayout().setItemClickListener(new DraggableLayout.DragItemClickListener() {
//            @Override
//            public void itemClick(View v, int position, WallBlock wallBlock) {
//                if (!TextUtils.isEmpty(wallBlock.getSchema())) {
//                    Uri uri = Uri.parse(wallBlock.getSchema());
//                    IntentUtils.toActivityFromUri(getActivity(), uri,v);
//                }
//            }
//        });
//        mList.isLoadMoreEnabled(false);
//        mList.setPullCallback(new PullCallback() {
//            @Override
//            public void onLoadMore() {
//                mPresenter.doRequest(mCurPage,4);
//            }
//
//            @Override
//            public void onRefresh() {
//                mPresenter.doRequest(1,4);
//                mIsHasLoadedAll = false;
//            }
//
//            @Override
//            public boolean isLoading() {
//                return mIsLoading;
//            }
//
//            @Override
//            public boolean hasLoadedAllItems() {
//                return mIsHasLoadedAll;
//            }
//        });
//        mPresenter.doRequest(1,4);
//    }
//
//    @Override
//    public void onFailure(int code,String msg) {
//        mList.setComplete();
//        mIsLoading = false;
//
//    }
//
//    @Override
//    public void onSuccess(Object entities, boolean pull) {
//        mList.setComplete();
//        if(((ArrayList<WallBlock>) entities).size() == 0){
//            mList.isLoadMoreEnabled(false);
//            ToastUtils.showShortToast(getContext(),getString(R.string.msg_all_load_down));
//        }else {
//            mList.isLoadMoreEnabled(true);
//        }
//        mIsLoading = false;
//        if (pull){
//            mCurPage = 2;
//            mList.getFsdLayout().setDragList(getContext(),(ArrayList<WallBlock>)entities);
//        }else {
//            mCurPage++;
//            mList.getFsdLayout().addList((ArrayList<WallBlock>) entities);
//        }
//    }
//
//    @Override
//    public void onChangeSuccess(Object entities) {
//
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        mPresenter.release();
//    }
//}
