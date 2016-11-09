package com.moemoe.lalala.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.app.Utils;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.FriendsMainActivity;
import com.moemoe.lalala.ImageBigSelectActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.DocItemBean;
import com.moemoe.lalala.data.DocTag;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.DocLabelView;
import com.moemoe.lalala.view.MyRoundedImageView;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

;

/**
 * Created by Haru on 2016/5/2 0002.
 */
public class DocListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int VIEW_TYPE_DOC_TOP_HOT = 0;
    private static final int VIEW_TYPE_DOC = 1;

    public static final int TYPE_DOC = 0;
    public static final int TYPE_CLUB_DOC = 1;
    public static final int TYPE_SEARCH = 2;
    public static final int TYPE_CLASS = 3;
    public static final int TYPE_NEW_DOC = 4;
    private int[] mTagId = {R.drawable.btn_class_from_orange,R.drawable.btn_class_from_blue,
            R.drawable.btn_class_from_green,R.drawable.btn_class_from_pink,R.drawable.btn_class_from_yellow};

    private OnItemClickListener mOnItemClickListener;
    private LayoutInflater mInflater;
    private ArrayList<DocItemBean> mDocData;
    private ArrayList<DocItemBean> mTopAndHot;
    private int mSplit;
   // private int mType;
    private Context mContext;
    private String mKeyWord;
    private boolean mNeedHighLight;
    private boolean mNeedShowLabel;
    public String TAG;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setKeyWord(String keyWord){
        mKeyWord = keyWord;
        if(mKeyWord != null){
            mNeedHighLight = true;
        }
    }

    public void setData(ArrayList<DocItemBean> data){
        int bfSize = mDocData.size();
        mDocData.clear();
        mDocData.addAll(data);
        int afSize = mDocData.size();
        if(bfSize == 0){
            notifyItemRangeInserted(1,afSize);
        }else {
            notifyItemRangeChanged(1, afSize);
            if(bfSize - afSize > 0){
                notifyItemRangeRemoved(afSize + 1,bfSize - afSize);
            }
        }
        notifyDataSetChanged();
    }

    public void addData(ArrayList<DocItemBean> data){
        int bp = mDocData.size();
        mDocData.addAll(data);
        notifyItemRangeInserted(bp + 1, data.size());
    }

    public void setTopAndHotData(ArrayList<DocItemBean> data,int split){
        mTopAndHot.clear();
        mTopAndHot.addAll(data);
        mSplit = split;
        notifyItemChanged(0);
    }

    public void clearTopAndHot(){
        mTopAndHot.clear();
    }

    public void setTopData(ArrayList<DocItemBean> data,int split){
        //mTopAndHot.clear();
        //mTopAndHot.addAll(data);
        mTopAndHot.addAll(0,data);
        mSplit = split;
        notifyItemChanged(0);
    }

    public void setHotData(ArrayList<DocItemBean> data){
        mTopAndHot.addAll(data);
        notifyItemChanged(0);
    }

    public void addNewData(DocItemBean data){
        mDocData.add(0, data);
        notifyItemInserted(0);
    }

    public ArrayList<DocItemBean> getData(){
        return mDocData;
    }

    public DocListAdapter(Context context,int type,boolean needShowLabel,String TAG){
        mDocData = new ArrayList<>();
        mTopAndHot = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        //mType = type;
        mContext = context;
        mNeedShowLabel = needShowLabel;
        this.TAG = TAG;
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
                    if(!TextUtils.isEmpty((mTopAndHot.get(position).doc.schema))){
                        Uri uri = Uri.parse(mTopAndHot.get(position).doc.schema);
                        IntentUtils.toActivityFromUri(mContext, uri,view);
                    }
                }
            });
        } else if(viewHolder instanceof DocViewHolder){
            final DocItemBean post = getItem(position);
            final DocViewHolder holder = (DocViewHolder) viewHolder;
            if(holder.ivClubCreatorFlag != null){
                holder.ivClubCreatorFlag.setVisibility(View.GONE);
                holder.tvCreatorName.setSelected(false);
            }
            if(holder.docLabel != null && post.tags != null){
                holder.docLabel.setDocLabelAdapter(holder.docLabelAdapter);
                holder.docLabelAdapter.setData(post.tags,false);
                holder.docLabel.setItemClickListener(new DocLabelView.LabelItemClickListener() {

                    @Override
                    public void itemClick(int position) {
                        if (position < post.tags.size()) {
                            holder.plusLabel(post, position);
                        }
                    }
                });
                if(post.tags.size()>0) {
                    holder.docLabel.setVisibility(View.VISIBLE);
                    holder.vDocSep.setVisibility(View.VISIBLE);
                }else{
                    holder.docLabel.setVisibility(View.GONE);
                    holder.vDocSep.setVisibility(View.GONE);
                }
            }

            holder.ivLevelColor.setBackgroundColor(post.user.level_color);
            holder.tvLevel.setText(post.user.level + "");
            holder.tvLevel.setTextColor(post.user.level_color);
            holder.tvCreatorName.setText(post.user.nickname);
            if(holder.ivCreatorAvatar != null){
                Picasso.with(mContext)
                        .load( StringUtils.getUrl(mContext, post.user.icon.path, DensityUtil.dip2px(44), DensityUtil.dip2px(44), false, false))
                        .resize(DensityUtil.dip2px(44), DensityUtil.dip2px(44))
                        .placeholder(R.drawable.ic_default_avatar_m)
                        .error(R.drawable.ic_default_avatar_m)
                        .config(Bitmap.Config.RGB_565)
                        .tag(TAG)
                        .into(holder.ivCreatorAvatar);
                holder.ivCreatorAvatar.setTag(R.id.id_creator_uuid, post.user.id);
            }
            holder.ivClubCreatorFlag.setVisibility(View.GONE);
            holder.tvCreatorName.setSelected(false);
            holder.ivClubCreatorFlag.setVisibility(View.GONE);
            holder.tvCreatorName.setSelected(false);
            if(TextUtils.isEmpty(post.doc.title)){
                holder.tvPostTitle.setVisibility(View.GONE);
            }else{
                        holder.tvPostTitle.setVisibility(View.VISIBLE);
                        holder. tvPostTitle.setText(post.doc.title);
            }
            // 点赞/评论
            holder.tvCommentNum.setText(StringUtils.getNumberInLengthLimit(post.doc.comments, 3));
            holder.tvPantsNum.setText(StringUtils.getNumberInLengthLimit(post.doc.likes, 3));
            // 时间,内容
            holder.tvPostDate.setText(StringUtils.timeFormate(post.doc.updateTime));
            holder.tvPostBrief.setText(post.doc.content);
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
            holder.tvPostFromName.setTag(R.id.id_filebean, post);
            holder.ivDocHot.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(post.doc.music.url)){
                holder.rlMusicRoot.setVisibility(View.VISIBLE);
                Picasso.with(mContext)
                        .load(StringUtils.getUrl(mContext, post.doc.music.cover.path, DensityUtil.dip2px(90), DensityUtil.dip2px(90), false, true))
                        .resize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                        .placeholder(R.drawable.ic_default_avatar_l)
                        .error(R.drawable.ic_default_avatar_l)
                        .centerCrop()
                        .config(Bitmap.Config.RGB_565)
                        .tag(TAG)
                        .into(holder.musicImg);
                holder.musicTitle.setText(post.doc.music.name);
            }else {
                holder.rlMusicRoot.setVisibility(View.GONE);
                if (post.doc.images != null && post.doc.images.size() > 0) {
                    holder.llImagePack.setVisibility(View.VISIBLE);
                    holder.rlIcon1.setVisibility(View.VISIBLE);
                    holder.rlIcon1.setVisibility(View.VISIBLE);
                    if (FileUtil.isGif(post.doc.images.get(0).path)) {
                        holder.ivGifIcon1.setVisibility(View.VISIBLE);
                    } else {
                        holder.ivGifIcon1.setVisibility(View.GONE);
                    }
                    Picasso.with(mContext)
                            .load(StringUtils.getUrl(mContext, post.doc.images.get(0).path, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, false, true))
                            .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                            .placeholder(R.drawable.ic_default_club_l)
                            .error(R.drawable.ic_default_club_l)
                            .centerCrop()
                            .config(Bitmap.Config.RGB_565)
                            .tag(TAG)
                            .into(holder.ivIcon1);
                    if(post.doc.images.size() > 1){
                        holder.rlIcon2.setVisibility(View.VISIBLE);
                        if (FileUtil.isGif(post.doc.images.get(1).path)) {
                            holder.ivGifIcon2.setVisibility(View.VISIBLE);
                        } else {
                            holder.ivGifIcon2.setVisibility(View.GONE);
                        }
                        Picasso.with(mContext)
                                .load(StringUtils.getUrl(mContext, post.doc.images.get(1).path, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, false, true))
                                .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                                .placeholder(R.drawable.ic_default_club_l)
                                .error(R.drawable.ic_default_club_l)
                                .centerCrop()
                                .config(Bitmap.Config.RGB_565)
                                .tag(TAG)
                                .into(holder.ivIcon2);
                    }
                    // 是否显示第三张图
                    if(holder.rlSpecialTypePack.getVisibility() != View.VISIBLE && post.doc.images.size() > 2){
                        holder.rlIcon3.setVisibility(View.VISIBLE);
                        if (FileUtil.isGif(post.doc.images.get(2).path)) {
                            holder.ivGifIcon3.setVisibility(View.VISIBLE);
                        } else {
                            holder.ivGifIcon3.setVisibility(View.GONE);
                        }
                        Picasso.with(mContext)
                                .load(StringUtils.getUrl(mContext, post.doc.images.get(2).path, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, false, true))
                                .resize((DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3, (DensityUtil.getScreenWidth() - DensityUtil.dip2px(56)) / 3)
                                .placeholder(R.drawable.ic_default_club_l)
                                .error(R.drawable.ic_default_club_l)
                                .config(Bitmap.Config.RGB_565)
                                .centerCrop()
                                .tag(TAG)
                                .into(holder.ivIcon3);
                    }
                    // 是否显示  “共xx张图”
                    if(post.doc.images.size() > 3 || (holder.rlSpecialTypePack.getVisibility() == View.VISIBLE && post.doc.images.size() > 2)){
                        holder.tvIconNum.setVisibility(View.VISIBLE);
                        holder.tvIconNum.setText(mContext.getString(R.string.label_post_icon_num, post.doc.images.size()));
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
        if(position == 0){
            return VIEW_TYPE_DOC_TOP_HOT;
        }else {
            return VIEW_TYPE_DOC;
        }
    }

    public DocItemBean getItem(int position){
        if(position > 0){
            return  mDocData.get(position - 1);
        }
        return  null;
    }

    @Override
    public int getItemCount() {
        return mDocData.size() + 1;
    }

    private View.OnClickListener mFromNameListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        }
    };

    public void setListViewHeightBasedOnChildren(ListView listView) {
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
            if (!TextUtils.isEmpty(uuid)) {
                Intent intent = new Intent(mContext, FriendsMainActivity.class);
                intent.putExtra(BaseActivity.EXTRA_KEY_UUID, uuid);
                mContext.startActivity(intent);
            }
        }
    };

    public static class TopAndHotHolder extends RecyclerView.ViewHolder{

        ListView mLvPerson;
        TopAndHotAdapter mAdapter;

        public TopAndHotHolder(View itemView) {
            super(itemView);
            mLvPerson = (ListView) itemView.findViewById(R.id.lv_timetable);
            mAdapter = new TopAndHotAdapter();
            mLvPerson.setAdapter(mAdapter);
        }
    }

    public static class TopAndHotAdapter extends BaseAdapter {

        private  ArrayList<DocItemBean> rssItems;
        private Context context;
        private int split;

        public TopAndHotAdapter(){
            rssItems = new ArrayList<>();
        }

        public void setDate( ArrayList<DocItemBean> rssItems,Context context,int split){
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
        public DocItemBean getItem(int position) {
            return rssItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            NormalHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_tag_doc_list_top_hot,
                        null);
                holder = new NormalHolder();
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tvChapter = (TextView) convertView.findViewById(R.id.tv_chapter);
                convertView.setTag(holder);
            }else {
                holder = (NormalHolder) convertView.getTag();
            }
            DocItemBean rss = getItem(position);
            holder.tvTitle.setText(rss.doc.title);
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

    public class DocViewHolder extends RecyclerView.ViewHolder{

        private View rlTopPanner;
        public ImageView ivClubAvatar;
        public TextView tvClubName;
        // 瀑布流才有
        //public View viewPostBottom;
        public ImageView ivDocHot;
        public ImageView ivCreatorAvatar;
        @FindView(R.id.tv_post_creator_name)
        public TextView tvCreatorName;
        @FindView(R.id.iv_post_owner_flag)
        public View ivClubCreatorFlag;
        @FindView(R.id.iv_doc_top_flag)
        public View ivDocTopFlag;
        @FindView(R.id.tv_post_update_time)
        public TextView tvPostDate;
        @FindView(R.id.tv_post_title)
        public TextView tvPostTitle;
        @FindView(R.id.tv_post_brief)
        public TextView tvPostBrief;
        @FindView(R.id.ll_image_3)
        public View llImagePack;
        @FindView(R.id.rl_post_special_flag)
        public View rlSpecialTypePack;
        @FindView(R.id.iv_post_flag_icon)
        public ImageView ivSpecialTypeIcon;
        @FindView(R.id.tv_post_vote_brief)
        public TextView tvVoteBrief;
        @FindView(R.id.rl_post_image_1)
        public View rlIcon1;
        @FindView(R.id.rl_post_image_2)
        public View rlIcon2;
        @FindView(R.id.rl_post_image_3)
        public View rlIcon3;
        @FindView(R.id.iv_post_image_1_gif_flag)
        public View ivGifIcon1;
        @FindView(R.id.iv_post_image_2_gif_flag)
        public View ivGifIcon2;
        @FindView(R.id.iv_post_image_3_gif_flag)
        public View ivGifIcon3;
        @FindView(R.id.iv_post_image_1)
        public ImageView ivIcon1;
        @FindView(R.id.iv_post_image_2)
        public ImageView ivIcon2;
        @FindView(R.id.iv_post_image_3)
        public ImageView ivIcon3;
        @FindView(R.id.tv_post_img_num)
        public TextView tvIconNum;
        @FindView(R.id.iv_pants)
        public ImageView ivPants;
        @FindView(R.id.iv_post_comment)
        public ImageView ivComment;
        @FindView(R.id.rl_doc_like_pack)
        public View rlPants;
        @FindView(R.id.rl_doc_comment_pack)
        public View rlComment;
        @FindView(R.id.tv_post_comment_num)
        public TextView tvCommentNum;
        @FindView(R.id.tv_post_pants_num)
        public TextView tvPantsNum;
        @FindView(R.id.view_flag_recommend)
        public View ivFlagRecommand;
        @FindView(R.id.rl_music_root)
        public View rlMusicRoot;
        @FindView(R.id.iv_item_image)
        public MyRoundedImageView musicImg;
        @FindView(R.id.tv_music_title)
        public TextView musicTitle;
        public View ivLevelColor;
        public TextView tvLevel;
        public TextView tvPostFromName;
        public ImageView ivIconClassOffical;
        //标签
        public View vDocSep;
        public DocLabelView docLabel;
        public NewDocLabelAdapter docLabelAdapter;

        public DocViewHolder(View itemView) {
            super(itemView);
            rlTopPanner = itemView.findViewById(R.id.rl_post_top);
            ivClubAvatar = (ImageView)itemView.findViewById(R.id.iv_post_club);
            tvClubName = (TextView)itemView.findViewById(R.id.tv_club_name);
            ivLevelColor = itemView.findViewById(R.id.iv_level_bg);
            tvLevel = (TextView)itemView.findViewById(R.id.tv_level);
            vDocSep = itemView.findViewById(R.id.view_doc_sep);
            docLabel = (DocLabelView) itemView.findViewById(R.id.dv_doc_label_root);
            tvPostFromName = (TextView) itemView.findViewById(R.id.tv_post_bottom_from_name);
            ivDocHot = (ImageView) itemView.findViewById(R.id.iv_class_doc_hot);
            ivIconClassOffical = (ImageView) itemView.findViewById(R.id.iv_class_post_img);
            Utils.view().inject(this, itemView);
            ivCreatorAvatar = (ImageView) itemView.findViewById(R.id.iv_post_creator);
            if (ivCreatorAvatar != null) {
                ivCreatorAvatar.setOnClickListener(mAvatarListener);
            }
           // if(mType != TYPE_SEARCH){
            docLabelAdapter = new NewDocLabelAdapter(mContext,true);
           // }
            ivIcon1.setOnClickListener(mIconListener);
            ivIcon2.setOnClickListener(mIconListener);
            ivIcon3.setOnClickListener(mIconListener);
            if(tvPostFromName != null){
                tvPostFromName.setVisibility(View.GONE);
            }
           // }
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
                }else if(v.equals(ivIconClassOffical)){
                    index = 0;
                }

                Context context = v.getContext();
                final DocItemBean docBean = (DocItemBean) v.getTag(R.id.id_filebean);
                Intent intent = new Intent(context, ImageBigSelectActivity.class);
                intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, docBean.doc.images);
                intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX, index);
                // 以后可选择 有返回数据
                context.startActivity(intent);

            }
        };

        private void plusLabel(final DocItemBean post, final int position){
            if (!NetworkUtils.checkNetworkAndShowError(mContext)) {
                return;
            }
            if (post != null) {
                if (DialogUtils.checkLoginAndShowDlg(mContext)) {
                    final DocTag tagBean = post.tags.get(position);
                    if(tagBean.liked){
                        ((BaseActivity)mContext).createDialog();
                        Otaku.getDocV2().dislikeNewTag(PreferenceManager.getInstance(mContext).getToken(), tagBean.id, post.doc.id).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                            @Override
                            public void success(String token, String s) {
                                ((BaseActivity) mContext).finalizeDialog();
                                post.tags.remove(position);
                                tagBean.liked = false;
                                tagBean.likes--;
                                if (tagBean.likes > 0) {
                                    post.tags.add(position, tagBean);
                                }
                                if (post.tags.size() > 0) {
                                    vDocSep.setVisibility(View.VISIBLE);
                                } else {
                                    vDocSep.setVisibility(View.GONE);
                                }
                                docLabelAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void failure(String e) {
                                ((BaseActivity) mContext).finalizeDialog();
                            }
                        }));
                    }else {
                        ((BaseActivity)mContext).createDialog();
                        Otaku.getDocV2().likeNewTag(PreferenceManager.getInstance(mContext).getToken(), tagBean.id, post.doc.id).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                            @Override
                            public void success(String token, String s) {
                                ((BaseActivity) mContext).finalizeDialog();
                                post.tags.remove(position);
                                tagBean.liked = true;
                                tagBean.likes++;
                                post.tags.add(position, tagBean);
                                docLabelAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void failure(String e) {
                                ((BaseActivity) mContext).finalizeDialog();
                            }
                        }));
                    }
                }
            }
        }
    }

}
