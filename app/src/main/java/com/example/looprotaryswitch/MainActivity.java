package com.example.looprotaryswitch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.looprotaryswitch.view.LoopRotarySwitchView;
import com.example.looprotaryswitch.view.OnItemClickListener;
import com.example.looprotaryswitch.view.OnItemSelectedListener;
import com.example.looprotaryswitch.view.OnLoopViewTouchListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LoopRotarySwitchView mLoopRotarySwitchView;

    private List<View> views;

    private boolean isCanClick;

    private float x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    /**
     *  初始化布局
     */
    private void initView() {
        mLoopRotarySwitchView=(LoopRotarySwitchView)findViewById(R.id.mLoopRotarySwitchView);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        views=new ArrayList<>();
        View view0= LayoutInflater.from(this).inflate(R.layout.loopview_item_view0,null);
        View view1= LayoutInflater.from(this).inflate(R.layout.loopview_item_view1,null);
        View view2= LayoutInflater.from(this).inflate(R.layout.loopview_item_view2,null);
        views.add(view0);
        views.add(view1);
        views.add(view2);
        for (int i=0;i<views.size();i++){
            mLoopRotarySwitchView.addView(views.get(i));
        }
        mLoopRotarySwitchView.setMultiple(3.5f);
        mLoopRotarySwitchView.setR(300);

        /**
         * 选中回调
         */
        mLoopRotarySwitchView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void selected(int position, View view) {
                Toast.makeText(MainActivity.this, "setOnItemSelectedListener－－－i="+position, Toast.LENGTH_SHORT).show();
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


}
