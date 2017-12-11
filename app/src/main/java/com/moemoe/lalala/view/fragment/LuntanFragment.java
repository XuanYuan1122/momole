package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerLuntan2Component;
import com.moemoe.lalala.di.modules.Luntan2Module;
import com.moemoe.lalala.model.entity.DepartmentGroupEntity;
import com.moemoe.lalala.model.entity.DocResponse;
import com.moemoe.lalala.presenter.Luntan2Contract;
import com.moemoe.lalala.presenter.Luntan2Presenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.MenuVItemDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.CreateRichDocActivity;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;
import com.moemoe.lalala.view.adapter.OldDocAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import io.rong.imlib.model.Conversation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.app.Activity.RESULT_OK;
import static com.moemoe.lalala.utils.StartActivityConstant.REQUEST_CODE_CREATE_DOC;

/**
 *
 * Created by yi on 2017/9/4.
 */

public class LuntanFragment extends BaseFragment implements Luntan2Contract.View{

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_to_wen)
    ImageView mCreateDoc;

    @Inject
    Luntan2Presenter mPresenter;
    private View groupView;
    private OldDocAdapter mAdapter;
    private boolean isLoading = false;
    private String id;

    public static LuntanFragment newInstance(String id,String name,boolean isCanDoc){
        LuntanFragment fragment = new LuntanFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("name",name);
        bundle.putBoolean("isCanDoc",isCanDoc);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerLuntan2Component.builder()
                .luntan2Module(new Luntan2Module(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        id = getArguments().getString("id");
        final String name = getArguments().getString("name");
        boolean canDoc = getArguments().getBoolean("isCanDoc");
        if(canDoc){
            mCreateDoc.setImageResource(R.drawable.btn_send_wen);
            mCreateDoc.setVisibility(View.VISIBLE);
        }
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.bg_f6f6f6));
        mAdapter= new OldDocAdapter();
        mListDocs.getRecyclerView().addItemDecoration(new MenuVItemDecoration((int) getResources().getDimension(R.dimen.y24)));
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mCreateDoc.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent intent = new Intent(getContext(), CreateRichDocActivity.class);
                intent.putExtra(CreateRichDocActivity.TYPE_QIU_MING_SHAN,4);
                intent.putExtra(CreateRichDocActivity.TYPE_TAG_NAME_DEFAULT,name);
                intent.putExtra("departmentId",id);
                intent.putExtra("from_name","");
                intent.putExtra("from_schema","");
                startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DocResponse docResponse = mAdapter.getItem(position);
                Intent i = new Intent(getContext(),NewDocDetailActivity.class);
                i.putExtra("uuid",docResponse.getId());
                startActivity(i);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                if(mAdapter.getList().size() == 0){
                    mPresenter.loadDocList(id,0);
                }else {
                    mPresenter.loadDocList(id,mAdapter.getItem(mAdapter.getList().size() - 1).getTimestamp());
                }
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadDocList(id,0);
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
        mPresenter.loadDepartmentGroup(id);
        mPresenter.loadDocList(id,0);
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
        ErrorCodeUtils.showErrorMsgByCode(getContext(),code,msg);
    }

    public void release(){
        if(mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_CREATE_DOC && resultCode == RESULT_OK){
            mPresenter.loadDocList(id,0);
        }
    }

    @Override
    public void onLoadGroupSuccess(ArrayList<DepartmentGroupEntity> entities) {
        if(entities.size() > 0){
            if(groupView == null){
                groupView = LayoutInflater.from(getContext()).inflate(R.layout.item_department_group, null);
                mAdapter.addHeaderView(groupView);
            }
            final DepartmentGroupEntity entity = entities.get(0);
            ImageView cover = groupView.findViewById(R.id.iv_group_img);
            TextView title = groupView.findViewById(R.id.tv_group_name);
            TextView num = groupView.findViewById(R.id.tv_group_num);
            ImageView addGroup = groupView.findViewById(R.id.iv_add_group);

            int size = (int) getResources().getDimension(R.dimen.y80);
            Glide.with(getContext())
                    .load(StringUtils.getUrl(getContext(),entity.getCover(),size,size,false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new RoundedCornersTransformation(getContext(), (int) getResources().getDimension(R.dimen.y8),0))
                    .into(cover);
            title.setText(entity.getGroupName());
            num.setText(entity.getUsers() + " 人");
            addGroup.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(entity.isJoin()){
                        Uri uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
                                .appendPath("conversation").appendPath(Conversation.ConversationType.GROUP.getName())
                                .appendQueryParameter("targetId",entity.getId())
                                .appendQueryParameter("title", entity.getGroupName()).build();
                        IntentUtils.toActivityFromUri(getContext(),uri,null);
                    }else {
                        if(entity.isAuthority()){
                            mPresenter.joinAuthor(entity.getId(),entity.getGroupName());
                        }else {
                            Uri uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
                                    .appendPath("conversation").appendPath("detail")
                                    .appendQueryParameter("targetId",entity.getId())
                                    .appendQueryParameter("title", entity.getGroupName()).build();
                            IntentUtils.toActivityFromUri(getContext(),uri,null);
                        }
                    }
                }
            });
        }else {
            if(groupView != null){
                mAdapter.removeHeaderView(groupView);
                groupView = null;
            }
        }
    }

    @Override
    public void onLoadDocListSuccess(ArrayList<DocResponse> responses, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        mListDocs.setLoadMoreEnabled(true);
        if(isPull){
            mAdapter.setList(responses);
        }else {
            mAdapter.addList(responses);
        }
    }

    @Override
    public void onJoinSuccess(String id,String name) {
        Uri uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(Conversation.ConversationType.GROUP.getName())
                .appendQueryParameter("targetId", id)
                .appendQueryParameter("title", name).build();
        IntentUtils.toActivityFromUri(getContext(),uri,null);
    }

}
