package com.empty.refreshlib.library.swipemenulistview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.empty.refreshlib.R;
import com.empty.refreshlib.library.ProgressWheel;


public class SwipeMenuListView extends ListView implements OnScrollListener {

    private final static String TAG = SwipeMenuListView.class.getName();

    //根据需求是否要实现下拉刷新的回调
    public boolean needLoadMore;

    // 下拉比例
    private final static int RATIO = 2;

    // 区分PULL和RELEASE的距离的大小
    private static final int SPACE = 20;

    // 定义header的四种状态
    private static final int NONE = 0;
    private static final int PULL = 1;
    private static final int RELEASE = 2;
    public static final int REFRESHING = 3;
    private static final int FINISHREFRESH = 4;
    private int state;

    private LayoutInflater inflater;
    private View           header;
    // private View footer;

    private TextView      tip;
    private ImageView     arrow;
    /**
     * 圆形进度条
     */
    private ProgressWheel refreshing;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    private int startY;

    private int firstVisibleItem;
    private int scrollState;
    private int headerContentInitialHeight;
    private int headerContentHeight;

    // 只有在listview第一个item显示的时候（listview滑到了顶部）才进行下拉刷新， 否则此时的下拉只是滑动listview
    private boolean isRecorded;
    private boolean refreshEnable = true;

    public RefreshListener onRefreshListener;

