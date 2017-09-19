package com.moemoe.lalala.view.adapter;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.BagMyEntity;
import com.moemoe.lalala.model.entity.BagMyShowEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.CreateRichDocActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
import com.moemoe.lalala.view.activity.NewFolderActivity;
import com.moemoe.lalala.view.activity.NewFolderEditActivity;
import com.moemoe.lalala.view.activity.NewFolderWenZhangActivity;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.moemoe.lalala.utils.StartActivityConstant.REQUEST_CODE_CREATE_DOC;

/**
 * Created by yi on 2017/7/21.
 */

public class BagMyViewHolder extends ClickableViewHolder {

    TextView tvMore;
    LinearLayout llRoot;
    View mainRoot;

    public BagMyViewHolder(View itemView) {
        super(itemView);
        tvMore = $(R.id.tv_more_add);
        llRoot = $(R.id.ll_folder_root);
        mainRoot = $(R.id.ll_root);
    }

    public void createItem(final BagMyShowEntity entity, final int position, final String userId, final String type){
        if(!userId.equals(PreferenceUtils.getUUid())){
            if(position != 4){
                if (entity.getItems().size() == 0){
                    mainRoot.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                    mainRoot.setVisibility(View.GONE);
                }else {
                    mainRoot.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                    mainRoot.setVisibility(View.VISIBLE);
                }
            }else {
                if(entity.getNum() == 0){
                    mainRoot.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                    mainRoot.setVisibility(View.GONE);
                }else {
                    mainRoot.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                    mainRoot.setVisibility(View.VISIBLE);
                }
            }
        }
        setText(R.id.tv_title,entity.getTitle());
        setText(R.id.tv_num,entity.getNum() + "项");
        llRoot.setVisibility(View.GONE);
        llRoot.removeAllViews();
        if(position == 0 || position == 1 || position == 2 || position == 3){
            if(entity.getNum() > 0){
                llRoot.setVisibility(View.VISIBLE);
                tvMore.setText("显示全部");
                tvMore.setCompoundDrawablesWithIntrinsicBounds (null,null, ContextCompat.getDrawable(itemView.getContext(),R.drawable.ic_bag_more),null);
                tvMore.setCompoundDrawablePadding(DensityUtil.dip2px(itemView.getContext(),4));
                tvMore.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(position == 0){
                            NewFolderActivity.startActivity(itemView.getContext(),userId, FolderType.ZH.toString(),type);
                        }else if(position == 1){
                            NewFolderActivity.startActivity(itemView.getContext(),userId, FolderType.TJ.toString(),type);
                        }else if(position == 2){
                            NewFolderActivity.startActivity(itemView.getContext(),userId, FolderType.MH.toString(),type);
                        }else {
                            NewFolderActivity.startActivity(itemView.getContext(),userId, FolderType.XS.toString(),type);
                        }
                    }
                });
                for (int n = 0;n < entity.getItems().size();n++){
                    final ShowFolderEntity item = entity.getItems().get(n);
                    View v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_bag_cover, null);
                    ImageView iv = (ImageView) v.findViewById(R.id.iv_cover);
                    TextView mark = (TextView) v.findViewById(R.id.tv_mark);
                    TextView title = (TextView) v.findViewById(R.id.tv_title);
                    TextView tag = (TextView) v.findViewById(R.id.tv_tag);
                    title.setText(item.getFolderName());
                    String tagStr = "";
                    for(int i = 0;i < item.getTexts().size();i++){
                        String tagTmp = item.getTexts().get(i);
                        if(i == 0){
                            tagStr = tagTmp;
                        }else {
                            tagStr += " · " + tagTmp;
                        }
                    }
                    tag.setText(tagStr);
                    mark.setText(entity.getTitle());
                    mark.setBackgroundResource(entity.getMark());
                    int width = (DensityUtil.getScreenWidth(itemView.getContext()) - DensityUtil.dip2px(itemView.getContext(),42)) / 3;
                    int height = DensityUtil.dip2px(itemView.getContext(),140);

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,height);
                    RecyclerView.LayoutParams lp2;
                    if(n == 1 || n == 2){
                        lp2 = new RecyclerView.LayoutParams(width + DensityUtil.dip2px(itemView.getContext(),9),height);
                        v.setPadding(DensityUtil.dip2px(itemView.getContext(),9),0,0,0);
                    }else {
                        lp2 = new RecyclerView.LayoutParams(width,height);
                        v.setPadding(0,0,0,0);
                    }
                    v.setLayoutParams(lp2);
                    iv.setLayoutParams(lp);
                    Glide.with(itemView.getContext())
                            .load(StringUtils.getUrl(itemView.getContext(),item.getCover(),width,height, false, true))
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .bitmapTransform(new CropTransformation(itemView.getContext(),width,height),new RoundedCornersTransformation(itemView.getContext(),DensityUtil.dip2px(itemView.getContext(),4),0))
                            .into(iv);
                    v.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            if(position == 0){
                                NewFileCommonActivity.startActivity(itemView.getContext(),FolderType.ZH.toString(),item.getFolderId(),item.getCreateUser());
                            }else if(position == 1){
                                NewFileCommonActivity.startActivity(itemView.getContext(),FolderType.TJ.toString(),item.getFolderId(),item.getCreateUser());
                            }else if(position == 2){
                                NewFileManHuaActivity.startActivity(itemView.getContext(),FolderType.MH.toString(),item.getFolderId(),item.getCreateUser());
                            }else {
                                NewFileXiaoshuoActivity.startActivity(itemView.getContext(),FolderType.XS.toString(),item.getFolderId(),item.getCreateUser());
                            }
                        }
                    });
                    llRoot.addView(v);
                }
            }else {
                llRoot.setVisibility(View.GONE);
                if(type.equals("my")){
                    tvMore.setText("添加");
                    tvMore.setCompoundDrawablesWithIntrinsicBounds (null,null, ContextCompat.getDrawable(itemView.getContext(),R.drawable.ic_bag_add),null);
                    tvMore.setCompoundDrawablePadding(DensityUtil.dip2px(itemView.getContext(),4));
                    tvMore.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            if(position == 0){
                                NewFolderEditActivity.startActivityForResult(itemView.getContext(),"create",FolderType.ZH.toString(),null);
                            }else if(position == 1){
                                NewFolderEditActivity.startActivityForResult(itemView.getContext(),"create",FolderType.TJ.toString(),null);
                            }else if(position == 2){
                                NewFolderEditActivity.startActivityForResult(itemView.getContext(),"create",FolderType.MH.toString(),null);
                            }else {
                                NewFolderEditActivity.startActivityForResult(itemView.getContext(),"create",FolderType.XS.toString(),null);
                            }

                        }
                    });
                }else {
                    tvMore.setVisibility(View.GONE);
                }
            }
        }else if(position == 4){
            if(entity.getNum() > 0){
                if(type.equals("my")){
                    tvMore.setText("显示全部");
                    tvMore.setCompoundDrawablesWithIntrinsicBounds (null,null, ContextCompat.getDrawable(itemView.getContext(),R.drawable.ic_bag_more),null);
                    tvMore.setCompoundDrawablePadding(DensityUtil.dip2px(itemView.getContext(),4));
                    tvMore.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            NewFolderWenZhangActivity.startActivity(itemView.getContext(),userId, FolderType.WZ.toString());
                        }
                    });
                }else {
                    tvMore.setVisibility(View.GONE);
                }
            }else {
                if(type.equals("my")){
                    tvMore.setText("添加");
                    tvMore.setCompoundDrawablesWithIntrinsicBounds (null,null, ContextCompat.getDrawable(itemView.getContext(),R.drawable.ic_bag_add),null);
                    tvMore.setCompoundDrawablePadding(DensityUtil.dip2px(itemView.getContext(),4));
                    tvMore.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            Intent intent = new Intent(itemView.getContext(), CreateRichDocActivity.class);
                            intent.putExtra(CreateRichDocActivity.TYPE_QIU_MING_SHAN,3);
                            intent.putExtra(CreateRichDocActivity.TYPE_TAG_NAME_DEFAULT,"书包");
                            intent.putExtra("from_name","书包");
                            intent.putExtra("from_schema","neta://com.moemoe.lalala/bag_1.0");
                            ((BaseAppCompatActivity)itemView.getContext()).startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
                        }
                    });
                }else {
                    tvMore.setVisibility(View.GONE);
                }
            }
        }
    }
}
