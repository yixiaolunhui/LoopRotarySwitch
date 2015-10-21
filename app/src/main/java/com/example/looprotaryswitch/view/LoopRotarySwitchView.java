package com.example.looprotaryswitch.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/***
 * 水平旋转轮播控件
 */
public class LoopRotarySwitchView extends RelativeLayout {

    private final static int LoopR = 200;

    private Context con;

    private ValueAnimator restAnimator = null;//回位动画

    private ValueAnimator rAnimation = null;//半径动画

    private GestureDetector mGestureDetector = null;//手势类

    private int selectItem = 0;//当前选择项

    private int size = 0;//个数

    private float r = LoopR;//半径

    private float BEISHU = 2.5f;//倍数

    private float distance = BEISHU * r;//camera和观察的旋转物体距离， 距离越长,最大物体和最小物体比例越不明显

    private float angle = 0;//角度

    private float last_angle = 0;

    private boolean autoRotation = false;//自动旋转

    private boolean touching = false;//正在触摸

    private List<View> views = new ArrayList<View>();//子view引用列表

    private OnItemSelectedListener onItemSelectedListener = null;//选择事件接口

    private OnLoopViewTouchListener onLoopViewTouchListener = null;//选择事件接口

    private OnItemClickListener onItemClickListener = null;//被点击的回调

    private boolean isCanClickListener=true;//是否可以点击回调

    private float x;//移动的x是否符合回调点击事件

    private float limitX=30;//滑动倒最低30

    /**
     * 构造方法
     *
     * @param context
     */
    public LoopRotarySwitchView(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public LoopRotarySwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public LoopRotarySwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        this.con = context;
        mGestureDetector = new GestureDetector(context, getGeomeryController());
    }

    /**
     * handler处理
     */
    LoopRotarySwitchViewHandler loopHandler = new LoopRotarySwitchViewHandler(3000) {
        @Override
        public void du() {
            try {
                if (size != 0) AnimRotationTo(angle - 360 / size, null);
            } catch (Exception e) {
            }
        }
    };