    // 下拉刷新监听
    public void setOnRefreshListener(RefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    private void initView(Context context) {

        state = NONE;// 初始化状态
        // 设置箭头特效
        animation = new RotateAnimation( 0, -180,
                                         RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                         RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(200);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation( -180, 0,
                                                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

        inflater = LayoutInflater.from( context);

        header = inflater.inflate(R.layout.swipe_listivew_header, null);
        arrow = (ImageView ) header.findViewById( R.id.arrow);
        tip = (TextView ) header.findViewById( R.id.tip);
        refreshing = (ProgressWheel ) header.findViewById( R.id.progressbar);

        // 为listview添加头部和尾部，并进行初始化
        // headerContentInitialHeight = header.getPaddingTop();
        measureView(header);

        headerContentHeight = header.getMeasuredHeight();

        topPadding(-headerContentHeight);// 使得heand隐藏
        this.addHeaderView(header);
        this.setOnScrollListener(this);
    }

    public void onRefresh() {
        if (onRefreshListener != null) {
            onRefreshListener.onRefresh();
        }
    }

    public void onRefreshComplete() {

        state = NONE;
        refreshHeaderViewByState();

    }

    int mFirstVisibleItem;
    int mVisibleItemCount;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;

        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;

        // 消息列表需要加载更多
        if (needLoadMore) {

            if ((mFirstVisibleItem + mVisibleItemCount) == this.getAdapter()
                    .getCount()
                    && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {

                if (listener != null) {
                    listener.load();
                }

            }
        }
    }

    private LoadMoreListener listener;

    public interface LoadMoreListener {

        public void load();
    }

    public void setLoadMoreListener(LoadMoreListener iListener) {
        if (iListener != null) {
            listener = iListener;
        }

    }

    private void whenMove(MotionEvent ev) {
        if (!isRecorded) {
            return;
        }
        int tmpY = (int) ev.getY();
        int space = (tmpY - startY) / RATIO;// //////////此处改动
        int topPadding = space - headerContentHeight;// 下拉的距离-头部的高度
        switch (state) {
            case NONE:// 如果当前状态是正常状态
                if (space > 0) {
                    state = PULL;
                    refreshHeaderViewByState();
                }
                break;
            case PULL:// 如果当前状态是正在下拉状态
                topPadding(topPadding);// 刷新头部的paddingtop值
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL
                        && space > headerContentHeight + SPACE) {
                    state = RELEASE;
                    refreshHeaderViewByState();
                }
                break;
            case RELEASE:// 如果当前状态是正在释放状态
                topPadding(topPadding);
                if (space > 0 && space < headerContentHeight + SPACE) {
                    state = PULL;
                    refreshHeaderViewByState();
                } else if (space <= 0) {
                    state = NONE;
                    refreshHeaderViewByState();
                }
                break;
        }

    }

    /**
     * *调整header的大小,其实调整的只是距离顶部的高度Padding值
     */
    private void topPadding(int topPadding) {
        header.setPadding(header.getPaddingLeft(), topPadding,
                header.getPaddingRight(), header.getPaddingBottom());
        header.invalidate();
    }

    /**
     * 根据当前状状态调整 头部 的视图
     */
    private void refreshHeaderViewByState() {
        switch (state) {
            case NONE:
                topPadding(-headerContentHeight);
                tip.setText(R.string.pull_to_refresh);// 下拉可以刷新
                refreshing.setVisibility( View.GONE);// 进度条
                arrow.clearAnimation();
                arrow.setImageResource(R.drawable.pull_to_refresh_arrow);
                break;
            case PULL:
                arrow.setVisibility( View.VISIBLE);
                tip.setVisibility( View.VISIBLE);
                refreshing.setVisibility( View.GONE);
                tip.setText(R.string.pull_to_refresh);
                arrow.clearAnimation();
                arrow.setAnimation(reverseAnimation);
                break;
            case RELEASE:
                arrow.setVisibility( View.VISIBLE);
                tip.setVisibility( View.VISIBLE);
                refreshing.setVisibility( View.GONE);
                tip.setText(R.string.pull_to_refresh);
                tip.setText(R.string.release_to_refresh);
                arrow.clearAnimation();
                arrow.startAnimation(animation);

                break;
            case REFRESHING:
                topPadding(headerContentInitialHeight);
                refreshing.setVisibility( View.VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility( View.GONE);
                tip.setVisibility( View.VISIBLE);
                tip.setText(R.string.refreshing);
                break;

            case FINISHREFRESH:
                refreshing.setVisibility( View.GONE);
                arrow.setVisibility( View.GONE);
                tip.setVisibility( View.GONE);
                break;

        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec( 0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec( lpHeight,
                                                           MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec( 0,
                                                           MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * 判断是否可以下拉刷新
     */
    public void setEnableRefresh(boolean flag) {
        refreshEnable = flag;
    }

    public interface RefreshListener {
        public void onRefresh();
    }

    public interface OnLoadListener {
        public void onLoad();
    }

    /****************************************************************************************************************************/

    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;

    private int MAX_Y = 5;
    private int MAX_X = 3;
    private float mDownX;
    private float mDownY;
    private int mTouchState;
    private int mTouchPosition;
    private SwipeMenuLayout mTouchView;
    private OnSwipeListener mOnSwipeListener;

    private SwipeMenuCreator        mMenuCreator;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private Interpolator            mCloseInterpolator;
    private Interpolator            mOpenInterpolator;

    public SwipeMenuListView(Context context) {
        super(context);
        init();
        initView(context);
    }

    public SwipeMenuListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        initView(context);
    }

    public SwipeMenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initView(context);
    }

    private void init() {
        MAX_X = dp2px(MAX_X);
        MAX_Y = dp2px(MAX_Y);
        mTouchState = TOUCH_STATE_NONE;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new SwipeMenuAdapter(getContext(), adapter) {
            @Override
            public void createMenu(SwipeMenu menu) {
                if (mMenuCreator != null) {
                    mMenuCreator.create(menu);
                }
            }

            @Override
            public void onItemClick(SwipeMenuView view, SwipeMenu menu,
                                    int index) {
                if (mOnMenuItemClickListener != null) {
                    mOnMenuItemClickListener.onMenuItemClick(
                            view.getPosition(), menu, index);
                }
                if (mTouchView != null) {
                    mTouchView.smoothCloseMenu();
                }
            }
        });
    }

    public void setCloseInterpolator(Interpolator interpolator) {
        mCloseInterpolator = interpolator;
    }

    public void setOpenInterpolator(Interpolator interpolator) {
        mOpenInterpolator = interpolator;
    }

    public Interpolator getOpenInterpolator() {
        return mOpenInterpolator;
    }

    public Interpolator getCloseInterpolator() {
        return mCloseInterpolator;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
            return super.onTouchEvent(ev);
        int action = MotionEventCompat.getActionMasked( ev);
        action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                try {
                    // 如果处于刷新状态，则屏蔽分发事件
                    if (state == REFRESHING) {
                        return false;
                    }

                    if (firstVisibleItem == 0) {
                        isRecorded = true;
                        startY = (int) ev.getY();
                    }

                    int oldPos = mTouchPosition;
                    mDownX = ev.getX();
                    mDownY = ev.getY();
                    mTouchState = TOUCH_STATE_NONE;

                    mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

                    if (mTouchPosition < 0 || mTouchPosition > this.getAdapter().getCount() - 1) {// 之前代码mTouchPosition < 0 || mTouchPosition >= this.getAdapter().getCount()-1
                        Log.e( "SwipeMenuListView", "数组越界");
                        return false;
                    }

                    if (mTouchPosition == oldPos && mTouchView != null
                            && mTouchView.isOpen()) {
                        mTouchState = TOUCH_STATE_X;
                        mTouchView.onSwipe(ev);
                        return true;
                    }

                    View view = getChildAt( mTouchPosition - getFirstVisiblePosition());

                    if (mTouchView != null && mTouchView.isOpen()) {
                        mTouchView.smoothCloseMenu();
                        mTouchView = null;
                        return super.onTouchEvent(ev);
                    }
                    if (view instanceof SwipeMenuLayout) {
                        mTouchView = (SwipeMenuLayout) view;
                    }
                    if (mTouchView != null) {
                        mTouchView.onSwipe(ev);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ev.setAction( MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                }

                break;
            case MotionEvent.ACTION_MOVE:



                try {

                    //向下拉
//                    if (ev.getY() > mDownY  && Math.abs(ev.getY() - mDownY) > Math.abs(ev.getX() - mDownX)){
//
//                        if (refreshEnable) {
//                            whenMove(ev);
//                        }
//
//                    }

                    //侧滑
                    //else if (ev.getY() < mDownY && Math.abs(ev.getY() - mDownY) < Math.abs(ev.getX() - mDownX)){

                    float dy = Math.abs( (ev.getY() - mDownY));
                    float dx = Math.abs( (ev.getX() - mDownX));
                    if (mTouchState == TOUCH_STATE_X) {
                        if (mTouchView != null) {
                            mTouchView.onSwipe(ev);
                        }
                        getSelector().setState(new int[]{0});
                        ev.setAction( MotionEvent.ACTION_CANCEL);
                        super.onTouchEvent(ev);
                        return true;
                    } else if (mTouchState == TOUCH_STATE_NONE) {
                        if ( Math.abs( dy) > MAX_Y) {
                            mTouchState = TOUCH_STATE_Y;
                        } else if (dx > MAX_X) {
                            mTouchState = TOUCH_STATE_X;
                            if (mOnSwipeListener != null) {
                                mOnSwipeListener.onSwipeStart(mTouchPosition);
                            }
                        }
                    }

                    //}

                } catch (Exception e) {
                    e.printStackTrace();
                    ev.setAction( MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_UP:

                try {
                    if (state == PULL) {
                        state = NONE;
                        refreshHeaderViewByState();
                    } else if (state == RELEASE) {
                        state = REFRESHING;
                        refreshHeaderViewByState();
                        onRefresh();
                    }
                    isRecorded = false;

                    if (mTouchState == TOUCH_STATE_X) {
                        if (mTouchView != null) {
                            mTouchView.onSwipe(ev);
                            if (!mTouchView.isOpen()) {
                                mTouchPosition = -1;
                                mTouchView = null;
                            }
                        }
                        if (mOnSwipeListener != null) {
                            mOnSwipeListener.onSwipeEnd(mTouchPosition);
                        }
                        ev.setAction( MotionEvent.ACTION_CANCEL);
                        super.onTouchEvent(ev);
                        return true;
                    }

                } catch (Exception e) {
                    ev.setAction( MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                }


                break;
        }
        return super.onTouchEvent(ev);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getmTouchState() {
        return mTouchState;
    }

    public void setmTouchState(int mTouchState) {
        this.mTouchState = mTouchState;
    }

    public void smoothOpenMenu(int position) {
        if (position >= getFirstVisiblePosition()
                && position <= getLastVisiblePosition()) {
            View view = getChildAt( position - getFirstVisiblePosition());
            if (view instanceof SwipeMenuLayout) {
                mTouchPosition = position;
                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                }
                mTouchView = (SwipeMenuLayout) view;
                mTouchView.smoothOpenMenu();
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, dp,
                                                getContext().getResources().getDisplayMetrics());
    }

    public void setMenuCreator(SwipeMenuCreator menuCreator) {
        this.mMenuCreator = menuCreator;
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.mOnSwipeListener = onSwipeListener;
    }

    public static interface OnMenuItemClickListener {
        void onMenuItemClick(int position, SwipeMenu menu, int index);
    }

    public static interface OnSwipeListener {
        void onSwipeStart(int position);

        void onSwipeEnd(int position);
    }
}