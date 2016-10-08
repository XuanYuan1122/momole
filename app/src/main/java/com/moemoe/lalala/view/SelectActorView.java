//package com.moemoe.lalala.view;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.AnimatorSet;
//import android.animation.ValueAnimator;
//import android.animation.ValueAnimator.AnimatorUpdateListener;
//import android.content.Context;
//import android.text.method.ScrollingMovementMethod;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.moemoe.lalala.R;
//import com.moemoe.lalala.galgame.Actor;
//import com.moemoe.lalala.galgame.ActorControl;
//import com.moemoe.lalala.utils.ResourceUtils;
//
///**
// * @author Haru
// * @version 创建时间：2015年10月12日 下午2:30:59
// */
//public class SelectActorView extends RelativeLayout implements View.OnClickListener {
//
//	public static final int SELECT_ACTOR = 0;
//	public static final int SELECT_ACTOR_DETAIL = 1;
//
//	private ImageView mIvActor;
//	private Button mBtnToDetail;
//	private Button mBtnToActor;
//	private TextView mTvActorName;
//	private TextView mTvIntroduction;
//	private TextView mGender;
//	private TextView mActorHeigh;
//	private TextView mBirthday;
//	private TextView mConstellation;
//	private TextView mBloodType;
//	private TextView mActorColor;
//
//	public EventListener mEventListenr;
//	private int mCurrentType = -1;
//	private View mCurrentView;
//	private LayoutInflater mInflater;
//	private ValueAnimator mScaleAnimator;
//	private ValueAnimator mAlphaAnimator;
//	private AnimatorSet mAnimSet;
//	private ValueAnimator mScaleAnimator1;
//	private ValueAnimator mAlphaAnimator1;
//	private AnimatorSet mAnimSet1;
//
//	private Context mContext;
//
//	public SelectActorView(Context ctx, int type) {
//		super(ctx);
//		mContext = ctx;
//		mInflater = LayoutInflater.from(ctx);
//		switch (type) {
//		case SELECT_ACTOR:
//			initActorView();
//			break;
//		case SELECT_ACTOR_DETAIL:
//			initDetailView();
//			break;
//		default:
//			break;
//		}
//		mCurrentType = type;
//	}
//
//	public void setActor(Actor actor) {
//		String temp = actor.getSelectEmoj().substring(0, actor.getSelectEmoj().indexOf("."));
//		mIvActor.setImageResource(ResourceUtils.getResource(getContext(), temp));
//		mTvActorName.setText(actor.getName());
//		mTvActorName.setBackgroundResource(
//				ResourceUtils.getResource(mContext, ActorControl.GALGAME_ACTOR_NAME + actor.getNickName()));
//		mTvIntroduction.setText(actor.getIntroduction());
//		mTvIntroduction.setBackgroundResource(
//				ResourceUtils.getResource(mContext, ActorControl.GALGAME_ACTOR_SUBTITLE + actor.getNickName()));
//	}
//
//	public void setDetail(Actor actor) {
//		mTvActorName.setText(actor.getName());
//		mTvActorName.setText(actor.getName());
//		mTvIntroduction.setText(actor.getIntroduction());
//		mBirthday.setText(actor.getBirthday());
//		mGender.setText(actor.getGender());
//	}
//
//	private void initActorView() {
//		if (mCurrentView != null) {
//			this.removeView(mCurrentView);
//		}
//		mCurrentView = mInflater.inflate(R.layout.item_select_actor, null);
//		this.addView(mCurrentView);
//		mIvActor = (ImageView) findViewById(R.id.iv_select_item_actor);
//		mBtnToDetail = (Button) findViewById(R.id.btn_select_item_to_detail);
//		mTvActorName = (TextView) findViewById(R.id.tv_select_item_name);
//		mTvIntroduction = (TextView) findViewById(R.id.tv_select_item_introduction);
//		mBtnToDetail.setOnClickListener(this);
//		mCurrentType = SELECT_ACTOR;
//	}
//
//	private void initDetailView() {
//		if (mCurrentView != null) {
//			this.removeView(mCurrentView);
//		}
//		mCurrentView = mInflater.inflate(R.layout.item_select_actor_detail, null);
//		this.addView(mCurrentView);
//		mBtnToActor = (Button) findViewById(R.id.btn_select_item_to_actor);
//		mTvActorName = (TextView) findViewById(R.id.tv_select_item_detail_name);
//		mTvIntroduction = (TextView) findViewById(R.id.tv_select_item_detail_introduction);
//		mGender = (TextView) findViewById(R.id.tv_select_item_detail_gender);
//		mActorHeigh = (TextView) findViewById(R.id.tv_select_item_detail_height);
//		mBirthday = (TextView) findViewById(R.id.tv_select_item_detail_birthday);
//		mConstellation = (TextView) findViewById(R.id.tv_select_item_detail_constellation);
//		mBloodType = (TextView) findViewById(R.id.tv_select_item_detail_blood_type);
//		mActorColor = (TextView) findViewById(R.id.tv_select_item_detail_color);
//		mBtnToActor.setOnClickListener(this);
//		mTvIntroduction.setMovementMethod(ScrollingMovementMethod.getInstance());
//		mCurrentType = SELECT_ACTOR_DETAIL;
//	}
//
//	@Override
//	public void onClick(View v) {
//		int id = v.getId();
//		switch (id) {
//		case R.id.btn_select_item_to_detail:
//			break;
//
//		case R.id.btn_select_item_to_actor:
//			break;
//		default:
//			break;
//		}
//		mEventListenr.beforeDetailClick();
//		mScaleAnimator = ValueAnimator.ofFloat(1.0f, 0);
//		mScaleAnimator.setTarget(SelectActorView.this);
//		mScaleAnimator.addUpdateListener(new AnimatorUpdateListener() {
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				SelectActorView.this.setScaleX((float) animation.getAnimatedValue());
//
//			}
//		});
//		mAlphaAnimator = ValueAnimator.ofFloat(1.0f, 0.5f);
//		mAlphaAnimator.setTarget(SelectActorView.this);
//		mAlphaAnimator.addUpdateListener(new AnimatorUpdateListener() {
//
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				SelectActorView.this.setAlpha((float) animation.getAnimatedValue());
//			}
//		});
//		mAnimSet = new AnimatorSet();
//		mAnimSet.setDuration(150);
//		mAnimSet.play(mScaleAnimator).with(mAlphaAnimator);
//		mAnimSet.start();
//		mAnimSet.addListener(new AnimatorListenerAdapter() {
//
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				switch (mCurrentType) {
//				case SELECT_ACTOR:
//					initDetailView();
//					break;
//				case SELECT_ACTOR_DETAIL:
//					initActorView();
//					break;
//				default:
//					break;
//				}
//				mScaleAnimator1 = ValueAnimator.ofFloat(0, 1.0f);
//				mScaleAnimator1.setTarget(SelectActorView.this);
//				mScaleAnimator1.addUpdateListener(new AnimatorUpdateListener() {
//					@Override
//					public void onAnimationUpdate(ValueAnimator animation) {
//						SelectActorView.this.setScaleX((float) animation.getAnimatedValue());
//					}
//				});
//				mAlphaAnimator1 = ValueAnimator.ofFloat(0.5f, 1.0f);
//				mAlphaAnimator1.setTarget(SelectActorView.this);
//				mAlphaAnimator1.addUpdateListener(new AnimatorUpdateListener() {
//
//					@Override
//					public void onAnimationUpdate(ValueAnimator animation) {
//						SelectActorView.this.setAlpha((float) animation.getAnimatedValue());
//					}
//				});
//				mAnimSet1 = new AnimatorSet();
//				mAnimSet1.setDuration(150);
//				mAnimSet1.play(mScaleAnimator1).with(mAlphaAnimator1);
//				mAnimSet1.start();
//				mAnimSet1.addListener(new AnimatorListenerAdapter() {
//					@Override
//					public void onAnimationEnd(Animator animation) {
//						mEventListenr.afterDetailClick();
//					}
//				});
//				if (mEventListenr != null) {
//					mEventListenr.onDetailClick(mCurrentType, SelectActorView.this);
//				}
//			}
//		});
//	}
//
//	public interface EventListener {
//		public void onDetailClick(int type, SelectActorView view);
//
//		public void beforeDetailClick();
//
//		public void afterDetailClick();
//	}
//
//	public void setEventLisenter(EventListener eventLisenter) {
//		this.mEventListenr = eventLisenter;
//	}
//
//	public int getCurrentType() {
//		return mCurrentType;
//	}
//
//}
