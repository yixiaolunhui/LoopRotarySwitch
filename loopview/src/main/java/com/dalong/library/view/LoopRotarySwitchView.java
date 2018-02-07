package com.dalong.library.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dalong.library.R;
import com.dalong.library.listener.OnItemClickListener;
import com.dalong.library.listener.OnItemSelectedListener;
import com.dalong.library.listener.OnLoopViewTouchListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import static android.content.ContentValues.TAG;

/***
 * 水平旋转轮播控件
 */
public class LoopRotarySwitchView extends RelativeLayout {

    private final static int LoopR = 200;

    private final static int vertical = 0;//竖直

    private final static int horizontal = 1;//水平

    private int mOrientation = horizontal;//方向

    private Context mContext;//上下文

    private ValueAnimator restAnimator = null;//回位动画

    private ValueAnimator rAnimation = null;//半径动画

    private ValueAnimator zAnimation = null;

    private ValueAnimator xAnimation = null;

    private int loopRotationX = 0, loopRotationZ = 0;//x轴旋转和z轴旋转，y轴无效果

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

    private AutoScrollDirection autoRotatinDirection = AutoScrollDirection.right; //默认自动滚动是从右往左

    private List<View> views = new ArrayList<View>();//子view引用列表

    private OnItemSelectedListener onItemSelectedListener = null;//选择事件接口

    private OnLoopViewTouchListener onLoopViewTouchListener = null;//选择事件接口

    private OnItemClickListener onItemClickListener = null;//被点击的回调

    private boolean isCanClickListener = true;//是否可以点击回调

    private float x;//移动的x是否符合回调点击事件

    private float limitX = 50;//滑动倒最低30

    private boolean isCanSwitchItem = true;

    public enum AutoScrollDirection {
        left, right
    }

