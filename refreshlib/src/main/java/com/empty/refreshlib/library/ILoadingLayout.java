package com.empty.refreshlib.library;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

public interface ILoadingLayout {

	/**
	 * Set the Last Updated Text. This displayed under the main label when
	 * Pulling
	 *
	 * @param label - Label to set
	 */
	public void setLastUpdatedLabel(CharSequence label);

	/**
	 * Set the drawable used in the loading layout. This is the same as calling
	 * <code>setLoadingDrawable(drawable, Mode.BOTH)</code>
	 *
	 * @param drawable - Drawable to display
	 */
	public void setLoadingDrawable(Drawable drawable);

	/**
	 * 设置下拉刷新或者上拉加载更多
	 */
	public void setPullLabel(CharSequence pullLabel);

	/**
	 * 正在刷新标题
	 */
	public void setRefreshingLabel(CharSequence refreshingLabel);

	/**
	 * 释放可以刷新
	 */
	public void setReleaseLabel(CharSequence releaseLabel);

	/**
	 * 设置文字的样式
	 */
	public void setTextTypeface(Typeface tf);

}