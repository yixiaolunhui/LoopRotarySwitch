package com.dalong.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.dalong.library.adapter.LoopViewAdapter;
import com.dalong.library.listener.OnItemClickListener;
import com.dalong.library.listener.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 水平旋转轮播控件
 */
public class LoopViewLayout extends RelativeLayout implements ILoopView<LoopViewAdapter>, LoopViewAdapter.OnLoopViewChangeListener {

    private final static int LoopR = 330;

    private final static int vertical = 0;//竖直

    private final static int horizontal = 1;//水平

    private int mOrientation = horizontal;//方向

    private Context mContext;//上下文

    private ValueAnimator restAnimator = null;//回位动画

    private ValueAnimator rAnimation = null;//半径动画

    private ValueAnimator zAnimation = null;

    private ValueAnimator xAnimation = null;

    private int loopRotationX = 0, loopRotationZ = 0;//x轴旋转和轴旋转，y轴无效果

    private GestureDetector mGestureDetector = null;//手势类

    private int selectItem = 0;//当前选择项

    private int size = 0;//个数

    private float r = LoopR;//半径

    private float multiple = 2f;//倍数

    private float distance = multiple * r;//camera和观察的旋转物体距离， 距离越长,最大物体和最小物体比例越不明显

    private float angle = 0;    //旋转的角度

    private float last_angle = 0;    //最后的角度，用来记录上一次取消touch之后的角度

    private boolean autoRotation = false;//自动旋转

    private boolean touching = false;//正在触摸

    private AutoScrollDirection autoRotatinDirection = AutoScrollDirection.LEFT; //默认自动滚动是从右往左

    private OnItemSelectedListener onItemSelectedListener = null;//选择事件接口

    private OnItemClickListener onItemClickListener = null;//被点击的回调

    private boolean isCanClickListener = true;//是否可以点击回调

    private float x;//移动的x是否符合回调点击事件

    private float limitX;//滑动倒最低30

    public enum AutoScrollDirection {
        LEFT, RIGHT
    }