    /**
     * 构造方法
     *
     * @param context
     */
    public LoopRotarySwitchView(Context context) {
        this(context, null);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public LoopRotarySwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
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
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoopRotarySwitchView);
        mOrientation = typedArray.getInt(R.styleable.LoopRotarySwitchView_orientation, horizontal);
        autoRotation = typedArray.getBoolean(R.styleable.LoopRotarySwitchView_autoRotation, false);
        r = typedArray.getDimension(R.styleable.LoopRotarySwitchView_r, LoopR);
        int direction = typedArray.getInt(R.styleable.LoopRotarySwitchView_direction, 0);
        typedArray.recycle();
        mGestureDetector = new GestureDetector(context, getGeomeryController());
        if (mOrientation == horizontal) {//如果是水平 z值为0  如果是竖直z值为90
            loopRotationZ = 0;
        } else {
            loopRotationZ = 90;
        }
        if (direction == 0) {//设置自定滚动的方向
            autoRotatinDirection = AutoScrollDirection.left;
        } else {
            autoRotatinDirection = AutoScrollDirection.right;
        }
        loopHandler.setLoop(autoRotation);

    }

    /**
     * handler处理
     */
    LoopRotarySwitchViewHandler loopHandler = new LoopRotarySwitchViewHandler(3000) {
        @Override
        public void doScroll() {
            try {
                if (size != 0) {//判断自动滑动从那边开始
                    int perAngle = 0;
                    switch (autoRotatinDirection) {
                        case left:
                            perAngle = 360 / size;
                            break;
                        case right:
                            perAngle = -360 / size;
                            break;
                    }
                    if (angle == 360) {
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
     * 排序
     * 對子View 排序，然后根据变化选中是否重绘,这样是为了实现view 在显示的时候来控制当前要显示的是哪三个view，可以改变排序看下效果
     *
     * @param list
     */
    @SuppressWarnings("unchecked")
    private <T> void sortList(List<View> list) {

        @SuppressWarnings("rawtypes")
        Comparator comparator = new SortComparator();
        T[] array = list.toArray((T[]) new Object[list.size()]);

        Arrays.sort(array, comparator);
        int i = 0;
        ListIterator<T> it = (ListIterator<T>) list.listIterator();
        while (it.hasNext()) {
            it.next();
            it.set(array[i++]);
        }
        for (int j = 0; j < list.size(); j++) {
            list.get(j).bringToFront();
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
                angle += Math.cos(Math.toRadians(loopRotationZ)) * (distanceX / 4f)
                        + Math.sin(Math.toRadians(loopRotationZ)) * (distanceY / 4f);
                initView();
                return true;
            }
        };
    }



    public void initView() {
        for (int i = 0; i < views.size()&&size!=0; i++) {
            double radians = angle + 180 - (float) (i * 360f / size);//注意360f是为了解决有余数的时候的偏移问题
            float x0 = (float) Math.sin(Math.toRadians(radians)) * r;
            float y0 = (float) Math.cos(Math.toRadians(radians)) * r;
            float scale0 = (distance - y0) / (distance + r);//计算子view之间的比例，可以看到distance越大的话 比例越小，也就是大小就相差越小
            views.get(i).setScaleX(Math.max(scale0,0.5f));//对view进行缩放0.5
            views.get(i).setScaleY(Math.max(scale0,0.5f));//对view进行缩放0.5
            views.get(i).setAlpha(Math.max(scale0,1f));//0.5
            float rotationX_y = (float) Math.sin(Math.toRadians(loopRotationX * Math.cos(Math.toRadians(radians)))) * r;//0
            float rotationZ_y = -(float) Math.sin(Math.toRadians(-loopRotationZ)) * x0;//0
            float rotationZ_x = (((float) Math.cos(Math.toRadians(-loopRotationZ)) * x0) - x0);//0
            views.get(i).setTranslationX(x0 + rotationZ_x);
            views.get(i).setTranslationY(rotationX_y + rotationZ_y);
        }
        List<View> arrayViewList = new ArrayList<>();
        arrayViewList.clear();
        for (int i = 0; i < views.size(); i++) {
            arrayViewList.add(views.get(i));
        }
        sortList(arrayViewList);
        postInvalidate();
    }

    public void setclickitem(ImageView img,int bitmapid){
        img.setImageDrawable(this.getResources().getDrawable(bitmapid));
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initView();
        if (autoRotation) {
            loopHandler.sendEmptyMessageDelayed(LoopRotarySwitchViewHandler.msgid, loopHandler.loopTime);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            checkChildView();
            if (onItemSelectedListener != null) {
                isCanClickListener = true;
                onItemSelectedListener.selected(selectItem, views.get(selectItem));
            }
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
                initView();
            }
        });
        rAnimation.setInterpolator(new DecelerateInterpolator());
        rAnimation.setDuration(2000);
        rAnimation.start();
    }


    /**
     * 初始化view
     */
    boolean debugselect=false;
    public void checkChildView() {
        for (int i = 0; i < views.size(); i++) {//先清空views里边可能存在的view防止重复
            views.remove(i);
        }
        final int count = getChildCount(); //获取子View的个数
        size = count;

        for (int i = 0; i < count; i++) {
            View view = getChildAt(i); //获取指定的子view
            final int position = i;
            views.add(view);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //对子view添加点击事件
                    if (position != selectItem) {
                        if (isCanSwitchItem) {
                            /*---------------设置好当前图标的左右两侧的id------------------*/
                            int left,right;
                            if(selectItem==0){
                                left=views.size()-1;
                                right=selectItem+1;
                            }else if(selectItem==views.size()-1){
                                left=selectItem-1;
                                right=0;
                            }else{
                                left=selectItem-1;
                                right=selectItem+1;
                            }
                           /*-------------------如果选择到后面的item就跳转到左边或者右边的item-----------------*/
                            if(debugselect){
                                Log.d(TAG, "/******************************************************/");
                            }
                            if(position==left||position==right){//符合左右位置的直接跳转
                                setSelectItem(position);
                                if(debugselect){
                                    Log.d(TAG, "select left or right");
                                }
                            }else{//不符合的做判断
                                if(selectItem>=((float)(views.size()/2f))){
                                    if(debugselect){
                                        Log.d(TAG, "当前选择的是: "+selectItem+"  ；点击的是"+position);
                                    }
                                    float sel=selectItem-(views.size()/2f);
                                    if(position>sel&&position<selectItem){
                                        setSelectItem(left);
                                        if(debugselect){
                                            Log.d(TAG, sel+"<"+position+"<"+selectItem+"------->左转到-->"+left);
                                        }
                                    }else{
                                        setSelectItem(right);
                                        if(debugselect){
                                            Log.d(TAG, sel+">"+position+"或者"+position+"<"+selectItem+"------->右转到-->"+right);
                                        }
                                    }
                                }else{
                                    if(debugselect){
                                        Log.d(TAG, "当前选择的是: "+selectItem+"  ；点击的是"+position);
                                    }
                                    float sel=selectItem+(views.size()/2f);
                                    if(position>selectItem&&position<sel){
                                        setSelectItem(right);
                                        if(debugselect){
                                            Log.d(TAG, selectItem+"<"+position+"<"+sel+"------->右转到-->"+right);
                                        }
                                    }else{
                                        setSelectItem(left);
                                        if(debugselect){
                                            Log.d(TAG, selectItem+">"+position+"或者"+position+"<"+sel+"------->左转到-->"+left);
                                        }
                                    }
                                }
                            }
                            if(debugselect){
                                Log.d(TAG, "/******************************************************/");
                            }
                        }
                    } else {
                        if (isCanClickListener && onItemClickListener != null) {
                            onItemClickListener.onItemClick(position, views.get(position));
                        }
                    }
                }
            });

        }

    }

    /**
     * 复位
     */
    boolean debugresetposition=false;

    private void restPosition() {
        if (size == 0) {
            return;
        }
        float finall = 0;
        float part=0;
        part = 360f / size;//一份的角度,注意这里360f是浮点，不然有余数的时候会出偏移问题
        if (angle < 0) {
            part = -part;
        }
        float minvalue = (int) (angle / part) * part;//最小角度
        float maxvalue = (int) (angle / part) * part + part;//最大角度
        if(debugresetposition){
            Log.d(TAG, "/******************************************************/");
            Log.d(TAG, "angle: "+angle);
            Log.d(TAG, "last_angle: "+last_angle);
            Log.d(TAG, "maxvalue: "+maxvalue);
            Log.d(TAG, "minvalue: "+minvalue);
        }
        if (angle >= 0) {//分为是否小于0的情况
            if(debugresetposition){
                Log.d(TAG, "angle >= 0");
            }

            if (angle - last_angle > 0) {
                if(Math.abs(angle-last_angle)>=(Math.abs((maxvalue-minvalue)/2f))){//这里做个判断，假如角度超多少才会跳到下一个Math.abs((maxvalue-minvalue)/3)
                    finall = maxvalue;
                    if(debugresetposition){
                        Log.d(TAG, "angle-last_angle >= 40, finall = maxvalue");
                    }

                }else{
                    if(debugresetposition){
                        Log.d(TAG, "angle-last_angle < 40, finall = last_angle");
                    }

                    finall = last_angle;
                }
            } else {
                if(Math.abs(angle-last_angle)>=(Math.abs((maxvalue-minvalue)/2f))){//这里做个判断，假如角度超多少才会跳到下一个Math.abs((maxvalue-minvalue)/3)
                    finall = minvalue;
                    if(debugresetposition){
                        Log.d(TAG, "last_angle-angle > 40, finall = minvalue");
                    }

                }else{
                    finall = last_angle;
                    if(debugresetposition){
                        Log.d(TAG, "last_angle-angle < 40, finall = last_angle");
                    }

                }
            }
        } else {
            if(debugresetposition){
                Log.d(TAG, "angle < 0");
            }

            if (angle - last_angle < 0) {
                if(Math.abs(angle - last_angle)>=(Math.abs((maxvalue-minvalue)/2f))){//这里做个判断，假如角度超多少才会跳到下一个Math.abs((minvalue-maxvalue)/3)
                    finall = maxvalue;
                    if(debugresetposition){
                        Log.d(TAG, "angle - last_angle > 40, finall = maxvalue");
                    }

                }else{
                    finall = last_angle;
                    if(debugresetposition){
                        Log.d(TAG, "angle - last_angle < 40, finall = last_angle");
                    }

                }
            } else {
                if(Math.abs(angle - last_angle)>=(Math.abs((maxvalue-minvalue)/2f))){//这里做个判断，假如角度超多少才会跳到下一个Math.abs((minvalue-maxvalue)/3)
                    finall = minvalue;
                    if(debugresetposition){
                        Log.d(TAG, "last_angle - angle > 40, finall = minvalue");
                    }

                }else{
                    finall = last_angle;
                    if(debugresetposition){
                        Log.d(TAG, "last_angle - angle < 40, finall = last_angle");
                    }

                }
            }
        }
        AnimRotationTo(finall, null);
        if(debugresetposition){
            Log.d(TAG, "finall: "+finall);
            Log.d(TAG, "/******************************************************/");
        }

    }


    /**
     * 动画
     *
     * @param finall
     * @param complete
     */
    boolean animrotating=false;
    boolean angledebug=false;
    private void AnimRotationTo(float finall, final Runnable complete) {
        animrotating=true;
        if (angle == finall) {//如果相同说明不需要旋转
            animrotating=false;
            return;
        }
        restAnimator = ValueAnimator.ofFloat(angle, finall);
        restAnimator.setInterpolator(new DecelerateInterpolator());//设置旋转减速插值器
        restAnimator.setDuration(200);

        restAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (touching == false) {
                    if(angledebug){
                        Log.d(TAG, "onAnimationUpdate angle: "+angle+"\r\n");
                    }
                    angle = (Float) animation.getAnimatedValue();
                    initView();
                }
            }
        });
        restAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isCanSwitchItem = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animrotating=false;
                isCanSwitchItem = true;
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
                    animrotating=false;
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
    private Float touchx,touchy;
    private boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchx=event.getX();
            last_angle = angle;
            touching = true;
        }
        boolean sc = mGestureDetector.onTouchEvent(event);
        if (sc) {
            this.getParent().requestDisallowInterceptTouchEvent(false);//通知父控件勿拦截本控件
        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            touching = false;
            // if(Math.abs(touchx-event.getX())>50){
            restPosition();
            //}
            return true;
        }
        return true;
    }


    /**
     * 触摸方法
     *后
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!animrotating){
            if (onLoopViewTouchListener != null) {
                onLoopViewTouchListener.onTouch(event);
            }
            isCanClickListener(event);
        }
        return true;
    }


    /**
     * 触摸停止计时器，抬起设置可下啦刷新,先
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!animrotating){
            onTouch(ev);
            if (onLoopViewTouchListener != null) {
                onLoopViewTouchListener.onTouch(ev);
            }
            isCanClickListener(ev);
        }
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
                    loopHandler.removeMessages(LoopRotarySwitchViewHandler.msgid);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (autoRotation) {
                    loopHandler.sendEmptyMessageDelayed(LoopRotarySwitchViewHandler.msgid, loopHandler.loopTime);
                }
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

    int bottom = 0;

    /**
     * 设置指定位置
     *
     * @param pos
     */
    public void setSelectItem(int pos) {
        if (pos < 0 || pos >= views.size()) {
            return;
        }
        bottom = calculateItem();
        if (bottom == pos) {
            Log.e("setToPos", "bottom");
        }
        int difPos = bottom - pos;
        if (difPos < 0 - Math.floor(views.size() / 2)) {
            difPos += views.size();
        }
        if (difPos > Math.floor(views.size() / 2)) {
            difPos -= views.size();
        }
        if (views.size() % 2 == 0 && difPos == views.size() / 2) {
            difPos = 0 + views.size() / 2;
        }
        float angle1 = angle - difPos * 360 / views.size();
        AnimRotationTo(angle1, null);
    }


    /**
     * 设置半径
     *
     * @param r
     */
    public LoopRotarySwitchView setR(float r) {
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
    public LoopRotarySwitchView setAutoRotation(boolean autoRotation) {
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
        return loopHandler.loopTime;
    }

    /**
     * 设置自动切换时间间隔
     *
     * @param autoRotationTime
     */
    public LoopRotarySwitchView setAutoRotationTime(long autoRotationTime) {
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
    public LoopRotarySwitchView setMultiple(float mMultiple) {
        this.multiple = mMultiple;
        distance = multiple * r;
        return this;
    }

    public LoopRotarySwitchView setAutoScrollDirection(AutoScrollDirection mAutoScrollDirection) {
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
                initView();
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
                initView();
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
    public LoopRotarySwitchView setOrientation(int mOrientation) {
        setHorizontal(mOrientation == horizontal, false);
        return this;
    }

    public LoopRotarySwitchView setHorizontal(boolean horizontal, boolean anim) {
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
            initView();
        }
        return this;
    }

    public LoopRotarySwitchView setLoopRotationX(int loopRotationX) {
        this.loopRotationX = loopRotationX;
        return this;
    }

    public LoopRotarySwitchView setLoopRotationZ(int loopRotationZ) {
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
}