    /**
     * 排序
     *
     * @param list
     */
    private void sortList(List<View> list) {
        Comparator comp = new SortComparator();
        Collections.sort(list, comp);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).bringToFront();
//            list.get(i).setEnabled(i == (list.size() - 1) && angle % (360 / size) == 0 ? true : false);
        }
    }

    /**
     * 筛选器
     */
    private class SortComparator implements Comparator<View> {
        @Override
        public int compare(View lhs, View rhs) {
            int result = 0;
            try {
                result = (int) (1000 * lhs.getScaleX() - 1000 * rhs.getScaleX());
            } catch (Exception e) {
            }
            return result;
        }
    }

    /**
     * 手势
     *
     * @return
     */
    private GestureDetector.SimpleOnGestureListener getGeomeryController() {
        return new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                angle += distanceX / 4;
                invate();
                return true;
            }
        };
    }

    public void invate() {
        int width = getWidth();
        for (int i = 0; i < views.size(); i++) {
            float x0 = (float) Math.sin(Math.toRadians(angle + 180 - i * 360 / size)) * r;
            float y0 = (float) Math.cos(Math.toRadians(angle + 180 - i * 360 / size)) * r;
            float scale0 = (distance - y0) / (distance + r);
            views.get(i).setScaleX(scale0);
            views.get(i).setScaleY(scale0);
            views.get(i).setX(width / 2 + x0 - views.get(i).getWidth() / 2);
        }
        List<View> arr = new ArrayList<View>();
        for (int i = 0; i < views.size(); i++) {
            arr.add(views.get(i));
            views.get(i).setTag(i);
        }
        sortList(arr);
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            InitData();
            RAnimation();
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
        rAnimation = ValueAnimator.ofFloat(from, to);
        rAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                r = (Float) valueAnimator.getAnimatedValue();
                invate();
            }
        });
        rAnimation.setInterpolator(new DecelerateInterpolator());
        rAnimation.setDuration(2000);
        rAnimation.start();
    }

    /**
     * 初始化
     */
    public void InitData() {
        initView();
        if (onItemSelectedListener != null) {
            isCanClickListener = true;
            onItemSelectedListener.selected(selectItem, views.get(selectItem));
        }

    }

    /**
     * 初始化view
     */
    private void initView() {
        for (int i = 0; i < views.size(); i++) {
            views.remove(i);
        }
        final int count = getChildCount();
        size = count;
        for (int i = 0; i < count; i++) {
            views.add(getChildAt(i));
            final int finalI = i;
            getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("888888","isCanClickListener--"+isCanClickListener);
                    if (calculateItem() != finalI) {
                        setSelectItem(finalI);
                    }else{
                        if(isCanClickListener)
                            onItemClickListener.onItemClick(finalI, views.get(finalI));
                    }
                }
            });
        }
    }

    /**
     * 复位
     */
    private void restPosition() {
        if (size == 0) {
            return;
        }
        float finall = 0;
        float part = 360 / size;//一份的角度
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
        if (angle == finall) {
            return;
        }
        restAnimator = ValueAnimator.ofFloat(angle, finall);
        restAnimator.setInterpolator(new DecelerateInterpolator());
        restAnimator.setDuration(300);

        restAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (touching == false) {
                    angle = (Float) animation.getAnimatedValue();
                    invate();
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
                        onItemSelectedListener.selected(selectItem, views.get(selectItem));
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
        return (int) (angle / (360 / size)) % size;
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
        //onTouch(event);
        if (onLoopViewTouchListener != null) {
            onLoopViewTouchListener.onTouch(event);
        }
        isCanClickListener(event);
        return true;
    }


    /**
     * 触摸停止计时器，抬起设置可下啦刷新
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        onTouch(ev);
        if (onLoopViewTouchListener != null) {
            onLoopViewTouchListener.onTouch(ev);
        }
        isCanClickListener(ev);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否可以点击回调
     * @param event
     */
    public void  isCanClickListener(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (event.getX() - x > limitX || x - event.getX() > limitX) {
                    isCanClickListener = false;
                } else {
                    isCanClickListener = true;
                }
                break;
        }
    }
    /**
     * 获取所有的view
     *
     * @return
     */
    public List<View> getViews() {
        return views;
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
     * 设置选中方法
     *
     * @param selectItem
     */
    public void setSelectItem(int selectItem) {

        if (selectItem >= 0) {
            float jiaodu = 0;
            if (getSelectItem() == 0) {
                if (selectItem == views.size() - 1) {
                    jiaodu = angle - (360 / size);
                } else {
                    jiaodu = angle + (360 / size);
                }
            } else if (getSelectItem() == views.size() - 1) {
                if (selectItem == 0) {
                    jiaodu = angle + (360 / size);
                } else {
                    jiaodu = angle - (360 / size);
                }
            } else {
                if (selectItem > getSelectItem()) {
                    jiaodu = angle + (360 / size);
                } else {
                    jiaodu = angle - (360 / size);
                }
            }

            float finall = 0;
            float part = 360 / size;//一份的角度
            if (jiaodu < 0) {
                part = -part;
            }
            float minvalue = (int) (jiaodu / part) * part;//最小角度
            float maxvalue = (int) (jiaodu / part) * part;//最大角度
            if (jiaodu >= 0) {//分为是否小于0的情况
                if (jiaodu - last_angle > 0) {
                    finall = maxvalue;
                } else {
                    finall = minvalue;
                }
            } else {
                if (jiaodu - last_angle < 0) {
                    finall = maxvalue;
                } else {
                    finall = minvalue;
                }
            }

            if (size > 0) AnimRotationTo(finall, null);
        }
    }

    /**
     * 设置半径
     *
     * @param r
     */
    public void setR(float r) {
        this.r = r;
        distance = BEISHU * r;
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
     * 触摸时间回调
     *
     * @param onLoopViewTouchListener
     */
    public void setOnLoopViewTouchListener(OnLoopViewTouchListener onLoopViewTouchListener) {
        this.onLoopViewTouchListener = onLoopViewTouchListener;
    }

    /**
     * 设置是否自动切换
     *
     * @param autoRotation
     */
    public void setAutoRotation(boolean autoRotation) {
        this.autoRotation = autoRotation;
        loopHandler.setLoop(autoRotation);
    }

    /**
     * 获取自动切换时间
     *
     * @return
     */
    public long getAutoRotationTime() {
        return loopHandler.loopTime;
    }

    /**
     * 设置自动切换时间间隔
     *
     * @param autoRotationTime
     */
    public void setAutoRotationTime(long autoRotationTime) {
        loopHandler.setLoopTime(autoRotationTime);
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
     * @param mMultiple 设置这个必须在setR之前调用，否则无效
     * @return
     */
    public void setMultiple(float mMultiple) {
        this.BEISHU = mMultiple;
    }
}
