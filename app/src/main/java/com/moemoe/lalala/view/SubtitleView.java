//package com.moemoe.lalala.view;
//
//import android.content.Context;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.moemoe.lalala.galgame.ActorControl;
//import com.moemoe.lalala.galgame.GalControl;
//import com.moemoe.lalala.galgame.Sentence;
//import com.moemoe.lalala.utils.ResourceUtils;
//
//
//public class SubtitleView extends TextView {
//
//	private static final String TAG = "SubtitleView";
//
//	public interface EventListener {
//
//		/**
//		 * 用户点击完一条字幕
//		 */
//		public void onSubtitleFinish(Sentence sentence);
//
//	}
//
//	public interface FinishSubtitlesListener {
//		/**
//		 * 用户完成一整段对话
//		 *
//		 * @param sentence
//		 * @author Haru
//		 */
//		public void onSubtitlesFinish(Sentence sentence);
//	}
//
//	private static final int MSG_ROLLING = 101;
//
//	public static final int STATE_ROLLING = 1;
//	public static final int STATE_DONE = 0;
//
//	private static final int SLOW = 40;
//	private static final int FAST = 8;
//
//	private int mSnapTime = SLOW;
//
//	private Sentence mSentence;
//
//	private EventListener mEventListener;
//	private FinishSubtitlesListener mFinishSubtitlesListener;
//
//	private CharSequence mText;
//
//	private int mState;
//
//	private TextView mTvName, mTvChoiceA, mTvChoiceB;
//	private ImageView mIvLoli;
//	private View mViewTouchOutsideBg;
//
//	private Handler mHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case MSG_ROLLING:
//				showNext();
//				break;
//
//			default:
//				break;
//			}
//		}
//	};
//
//	public SubtitleView(Context context) {
//		this(context, null);
//	}
//
//	public SubtitleView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//
//		setListener();
//	}
//
//	public void setNameView(ImageView ivLoli, TextView name, TextView choiceA, TextView choiceB) {
//		mIvLoli = ivLoli;
//		mTvName = name;
//		mTvChoiceA = choiceA;
//		mTvChoiceB = choiceB;
//	}
//
//	public void setOutsideBgView(View outsideBgView) {
//		mViewTouchOutsideBg = outsideBgView;
//		mViewTouchOutsideBg.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				handleSubtitle(true);
//			}
//		});
//	}
//
//	private void setListener() {
//		setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				handleSubtitle(true);
//			}
//		});
//	}
//
//	public void handleSubtitle(boolean needNext) {
//		if (mState == STATE_ROLLING) {
//			mSnapTime = FAST;
//		} else {
//			if (mSentence != null) {
//				if (mSentence.hasChoice) {
//
//				} else {
//					if (needNext && mSentence.next > 0) {
//						Sentence s = GalControl.pickSentence(getContext(), mSentence, 0, 0);
//						if(s != null){
//							setSubTitle(s);
//						}else{
//							hideView();
//							if (mFinishSubtitlesListener != null) {
//								mFinishSubtitlesListener.onSubtitlesFinish(mSentence);
//							}
//						}
//					} else {
//						hideView();
//						if (mFinishSubtitlesListener != null) {
//							mFinishSubtitlesListener.onSubtitlesFinish(mSentence);
//						}
//					}
//					if (mEventListener != null) {
//						mEventListener.onSubtitleFinish(mSentence);
//					}
//				}
//			}
//		}
//	}
//
//	private void startRolling() {
//		mState = STATE_ROLLING;
//		setText("");
//		mSnapTime = SLOW;
//		mHandler.sendEmptyMessageDelayed(MSG_ROLLING, mSnapTime);
//	}
//
//	public boolean showAll() {
//		boolean res = false;
//		if (mState == STATE_ROLLING) {
//			setText(mText);
//			mState = STATE_DONE;
//			showChoice();
//		}
//		return res;
//	}
//
//	private void showNext() {
//		if (mState == STATE_ROLLING) {
//			int curLen = getText().length();
//			if (curLen < mText.length()) {
//				setText(mText.subSequence(0, curLen + 1));
//				mHandler.sendEmptyMessageDelayed(MSG_ROLLING, mSnapTime);
//			} else {
//				mState = STATE_DONE;
//				showChoice();
//			}
//		}
//	}
//
//	private void hideView() {
//		// setVisibility(View.INVISIBLE);
////		if (mIvLoli != null) {
////			mIvLoli.setImageResource(
////					ResourceUtils.getResource(getContext(), ActorControl.getCurrentActor(getContext()).getEmojDefault()
////							.substring(0, ActorControl.getCurrentActor(getContext()).getEmojDefault().indexOf("."))));
////		}
//		// if(mTvName != null){
//		// mTvName.setVisibility(View.INVISIBLE);
//		// }
//		if (mTvChoiceA != null && mTvChoiceB != null) {
//			mTvChoiceA.setVisibility(View.GONE);
//			mTvChoiceB.setVisibility(View.GONE);
//		}
//		if (mViewTouchOutsideBg != null) {
//			mViewTouchOutsideBg.setVisibility(View.GONE);
//		}
//	}
//
//	private void showChoice() {
//		if (mSentence != null) {
//			if (mSentence.hasChoice && mTvChoiceA != null && mTvChoiceB != null) {
//				mTvChoiceA.setVisibility(View.VISIBLE);
//				mTvChoiceB.setVisibility(View.VISIBLE);
//			}
//		}
//	}
//
//	public Sentence getSentence() {
//		return mSentence;
//	}
//
//	public void setSubTitle(Sentence sentence) {
//		mSentence = sentence;
//		if (mSentence != null) {
//			// loli_emoj
//			if(mIvLoli != null) {
//				if (!TextUtils.isEmpty(mSentence.loli_emoj)) {
//					try {
//						mIvLoli.setImageResource(ResourceUtils.getResource(getContext(),
//								mSentence.loli_emoj.substring(0, mSentence.loli_emoj.indexOf("."))));
//						if (mIvLoli.getDrawable() == null) {
//							mIvLoli.setImageResource(ActorControl.getCurrnetActorDefaultImg(getContext()));
//						}
//					} catch (Exception e) {
//					}
//				} else {
//					mIvLoli.setImageResource(ActorControl.getCurrnetActorDefaultImg(getContext()));
//				}
//			}
//			// ddd
//			mText = mSentence.sentence;
//			startRolling();
//			setVisibility(View.VISIBLE);
//			if (mTvName != null) {
//				mTvName.setText(mSentence.name);
//				// mTvName.setVisibility(View.VISIBLE);
//			}
//			if (mViewTouchOutsideBg != null) {
//				mViewTouchOutsideBg.setVisibility(View.VISIBLE);
//			}
//			if (mTvChoiceA != null && mTvChoiceB != null) {
//				mTvChoiceA.setVisibility(View.GONE);
//				mTvChoiceB.setVisibility(View.GONE);
//				if (mSentence.hasChoice) {
//					mTvChoiceA.setText(mSentence.choiceA);
//					mTvChoiceB.setText(mSentence.choiceB);
//					mTvChoiceA.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							mSentence.choiceAListener.onClick(v);
//							hideView();
//						}
//					});
//					mTvChoiceB.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							mSentence.choiceBListener.onClick(v);
//							hideView();
//						}
//					});
//				}
//			}
//		} else {
//			hideView();
//		}
//
//	}
//
//	public void setEventListener(EventListener listener) {
//		mEventListener = listener;
//	}
//
//	public void setFinishiSubtitleListener(FinishSubtitlesListener listener) {
//		mFinishSubtitlesListener = listener;
//	}
//}
