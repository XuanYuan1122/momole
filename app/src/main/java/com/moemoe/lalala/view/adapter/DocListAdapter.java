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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GlideCircleTransform;
import com.moemoe.lalala.utils.GlideRoundTransform;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.activity.NewPersonalActivity;
import com.moemoe.lalala.view.widget.adapter.NewDocLabelAdapter;
import com.moemoe.lalala.view.widget.view.DocLabelView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by yi on 2016/11/29.
 */

public class DocListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DOC_TOP_HOT = 0;
    private static final int VIEW_TYPE_DOC = 1;
    private LayoutInflater mInflater;
    private ArrayList<DocListEntity> mDocData;
    private ArrayList<DocListEntity> mTopAndHot;
    private int mSplit;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private boolean hasTop;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    
    public void setData(ArrayList<DocListEntity> data){
        int bfSize = mDocData.size();
        mDocData.clear();
        mDocData.addAll(data);
        int afSize = mDocData.size();
        if(bfSize == 0){
            if(hasTop){
                notifyItemRangeInserted(1,afSize);
            }else {
                notifyItemRangeInserted(0,afSize);
            }

        }else {
            if(hasTop){
                notifyItemRangeChanged(1, afSize);
            }else {
                notifyItemRangeChanged(0, afSize);
            }
            if(bfSize - afSize > 0){
                if(hasTop){
                    notifyItemRangeRemoved(afSize + 1,bfSize - afSize);
                }else {
                    notifyItemRangeRemoved(afSize,bfSize - afSize);
                }
            }
        }
    }

    public void addData(ArrayList<DocListEntity> data){
        int bp = mDocData.size();
        mDocData.addAll(data);
        if(hasTop){
            notifyItemRangeInserted(bp + 1, data.size());
        }else {
            notifyItemRangeInserted(bp, data.size());
        }
    }

    public void clearTopAndHot(){
        mTopAndHot.clear();
    }

    public void setTopData(ArrayList<DocListEntity> data, int split){
        mTopAndHot.addAll(0,data);
        mSplit = split;
        notifyItemChanged(0);
    }

    public void setHotData(ArrayList<DocListEntity> data){
        mTopAndHot.addAll(data);
        notifyItemChanged(0);
    }

    public int getSplit(){
        return mSplit;
    }

    public void setSplit(int s){
        mSplit = s;
    }

    public ArrayList<DocListEntity> getTopAndHot(){
        return mTopAndHot;
    }

    public ArrayList<DocListEntity> getData(){
        return mDocData;
    }

    public DocListAdapter(Context context,boolean hasTop){
        mDocData = new ArrayList<>();
        mTopAndHot = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.hasTop = hasTop;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_DOC){
            return new DocViewHolder(mInflater.inflate(R.layout.item_doc_club_class,parent,false));
        }else if(viewType == VIEW_TYPE_DOC_TOP_HOT){
            return new TopAndHotHolder(mInflater.inflate(R.layout.item_calender_type5,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof  TopAndHotHolder){
            final TopAndHotHolder topAndHotHolder = (TopAndHotHolder) viewHolder;
            topAndHotHolder.mAdapter.setDate(mTopAndHot,mContext,mSplit);
            setListViewHeightBasedOnChildren(topAndHotHolder.mLvPerson);
            topAndHotHolder.mLvPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(!TextUtils.isEmpty((mTopAndHot.get(position).getDesc().getSchema()))){
                        Uri uri = Uri.parse(mTopAndHot.get(position).getDesc().getSchema());
                        IntentUtils.toActivityFromUri(mContext, uri,view);
                    }
                }
            });
        } else if(viewHolder instanceof DocViewHolder){
            final DocListEntity post = getItem(position);
            final DocViewHolder holder = (DocViewHolder) viewHolder;
            if(holder.ivClubCreatorFlag != null){
                holder.ivClubCreatorFlag.setVisibility(View.GONE);
                holder.tvCreatorName.setSelected(false);
            }
            if(holder.docLabel != null && post.getTags() != null){
                holder.docLabel.setDocLabelAdapter(holder.docLabelAdapter);
                holder.docLabelAdapter.setData(post.getTags(),false);
                if(post.getTags().size()>0) {
                    holder.docLabel.setVisibility(View.VISIBLE);
                    holder.vDocSep.setVisibility(View.VISIBLE);
                }else{
                    holder.docLabel.setVisibility(View.GONE);
                    holder.vDocSep.setVisibility(View.GONE);
                }
            }
            int radius1 = DensityUtil.dip2px(mContext,5);
            float[] outerR1 = new float[] { radius1, radius1, radius1, radius1, radius1, radius1, radius1, radius1};
            RoundRectShape roundRectShape1 = new RoundRectShape(outerR1, null, null);
            ShapeDrawable shapeDrawable1 = new ShapeDrawable();
            shapeDrawable1.setShape(roundRectShape1);
            shapeDrawable1.getPaint().setStyle(Paint.Style.FILL);
            shapeDrawable1.getPaint().setColor(StringUtils.readColorStr(post.getUserLevelColor(), ContextCompat.getColor(mContext, R.color.main_cyan)));
            holder.mLevelRoot.setBackgroundDrawable(shapeDrawable1);
            holder.tvLevel.setText(String.valueOf(post.getUserLevel()));
            holder.tvCreatorName.setText(post.getUserName());
            if(post.getBadgeList().size() > 0){
                int size = 3;
                if(post.getBadgeList().size() < 3){
                    size = post.getBadgeList().size();
                }
                for (int i = 0;i < size;i++){
                    holder.huiZhangTexts[i].setVisibility(View.VISIBLE);
                    holder.huiZhangRoots[i].setVisibility(View.VISIBLE);
                    BadgeEntity badgeEntity = post.getBadgeList().get(i);
                    TextView tv = holder.huiZhangTexts[i];
                    tv.setText(badgeEntity.getTitle());
                    tv.setText(badgeEntity.getTitle());
                    tv.setBackgroundResource(R.drawable.bg_badge_cover);
                    int px = DensityUtil.dip2px(mContext,4);
                    tv.setPadding(px,0,px,0);
                    int radius2 = DensityUtil.dip2px(mContext,2);
                    float[] outerR2 = new float[] { radius2, radius2, radius2, radius2, radius2, radius2, radius2, radius2};
                    RoundRectShape roundRectShape2 = new RoundRectShape(outerR2, null, null);
                    ShapeDrawable shapeDrawable2 = new ShapeDrawable();
                    shapeDrawable2.setShape(roundRectShape2);
                    shapeDrawable2.getPaint().setStyle(Paint.Style.FILL);
                    shapeDrawable2.getPaint().setColor(StringUtils.readColorStr(badgeEntity.getColor(), ContextCompat.getColor(mContext, R.color.main_cyan)));
                    holder.huiZhangRoots[i].setBackgroundDrawable(shapeDrawable2);
                }
            }else {
                for(int i = 0;i < 3;i++){
                    holder.huiZhangTexts[i].setVisibility(View.INVISIBLE);
                    holder.huiZhangRoots[i].setVisibility(View.INVISIBLE);
                }
            }
            if(holder.ivCreatorAvatar != null){
                Glide.with(mContext)
                        .load( StringUtils.getUrl(mContext, ApiService.URL_QINIU +  post.getUserIcon().getPath(), DensityUtil.dip2px(mContext,44), DensityUtil.dip2px(mContext,44), false, false))
                        .override(DensityUtil.dip2px(mContext,44), DensityUtil.dip2px(mContext,44))
                        .placeholder(R.drawable.bg_default_circle)
                        .error(R.drawable.bg_default_circle)
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(holder.ivCreatorAvatar);
                holder.ivCreatorAvatar.setTag(R.id.id_creator_uuid, post.getUserId());
            }
            holder.ivClubCreatorFlag.setVisibility(View.GONE);
            holder.tvCreatorName.setSelected(false);
            holder.ivClubCreatorFlag.setVisibility(View.GONE);
            holder.tvCreatorName.setSelected(false);
            if(TextUtils.isEmpty(post.getDesc().getTitle())){
                holder.tvPostTitle.setVisibility(View.GONE);
            }else{
                holder.tvPostTitle.setVisibility(View.VISIBLE);
                holder. tvPostTitle.setText(post.getDesc().getTitle());
            }
            // 点赞/评论
            holder.tvCommentNum.setText(StringUtils.getNumberInLengthLimit(post.getDesc().getComments(), 3));
            holder.tvPantsNum.setText(StringUtils.getNumberInLengthLimit(post.getDesc().getLikes(), 3));
            // 时间,内容
            holder.tvPostDate.setText(StringUtils.timeFormate(post.getUpdateTime()));
            holder.tvPostBrief.setText(post.getDesc().getContent());
            // 加载特殊帖子样式：投票，视频
            holder.rlSpecialTypePack.setVisibility(View.GONE);
            // 加载图片
            holder.ivIcon1.setImageBitmap(null);
            holder.ivIcon2.setImageBitmap(null);
            holder.ivIcon3.setImageBitmap(null);
            holder.ivIcon1.setTag(R.id.id_filebean, post);
            holder.ivIcon2.setTag(R.id.id_filebean, post);
            holder. ivIcon3.setTag(R.id.id_filebean, post);
            holder.rlIcon1.setVisibility(View.INVISIBLE);
            holder. rlIcon2.setVisibility(View.INVISIBLE);
            if (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE) {
                holder. rlIcon3.setVisibility(View.GONE);
            } else {
                holder.rlIcon3.setVisibility(View.INVISIBLE);
            }
            holder.tvIconNum.setVisibility(View.GONE);
            if (post.getDesc().getMusic() != null){
                holder.llImagePack.setVisibility(View.VISIBLE);
                holder.rlMusicRoot.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + post.getDesc().getMusic().getCover().getPath(), DensityUtil.dip2px(mContext,90), DensityUtil.dip2px(mContext,90), false, true))
                        .override(DensityUtil.dip2px(mContext,90), DensityUtil.dip2px(mContext,90))
                        .placeholder(R.drawable.bg_default_square)
                        .error(R.drawable.bg_default_square)
                        .centerCrop()
                        .transform(new GlideRoundTransform(mContext,5,0,0,5))
                        .into(holder.musicImg);
                holder.musicTitle.setText(post.getDesc().getMusic().getName());
            }else {
                holder.rlMusicRoot.setVisibility(View.GONE);
                if (post.getDesc().getImages() != null && post.getDesc().getImages().size() > 0) {
                    holder.llImagePack.setVisibility(View.VISIBLE);
                    holder.rlIcon1.setVisibility(View.VISIBLE);
                    holder.rlIcon1.setVisibility(View.VISIBLE);
                    if (FileUtil.isGif(post.getDesc().getImages().get(0).getPath())) {
                        holder.ivGifIcon1.setVisibility(View.VISIBLE);
                    } else {
                        holder.ivGifIcon1.setVisibility(View.GONE);
                    }
                    Glide.with(mContext)
                            .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + post.getDesc().getImages().get(0).getPath(), (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,56)) / 3, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,56)) / 3, false, true))
                            .override((DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,56)) / 3, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,56)) / 3)
                            .placeholder(R.drawable.bg_default_square)
                            .error(R.drawable.bg_default_square)
                            .centerCrop()
                            .into(holder.ivIcon1);
                    if(post.getDesc().getImages().size() > 1){
                        holder.rlIcon2.setVisibility(View.VISIBLE);
                        if (FileUtil.isGif(post.getDesc().getImages().get(1).getPath())) {
                            holder.ivGifIcon2.setVisibility(View.VISIBLE);
                        } else {
                            holder.ivGifIcon2.setVisibility(View.GONE);
                        }
                        Glide.with(mContext)
                                .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + post.getDesc().getImages().get(1).getPath(), (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,56)) / 3, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,56)) / 3, false, true))
                                .override((DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,56)) / 3, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,56)) / 3)
                                .placeholder(R.drawable.bg_default_square)
                                .error(R.drawable.bg_default_square)
                                .centerCrop()
                                .into(holder.ivIcon2);
                    }
                    // 是否显示第三张图
                    if(holder.rlSpecialTypePack.getVisibility() != View.VISIBLE && post.getDesc().getImages().size() > 2){
                        holder.rlIcon3.setVisibility(View.VISIBLE);
                        if (FileUtil.isGif(post.getDesc().getImages().get(2).getPath())) {
                            holder.ivGifIcon3.setVisibility(View.VISIBLE);
                        } else {
                            holder.ivGifIcon3.setVisibility(View.GONE);
                        }
                        Glide.with(mContext)
                                .load(StringUtils.getUrl(mContext,ApiService.URL_QINIU + post.getDesc().getImages().get(2).getPath(), (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,56)) / 3, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,56)) / 3, false, true))
                                .override((DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,56)) / 3, (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext,56)) / 3)
                                .placeholder(R.drawable.bg_default_square)
                                .error(R.drawable.bg_default_square)
                                .into(holder.ivIcon3);
                    }
                    // 是否显示  “共xx张图”
                    if(post.getDesc().getImages().size() > 3 || (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE && post.getDesc().getImages().size() > 2)){
                        holder.tvIconNum.setVisibility(View.VISIBLE);
                        holder.tvIconNum.setText(mContext.getString(R.string.label_post_icon_num, post.getDesc().getImages().size()));
                    }
                    //  }
                }else if (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE){
                    // 没有图片，只有投票或者视频
                    holder.llImagePack.setVisibility(View.VISIBLE);
                } else {
                    holder.llImagePack.setVisibility(View.GONE);
                }
            }
            holder.itemView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(holder.itemView, pos);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(hasTop){
            if(position == 0){
                return VIEW_TYPE_DOC_TOP_HOT;
            }else {
                return VIEW_TYPE_DOC;
            }
        }else {
            return VIEW_TYPE_DOC;
        }

    }

    public DocListEntity getItem(int position){
        if(hasTop){
            if(position > 0){
                return  mDocData.get(position - 1);
            }
        }else {
            return mDocData.get(position);
        }
        return  null;
    }

    @Override
    public int getItemCount() {
        if (hasTop){
            return mDocData.size() + 1;
        }else {
            return mDocData.size();
        }
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    private View.OnClickListener mAvatarListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String uuid = (String) v.getTag(R.id.id_creator_uuid);
            if (!TextUtils.isEmpty(uuid) && !uuid.equals(PreferenceUtils.getUUid())) {
                Intent intent = new Intent(mContext, NewPersonalActivity.class);
                intent.putExtra(BaseAppCompatActivity.UUID, uuid);
                mContext.startActivity(intent);
            }
        }
    };

    private static class TopAndHotHolder extends RecyclerView.ViewHolder{

        ListView mLvPerson;
        TopAndHotAdapter mAdapter;

        TopAndHotHolder(View itemView) {
            super(itemView);
            mLvPerson = (ListView) itemView.findViewById(R.id.lv_timetable);
            mAdapter = new TopAndHotAdapter();
            mLvPerson.setAdapter(mAdapter);
        }
    }

    private static class TopAndHotAdapter extends BaseAdapter {

        private  ArrayList<DocListEntity> rssItems;
        private Context context;
        private int split;

        TopAndHotAdapter(){
            rssItems = new ArrayList<>();
        }

        void setDate( ArrayList<DocListEntity> rssItems,Context context,int split){
            this.rssItems.clear();
            this.rssItems.addAll(rssItems);
            this.context = context;
            this.split = split;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return rssItems.size();
        }

        @Override
        public DocListEntity getItem(int position) {
            return rssItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NormalHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_tag_doc_list_top_hot,
                        parent,false);
                holder = new NormalHolder();
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tvChapter = (TextView) convertView.findViewById(R.id.tv_chapter);
                convertView.setTag(holder);
            }else {
                holder = (NormalHolder) convertView.getTag();
            }
            DocListEntity rss = getItem(position);
            holder.tvTitle.setText(rss.getDesc().getTitle());
            holder.tvChapter.setVisibility(View.VISIBLE);
            if (position < split){
                holder.tvChapter.setText(R.string.label_top);
            }else {
                holder.tvChapter.setText(R.string.label_hot);
            }
            return convertView;
        }

        class NormalHolder {
            TextView tvTitle;
            TextView tvChapter;
        }
    }

    class DocViewHolder extends RecyclerView.ViewHolder{
        // 瀑布流才有
        @BindView(R.id.iv_post_creator)
        ImageView ivCreatorAvatar;
        @BindView(R.id.tv_post_creator_name)
        TextView tvCreatorName;
        @BindView(R.id.iv_post_owner_flag)
        View ivClubCreatorFlag;
        @BindView(R.id.tv_post_update_time)
        TextView tvPostDate;
        @BindView(R.id.tv_post_title)
        TextView tvPostTitle;
        @BindView(R.id.tv_post_brief)
        TextView tvPostBrief;
        @BindView(R.id.ll_image_3)
        View llImagePack;
        @BindView(R.id.rl_post_special_flag)
        View rlSpecialTypePack;
        @BindView(R.id.rl_post_image_1)
        View rlIcon1;
        @BindView(R.id.rl_post_image_2)
        View rlIcon2;
        @BindView(R.id.rl_post_image_3)
        View rlIcon3;
        @BindView(R.id.iv_post_image_1_gif_flag)
        View ivGifIcon1;
        @BindView(R.id.iv_post_image_2_gif_flag)
        View ivGifIcon2;
        @BindView(R.id.iv_post_image_3_gif_flag)
        View ivGifIcon3;
        @BindView(R.id.iv_post_image_1)
        ImageView ivIcon1;
        @BindView(R.id.iv_post_image_2)
        ImageView ivIcon2;
        @BindView(R.id.iv_post_image_3)
        ImageView ivIcon3;
        @BindView(R.id.tv_post_img_num)
        TextView tvIconNum;
        @BindView(R.id.tv_post_comment_num)
        TextView tvCommentNum;
        @BindView(R.id.tv_post_pants_num)
        TextView tvPantsNum;
        @BindView(R.id.rl_music_root)
        View rlMusicRoot;
        @BindView(R.id.iv_item_image)
        ImageView musicImg;
        @BindView(R.id.tv_music_title)
        TextView musicTitle;
        @BindView(R.id.tv_level)
        TextView tvLevel;
        @BindView(R.id.rl_level_bg)
        View mLevelRoot;
        //标签
        @BindView(R.id.view_doc_sep)
        View vDocSep;
        @BindView(R.id.dv_doc_label_root)
        DocLabelView docLabel;
        @BindView(R.id.fl_huizhang_1)
        View rlHuiZhang1;
        @BindView(R.id.fl_huizhang_2)
        View rlHuiZhang2;
        @BindView(R.id.fl_huizhang_3)
        View rlHuiZhang3;
        @BindView(R.id.tv_huizhang_1)
        TextView tvHuiZhang1;
        @BindView(R.id.tv_huizhang_2)
        TextView tvHuiZhang2;
        @BindView(R.id.tv_huizhang_3)
        TextView tvHuiZhang3;
        View[] huiZhangRoots;
        TextView[] huiZhangTexts;
        NewDocLabelAdapter docLabelAdapter;

        DocViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (ivCreatorAvatar != null) {
                ivCreatorAvatar.setOnClickListener(mAvatarListener);
            }
            docLabelAdapter = new NewDocLabelAdapter(mContext,true);
            if(AppSetting.SUB_TAG)docLabel.setmMaxLines(3);
            ivIcon1.setOnClickListener(mIconListener);
            ivIcon2.setOnClickListener(mIconListener);
            ivIcon3.setOnClickListener(mIconListener);
            huiZhangRoots = new View[]{rlHuiZhang1,rlHuiZhang2,rlHuiZhang3};
            huiZhangTexts = new TextView[]{tvHuiZhang1,tvHuiZhang2,tvHuiZhang3};
        }

        private View.OnClickListener mIconListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int index = 0;
                if (v.equals(ivIcon1)){
                    index = 0;
                } else if (v.equals(ivIcon2)) {
                    index = 1;
                } else if (v.equals(ivIcon3)) {
                    index = 2;
                }

                Context context = v.getContext();
                final DocListEntity docBean = (DocListEntity) v.getTag(R.id.id_filebean);
                Intent intent = new Intent(context, ImageBigSelectActivity.class);
                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, docBean.getDesc().getImages());
                intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, index);
                // 以后可选择 有返回数据
                context.startActivity(intent);

            }
        };
    }
}
