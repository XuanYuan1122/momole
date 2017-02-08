package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.CoinDetailEntity;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.PersonDocEntity;
import com.moemoe.lalala.model.entity.PersonFollowEntity;
import com.moemoe.lalala.model.entity.ReplyEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.GlideCircleTransform;
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.BadgeActivity;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.NewPersonalActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by yi on 2016/12/15.
 */

public class PersonListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int mType;
    private Context context;
    private ArrayList<Object> list;
    private OnItemClickListener onItemClickListener;
    private HashMap<Integer,BadgeEntity> mCurSelectNum;
    private boolean canDelete;

    public PersonListAdapter(Context context,int type){
        this.context = context;
        mType = type;
        list = new ArrayList<>();
        mCurSelectNum = new HashMap<>();
    }

    public int getSelectNum(){
        return mCurSelectNum.size();
    }

    public int increaseSelectNum(){
        if(!mCurSelectNum.containsKey(1)){
            return 1;
        }else if(!mCurSelectNum.containsKey(2)){
            return 2;
        }else{
            return 3;
        }
    }

    public void setCanDelete(boolean canDelete){
        this.canDelete = canDelete;
    }

    public HashMap<Integer,BadgeEntity> getCurSelectNum(){
        return mCurSelectNum;
    }

    public ArrayList<Object> getList(){
        return list;
    }

    public void decreaseSelectNum(int entity){
        mCurSelectNum.remove(entity);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(Collection list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addData(Collection list){
        int bfSize = getItemCount();
        this.list.addAll(list);
        notifyItemRangeInserted(bfSize,list.size());
    }

    public void addData(int position,Object list){
        this.list.add(position,list);
        notifyItemInserted(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         if(mType == 0){//doc favorite
             return new DocViewHolder(LayoutInflater.from(context).inflate(R.layout.item_person_doc,parent,false));
         }else if(mType == 1){//follow
             return new FollowViewHolder(LayoutInflater.from(context).inflate(R.layout.item_person_follow,parent,false));
         }else if(mType == 2){//msg
             return new MsgViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_new,parent,false));
         }else if(mType == 3){
             return new CoinViewHolder(LayoutInflater.from(context).inflate(R.layout.item_coin_detail,parent,false));
         }else if(mType == 4){
             return new CommentHolder(LayoutInflater.from(context).inflate(R.layout.item_post_comment,parent,false));
         }else if(mType == 5){
             return new BadgeHolder(LayoutInflater.from(context).inflate(R.layout.item_badge,parent,false));
         }else if(mType == 6){
             return new BadgeAllHolder(LayoutInflater.from(context).inflate(R.layout.item_all_badge,parent,false));
         }else if(mType == 7){
             return new BagFavoriteHolder(LayoutInflater.from(context).inflate(R.layout.item_bag_get,parent,false));
         }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(mType == 0){
            PersonDocEntity entity = (PersonDocEntity) getItem(position);
            DocViewHolder docViewHolder = (DocViewHolder) holder;
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + entity.getImage(), DensityUtil.dip2px(context,80),DensityUtil.dip2px(context,80),false,true))
                    .override(DensityUtil.dip2px(context,80),DensityUtil.dip2px(context,80))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .into(docViewHolder.img);
            docViewHolder.title.setText(entity.getTitle());
            docViewHolder.content.setText(entity.getDesc());
            docViewHolder.time.setText(entity.getCreateTime());
            // 点赞/评论
            docViewHolder.commentNum.setText(StringUtils.getNumberInLengthLimit(entity.getComments(), 3));
            docViewHolder.likeNum.setText(StringUtils.getNumberInLengthLimit(entity.getLikes(), 3));
        }else if(mType == 1){
            PersonFollowEntity entity = (PersonFollowEntity) getItem(position);
            FollowViewHolder followViewHolder = (FollowViewHolder) holder;
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + entity.getUserIcon(), DensityUtil.dip2px(context,50),DensityUtil.dip2px(context,50),false,true))
                    .override(DensityUtil.dip2px(context,50),DensityUtil.dip2px(context,50))
                    .transform(new GlideCircleTransform(context))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .into(followViewHolder.img);
            followViewHolder.levelBg.setBackgroundColor(StringUtils.readColorStr(entity.getUserLevelColor(), ContextCompat.getColor(context,R.color.main_cyan)));
            followViewHolder.level.setText(String.valueOf(entity.getUserLevel()));
            followViewHolder.level.setTextColor(StringUtils.readColorStr(entity.getUserLevelColor(),ContextCompat.getColor(context,R.color.main_cyan)));
            followViewHolder.name.setText(entity.getUserName());
            followViewHolder.content.setText(entity.getSignature());
        }else if(mType == 2){
            ReplyEntity bean = (ReplyEntity) getItem(position);
            MsgViewHolder msgViewHolder = (MsgViewHolder) holder;
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + bean.getFromIcon().getPath(), DensityUtil.dip2px(context,50), DensityUtil.dip2px(context,50),false,false))
                    .override(DensityUtil.dip2px(context,50), DensityUtil.dip2px(context,50))
                    .placeholder(R.drawable.bg_default_circle)
                    .error(R.drawable.bg_default_circle)
                    .transform(new GlideCircleTransform(context))
                    .into(msgViewHolder.ivAvatar);
            msgViewHolder.tvName.setText(bean.getFromName());
            msgViewHolder.tvDate.setText(bean.getCreateTime());
            msgViewHolder.tvContent.setText(bean.getContent());
        }else if(mType == 3){
            CoinDetailEntity entity = (CoinDetailEntity) getItem(position);
            CoinViewHolder coinViewHolder = (CoinViewHolder) holder;

            if(entity.getCoin() > 0){
                coinViewHolder.tvCoin.setText("+"+entity.getCoin() + "");
                coinViewHolder.tvCoin.setTextColor(ContextCompat.getColor(context,R.color.green_93d856));
                coinViewHolder.tvLabel.setTextColor(ContextCompat.getColor(context,R.color.green_93d856));
            }else {
                coinViewHolder.tvCoin.setText(entity.getCoin() + "");
                coinViewHolder.tvCoin.setTextColor(ContextCompat.getColor(context,R.color.pink_fb7ba2));
                coinViewHolder.tvLabel.setTextColor(ContextCompat.getColor(context,R.color.pink_fb7ba2));
            }
            coinViewHolder.tvType.setText(entity.getType());
            coinViewHolder.tvTime.setText(entity.getCreateTime());
        }else if(mType == 4){
            final NewCommentEntity commentEntity = (NewCommentEntity) getItem(position);
            final CommentHolder commentHolder = (CommentHolder) holder;
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + commentEntity.getFromUserIcon().getPath(), DensityUtil.dip2px(context,35), DensityUtil.dip2px(context,35), false, false))
                    .override(DensityUtil.dip2px(context,35), DensityUtil.dip2px(context,35))
                    .placeholder(R.drawable.bg_default_circle)
                    .error(R.drawable.bg_default_circle)
                    .transform(new GlideCircleTransform(context))
                    .into(commentHolder.ivCreator);
            commentHolder.tvCreatorName.setText(commentEntity.getFromUserName());
            commentHolder.tvTime.setText(StringUtils.timeFormate(commentEntity.getCreateTime()));
            if(commentEntity.isDeleteFlag()){
                commentHolder.tvContent.setText(context.getString(R.string.label_comment_already));
            }else {
                String comm;
                if (!TextUtils.isEmpty(commentEntity.getToUserName()) ) {
                    comm = "回复 " + (TextUtils.isEmpty(commentEntity.getToUserName()) ? "" :commentEntity.getToUserName()) + ": "
                            + commentEntity.getContent();
                } else {
                    comm = commentEntity.getContent();
                }
                commentHolder.tvContent.setText(StringUtils.getUrlClickableText(context, comm));
                commentHolder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());
            }
            commentHolder.ivOwnerFlag.setVisibility(View.GONE);
            commentHolder.tvLevel.setText(String.valueOf(commentEntity.getFromUserLevel()));
            int radius1 = DensityUtil.dip2px(context,5);
            float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
            RoundRectShape roundRectShape1 = new RoundRectShape(outerR1, null, null);
            ShapeDrawable shapeDrawable1 = new ShapeDrawable();
            shapeDrawable1.setShape(roundRectShape1);
            shapeDrawable1.getPaint().setStyle(Paint.Style.FILL);
            shapeDrawable1.getPaint().setColor(StringUtils.readColorStr(commentEntity.getFromUserLevelColor(), ContextCompat.getColor(context, R.color.main_cyan)));
            commentHolder.ivLevelColor.setBackgroundDrawable(shapeDrawable1);
            Observable.range(0,3)
                    .subscribe(new Subscriber<Integer>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Integer i) {
                            commentHolder.huiZhangTexts[i].setVisibility(View.INVISIBLE);
                            commentHolder.huiZhangRoots[i].setVisibility(View.INVISIBLE);
                        }
                    });
            if(commentEntity.getBadgeList().size() > 0){
                int size = 3;
                if(commentEntity.getBadgeList().size() < 3){
                    size = commentEntity.getBadgeList().size();
                }
                for (int i = 0;i < size;i++){
                    commentHolder.huiZhangTexts[i].setVisibility(View.VISIBLE);
                    commentHolder.huiZhangRoots[i].setVisibility(View.VISIBLE);
                    BadgeEntity badgeEntity = commentEntity.getBadgeList().get(i);
                    TextView tv = commentHolder.huiZhangTexts[i];
                    tv.setText(badgeEntity.getTitle());
                    tv.setText(badgeEntity.getTitle());
                    tv.setBackgroundResource(R.drawable.bg_badge_cover);
                    int px = DensityUtil.dip2px(context,4);
                    tv.setPadding(px,0,px,0);
                    int radius2 = DensityUtil.dip2px(context,2);
                    float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
                    RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
                    ShapeDrawable shapeDrawable2 = new ShapeDrawable();
                    shapeDrawable2.setShape(roundRectShape2);
                    shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
                    shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(context, R.color.main_cyan)));
                    commentHolder.huiZhangRoots[i].setBackgroundDrawable(shapeDrawable2);
                }
            }
            commentHolder.ivCreator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(commentEntity.getId()) && !commentEntity.getId().equals(PreferenceUtils.getUUid())) {
                        Intent i = new Intent(context,NewPersonalActivity.class);
                        i.putExtra(BaseAppCompatActivity.UUID,commentEntity.getId());
                        context.startActivity(i);
                    }
                }
            });
        }else if(mType == 5){
            BadgeEntity badgeEntity = (BadgeEntity) getItem(position);
            BadgeHolder badgeHolder = (BadgeHolder) holder;
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + badgeEntity.getImg(), DensityUtil.dip2px(context,80), DensityUtil.dip2px(context,80), false, false))
                    .override(DensityUtil.dip2px(context,80), DensityUtil.dip2px(context,80))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .into(badgeHolder.ivImg);
            badgeHolder.tvTitle.setText(badgeEntity.getName());
            badgeHolder.tvDesc.setText(badgeEntity.getDesc());
            badgeHolder.root.setBackgroundColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(context, R.color.main_cyan)));
            if(badgeEntity.getRank() > 0){
                badgeHolder.tvSelect.setSelected(true);
                badgeHolder.tvSelect.setText(badgeEntity.getRank() + "");
                mCurSelectNum.put(badgeEntity.getRank(),badgeEntity);
            }else {
                badgeHolder.tvSelect.setText("");
                badgeHolder.tvSelect.setSelected(false);
            }
        }else if(mType == 6){
            final BadgeEntity badgeEntity = (BadgeEntity) getItem(position);
            BadgeAllHolder badgeHolder = (BadgeAllHolder) holder;
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + badgeEntity.getImg(), DensityUtil.dip2px(context,80), DensityUtil.dip2px(context,80), false, true))
                    .override(DensityUtil.dip2px(context,80), DensityUtil.dip2px(context,80))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .into(badgeHolder.ivImg);
            badgeHolder.tvTitle.setText(badgeEntity.getName());
            badgeHolder.tvDesc.setText(badgeEntity.getDesc());
            badgeHolder.root.setBackgroundColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(context, R.color.main_cyan)));
            badgeHolder.tvHave.setVisibility(badgeEntity.isHave()?View.VISIBLE : View.GONE);
            badgeHolder.tvBuy.setVisibility(badgeEntity.isBuy() && !badgeEntity.isHave()?View.VISIBLE : View.GONE);
            badgeHolder.tvBuy.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ((BadgeActivity)context).buyBadge(position,badgeEntity.getId());
                }
            });
            if(badgeEntity.isBuy()){
                badgeHolder.tvCoin.setVisibility(View.VISIBLE);
                badgeHolder.tvCoin.setText("【" + badgeEntity.getCoin() + "节操】");
            }else {
                badgeHolder.tvCoin.setVisibility(View.GONE);
            }
        }else if(mType == 7){
            BagDirEntity entity = (BagDirEntity) getItem(position);
            BagFavoriteHolder bagFavoriteHolder = (BagFavoriteHolder) holder;
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + entity.getCover(), DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,20), DensityUtil.dip2px(context,120), false, true))
                    .override(DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,20), DensityUtil.dip2px(context,120))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .centerCrop()
                    .transform(new GlideRoundTransform(context,5))
                    .into(bagFavoriteHolder.ivBg);
            bagFavoriteHolder.ivSelect.setVisibility(canDelete?View.VISIBLE:View.GONE);
            bagFavoriteHolder.ivSelect.setSelected(entity.isSelect());
            bagFavoriteHolder.tvNum.setText(entity.getNumber() + "项");
            bagFavoriteHolder.tvName.setText(entity.getName());
            bagFavoriteHolder.tvTime.setText(entity.getUpdateTime() + " 更新");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(view,position);
                }
            }
        });
    }

    public Object getItem(int position){
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class DocViewHolder extends RecyclerView.ViewHolder{

        ImageView img;
        TextView title,content,time,likeNum,commentNum;
        DocViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.iv_img);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            content = (TextView) itemView.findViewById(R.id.tv_content);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            likeNum = (TextView) itemView.findViewById(R.id.tv_post_pants_num);
            commentNum = (TextView) itemView.findViewById(R.id.tv_post_comment_num);
        }
    }

    private class FollowViewHolder extends RecyclerView.ViewHolder{

        ImageView img,levelBg;
        TextView name,content,level;

        FollowViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.iv_img);
            levelBg = (ImageView) itemView.findViewById(R.id.iv_level_bg);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            content = (TextView) itemView.findViewById(R.id.tv_content);
            level = (TextView) itemView.findViewById(R.id.tv_level);
        }
    }

    private class MsgViewHolder extends RecyclerView.ViewHolder{
        ImageView ivAvatar;
        TextView tvName;
        TextView tvDate;
        TextView tvContent;
        TextView tvDocContent;
        TextView tvClubName;
        MsgViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_creator);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvDocContent = (TextView) itemView.findViewById(R.id.tv_doc_content);
            tvClubName = (TextView) itemView.findViewById(R.id.tv_club_name);
        }
    }

    private class CoinViewHolder extends RecyclerView.ViewHolder{

        TextView tvCoin,tvType,tvTime,tvLabel;

        CoinViewHolder(View itemView) {
            super(itemView);
            tvCoin = (TextView) itemView.findViewById(R.id.tv_coin);
            tvLabel = (TextView) itemView.findViewById(R.id.tv_label);
            tvType = (TextView) itemView.findViewById(R.id.tv_type);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

    private class CommentHolder extends RecyclerView.ViewHolder{

        ImageView ivCreator;
        TextView tvCreatorName;
        TextView tvTime;
        TextView tvContent;
        View ivOwnerFlag;
        View ivLevelColor;
        TextView tvLevel;
        View rlHuiZhang1;
        View rlHuiZhang2;
        View rlHuiZhang3;
        TextView tvHuiZhang1;
        TextView tvHuiZhang2;
        TextView tvHuiZhang3;
        View[] huiZhangRoots;
        TextView[] huiZhangTexts;

        CommentHolder(View itemView) {
            super(itemView);
            ivCreator = (ImageView) itemView.findViewById(R.id.iv_comment_creator);
            tvCreatorName = (TextView) itemView.findViewById(R.id.tv_comment_creator_name);
            tvTime = (TextView) itemView.findViewById(R.id.tv_comment_time);
            tvContent = (TextView) itemView.findViewById(R.id.tv_comment);
            ivOwnerFlag = itemView.findViewById(R.id.iv_club_owner_flag);
            ivLevelColor = itemView.findViewById(R.id.rl_level_bg);
            tvLevel = (TextView)itemView.findViewById(R.id.tv_level);
            tvHuiZhang1 = (TextView)itemView.findViewById(R.id.tv_huizhang_1);
            tvHuiZhang2 = (TextView)itemView.findViewById(R.id.tv_huizhang_2);
            tvHuiZhang3 = (TextView)itemView.findViewById(R.id.tv_huizhang_3);
            rlHuiZhang1 = itemView.findViewById(R.id.fl_huizhang_1);
            rlHuiZhang2 = itemView.findViewById(R.id.fl_huizhang_2);
            rlHuiZhang3 = itemView.findViewById(R.id.fl_huizhang_3);
            huiZhangRoots = new View[]{rlHuiZhang1,rlHuiZhang2,rlHuiZhang3};
            huiZhangTexts = new TextView[]{tvHuiZhang1,tvHuiZhang2,tvHuiZhang3};
        }
    }

    private class BadgeHolder extends RecyclerView.ViewHolder{

        ImageView ivImg;
        TextView tvTitle,tvDesc,tvSelect;
        View root;

        public BadgeHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.rl_root);
            ivImg = (ImageView) itemView.findViewById(R.id.iv_img);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            tvSelect = (TextView) itemView.findViewById(R.id.tv_select);
        }
    }

    private class BadgeAllHolder extends RecyclerView.ViewHolder{

        ImageView ivImg;
        TextView tvTitle,tvDesc,tvHave,tvBuy,tvCoin;
        View root;

        public BadgeAllHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.rl_root);
            ivImg = (ImageView) itemView.findViewById(R.id.iv_img);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            tvHave = (TextView) itemView.findViewById(R.id.tv_have);
            tvBuy = (TextView) itemView.findViewById(R.id.tv_buy);
            tvCoin = (TextView) itemView.findViewById(R.id.tv_coin);
        }
    }

    private class BagFavoriteHolder extends RecyclerView.ViewHolder{

        ImageView ivBg,ivSelect;
        TextView tvNum,tvName,tvTime;

        public BagFavoriteHolder(View itemView) {
            super(itemView);
            ivBg = (ImageView) itemView.findViewById(R.id.iv_bg);
            ivSelect = (ImageView) itemView.findViewById(R.id.iv_select);
            tvNum = (TextView) itemView.findViewById(R.id.tv_num);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);

            ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
            layoutParams.height = DensityUtil.dip2px(context,120);
            layoutParams.width = DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context,20);
            ivBg.setLayoutParams(layoutParams);
        }
    }
}
