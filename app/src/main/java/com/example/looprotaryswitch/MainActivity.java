package com.example.looprotaryswitch;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.looprotaryswitch.view.LoopRotarySwitchView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LoopRotarySwitchView mLoopRotarySwitchView;

    private List<View> views;
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
            final int finalI = i;
            views.get(i).findViewById(R.id.loopView_rel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mLoopRotarySwitchView.getSelectItem()!=finalI){
                        mLoopRotarySwitchView.setSelectItem(finalI);
                    }else{
                        Toast.makeText(MainActivity.this, "i="+finalI, Toast.LENGTH_SHORT).show();
                    }

                }
            });
            mLoopRotarySwitchView.addView(views.get(i));
        }
        mLoopRotarySwitchView.setMultiple(3.5f);
        mLoopRotarySwitchView.setR(300);

    }


}
