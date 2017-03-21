package com.empty.refreshlib.library.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView.ScaleType;

import com.empty.refreshlib.R;
import com.empty.refreshlib.library.PullToRefreshBase;

@SuppressLint("ViewConstructor")
public class FlipLoadingLayout extends LoadingLayout {

	static final int FLIP_ANIMATION_DURATION = 150;

	private final Animation mRotateAnimation, mResetRotateAnimation;

	public FlipLoadingLayout(Context context, final PullToRefreshBase.Mode mode, final PullToRefreshBase.Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);

		final int rotateAngle = mode == PullToRefreshBase.Mode.PULL_FROM_START ? -180 : 180;

		mRotateAnimation = new RotateAnimation(0, rotateAngle, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		mRotateAnimation.setDuration(FLIP_ANIMATION_DURATION);
		mRotateAnimation.setFillAfter(true);

		mResetRotateAnimation = new RotateAnimation(rotateAngle, 0, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mResetRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		mResetRotateAnimation.setDuration(FLIP_ANIMATION_DURATION);
		mResetRotateAnimation.setFillAfter(true);
	}

	@Override
	protected void onLoadingDrawableSet(Drawable imageDrawable) {
		if (null != imageDrawable && mHeaderImage != null) {
			final int dHeight = imageDrawable.getIntrinsicHeight();//���drawable���еĸ߶�
			final int dWidth = imageDrawable.getIntrinsicWidth();
			/**
			 * We need to set the width/height of the ImageView so that it is
			 * square with each side the size of the largest drawable dimension.
			 * This is so that it doesn't clip when rotated.
			 */
			ViewGroup.LayoutParams lp = mHeaderImage.getLayoutParams();
			lp.width = lp.height = Math.max(dHeight, dWidth);
			mHeaderImage.requestLayout();//ǿ��ˢ�� onmesure �� onLayout ����


			/**
			 * We now rotate the Drawable so that is at the correct rotation,
			 * and is centered.
			 */

			mHeaderImage.setScaleType(ScaleType.MATRIX);

			Matrix matrix = new Matrix();
			matrix.postTranslate((lp.width - dWidth) / 2f, (lp.height - dHeight) / 2f);
			matrix.postRotate(getDrawableRotationAngle(), lp.width / 2f, lp.height / 2f);
			mHeaderImage.setImageMatrix(matrix);

		}
	}

	@Override
	protected void onPullImpl(float scaleOfLayout) {
		// NO-OP
	}

	@Override
	protected void pullToRefreshImpl() {
		if (mHeaderImage != null) {
			// Only start reset Animation, we've previously show the rotate anim
			if (mRotateAnimation == mHeaderImage.getAnimation()) {
				mHeaderImage.startAnimation(mResetRotateAnimation);
			}
		}

	}

	@Override
	protected void refreshingImpl() {
		if (mHeaderImage != null) {
			mHeaderImage.clearAnimation();
			mHeaderImage.setVisibility(View.INVISIBLE);
		}
		mHeaderProgress.setVisibility(View.VISIBLE);//��ʾ������
	}

	@Override
	protected void releaseToRefreshImpl() {
		if (mHeaderImage != null) {
			mHeaderImage.startAnimation(mRotateAnimation);
		}
	}

	@Override
	protected void resetImpl() {
		if (mHeaderImage != null) {
			mHeaderImage.clearAnimation();
			mHeaderImage.setVisibility(View.VISIBLE);
		}

		mHeaderProgress.setVisibility(View.GONE);

	}

	@Override
	protected int getDefaultDrawableResId() {
		return R.drawable.pull_to_refresh_arrow;
	}

	private float getDrawableRotationAngle() {
		float angle = 0f;
		switch (mMode) {
			case PULL_FROM_END:
				if (mScrollDirection == PullToRefreshBase.Orientation.HORIZONTAL) {
					angle = 90f;
				} else {
					angle = 180f;
				}
				break;

			case PULL_FROM_START:
				if (mScrollDirection == PullToRefreshBase.Orientation.HORIZONTAL) {
					angle = 270f;
				}
				break;

			default:
				break;
		}

		return angle;
	}

}
