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
import com.moemoe.lalala.utils.TagUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.CreateRichDocActivity;
import com.moemoe.lalala.view.activity.FileMovieActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.activity.NewDocDetailActivity;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
import com.moemoe.lalala.view.activity.NewFolderActivity;
import com.moemoe.lalala.view.activity.NewFolderEditActivity;
import com.moemoe.lalala.view.activity.NewFolderWenZhangActivity;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.moemoe.lalala.utils.StartActivityConstant.REQUEST_CODE_CREATE_DOC;

/**
 *
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
            if(entity.getNum() == 0){
                mainRoot.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                mainRoot.setVisibility(View.GONE);
            }else {
                mainRoot.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                mainRoot.setVisibility(View.VISIBLE);
            }
        }

        setText(R.id.tv_title,entity.getTitle());
        setText(R.id.tv_num,entity.getNum() + "项");
        llRoot.setVisibility(View.GONE);
        llRoot.removeAllViews();

        if(position == 6){
            llRoot.setOrientation(LinearLayout.VERTICAL);
            llRoot.setPadding(0,0,0,0);
        }else {
            llRoot.setOrientation(LinearLayout.HORIZONTAL);
            int padingH = getResources().getDimensionPixelSize(R.dimen.x24);
            int padingV = getResources().getDimensionPixelSize(R.dimen.y36);
            llRoot.setPadding(padingH,0,padingH,padingV);
        }

        if(entity.getNum() > 0){
            llRoot.setVisibility(View.VISIBLE);
            tvMore.setText("显示全部");
            tvMore.setCompoundDrawablesWithIntrinsicBounds (null,null, ContextCompat.getDrawable(itemView.getContext(),R.drawable.ic_bag_more),null);
            tvMore.setCompoundDrawablePadding(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.x8));
            tvMore.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(position == 0){
                        NewFolderActivity.startActivity(itemView.getContext(),userId, FolderType.ZH.toString(),type);
                    }else if(position == 1){
                        NewFolderActivity.startActivity(itemView.getContext(),userId, FolderType.TJ.toString(),type);
                    }else if(position == 2){
                        NewFolderActivity.startActivity(itemView.getContext(),userId, FolderType.MH.toString(),type);
                    }else if(position == 3){
                        NewFolderActivity.startActivity(itemView.getContext(),userId, FolderType.XS.toString(),type);
                    }else if(position == 4){
                        NewFolderActivity.startActivity(itemView.getContext(),userId, FolderType.SP.toString(),type);
                    }else if(position == 5){
                        NewFolderActivity.startActivity(itemView.getContext(),userId, FolderType.YY.toString(),type);
                    }else if(position == 6){
                        NewFolderWenZhangActivity.startActivity(itemView.getContext(),userId, FolderType.WZ.toString(),type,false);
                    }
                }
            });
            for (int n = 0;n < entity.getItems().size();n++){
                final ShowFolderEntity item = entity.getItems().get(n);
                View v;
                if(position != 6){
                    v = View.inflate(context,R.layout.item_feed_type_4_v3,null);
                }else {
                    v = View.inflate(context,R.layout.item_feed_type_1_v3,null);
                }

                ImageView ivCover = v.findViewById(R.id.iv_cover);
                TextView tvTitle = v.findViewById(R.id.tv_title);
                ImageView ivUserAvatar = v.findViewById(R.id.iv_user_avatar);
                TextView tvUserName = v.findViewById(R.id.tv_user_name);
                View userRoot = v.findViewById(R.id.ll_user_root);
                TextView tvTag1 = v.findViewById(R.id.tv_tag_1);
                TextView tvTag2 = v.findViewById(R.id.tv_tag_2);

                int w,h;
                if(position == 6){
                    View viewStep = v.findViewById(R.id.view_step);
                    v.setPadding(0,0,0,0);
                    viewStep.setVisibility(View.GONE);
                    w = h = getResources().getDimensionPixelSize(R.dimen.y180);
                    Glide.with(context)
                            .load(StringUtils.getUrl(context,item.getCover(),w,h,false,true))
                            .error(R.drawable.bg_default_square)
                            .placeholder(R.drawable.bg_default_square)
                            .bitmapTransform(new CropSquareTransformation(context))
                            .into(ivCover);

                    TextView tvExtraContent = v.findViewById(R.id.tv_extra_content);
                    tvExtraContent.setText("阅读 " + item.getPlayNum() + " · " + StringUtils.timeFormat(item.getTime()));
                }else {
                    w = (DensityUtil.getScreenWidth(context) - getResources().getDimensionPixelSize(R.dimen.x84)) / 3;
                    h = getResources().getDimensionPixelSize(R.dimen.y290);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w,h);
                    RecyclerView.LayoutParams lp2;
                    if(n == 1 || n == 2){
                        lp2 = new RecyclerView.LayoutParams(w + getResources().getDimensionPixelSize(R.dimen.x18), ViewGroup.LayoutParams.WRAP_CONTENT);
                        v.setPadding(getResources().getDimensionPixelSize(R.dimen.x18),0,0,0);
                    }else {
                        lp2 = new RecyclerView.LayoutParams(w,ViewGroup.LayoutParams.WRAP_CONTENT);
                        v.setPadding(0,0,0,0);
                    }
                    v.setLayoutParams(lp2);
                    ivCover.setLayoutParams(lp);
                    Glide.with(context)
                            .load(StringUtils.getUrl(context,item.getCover(),w,h, false, true))
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .bitmapTransform(new CropTransformation(context,w,h),new RoundedCornersTransformation(context,getResources().getDimensionPixelSize(R.dimen.y8),0))
                            .into(ivCover);

                    TextView tvMark = v.findViewById(R.id.tv_mark);

                    if(item.getType().equals(FolderType.ZH.toString())){
                        tvMark.setText("综合");
                        tvMark.setBackgroundResource(R.drawable.shape_rect_zonghe);
                    }else if(item.getType().equals(FolderType.TJ.toString())){
                        tvMark.setText("图集");
                        tvMark.setBackgroundResource(R.drawable.shape_rect_tuji);
                    }else if(item.getType().equals(FolderType.MH.toString())){
                        tvMark.setText("漫画");
                        tvMark.setBackgroundResource(R.drawable.shape_rect_manhua);
                    }else if(item.getType().equals(FolderType.XS.toString())){
                        tvMark.setText("小说");
                        tvMark.setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
                    }else if(item.getType().equals(FolderType.WZ.toString())){
                        tvMark.setText("文章");
                        tvMark.setBackgroundResource(R.drawable.shape_rect_zonghe);
                    }else if(item.getType().equals(FolderType.SP.toString())){
                        tvMark.setText("视频集");
                        tvMark.setBackgroundResource(R.drawable.shape_rect_shipin);
                    }else if(item.getType().equals(FolderType.YY.toString())){
                        tvMark.setText("音乐集");
                        tvMark.setBackgroundResource(R.drawable.shape_rect_shipin);
                    }else {
                        tvMark.setVisibility(View.GONE);
                    }

                    TextView tvCoin = v.findViewById(R.id.tv_coin);
                    if(item.getCoin() == 0){
                        tvCoin.setText("免费");
                    }else {
                        tvCoin.setText(item.getCoin() + "节操");
                    }

                    TextView tvBagNum = v.findViewById(R.id.tv_bag_num);
                    tvBagNum.setText(item.getItems() + "项");
                }
                tvTitle.setText(item.getFolderName());

                int size = getResources().getDimensionPixelSize(R.dimen.y32);
                ivUserAvatar.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(StringUtils.getUrl(context,item.getUserIcon(),size,size,false,true))
                        .error(R.drawable.bg_default_circle)
                        .placeholder(R.drawable.bg_default_circle)
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(ivUserAvatar);
                tvUserName.setVisibility(View.VISIBLE);
                tvUserName.setText(item.getCreateUserName());
                userRoot.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        ViewUtils.toPersonal(context,item.getCreateUser());
                    }
                });

                //tag
                View[] tags = {tvTag1,tvTag2};
                if(item.getTextsV2().size() > 1){
                    tags[0].setVisibility(View.VISIBLE);
                    tags[1].setVisibility(View.VISIBLE);
                }else if(item.getTextsV2().size() > 0){
                    tags[0].setVisibility(View.VISIBLE);
                    tags[1].setVisibility(View.INVISIBLE);
                }else {
                    tags[0].setVisibility(View.INVISIBLE);
                    tags[1].setVisibility(View.INVISIBLE);
                }
                int size1 = tags.length > item.getTextsV2().size() ? item.getTextsV2().size() : tags.length;
                for (int i = 0;i < size1;i++){
                    TagUtils.setBackGround(item.getTextsV2().get(i).getText(),tags[i]);
                    tags[i].setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            //TODO 跳转标签页
                        }
                    });
                }
                v.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(item.getType().equals(FolderType.ZH.toString())){
                            NewFileCommonActivity.startActivity(context, FolderType.ZH.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.TJ.toString())){
                            NewFileCommonActivity.startActivity(context,FolderType.TJ.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.MH.toString())){
                            NewFileManHuaActivity.startActivity(context,FolderType.MH.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.XS.toString())){
                            NewFileXiaoshuoActivity.startActivity(context,FolderType.XS.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.WZ.toString())){
                            Intent i = new Intent(itemView.getContext(), NewDocDetailActivity.class);
                            i.putExtra("uuid",item.getFolderId());
                            itemView.getContext().startActivity(i);
                        }else if(item.getType().equals(FolderType.SP.toString())){
                            FileMovieActivity.startActivity(context, FolderType.SP.toString(),item.getFolderId(),item.getCreateUser());
                        }else if(item.getType().equals(FolderType.YY.toString())){
                            FileMovieActivity.startActivity(context, FolderType.YY.toString(),item.getFolderId(),item.getCreateUser());
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
                tvMore.setCompoundDrawablePadding(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.x8));
                tvMore.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(position == 0){
                            NewFolderEditActivity.startActivityForResult(itemView.getContext(),"create",FolderType.ZH.toString(),null);
                        }else if(position == 1){
                            NewFolderEditActivity.startActivityForResult(itemView.getContext(),"create",FolderType.TJ.toString(),null);
                        }else if(position == 2){
                            NewFolderEditActivity.startActivityForResult(itemView.getContext(),"create",FolderType.MH.toString(),null);
                        }else if(position == 3){
                            NewFolderEditActivity.startActivityForResult(itemView.getContext(),"create",FolderType.XS.toString(),null);
                        }else if(position == 4){
                            NewFolderEditActivity.startActivityForResult(itemView.getContext(),"create",FolderType.SP.toString(),null);
                        }else if(position == 5){
                            NewFolderEditActivity.startActivityForResult(itemView.getContext(),"create",FolderType.YY.toString(),null);
                        }else if(position == 6){
                            Intent intent = new Intent(itemView.getContext(), CreateRichDocActivity.class);
                            intent.putExtra(CreateRichDocActivity.TYPE_QIU_MING_SHAN,3);
                            intent.putExtra(CreateRichDocActivity.TYPE_TAG_NAME_DEFAULT,"书包");
                            intent.putExtra("from_name","书包");
                            intent.putExtra("from_schema","neta://com.moemoe.lalala/bag_1.0");
                            ((BaseAppCompatActivity)itemView.getContext()).startActivityForResult(intent, REQUEST_CODE_CREATE_DOC);
                        }
                    }
                });
            }else {
                tvMore.setVisibility(View.GONE);
            }
        }
    }
}
