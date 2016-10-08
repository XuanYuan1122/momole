package com.moemoe.lalala.utils;

import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yi on 2016/9/23.
 */

public class TrashListAnim extends SimpleItemAnimator{

    List<MoveInfo> mMoveInfoAnimatorViewList = new ArrayList<>();
    List<RecyclerView.ViewHolder> mRemoveAnimatorViewList = new ArrayList<>();

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        mRemoveAnimatorViewList.add(holder);
        return true;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        final View view = holder.itemView;
        fromX += ViewCompat.getTranslationX(holder.itemView);
        fromY += ViewCompat.getTranslationY(holder.itemView);
        AnimatorCompatHelper.clearInterpolator(holder.itemView);
        endAnimation(holder);
        int deltaX = toX - fromX;
        int deltaY = toY - fromY;
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder);
            return false;
        }
        if (deltaX != 0) {
            ViewCompat.setTranslationX(view, -deltaX);
        }
        if (deltaY != 0) {
            ViewCompat.setTranslationY(view, -deltaY);
        }
        mMoveInfoAnimatorViewList.add(new MoveInfo(holder , fromX, fromY, toX, toY));
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations() {
        boolean needRemove = mRemoveAnimatorViewList.size() > 0;
        for (RecyclerView.ViewHolder holder : mRemoveAnimatorViewList) {
            final ViewPropertyAnimatorCompat animation = ViewCompat.animate(holder.itemView);
            animation.translationY(-1000).setDuration(getRemoveDuration()).start();
            animation.alpha(0).setDuration(getRemoveDuration()).start();
        }
        mRemoveAnimatorViewList.clear();
        Runnable moveRunnable = new Runnable() {
            @Override
            public void run() {
                for (MoveInfo moveInfo : mMoveInfoAnimatorViewList) {
                    final RecyclerView.ViewHolder holder = moveInfo.holder;
                    final View view = moveInfo.holder.itemView;
                    final int deltaX = moveInfo.toX - moveInfo.fromX;
                    final int deltaY = moveInfo.toY - moveInfo.fromY;
                    if (deltaX != 0) {
                        ViewCompat.animate(view).translationX(0);
                    }
                    if (deltaY != 0) {
                        ViewCompat.animate(view).translationY(0);
                    }

                    final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
                    animation.setDuration(getMoveDuration()).setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {
                            dispatchMoveStarting(holder);
                        }
                        @Override
                        public void onAnimationCancel(View view) {
                            if (deltaX != 0) {
                                ViewCompat.setTranslationX(view, 0);
                            }
                            if (deltaY != 0) {
                                ViewCompat.setTranslationY(view, 0);
                            }
                        }
                        @Override
                        public void onAnimationEnd(View view) {

                        }
                    }).start();
                }



                mMoveInfoAnimatorViewList.clear();
            }
        };

        if (needRemove && mMoveInfoAnimatorViewList.size() > 0){
            mMoveInfoAnimatorViewList.get(0).holder.itemView.postDelayed(moveRunnable ,getRemoveDuration());
        }else {
            moveRunnable.run();
        }
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    private static class MoveInfo {
        RecyclerView.ViewHolder holder;
        int fromX;
        int fromY;
        int toX;
        int toY;

        MoveInfo(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }
}
