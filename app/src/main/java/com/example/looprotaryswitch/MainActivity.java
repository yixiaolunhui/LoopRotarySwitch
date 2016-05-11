package com.example.looprotaryswitch;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.dalong.library.listener.OnItemClickListener;
import com.dalong.library.listener.OnItemSelectedListener;
import com.dalong.library.listener.OnLoopViewTouchListener;
import com.dalong.library.view.LoopRotarySwitchView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LoopRotarySwitchView mLoopRotarySwitchView,mLoopRotarySwitchView2,mLoopRotarySwitchView3;

    private List<View> views;

    private  int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initLoopRotarySwitchView();
        initLinstener();
    }

    private void initLinstener() {
        /**
         * 选中回调
         */
        mLoopRotarySwitchView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void selected(int position, View view) {
//                Toast.makeText(MainActivity.this, "setOnItemSelectedListener－－－i="+position, Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * 触摸回调
         */
        mLoopRotarySwitchView.setOnLoopViewTouchListener(new OnLoopViewTouchListener() {
            @Override
            public void onTouch(MotionEvent event) {
            }
        });
        /**
         * 点击事件
         */
        mLoopRotarySwitchView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int item, View view) {
                Toast.makeText(MainActivity.this, "setOnItemClickListener－－－i="+item, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 设置LoopRotarySwitchView
     */
    private void initLoopRotarySwitchView() {
        mLoopRotarySwitchView
                .setR(width/3)//设置R的大小
                .setAutoRotation(true)//是否自动切换
                .setAutoScrollDirection(LoopRotarySwitchView.AutoScrollDirection.left)//切换方向
                .setAutoRotationTime(2000);//自动切换的时间  单位毫秒
        mLoopRotarySwitchView2
                .setR(width/3)//设置R的大小
                .setAutoRotation(true)//是否自动切换
                .setAutoScrollDirection(LoopRotarySwitchView.AutoScrollDirection.left)//切换方向
                .setAutoRotationTime(2000);//自动切换的时间  单位毫秒
        mLoopRotarySwitchView3
                .setR(width/3)//设置R的大小
                .setAutoRotation(true)//是否自动切换
                .setAutoScrollDirection(LoopRotarySwitchView.AutoScrollDirection.left)//切换方向
                .setAutoRotationTime(2000);//自动切换的时间  单位毫秒
    }

    /**
     *  初始化布局
     */
    private void initView() {
        mLoopRotarySwitchView=(LoopRotarySwitchView)findViewById(R.id.mLoopRotarySwitchView);
        mLoopRotarySwitchView2=(LoopRotarySwitchView)findViewById(R.id.mLoopRotarySwitchView2);
        mLoopRotarySwitchView3=(LoopRotarySwitchView)findViewById(R.id.mLoopRotarySwitchView3);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        width=dm.widthPixels;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        views=new ArrayList<>();
        View view0= LayoutInflater.from(this).inflate(R.layout.loopview_item_view0,null);
        View view1= LayoutInflater.from(this).inflate(R.layout.loopview_item_view1,null);
        View view2= LayoutInflater.from(this).inflate(R.layout.loopview_item_view2,null);
        View view3= LayoutInflater.from(this).inflate(R.layout.loopview_item_view4,null);
        View view4= LayoutInflater.from(this).inflate(R.layout.loopview_item_view5,null);
        views.add(view0);
        views.add(view1);
        views.add(view2);
        views.add(view3);
//        views.add(view4);
        for (int i=0;i<views.size();i++){
            mLoopRotarySwitchView.addView(views.get(i));
        }

    }


}
