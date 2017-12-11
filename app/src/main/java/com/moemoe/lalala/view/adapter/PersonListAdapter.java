package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
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
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.NetaMsgEntity;
import com.moemoe.lalala.model.entity.NewCommentEntity;
import com.moemoe.lalala.model.entity.PersonDocEntity;
import com.moemoe.lalala.model.entity.PersonFollowEntity;
import com.moemoe.lalala.model.entity.ReplyEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.BadgeActivity;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.NewBagActivity;
import com.moemoe.lalala.view.activity.PersonalV2Activity;
import com.moemoe.lalala.view.activity.TagControlActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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

    public void removeData(int position){
        if(position < list.size()) this.list.remove(position);
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
    public int getItemViewType(int position) {
        if (mType == 2){
            if(position == 0 || position == 1 || position == 2){
                return 1;
            }else {
                return 0;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         if(mType == 0 || mType == 10){//doc favorite
             return new SearchDocViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_doc,parent,false));
         }else if(mType == 1){//follow
             return new FollowViewHolder(LayoutInflater.from(context).inflate(R.layout.item_person_follow,parent,false));
         }else if(mType == 2){//msg
             if(viewType == 0){
                 return new MsgViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_new,parent,false));
             }else{
                 return new RedMsgViewHolder(LayoutInflater.from(context).inflate(R.layout.item_msg_offical,parent,false));
             }
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
         }else if(mType == 8){
             return new TagHolder(LayoutInflater.from(context).inflate(R.layout.item_tag_del,parent,false));
         }else if (mType == 9){
             return new SysMsgHolder(LayoutInflater.from(context).inflate(R.layout.item_msg_offical_detail,parent,false));
         }else if(mType == 11){
             return new BagItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_bag_dir,parent,false));
         }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(mType == 0 || mType == 10){
            final PersonDocEntity entity = (PersonDocEntity) getItem(position);
            SearchDocViewHolder searchDocViewHolder = (SearchDocViewHolder) holder;
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + entity.getImage(), (int)context.getResources().getDimension(R.dimen.y160),(int)context.getResources().getDimension(R.dimen.y160),false,true))
                    .override((int)context.getResources().getDimension(R.dimen.y160),(int)context.getResources().getDimension(R.dimen.y160))
                    .error(R.drawable.bg_cardbg_nopic)
                    .placeholder(R.drawable.bg_cardbg_nopic)
                    .into(searchDocViewHolder.img);
            searchDocViewHolder.title.setText(entity.getTitle());
            searchDocViewHolder.content.setText(entity.getDesc());
            searchDocViewHolder.time.setText(StringUtils.timeFormat(entity.getCreateTime()));
            // 点赞/评论
            searchDocViewHolder.commentNum.setText(StringUtils.getNumberInLengthLimit(entity.getComments(), 3));
            searchDocViewHolder.likeNum.setText(StringUtils.getNumberInLengthLimit(entity.getLikes(), 3));
            searchDocViewHolder.name.setText(entity.getCreateUserName());
            searchDocViewHolder.name.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(!entity.getCreateUserId().equals(PreferenceUtils.getUUid())){
                        Intent i = new Intent(context, PersonalV2Activity.class);
                        i.putExtra("uuid",entity.getCreateUserId());
                        context.startActivity(i);
                    }
                }
            });
            searchDocViewHolder.address.setText(entity.getDocType());
            searchDocViewHolder.address.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if (!TextUtils.isEmpty(entity.getDocTypeSchema())) {
                        Uri uri = Uri.parse(entity.getDocTypeSchema());
                        IntentUtils.toActivityFromUri(context, uri,v);
                    }
                }
            });
        }else if(mType == 1){
            PersonFollowEntity entity = (PersonFollowEntity) getItem(position);
            final FollowViewHolder followViewHolder = (FollowViewHolder) holder;
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + entity.getUserIcon(),(int)context.getResources().getDimension(R.dimen.y100),(int)context.getResources().getDimension(R.dimen.y100),false,true))
                    .override((int)context.getResources().getDimension(R.dimen.y100),(int)context.getResources().getDimension(R.dimen.y100))
                    .bitmapTransform(new CropCircleTransformation(context))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .into(followViewHolder.img);
            followViewHolder.level.setText(String.valueOf(entity.getUserLevel()));
            int radius1 = (int)context.getResources().getDimension(R.dimen.y10);
            float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
            RoundRectShape roundRectShape1 = new RoundRectShape(outerR1, null, null);
            ShapeDrawable shapeDrawable1 = new ShapeDrawable();
            shapeDrawable1.setShape(roundRectShape1);
            shapeDrawable1.getPaint().setStyle(Paint.Style.FILL);
            shapeDrawable1.getPaint().setColor(StringUtils.readColorStr(entity.getUserLevelColor(), ContextCompat.getColor(context, R.color.main_cyan)));
            followViewHolder.ivLevelColor.setBackgroundDrawable(shapeDrawable1);
            Observable.range(0,3)
                    .subscribe(new Observer<Integer>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Integer integer) {
                            followViewHolder.huiZhangTexts[integer].setVisibility(View.INVISIBLE);
                            followViewHolder.huiZhangRoots[integer].setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
            if(entity.getBadgeList().size() > 0){
                int size = 3;
                if(entity.getBadgeList().size() < 3){
                    size = entity.getBadgeList().size();
                }
                for (int i = 0;i < size;i++){
                    followViewHolder.huiZhangTexts[i].setVisibility(View.VISIBLE);
                    followViewHolder.huiZhangRoots[i].setVisibility(View.VISIBLE);
                    BadgeEntity badgeEntity = entity.getBadgeList().get(i);
                    TextView tv = followViewHolder.huiZhangTexts[i];
                    tv.setText(badgeEntity.getTitle());
                    tv.setText(badgeEntity.getTitle());
                    tv.setBackgroundResource(R.drawable.bg_badge_cover);
                    int px = (int)context.getResources().getDimension(R.dimen.x8);
                    tv.setPadding(px,0,px,0);
                    int radius2 = (int)context.getResources().getDimension(R.dimen.y4);
                    float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
                    RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
                    ShapeDrawable shapeDrawable2 = new ShapeDrawable();
                    shapeDrawable2.setShape(roundRectShape2);
                    shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
                    shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(context, R.color.main_cyan)));
                    followViewHolder.huiZhangRoots[i].setBackgroundDrawable(shapeDrawable2);
                }
            }
            followViewHolder.name.setText(entity.getUserName());
            followViewHolder.content.setText(entity.getSignature());
        }else if(mType == 2){
            if(holder instanceof MsgViewHolder){
                ReplyEntity bean = (ReplyEntity) getItem(position - 3);
                MsgViewHolder msgViewHolder = (MsgViewHolder) holder;
                Glide.with(context)
                        .load(StringUtils.getUrl(context, ApiService.URL_QINIU + bean.getFromIcon().getPath(),(int)context.getResources().getDimension(R.dimen.y100),(int)context.getResources().getDimension(R.dimen.y100),false,false))
                        .placeholder(R.drawable.bg_default_circle)
                        .error(R.drawable.bg_default_circle)
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(msgViewHolder.ivAvatar);
                msgViewHolder.tvName.setText(bean.getFromName());
                msgViewHolder.tvDate.setText(StringUtils.timeFormat(bean.getCreateTime()));
                msgViewHolder.tvContent.setText(bean.getContent());
            }else {
                RedMsgViewHolder viewHolder = (RedMsgViewHolder) holder;
                if(position == 0){
                    Glide.with(context)
                            .load(R.drawable.ic_inform_notice)
                            .override((int)context.getResources().getDimension(R.dimen.y100),(int)context.getResources().getDimension(R.dimen.y100))
                            .placeholder(R.drawable.bg_default_circle)
                            .error(R.drawable.bg_default_circle)
                            .into(viewHolder.ivImg);
                    viewHolder.tvName.setText("系统通知");
                    if(PreferenceUtils.getMessageDot(context,"system")){
                        viewHolder.ivRed.setVisibility(View.VISIBLE);
                    }else {
                        viewHolder.ivRed.setVisibility(View.GONE);
                    }
                }else if(position == 1){
                    Glide.with(context)
                            .load(R.drawable.ic_inform_at)
                            .override((int)context.getResources().getDimension(R.dimen.y100),(int)context.getResources().getDimension(R.dimen.y100))
                            .placeholder(R.drawable.bg_default_circle)
                            .error(R.drawable.bg_default_circle)
                            .into(viewHolder.ivImg);
                    viewHolder.tvName.setText("@我的");
                    if(PreferenceUtils.getMessageDot(context,"at_user")){
                        viewHolder.ivRed.setVisibility(View.VISIBLE);
                    }else {
                        viewHolder.ivRed.setVisibility(View.GONE);
                    }
                }else {
                    Glide.with(context)
                            .load(R.drawable.ic_inform_official)
                            .override((int)context.getResources().getDimension(R.dimen.y100),(int)context.getResources().getDimension(R.dimen.y100))
                            .placeholder(R.drawable.bg_default_circle)
                            .error(R.drawable.bg_default_circle)
                            .into(viewHolder.ivImg);
                    viewHolder.tvName.setText("Neta官方");
                    if(PreferenceUtils.getMessageDot(context,"neta")){
                        viewHolder.ivRed.setVisibility(View.VISIBLE);
                    }else {
                        viewHolder.ivRed.setVisibility(View.GONE);
                    }
                }
            }
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
            if(!TextUtils.isEmpty(entity.getSchema())){
                coinViewHolder.tvType.setTextColor(ContextCompat.getColor(context,R.color.main_cyan));
            }else {
                coinViewHolder.tvType.setTextColor(ContextCompat.getColor(context,R.color.gray_d7d7d7));
            }
            coinViewHolder.tvType.setText(entity.getType());
            coinViewHolder.tvTime.setText(StringUtils.timeFormat(entity.getCreateTime()));
        }else if(mType == 4){
            final NewCommentEntity commentEntity = (NewCommentEntity) getItem(position);
            final CommentHolder commentHolder = (CommentHolder) holder;
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + commentEntity.getFromUserIcon().getPath(),(int)context.getResources().getDimension(R.dimen.y70),(int)context.getResources().getDimension(R.dimen.y70), false, false))
                    .override((int)context.getResources().getDimension(R.dimen.y70),(int)context.getResources().getDimension(R.dimen.y70))
                    .placeholder(R.drawable.bg_default_circle)
                    .error(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(commentHolder.ivCreator);
            commentHolder.tvCreatorName.setText(commentEntity.getFromUserName());
            commentHolder.tvTime.setText(StringUtils.timeFormat(commentEntity.getCreateTime()));
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
            commentHolder.tvLevel.setText(String.valueOf(commentEntity.getFromUserLevel()));
            int radius1 = (int)context.getResources().getDimension(R.dimen.y10);
            float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
            RoundRectShape roundRectShape1 = new RoundRectShape(outerR1, null, null);
            ShapeDrawable shapeDrawable1 = new ShapeDrawable();
            shapeDrawable1.setShape(roundRectShape1);
            shapeDrawable1.getPaint().setStyle(Paint.Style.FILL);
            shapeDrawable1.getPaint().setColor(StringUtils.readColorStr(commentEntity.getFromUserLevelColor(), ContextCompat.getColor(context, R.color.main_cyan)));
            commentHolder.ivLevelColor.setBackgroundDrawable(shapeDrawable1);
            Observable.range(0,3)
                    .subscribe(new Observer<Integer>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Integer integer) {
                            commentHolder.huiZhangTexts[integer].setVisibility(View.INVISIBLE);
                            commentHolder.huiZhangRoots[integer].setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

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
                    int px = (int)context.getResources().getDimension(R.dimen.x8);
                    tv.setPadding(px,0,px,0);
                    int radius2 = (int)context.getResources().getDimension(R.dimen.y4);
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
                    if (!TextUtils.isEmpty(commentEntity.getFromUserId()) && !commentEntity.getFromUserId().equals(PreferenceUtils.getUUid())) {
                        Intent i = new Intent(context,PersonalV2Activity.class);
                        i.putExtra(BaseAppCompatActivity.UUID,commentEntity.getFromUserId());
                        context.startActivity(i);
                    }
                }
            });
        }else if(mType == 5){
            BadgeEntity badgeEntity = (BadgeEntity) getItem(position);
            BadgeHolder badgeHolder = (BadgeHolder) holder;
            Glide.with(context)
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + badgeEntity.getImg(), (int)context.getResources().getDimension(R.dimen.y160), (int)context.getResources().getDimension(R.dimen.y160), false, false))
                    .override((int)context.getResources().getDimension(R.dimen.y160), (int)context.getResources().getDimension(R.dimen.y160))
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
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + badgeEntity.getImg(), (int)context.getResources().getDimension(R.dimen.y160), (int)context.getResources().getDimension(R.dimen.y160), false, false))
                    .override((int)context.getResources().getDimension(R.dimen.y160), (int)context.getResources().getDimension(R.dimen.y160))
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
                    .load(StringUtils.getUrl(context, ApiService.URL_QINIU + entity.getCover(), DensityUtil.getScreenWidth(context) - (int)context.getResources().getDimension(R.dimen.x72), (int)context.getResources().getDimension(R.dimen.y240), false, true))
                    .override(DensityUtil.getScreenWidth(context) - (int)context.getResources().getDimension(R.dimen.x72), (int)context.getResources().getDimension(R.dimen.y240))
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .transform(new GlideRoundTransform(context,5))
                    .into(bagFavoriteHolder.ivBg);
            bagFavoriteHolder.ivSelect.setVisibility(canDelete?View.VISIBLE:View.GONE);
            bagFavoriteHolder.ivSelect.setSelected(entity.isSelect());
            bagFavoriteHolder.tvNum.setText(entity.getNumber() + "项");
            bagFavoriteHolder.tvName.setText(entity.getName());
            bagFavoriteHolder.tvTime.setText(StringUtils.timeFormat(entity.getUpdateTime()) + " 更新");
        }else if(mType == 8){
            final DocTagEntity entity = (DocTagEntity) getItem(position);
            TagHolder tagHolder = (TagHolder) holder;
            tagHolder.content.setText(entity.getName());
            tagHolder.del.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    ((TagControlActivity)context).delTag(position);
                }
            });
        }else if(mType == 9){
            NetaMsgEntity entity = (NetaMsgEntity) getItem(position);
            SysMsgHolder msgHolder = (SysMsgHolder) holder;
            msgHolder.content.setText(entity.getContent());
            if(!TextUtils.isEmpty(entity.getSchema())){
                msgHolder.watch.setVisibility(View.VISIBLE);
            }else {
                msgHolder.watch.setVisibility(View.INVISIBLE);
            }
            msgHolder.time.setText(StringUtils.timeFormat(entity.getCreateTime()));
        } else if(mType == 11){
            BagItemViewHolder viewHolder = (BagItemViewHolder) holder;
            final ShowFolderEntity entity = (ShowFolderEntity) getItem(position);
            String path ;
            if(entity.getCover().startsWith("/")){
                path = entity.getCover();
            }else {
                path = StringUtils.getUrl(context, ApiService.URL_QINIU +  entity.getCover(), (DensityUtil.getScreenWidth(context) - (int)context.getResources().getDimension(R.dimen.x60))/2 ,(DensityUtil.getScreenWidth(context) - (int)context.getResources().getDimension(R.dimen.x60))/2, false, true);
            }
            Glide.with(context)
                    .load(path)
                    .override((DensityUtil.getScreenWidth(context) - (int)context.getResources().getDimension(R.dimen.x60))/2,(DensityUtil.getScreenWidth(context) - (int)context.getResources().getDimension(R.dimen.x60))/2)
                    .placeholder(R.drawable.bg_default_square)
                    .error(R.drawable.bg_default_square)
                    .centerCrop()
                    .bitmapTransform(new CropSquareTransformation(context),new RoundedCornersTransformation(context,(int)context.getResources().getDimension(R.dimen.y8),0))
                    .into(viewHolder.ivBg);
            if(entity.getType().equals(FolderType.ZH.toString())){
                viewHolder.tvMark.setText("综合");
                viewHolder.tvMark.setBackgroundResource(R.drawable.shape_rect_zonghe);
            }else if(entity.getType().equals(FolderType.TJ.toString())){
                viewHolder.tvMark.setText("图集");
                viewHolder.tvMark.setBackgroundResource(R.drawable.shape_rect_tuji);
            }else if(entity.getType().equals(FolderType.MH.toString())){
                viewHolder.tvMark.setText("漫画");
                viewHolder.tvMark.setBackgroundResource(R.drawable.shape_rect_manhua);
            }else if(entity.getType().equals(FolderType.XS.toString())){
                viewHolder.tvMark.setText("小说");
                viewHolder.tvMark.setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
            }

            viewHolder.tvName.setText(entity.getFolderName());
            String tagStr = "";
            for(int i = 0;i < entity.getTexts().size();i++){
                String tagTmp = entity.getTexts().get(i);
                if(i == 0){
                    tagStr = tagTmp;
                }else {
                    tagStr += " · " + tagTmp;
                }
            }
            viewHolder.tvTime.setText(tagStr);
            viewHolder.ivSelected.setVisibility(View.GONE);
            viewHolder.tvCreator.setText(entity.getCreateUserName());
            viewHolder.tvCreator.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(!entity.getCreateUser().equals(PreferenceUtils.getUUid())){
                        Intent i = new Intent(context, PersonalV2Activity.class);
                        i.putExtra("uuid",entity.getCreateUser());
                        context.startActivity(i);
                    }
                }
            });
            viewHolder.tvBag.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i2 = new Intent(context,NewBagActivity.class);
                    i2.putExtra("uuid",entity.getCreateUser());
                    context.startActivity(i2);
                }
            });
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
        if(mType == 2){
            return list.size() + 3;
        }else {
            return list.size();
        }
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

        ImageView img;
        TextView name,content,level;
        View ivLevelColor;
        View rlHuiZhang1;
        View rlHuiZhang2;
        View rlHuiZhang3;
        TextView tvHuiZhang1;
        TextView tvHuiZhang2;
        TextView tvHuiZhang3;
        View[] huiZhangRoots;
        TextView[] huiZhangTexts;

        FollowViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.iv_img);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            ivLevelColor = itemView.findViewById(R.id.rl_level_bg);
            content = (TextView) itemView.findViewById(R.id.tv_content);
            level = (TextView) itemView.findViewById(R.id.tv_level);
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

    private class MsgViewHolder extends RecyclerView.ViewHolder{
        ImageView ivAvatar;
        TextView tvName;
        TextView tvDate;
        TextView tvContent;
        TextView tvDocContent;
        TextView tvClubName;
        TextView tvDot;

        MsgViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_creator);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvDocContent = (TextView) itemView.findViewById(R.id.tv_doc_content);
            tvClubName = (TextView) itemView.findViewById(R.id.tv_club_name);
            tvDot = (TextView) itemView.findViewById(R.id.tv_dot);
        }
    }

    private class RedMsgViewHolder extends RecyclerView.ViewHolder{
        ImageView ivImg;
        TextView tvName;
        ImageView ivRed;

        RedMsgViewHolder(View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.iv_img);
            tvName = itemView.findViewById(R.id.tv_name);
            ivRed = itemView.findViewById(R.id.iv_red_msg);
        }
    }

    private class CoinViewHolder extends RecyclerView.ViewHolder{

        TextView tvCoin,tvType,tvTime,tvLabel;

        CoinViewHolder(View itemView) {
            super(itemView);
            tvCoin = itemView.findViewById(R.id.tv_coin);
            tvLabel = itemView.findViewById(R.id.tv_label);
            tvType = itemView.findViewById(R.id.tv_type);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }

    private class CommentHolder extends RecyclerView.ViewHolder{

        ImageView ivCreator;
        TextView tvCreatorName;
        TextView tvTime;
        TextView tvContent;
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
            ivBg = itemView.findViewById(R.id.iv_bg);
            ivSelect = itemView.findViewById(R.id.iv_select);
            tvNum = itemView.findViewById(R.id.tv_num);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);

            ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
            layoutParams.height = (int)context.getResources().getDimension(R.dimen.y240);
            layoutParams.width = DensityUtil.getScreenWidth(context) - (int)context.getResources().getDimension(R.dimen.x72);
            ivBg.setLayoutParams(layoutParams);
        }
    }

    private class TagHolder extends RecyclerView.ViewHolder{

        TextView content;
        ImageView del;

        public TagHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tv_content);
            del = itemView.findViewById(R.id.iv_del);
        }
    }

    private class SysMsgHolder extends RecyclerView.ViewHolder{

        TextView content,watch,time;

        public SysMsgHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tv_content);
            watch = itemView.findViewById(R.id.tv_watch);
            time = itemView.findViewById(R.id.tv_time);
        }
    }

    private class SearchDocViewHolder extends RecyclerView.ViewHolder{

        ImageView img;
        TextView title,content,time,likeNum,commentNum,name,address;
        SearchDocViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.iv_img);
            title = itemView.findViewById(R.id.tv_title);
            content = itemView.findViewById(R.id.tv_content);
            time = itemView.findViewById(R.id.tv_time);
            likeNum = itemView.findViewById(R.id.tv_post_pants_num);
            commentNum = itemView.findViewById(R.id.tv_post_comment_num);
            name = itemView.findViewById(R.id.tv_create_name);
            address = itemView.findViewById(R.id.tv_address);
        }
    }

    private class BagItemViewHolder extends RecyclerView.ViewHolder{

        TextView tvMark,tvTime,tvName,tvCreator,tvBag;
        ImageView ivSelected,ivBg;

        View root;

        public BagItemViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.rl_root);
            tvMark = itemView.findViewById(R.id.tv_mark);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvName = itemView.findViewById(R.id.tv_name);
            tvBag = itemView.findViewById(R.id.tv_bag);
            tvCreator = itemView.findViewById(R.id.tv_create_name);
            ivBg = itemView.findViewById(R.id.iv_bg);
            ivSelected = itemView.findViewById(R.id.iv_selected);
            ViewGroup.MarginLayoutParams layoutParams1 = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
            layoutParams1.height = (DensityUtil.getScreenWidth(context) - (int)context.getResources().getDimension(R.dimen.x60))/2;
            layoutParams1.width = (DensityUtil.getScreenWidth(context) - (int)context.getResources().getDimension(R.dimen.x60))/2;
            layoutParams1.topMargin = (int)context.getResources().getDimension(R.dimen.y20);
            layoutParams1.rightMargin = (int)context.getResources().getDimension(R.dimen.x20);
            root.setLayoutParams(layoutParams1);
            ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
            layoutParams.height = (DensityUtil.getScreenWidth(context) - (int)context.getResources().getDimension(R.dimen.x60))/2;
            layoutParams.width = (DensityUtil.getScreenWidth(context) - (int)context.getResources().getDimension(R.dimen.x60))/2;
            ivBg.setLayoutParams(layoutParams);
        }
    }
}
