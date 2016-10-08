package com.moemoe.lalala;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.moemoe.lalala.data.Live2dModelItem;
import com.moemoe.lalala.galgame.Live2DDefine;
import com.moemoe.lalala.utils.ToastUtil;
import com.moemoe.lalala.view.NoDoubleClickListener;

import java.util.ArrayList;

/**
 * Created by yi on 2016/9/27.
 */
@ContentView(R.layout.ac_select_fuku)
public class SelectFukuActivity extends BaseActivity {

    public static final int RES_OK = 200;

    @FindView(R.id.toolbar)
    private Toolbar mToolbar;
    @FindView(R.id.tv_menu)
    private TextView mTvDone;
    @FindView(R.id.rv_list)
    private RecyclerView mRvList;

    private String mModel;
    private ArrayList<Live2dModelItem> mItems;
    private int mLevel;

    @Override
    protected void initView() {
        mToolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mModel = mPreferMng.getSelectFuku();
        mTvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("model",mModel);
                setResult(RES_OK,i);
                finish();
            }
        });
        initData();
        mLevel = mPreferMng.getThirdPartyLoginMsg().getLevel();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRvList.setLayoutManager(manager);
        mRvList.setAdapter(new FukuSelectAdapter());
    }

    private void initData(){
        Live2dModelItem item1 = new Live2dModelItem();
        item1.fuku = R.drawable.icon_gal_len_uniform;
        item1.name = "校服";
        item1.info = "Neta学院对校服的要求很宽松，这是莲的秋季款";
        item1.reqLevel = 0;
        item1.model = Live2DDefine.MODEL_LEN;
        Live2dModelItem item2= new Live2dModelItem();
        item2.fuku = R.drawable.icon_gal_len_swim;
        item2.name = "死库水";
        item2.info = "莲的死库水，千世按照贫乳定制的，但好像有点紧了…";
        item2.reqLevel = 5;
        item2.model = Live2DDefine.MODEL_LEN_SWIN;
        mItems = new ArrayList<>();
        mItems.add(item1);
        mItems.add(item2);
    }

    private class FukuSelectAdapter extends RecyclerView.Adapter<FukuSelectAdapter.ItemViewHolder>{

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(SelectFukuActivity.this).inflate(R.layout.item_fuku,parent,false));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, final int position) {
            final Live2dModelItem item = mItems.get(position);
            holder.name.setText(item.name);
            holder.info.setText(item.info);
            holder.req.setText(getString(R.string.label_fuku_req,item.reqLevel));
            holder.fuku.setImageResource(item.fuku);
            if(item.model.equals(Live2DDefine.MODEL_LEN)){
                holder.req.setVisibility(View.GONE);
            }else {
                holder.req.setVisibility(View.VISIBLE);
            }
            if(item.model.equals(mModel)){
                holder.root.setBackgroundResource(R.drawable.bg_rect_corner_cyan);
                holder.select.setImageResource(R.drawable.ic_select_hover);
            }else {
                holder.root.setBackgroundResource(R.drawable.bg_rect_corner_gray);
                holder.select.setImageResource(R.drawable.ic_select_normal);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mLevel >= item.reqLevel){
                        mPreferMng.saveSelectFuku(item.model);
                        mModel = item.model;
                        notifyDataSetChanged();
                    }else {
                        ToastUtil.showCenterToast(SelectFukuActivity.this,"快去升级吧", Toast.LENGTH_SHORT);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder{

            public TextView name,info,req;
            public ImageView fuku,select;
            public RelativeLayout root;

            public ItemViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.tv_fuku_name);
                info = (TextView) itemView.findViewById(R.id.tv_fuku_info);
                req = (TextView) itemView.findViewById(R.id.tv_fuku_req);
                fuku = (ImageView) itemView.findViewById(R.id.iv_fuku);
                select = (ImageView) itemView.findViewById(R.id.iv_select);
                root = (RelativeLayout) itemView.findViewById(R.id.rl_root);
            }
        }
    }
}
