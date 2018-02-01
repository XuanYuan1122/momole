package com.moemoe.lalala.view.widget.trashcard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.TrashEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.MoeMoeCallback;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.activity.ImageTrashActivity;
import com.moemoe.lalala.view.activity.TrashActivity;
import com.moemoe.lalala.view.widget.view.NewDocLabelAdapter;
import com.moemoe.lalala.view.widget.view.DocLabelView;

/**
 * 卡片View项
 *
 * @author xmuSistone
 */
@SuppressLint("NewApi")
public class CardItemView extends FrameLayout {
    private Spring springX, springY;
    public ImageView imageView;
    public TextView favorite;
    public TextView add;
    private TextView title;
    private TextView content;
    private CardSlidePanel parentView;
    private DocLabelView docLabelView;
    private NewDocLabelAdapter docLabelAdapter;
    public View rlLayout;
    private int type;
    private MoeMoeCallback callback;
    private MoeMoeCallback tagCallback;

    public CardItemView(Context context) {
        this(context, null);
    }

    public CardItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardItemView);
        try {
            type =  a.getInteger(R.styleable.CardItemView_show_type,0);
        }finally {
            a.recycle();
        }
        if(type == 0){
            inflate(context, R.layout.item_trash_img_card, this);
            imageView = (ImageView) findViewById(R.id.iv_img);
        }else {
            inflate(context, R.layout.item_trash_text_card_new, this);
            content = (TextView) findViewById(R.id.tv_content);
        }

        favorite = (TextView) findViewById(R.id.tv_favorite);
        add = (TextView) findViewById(R.id.tv_add_label);
        title = (TextView) findViewById(R.id.tv_title);
        docLabelView = (DocLabelView) findViewById(R.id.dv_doc_label);
        rlLayout = findViewById(R.id.rl_layout);
        docLabelAdapter = new NewDocLabelAdapter(context,false);
        callback = new MoeMoeCallback() {
            @Override
            public void onSuccess(Object o) {
                if(docLabelAdapter != null && docLabelView != null){
                    docLabelAdapter.getTags().add((DocTagEntity) o);
                    docLabelView.notifyAdapter();
                }
            }
        };
        tagCallback = new MoeMoeCallback() {
            @Override
            public void onSuccess(Object o) {
                if(docLabelAdapter != null && docLabelView != null){
                    DocTagEntity tagBean = docLabelAdapter.getItem((int) o);
                    docLabelAdapter.getTags().remove((int) o);
                    tagBean.setLiked(!tagBean.isLiked());
                    if(tagBean.isLiked()){
                        tagBean.setLikes(tagBean.getLikes() + 1);
                        docLabelAdapter.getTags().add((int) o, tagBean);
                    }else {
                        tagBean.setLikes(tagBean.getLikes() - 1);
                        if (tagBean.getLikes() > 0) {
                            docLabelAdapter.getTags().add((int) o, tagBean);
                        }
                    }
                    docLabelView.notifyAdapter();
                }
            }
        };
        initSpring();
    }

    private void initSpring() {
        SpringConfig springConfig = SpringConfig.fromBouncinessAndSpeed(15, 20);
        SpringSystem mSpringSystem = SpringSystem.create();
        springX = mSpringSystem.createSpring().setSpringConfig(springConfig);
        springY = mSpringSystem.createSpring().setSpringConfig(springConfig);

        springX.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                int xPos = (int) spring.getCurrentValue();
                setScreenX(xPos);
                if(parentView != null) parentView.onViewPosChanged(CardItemView.this);
            }
        });

        springY.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                int yPos = (int) spring.getCurrentValue();
                setScreenY(yPos);
                if(parentView != null) parentView.onViewPosChanged(CardItemView.this);
            }
        });
    }

    public void fillData(final TrashEntity itemData) {
        if(type == 0){
            Glide.with(getContext())
                    .load(StringUtils.getUrl(getContext(), ApiService.URL_QINIU +  itemData.getImage().getPath(), getResources().getDimensionPixelSize(R.dimen.x620),getResources().getDimensionPixelSize(R.dimen.y420), false, true))
                    .override(getResources().getDimensionPixelSize(R.dimen.x620), getResources().getDimensionPixelSize(R.dimen.y420))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .centerCrop()
                    .into(imageView);
        }else {
            content.setText(itemData.getContent());
        }
        favorite.setSelected(itemData.isMark());
        title.setText(itemData.getTitle());
        docLabelView.setDocLabelAdapter(docLabelAdapter);
        docLabelAdapter.setData(itemData.getTags(),false);
        if(parentView != null){
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(NetworkUtils.checkNetworkAndShowError(getContext()) && DialogUtils.checkLoginAndShowDlg(getContext())){
                        favorite.setSelected(!itemData.isMark());
                        if(type == 0){
                            ((ImageTrashActivity)getContext()).favoriteTrash(itemData);
                        }else {
                            ((TrashActivity)getContext()).favoriteTrash(itemData);
                        }
                    }
                }
            });
            add.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if(type == 0){
                        ((ImageTrashActivity)getContext()).createLabel(itemData.getDustbinId(),callback);
                    }else {
                        ((TrashActivity)getContext()).createLabel(itemData.getDustbinId(),callback);
                    }
                }
            });
            docLabelView.setItemClickListener(new DocLabelView.LabelItemClickListener() {
                @Override
                public void itemClick(int position) {
                    if (!NetworkUtils.checkNetworkAndShowError(getContext())) {
                        return;
                    }
                    if (DialogUtils.checkLoginAndShowDlg(getContext())) {
                        if(type == 0){
                            ((ImageTrashActivity)getContext()).addLabel(docLabelAdapter.getItem(position).getId(),itemData.getDustbinId(),docLabelAdapter.getItem(position).isLiked(),position,tagCallback);
                        }else {
                            ((TrashActivity)getContext()).addLabel(docLabelAdapter.getItem(position).getId(),itemData.getDustbinId(),docLabelAdapter.getItem(position).isLiked(),position,tagCallback);
                        }
                    }
                }
            });
        }
    }

    /**
     * 动画移动到某个位置
     */
    public void animTo(int xPos, int yPos) {
        setCurrentSpringPos(getLeft(), getTop());
        springX.setEndValue(xPos);
        springY.setEndValue(yPos);
    }

    /**
     * 设置当前spring位置
     */
    private void setCurrentSpringPos(int xPos, int yPos) {
        springX.setCurrentValue(xPos);
        springY.setCurrentValue(yPos);
    }

    public void setScreenX(int screenX) {
        this.offsetLeftAndRight(screenX - getLeft());
    }

    public void setScreenY(int screenY) {
        this.offsetTopAndBottom(screenY - getTop());
    }

    public void setParentView(CardSlidePanel parentView) {
        this.parentView = parentView;
    }

    public void onStartDragging() {
        springX.setAtRest();
        springY.setAtRest();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 兼容ViewPager，触点需要按在可滑动区域才行
            boolean shouldCapture = shouldCapture((int) ev.getX(), (int) ev.getY());
            if (shouldCapture && parentView != null) {
                parentView.getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判断(x, y)是否在可滑动的矩形区域内
     * 这个函数也被CardSlidePanel调用
     *
     * @param x 按下时的x坐标
     * @param y 按下时的y坐标
     * @return 是否在可滑动的矩形区域
     */
    public boolean shouldCapture(int x, int y) {
        int captureLeft = rlLayout.getLeft() + rlLayout.getPaddingLeft();
        int captureTop = rlLayout.getTop() + rlLayout.getPaddingTop();
        int captureRight = rlLayout.getRight() - rlLayout.getPaddingRight();
        int captureBottom = rlLayout.getBottom() - rlLayout.getPaddingBottom();

        if (x > captureLeft && x < captureRight && y > captureTop && y < captureBottom) {
            return true;
        }
        return false;
    }
}