    /**
     * 构造方法
     *
     * @param context
     */
    public LoopViewLayout(Context context) {
        this(context, null);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public LoopViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public LoopViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        setGravity(Gravity.CENTER);
        ViewConfiguration mVelocityTracker = new ViewConfiguration();
        limitX = mVelocityTracker.getScaledTouchSlop();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoopViewLayout);
        mOrientation = typedArray.getInt(R.styleable.LoopViewLayout_orientation, horizontal);
        autoRotation = typedArray.getBoolean(R.styleable.LoopViewLayout_autoRotation, false);
        r = typedArray.getDimension(R.styleable.LoopViewLayout_r, LoopR);
        int direction = typedArray.getInt(R.styleable.LoopViewLayout_direction, 0);
        typedArray.recycle();
        mGestureDetector = new GestureDetector(context, getGeomeryController());
        if (mOrientation == horizontal) {//如果是水平 z值为0  如果是竖直z值为90
            loopRotationZ = 0;
        } else {
            loopRotationZ = 90;
        }
        if (direction == 0) {//设置自定滚动的方向
            autoRotatinDirection = AutoScrollDirection.LEFT;
        } else {
            autoRotatinDirection = AutoScrollDirection.RIGHT;
        }
        loopHandler.setLoop(autoRotation);

    }

    /**
     * handler处理
     */
    LoopViewHandler loopHandler = new LoopViewHandler(2000) {
        @Override
        public void doScroll() {
            try {
                if (size != 0) {//判断自动滑动从那边开始
                    float perAngle = 0;
                    switch (autoRotatinDirection) {
                        case LEFT:
                            perAngle = 360f / size;
                            break;
                        case RIGHT:
                            perAngle = -360f / size;
                            break;
                    }
                    if (angle == 360f) {
                        angle = 0f;
                    }
                    AnimRotationTo(angle + perAngle, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 手势
     *
     * @return
     */
    private GestureDetector.SimpleOnGestureListener getGeomeryController() {
        return new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                angle += Math.cos(Math.toRadians(loopRotationZ)) * (distanceX / 4)
                        + Math.sin(Math.toRadians(loopRotationZ)) * (distanceY / 4);
                updateLoopViews();
                return true;
            }
        };
    }

    /**
     * 更新view位置
     */
    public void updateLoopViews() {
        int count = getChildCount();
        Map<Integer, Float> map = new HashMap<>();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            float radians = angle + 180 - (float) (i * 360f / size);
            float x0 = (float) Math.sin(Math.toRadians(radians)) * r;
            float y0 = (float) Math.cos(Math.toRadians(radians)) * r;
            float scale0 = (distance - y0) / (distance + r);//计算子view之间的比例，可以看到distance越大的话 比例越小，也就是大小就相差越小
            view.setScaleX(Math.max(scale0, 0.4f));//对view进行缩放
            view.setScaleY(Math.max(scale0, 0.4f));//对view进行缩放
            view.setAlpha(Math.max(scale0, 0.4f));
            map.put((Integer) view.getTag(), scale0);

            float rotationX_y = (float) Math.sin(Math.toRadians(loopRotationX * Math.cos(Math.toRadians(radians)))) * r;
            float rotationZ_y = -(float) Math.sin(Math.toRadians(-loopRotationZ)) * x0;
            float rotationZ_x = (((float) Math.cos(Math.toRadians(-loopRotationZ)) * x0) - x0);
            view.setTranslationX(x0 + rotationZ_x);
            view.setTranslationY(rotationX_y + rotationZ_y);
        }
        postInvalidate();
    }


    /**
     * 排序
     */
    private <T> void sortList(Map<Integer, Float> map) {
        List<Map.Entry<Integer, Float>> list = new ArrayList<Map.Entry<Integer, Float>>(map.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
            @Override
            public int compare(Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        for (Map.Entry<Integer, Float> mapping : list) {
            getChildAt(mapping.getKey()).bringToFront();
        }

    }


    public void RAnimation() {
        RAnimation(1f, r);
    }

    public void RAnimation(boolean fromZeroToLoopR) {
        if (fromZeroToLoopR) {
            RAnimation(1f, LoopR);
        } else {
            RAnimation(LoopR, 1f);
        }
    }

    public void RAnimation(float from, float to) {
        if (rAnimation != null) {
            if (rAnimation.isRunning()) {
                rAnimation.cancel();
            }
        }
        rAnimation = ValueAnimator.ofFloat(from, to);
        rAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                r = (Float) valueAnimator.getAnimatedValue();
                updateLoopViews();
            }
        });
        rAnimation.setInterpolator(new DecelerateInterpolator());
        rAnimation.setDuration(2000);
        rAnimation.start();
    }


    /**
     * 复位
     */
    private void restPosition() {
        if (size == 0) {
            return;
        }
        float finall = 0;
        float part = 360f / size;//一份的角度
        if (angle < 0) {
            part = -part;
        }
        float minvalue = (int) (angle / part) * part;//最小角度
        float maxvalue = (int) (angle / part) * part + part;//最大角度
        if (angle >= 0) {//分为是否小于0的情况
            if (angle - last_angle > 0) {
                finall = maxvalue;
            } else {
                finall = minvalue;
            }
        } else {
            if (angle - last_angle < 0) {
                finall = maxvalue;
            } else {
                finall = minvalue;
            }
        }
        AnimRotationTo(finall, null);
    }


    /**
     * 动画
     *
     * @param finall
     * @param complete
     */
    private void AnimRotationTo(float finall, final Runnable complete) {
        if (angle == finall) {//如果相同说明不需要旋转
            return;
        }
        restAnimator = ValueAnimator.ofFloat(angle, finall);
        restAnimator.setInterpolator(new DecelerateInterpolator());//设置旋转减速插值器
        restAnimator.setDuration(300);

        restAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (touching == false) {
                    angle = (Float) animation.getAnimatedValue();
                    updateLoopViews();
                }
            }
        });
        restAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (touching == false) {
                    selectItem = calculateItem();
                    if (selectItem < 0) {
                        selectItem = size + selectItem;
                    }
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.selected(selectItem, getChildAt(selectItem));
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        if (complete != null) {
            restAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    complete.run();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        restAnimator.start();
    }

    /**
     * 通过角度计算是第几个item
     *
     * @return
     */
    private int calculateItem() {
//        int i = (int) (angle / (360f / size)) % size;
//        return i >= 0 ? i : i + size;

        int maxIndex = 0;
        float maxScale = 0;
        for (int i = 0; i < size; i++) {
            View view = getChildAt(i);
            float scaleX = view.getScaleX();
            if (scaleX >= maxScale) {
                maxScale = scaleX;
                maxIndex = i;
            }
        }
        return (int) getChildAt(maxIndex).getTag();
    }

    /**
     * 触摸操作
     *
     * @param event
     * @return
     */
    private boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            last_angle = angle;
            touching = true;
        }
        boolean sc = mGestureDetector.onTouchEvent(event);
        if (sc) {
            this.getParent().requestDisallowInterceptTouchEvent(true);//通知父控件勿拦截本控件
        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            touching = false;
            restPosition();
            return true;
        }
        return true;
    }


    /**
     * 触摸方法
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isCanClickListener(event);
        return true;
    }


    /**
     * 触摸停止计时器，抬起设置可下啦刷新
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        onTouch(ev);
        isCanClickListener(ev);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否可以点击回调
     *
     * @param event
     */
    public void isCanClickListener(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                if (autoRotation) {
                    loopHandler.removeMessages(LoopViewHandler.MSG_WHAT);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (autoRotation) {
                    loopHandler.sendEmptyMessageDelayed(LoopViewHandler.MSG_WHAT, loopHandler.mLoopTime);
                }
                isCanClickListener = (event.getX() - x < limitX) ? true : false;
                break;
        }
    }


    /**
     * 获取角度
     *
     * @return
     */
    public float getAngle() {
        return angle;
    }


    /**
     * 设置角度
     *
     * @param angle
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * 获取距离
     *
     * @return
     */
    public float getDistance() {
        return distance;
    }

    /**
     * 设置距离
     *
     * @param distance
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }

    /**
     * 获取半径
     *
     * @return
     */
    public float getR() {
        return r;
    }

    /**
     * 获取选择是第几个item
     *
     * @return
     */
    public int getSelectItem() {
        return selectItem;
    }


    /**
     * 设置指定位置
     *
     * @param pos
     */
    public void setSelectItem(int pos) {
        int count = getChildCount();
        int currentIndex = calculateItem();
        if (currentIndex == pos) {
            return;
        }
        //目标切换的view
        View targetView = getChildAt(pos);
        float perAngle;
        //当前位置和目标位置的差值(不是直接相减 而是位置相差多少个距离）
        int difPos;
        if (targetView.getTranslationX() >= 0) {//在右边
            if (pos >= currentIndex) {
                difPos = Math.abs(pos - currentIndex);
            } else {
                difPos = Math.abs(pos + count - currentIndex);
            }
            perAngle = 360f / size;
        } else {//在左边
            if (currentIndex >= pos) {
                difPos = Math.abs(currentIndex - pos);
            } else {
                difPos = Math.abs(currentIndex + count - pos);
            }
            perAngle = -360f / size;
        }
        AnimRotationTo(angle + perAngle * difPos, null);
    }


    /**
     * 设置半径
     *
     * @param r
     */
    public LoopViewLayout setR(float r) {
        this.r = r;
        distance = multiple * r;
        return this;
    }

    /**
     * 选中回调接口实现
     *
     * @param onItemSelectedListener
     */
    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    /**
     * 点击事件回调
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    /**
     * 设置是否自动切换
     *
     * @param autoRotation
     */
    public LoopViewLayout setAutoRotation(boolean autoRotation) {
        this.autoRotation = autoRotation;
        loopHandler.setLoop(autoRotation);
        return this;
    }

    /**
     * 获取自动切换时间
     *
     * @return
     */
    public long getAutoRotationTime() {
        return loopHandler.mLoopTime;
    }

    /**
     * 设置自动切换时间间隔
     *
     * @param autoRotationTime
     */
    public LoopViewLayout setAutoRotationTime(long autoRotationTime) {
        loopHandler.setLoopTime(autoRotationTime);
        return this;
    }

    /**
     * 是否自动切换
     *
     * @return
     */
    public boolean isAutoRotation() {
        return autoRotation;
    }

    /**
     * 设置倍数
     *
     * @param mMultiple
     * @return
     */
    public LoopViewLayout setMultiple(float mMultiple) {
        this.multiple = mMultiple;
        distance = multiple * r;
        return this;
    }

    public LoopViewLayout setAutoScrollDirection(AutoScrollDirection mAutoScrollDirection) {
        this.autoRotatinDirection = mAutoScrollDirection;
        return this;
    }

    public void createXAnimation(int from, int to, boolean start) {
        if (xAnimation != null) if (xAnimation.isRunning() == true) xAnimation.cancel();
        xAnimation = ValueAnimator.ofInt(from, to);
        xAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                loopRotationX = (Integer) animation.getAnimatedValue();
                updateLoopViews();
            }
        });
        xAnimation.setInterpolator(new DecelerateInterpolator());
        xAnimation.setDuration(2000);
        if (start) xAnimation.start();
    }


    public ValueAnimator createZAnimation(int from, int to, boolean start) {
        if (zAnimation != null) if (zAnimation.isRunning() == true) zAnimation.cancel();
        zAnimation = ValueAnimator.ofInt(from, to);
        zAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                loopRotationZ = (Integer) animation.getAnimatedValue();
                updateLoopViews();
            }
        });
        zAnimation.setInterpolator(new DecelerateInterpolator());
        zAnimation.setDuration(2000);
        if (start) zAnimation.start();
        return zAnimation;
    }

    /**
     * 设置方向
     *
     * @param mOrientation
     * @return
     */
    public LoopViewLayout setOrientation(int mOrientation) {
        setHorizontal(mOrientation == horizontal, false);
        return this;
    }

    public LoopViewLayout setHorizontal(boolean horizontal, boolean anim) {
        if (anim) {
            if (horizontal) {
                createZAnimation(getLoopRotationZ(), 0, true);
            } else {
                createZAnimation(getLoopRotationZ(), 90, true);
            }
        } else {
            if (horizontal) {
                setLoopRotationZ(0);
            } else {
                setLoopRotationZ(90);
            }
            updateLoopViews();
        }
        return this;
    }

    public LoopViewLayout setLoopRotationX(int loopRotationX) {
        this.loopRotationX = loopRotationX;
        return this;
    }

    public LoopViewLayout setLoopRotationZ(int loopRotationZ) {
        this.loopRotationZ = loopRotationZ;
        return this;
    }

    public int getLoopRotationX() {
        return loopRotationX;
    }

    public int getLoopRotationZ() {
        return loopRotationZ;
    }

    public ValueAnimator getRestAnimator() {
        return restAnimator;
    }

    public ValueAnimator getrAnimation() {
        return rAnimation;
    }

    public void setzAnimation(ValueAnimator zAnimation) {
        this.zAnimation = zAnimation;
    }

    public ValueAnimator getzAnimation() {
        return zAnimation;
    }

    public void setxAnimation(ValueAnimator xAnimation) {
        this.xAnimation = xAnimation;
    }

    public ValueAnimator getxAnimation() {
        return xAnimation;
    }


    LoopViewAdapter mAdapter;

    @Override
    public LoopViewAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(LoopViewAdapter adapter) {
        if (adapter == null) {
            throw new RuntimeException("adapter must not be null");
        }
        if (mAdapter != null) {
            throw new RuntimeException("you have already set an Adapter");
        }
        this.mAdapter = adapter;
        mAdapter.setOnLoopViewChangeListener(this);
    }

    /**
     * 重置
     */
    public void reset() {
        removeAllViews();
        selectItem = 0;
        angle = 0;
        last_angle = 0;
    }

    /**
     * 初始化view
     */
    public void initLoopViews() {
        reset();
        if (mAdapter != null) {
            size = mAdapter.getCount();
            if (size == 0) return;
            for (int i = 0; i < size; i++) {
                View view = mAdapter.getView(i, getChildAt(i), this); //获取指定的子view
                final int position = i;
                view.setTag(i);
                addView(view);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //对子view添加点击事件
                        if (position != selectItem) {
                            setSelectItem(position);
                        } else {
                            if (isCanClickListener && onItemClickListener != null) {
                                onItemClickListener.onItemClick(position, getChildAt(position));
                            }
                        }
                    }
                });

            }
        }
    }


    @Override
    public void notifyDataSetChanged() {
        initLoopViews();
        updateLoopViews();
        if (autoRotation) {
            loopHandler.sendEmptyMessageDelayed(LoopViewHandler.MSG_WHAT, loopHandler.mLoopTime);
        }

        if (onItemSelectedListener != null) {
            isCanClickListener = true;
            onItemSelectedListener.selected(selectItem, getChildAt(selectItem));
        }
    }
}
