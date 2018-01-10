package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerCoinShopComponent;
import com.moemoe.lalala.di.modules.CoinShopModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CoinShopEntity;
import com.moemoe.lalala.model.entity.OrderEntity;
import com.moemoe.lalala.presenter.CoinShopContract;
import com.moemoe.lalala.presenter.CoinShopPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.CoinShopAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.pingplusplus.android.Pingpp;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * 商品列表
 * Created by yi on 2017/6/26.
 */

public class CoinShopActivity extends BaseAppCompatActivity implements CoinShopContract.View{

    @BindView(R.id.ll_root)
    LinearLayout mLlRoot;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;
    @BindView(R.id.rv_list)
    PullAndLoadView mListDocs;

    @Inject
    CoinShopPresenter mPresenter;

    private CoinShopAdapter mAdapter;
    private boolean isLoading = false;
    private String productId = "";
    private boolean isPaying = false;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_bar_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerCoinShopComponent.builder()
                .coinShopModule(new CoinShopModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        //top time
//        TextView tvTop = new TextView(this);
//        tvTop.setTextColor(ContextCompat.getColor(this,R.color.white));
//        tvTop.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
//        tvTop.setBackgroundColor(ContextCompat.getColor(this,R.color.main_cyan));
//        tvTop.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this,45)));
//        tvTop.setGravity(Gravity.CENTER);
//        tvTop.setText("[50元 话费充值] 刷新还有: 17小时");
//        mLlRoot.addView(tvTop,2);
        //
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.getRecyclerView().setHasFixedSize(true);
        mAdapter = new CoinShopAdapter(this);
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
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTitle.setTextColor(ContextCompat.getColor(this,R.color.main_cyan));
        mTitle.setText(getString(R.string.label_coin_shop));
        mIvMenu.setVisibility(View.VISIBLE);
        final BottomMenuFragment bottomFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(0, getString(R.string.label_bag_buy_list));
        items.add(item);
        bottomFragment.setShowTop(false);
        bottomFragment.setMenuItems(items);
        bottomFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if(itemId == 0){
                    Intent i = new Intent(CoinShopActivity.this, OrderListActivity.class);
                    startActivity(i);
                }
            }
        });
        mIvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomFragment.show(getSupportFragmentManager(),"BagMenu");
            }
        });
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(CoinShopActivity.this, ShopDetailActivity.class);
                i.putExtra("shop_detail", mAdapter.getItem(position));
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
                mPresenter.loadShopList(mAdapter.getList().size());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadShopList(0);
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
        mPresenter.loadShopList(0);
    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this, code, msg);
    }

    @Override
    public void onLoadShopListSuccess(ArrayList<CoinShopEntity> list, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if(list.size() >= ApiService.LENGHT){
            mListDocs.setLoadMoreEnabled(true);
        }else {
            mListDocs.setLoadMoreEnabled(false);
        }
        if(isPull){
            mAdapter.setList(list);
        }else {
            mAdapter.addList(list);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == REQ_GET_FROM_SELECT_BOOK && resultCode == RESULT_OK){
//            if(data != null){
//                BookInfo entity = data.getParcelableExtra(SelectBookActivity.EXTRA_SELECT_BOOK);
//                BufferedReader reader = null;
//                try {
//                    reader = new BufferedReader(new FileReader(new File(entity.getPath())));
//                    ArrayList<String> list = new ArrayList<>();
//                    String tmp = "";
//                    while ((tmp = reader.readLine()) != null){
//                        list.add(tmp);
//                    }
//                    OrderTmp orderTmp = new OrderTmp(productId,list);
//                    mPresenter.createOrderList(orderTmp);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }finally {
//                    if(reader != null){
//                        try {
//                            reader.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }else if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
//            if (resultCode == Activity.RESULT_OK) {
//                String result = data.getExtras().getString("pay_result");
//                /* 处理返回值
//                 * "success" - payment succeed
//                 * "fail"    - payment failed
//                 * "cancel"  - user canceld
//                 * "invalid" - payment plugin not installed
//                 */
//                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
//                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
//                finalizeDialog();
//                if("success".equals(result)){
//                    showToast("支付成功");
//                }else {
//                    showToast(errorMsg);
//                }
//                curOrder++;
//                mHander.post(mProgressCallback);
//            }
//        }
//    }

    public void createOrder(final CoinShopEntity entity){
        //mPresenter.createOrder(entity);

//        productId = entity.getId();
//        Intent i = new Intent(this,SelectBookActivity.class);
//        startActivityForResult(i,REQ_GET_FROM_SELECT_BOOK);

        if(entity.getBuyLimit() == 1){
            mPresenter.createOrder(entity);
        }else {
            final AlertDialogUtil dialogUtil = AlertDialogUtil.getInstance();
            dialogUtil.createEditDialog(this, entity.getBuyLimit(),2);
            dialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                @Override
                public void CancelOnClick() {
                    dialogUtil.dismissDialog();
                }

                @Override
                public void ConfirmOnClick() {
                    String content = dialogUtil.getEditTextContent();
                    try {
                        if(!TextUtils.isEmpty(content) && Integer.valueOf(content) > 0){
                            if(entity.getBuyLimit() != 0 && Integer.valueOf(content) > entity.getBuyLimit()){
                                showToast("超过购买限制");
                            }else {
                                mPresenter.createOrder(entity,Integer.valueOf(content));
                                dialogUtil.dismissDialog();
                            }
                        }else {
                            showToast(R.string.msg_input_err_coin);
                        }
                    }catch (Exception e){
                        showToast(R.string.msg_input_err_coin);
                    }
                }
            });
            dialogUtil.showDialog();
        }
    }

    @Override
    public void onCreateOrderSuccess(OrderEntity entity) {
        Intent i = new Intent(this,OrderActivity.class);
        i.putExtra("order",entity);
        i.putExtra("show_top",true);
        i.putExtra("show_status",false);
        startActivity(i);
    }

    private ArrayList<JsonObject> jsonObjects = new ArrayList<>();
    private int curOrder = 0;

    private Handler mHander = new Handler();
    private Runnable mProgressCallback = new Runnable(){

        @Override
        public void run() {
            if(curOrder < jsonObjects.size()) Pingpp.createPayment(CoinShopActivity.this, jsonObjects.get(curOrder).toString());
        }
    };

    @Override
    public void onCreateOrderListSuccess(ArrayList<JsonObject> jsonObjects) {
        this.jsonObjects = jsonObjects;
        mHander.post(mProgressCallback);
    }

    @Override
    public void onLoadShopDetailSuccess(CoinShopEntity entity) {

    }

}
