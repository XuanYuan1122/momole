package com.moemoe.lalala.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.Utils;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.ClubBean;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.MyRoundedImageView;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.moemoe.lalala.view.TagTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/5/3 0003.
 */
public class ClubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 分割线
     */
    private static final int VIEW_TYPE_SPILIT = 0;
    /**
     * 社团
     */
    public static final int VIEW_TYPE_CLUB = 1;
    public static final int VIEW_TYPE_CLUB_SEARCH = 2;
    public static final int VIEW_TYPE_FRIEND = 3;
    public static final int VIEW_TYPE_NORMAL = 4;


    /**
     * 第一根分割线：
     *
     * 推荐： 官方
     * 我的：我创建的
     */
    private int mPosFstSplit;
    /**
     * 第二根：
     *
     * 推荐： 推荐
     * 我的：-1
     */
    private int mPosSecSplit;
    /**
     * 第三根分割线位置
     *
     *  推荐： 所有
     * 我的：我的关注
     */
    private int mPosTrdSplit;
    private String[] mStrSplit;

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<ClubBean> mCreateData = new ArrayList<>();

    private ArrayList<ClubBean> mFollowData = new ArrayList<ClubBean>();
    /**
     * 关键字
     */
    private String mKeyWord;
    /**
     * 是否高亮
     */
    private boolean mNeedHighLight = false;
    private int mType = -1;

    public void setKeyWord(String keyWord){
        mKeyWord = keyWord;
        if (mKeyWord == null){
            mNeedHighLight = false;
        }else {
            mNeedHighLight = true;
        }

    }

    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public ClubListAdapter(Context context,int type,String keyWord){
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mType = type;
        if(type == VIEW_TYPE_CLUB){
            mStrSplit = new String[]{context.getString(R.string.label_office),
                    context.getString(R.string.label_recommend), context.getString(R.string.label_other)};
        }else if(type == VIEW_TYPE_FRIEND){
            mStrSplit = new String[]{context.getString(R.string.label_friend_group), context.getString(R.string.label_friend_follow_club)};
        }
        setKeyWord(keyWord);
        mPosFstSplit = -1;
        mPosSecSplit = -1;
        mPosTrdSplit = -1;
    }

    public void setClub1(ArrayList<ClubBean> followClub){
        mPosFstSplit = -1;
        mPosSecSplit = -1;
        mPosTrdSplit = -1;
        if(followClub == null || followClub.size() == 0){
            mCreateData = new ArrayList<>();
            mPosTrdSplit = 0;
        }else{
            mPosFstSplit = 0;
            mCreateData = followClub;
            if (mType == VIEW_TYPE_CLUB) {
                for (int i = 0; i < mCreateData.size(); i++) {
                    ClubBean bean = mCreateData.get(i);
                    if (!bean.isOfficalClub()) {
                        if(i == 0){
                            mPosSecSplit = 0;
                        }else{
                            mPosSecSplit = i + 1;
                        }
                        break;
                    }
                }
                if(mPosSecSplit == 0){
                    mPosFstSplit = -1;
                }

            } else {
                mPosSecSplit = -1;
            }
            if (mPosSecSplit >= 0 && mPosFstSplit >= 0) {
                // 前面有两条分割线
                mPosTrdSplit = mCreateData.size() + 2;
            } else {
                // 前面有1条分隔线
                mPosTrdSplit = mCreateData.size() + 1;
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<ClubBean> getClub1(){
        return mCreateData;
    }

    public void setClub2(ArrayList<ClubBean> followClub){
        if(followClub == null){
            mFollowData = new ArrayList<ClubBean>();
        }else{
            mFollowData = followClub;
        }
        notifyDataSetChanged();
    }

    public ArrayList<ClubBean> getClub2(){
        return mFollowData;
    }

    public int getClub2Count() {
        return mFollowData.size();
    }

    public void setData(ArrayList<ClubBean> createClub, ArrayList<ClubBean> followClub){
        setClub1(createClub);
        setClub2(followClub);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SPILIT){
            return new SplitViewHolder(mInflater.inflate(R.layout.item_label_split,parent,false));
        }else if(viewType == VIEW_TYPE_CLUB){
            return new ClubViewHolder(mInflater.inflate(R.layout.item_club,parent,false));
        }else if(viewType == VIEW_TYPE_CLUB_SEARCH){
            return new ClubViewHolder(mInflater.inflate(R.layout.item_club_search_result,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SplitViewHolder){
            SplitViewHolder viewHolder = (SplitViewHolder) holder;
            if (position == mPosFstSplit) {
                viewHolder.tv.setText(mStrSplit[0]);
            } else if (position == mPosSecSplit) {
                viewHolder.tv.setText(mStrSplit[1]);
            } else if (position == mPosTrdSplit) {
                viewHolder.tv.setText(mStrSplit[mStrSplit.length - 1]);
            }
        }else if(holder instanceof ClubViewHolder){
            final ClubViewHolder viewHolder = (ClubViewHolder) holder;
            ClubBean mGroupData = (ClubBean)getItem(position);
            if(mNeedHighLight){
                Spanned title = StringUtils.highLightKeyWord(mContext, mGroupData.title, mKeyWord);
                if(TextUtils.isEmpty(title)){
                    viewHolder.mTvGroupTitle.setText(mGroupData.title);
                }else{
                    viewHolder.mTvGroupTitle.setText(title);
                }
            }else{
                viewHolder.mTvGroupTitle.setText(mGroupData.title);
            }
//            Utils.image().bind(viewHolder.mIvIcon,StringUtils.getUrl(mContext, mGroupData.icon.path,DensityUtil.dip2px(80),DensityUtil.dip2px(80),false,false), new ImageOptions.Builder()
//                    .setSize(DensityUtil.dip2px(80), DensityUtil.dip2px(80))
//                    .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                    .setLoadingDrawableId(R.drawable.ic_default_club_m)
//                    .setFailureDrawableId(R.drawable.ic_default_club_m)
//                    .build());
            Picasso.with(mContext)
                    .load(StringUtils.getUrl(mContext, mGroupData.icon.path,DensityUtil.dip2px(80),DensityUtil.dip2px(80),false,false))
                    .resize(DensityUtil.dip2px(80), DensityUtil.dip2px(80))
                    .placeholder(R.drawable.ic_default_club_m)
                    .error(R.drawable.ic_default_club_m)
                    .config(Bitmap.Config.RGB_565)
                    .into(viewHolder.mIvIcon);
            if (mGroupData.isOfficalClub()) {
                viewHolder.mIvOfficeFlag.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mIvOfficeFlag.setVisibility(View.GONE);
            }
            if(mType != VIEW_TYPE_CLUB_SEARCH &&  mGroupData.list_bg != null){
                viewHolder.mRlofficalRoot.setVisibility(View.VISIBLE);
                viewHolder. mRoot.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.mIvOfficalBG.getLayoutParams();
                params.width = DensityUtil.getScreenWidth() - DensityUtil.dip2px(24);
                params.height = DensityUtil.dip2px(90);
                viewHolder.mIvOfficalBG.setLayoutParams(params);
//                Utils.image().bind(viewHolder.mIvOfficalBG, StringUtils.getUrl(mContext, mGroupData.list_bg.path, DensityUtil.getScreenWidth() - DensityUtil.dip2px(24), DensityUtil.dip2px(90), false, false), new ImageOptions.Builder()
//                        .setSize(DensityUtil.getScreenWidth() - DensityUtil.dip2px(24), DensityUtil.dip2px(90))
//                        .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                        .setFailureDrawableId(R.drawable.ic_default_doc_l)
//                        .setLoadingDrawableId(R.drawable.ic_default_doc_l)
//                        .build());
                Picasso.with(mContext)
                        .load(StringUtils.getUrl(mContext, mGroupData.list_bg.path, DensityUtil.getScreenWidth() - DensityUtil.dip2px(24), DensityUtil.dip2px(90)))
                        .resize(DensityUtil.getScreenWidth() - DensityUtil.dip2px(24), DensityUtil.dip2px(90))
                        .placeholder(R.drawable.ic_default_doc_l)
                        .error(R.drawable.ic_default_doc_l)
                        .config(Bitmap.Config.RGB_565)
                        .into(viewHolder.mIvOfficalBG);
                viewHolder.mIvOfficalName.setText(mGroupData.title);
                viewHolder.mTvMemberNum.setText("" + mGroupData.follower_num);
            }else{
                if(viewHolder.mRlofficalRoot != null){
                    viewHolder.mRlofficalRoot.setVisibility(View.GONE);
                }
                viewHolder. mRoot.setVisibility(View.VISIBLE);
                for(int i = 0; i < 5; i++){
                    if(mGroupData.tag != null){
                        if(i >= mGroupData.tag.length){
                            viewHolder.mTags[i].setVisibility(View.GONE);
                        }else{
                            viewHolder.mTags[i].setVisibility(View.VISIBLE);
                            viewHolder. mTags[i].setTag(mGroupData.tag[i]);
                        }
                    }else{
                        viewHolder.mTags[i].setVisibility(View.GONE);
                    }
                }
                viewHolder.mTvSecondBrief.setText(
                        mContext.getString(R.string.label_club_follower_doc_num, mGroupData.follower_num, mGroupData.doc_num));
            }
            viewHolder.itemView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(viewHolder.itemView, pos);
                    }
                }
            });
        }
    }

    public Object getItem(int position){
        Object ret = null;
        if (mType == VIEW_TYPE_CLUB || mType == VIEW_TYPE_FRIEND) {
            int fstSepNum = mPosFstSplit >= 0 ? 1 : 0;
            int secSepNum = mPosSecSplit >= 0 ? 1 : 0;

            if(position > 0){
                //  分割线1 - 2之间
                if(position < mPosSecSplit){
                    ret = mCreateData.get(position - fstSepNum);
                }else if(position > mPosSecSplit && position < mPosTrdSplit){
                    // 分割线2 - 3之间
                    ret = mCreateData.get(position - fstSepNum - secSepNum);
                }else if(position > mPosTrdSplit){
                    // 分割线3之后
                    int i = position - mPosTrdSplit - 1;
                    if(i == mFollowData.size()){
                        i--;
                    }
                    ret = mFollowData.get(i);
                }
            }
        }else{
            // 其他场合，没有分割线
            if (position < mCreateData.size()) {
                ret = mCreateData.get(position);
            } else {
                ret = mFollowData.get(position - mCreateData.size());
            }
        }
        return ret;
    }

    @Override
    public int getItemCount() {
        if(mType == VIEW_TYPE_CLUB || mType == VIEW_TYPE_FRIEND){
            int splitNum = 0;
            if(mPosFstSplit >= 0){
                splitNum ++;
            }
            if(mPosSecSplit >= 0){
                splitNum ++;
            }
            if(mFollowData.size() > 0){
                splitNum ++;
            }
            return mCreateData.size() + mFollowData.size() + splitNum;
        } else {
            return mCreateData.size() + mFollowData.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mType == VIEW_TYPE_CLUB || mType == VIEW_TYPE_FRIEND){
            if(position == 0 || position == mPosSecSplit || position == mPosTrdSplit){
                return VIEW_TYPE_SPILIT;
            }else{
                return VIEW_TYPE_CLUB;
            }
        }else if (mType == VIEW_TYPE_CLUB_SEARCH){
            return VIEW_TYPE_CLUB_SEARCH;
        }else {
            return VIEW_TYPE_CLUB;
        }
    }

    public class SplitViewHolder extends RecyclerView.ViewHolder{
        @FindView(R.id.tv_time_table)
        public TextView tv;

        public SplitViewHolder(View itemView) {
            super(itemView);
            Utils.view().inject(this, itemView);
        }
    }

    public class ClubViewHolder extends RecyclerView.ViewHolder{

        @FindView(R.id.rl_group_head_pack)
        public View mRoot;
        @FindView(R.id.iv_group_image)
        public MyRoundedImageView mIvIcon;
        @FindView(R.id.iv_group_office_flag)
        public View mIvOfficeFlag;
        @FindView(R.id.tv_group_name)
        public TextView mTvGroupTitle;
        @FindView(R.id.tv_group_update_time)
        public TextView mTvSecondBrief;
        public TagTextView mTags[] = new TagTextView[5];
        public View mRlofficalRoot;
        public ImageView mIvOfficalBG;
        public TextView mIvOfficalName;
        public TextView mTvMemberNum;

        public ClubViewHolder(View itemView) {
            super(itemView);
            Utils.view().inject(this, itemView);
            mRlofficalRoot = itemView.findViewById(R.id.rl_club_offical);
            mIvOfficalBG = (ImageView) itemView.findViewById(R.id.iv_club_bg);
            mIvOfficalName = (TextView) itemView.findViewById(R.id.tv_club_offical_name);
            mTvMemberNum = (TextView) itemView.findViewById(R.id.tv_club_member_num);
            mTags[0] = (TagTextView)itemView.findViewById(R.id.iv_group_tag1);
            mTags[1] = (TagTextView)itemView.findViewById(R.id.iv_group_tag2);
            mTags[2] = (TagTextView)itemView.findViewById(R.id.iv_group_tag3);
            mTags[3] = (TagTextView)itemView.findViewById(R.id.iv_group_tag4);
            mTags[4] = (TagTextView)itemView.findViewById(R.id.iv_group_tag5);
        }
    }
}
