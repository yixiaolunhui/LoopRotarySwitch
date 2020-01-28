package com.example.looprotaryswitch;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dalong.library.adapter.LoopViewAdapter;
import com.dalong.library.listener.OnItemClickListener;
import com.dalong.library.listener.OnItemSelectedListener;
import com.dalong.library.LoopViewLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LoopViewLayout mLoopRotarySwitchView;

    private int width;
    private SeekBar mSeekBarX, mSeekBarZ;
    private CheckBox mCheckbox;
    private Switch mSwitchLeftright;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initLoopRotarySwitchView();
        initLinstener();
    }

    private void initLinstener() {
        mLoopRotarySwitchView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int item, View view) {
                Toast.makeText(MainActivity.this, "item:" + item, Toast.LENGTH_SHORT).show();
            }
        });
        /**
         * 选中回调
         */
        mLoopRotarySwitchView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void selected(int position, View view) {

            }
        });
        /**
         * 设置子view的x坐标
         */
        mSeekBarX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mLoopRotarySwitchView.setLoopRotationX(progress - seekBar.getMax() / 2);
                mLoopRotarySwitchView.notifyDataSetChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        /**
         * 设置子view的z坐标
         */
        mSeekBarZ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mLoopRotarySwitchView.setLoopRotationZ(progress - seekBar.getMax() / 2);
                mLoopRotarySwitchView.notifyDataSetChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        /**
         * 设置是否自动旋转
         */
        mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mLoopRotarySwitchView.setAutoRotation(isChecked);//启动LoopViewPager自动切换
            }
        });
        /**
         * 设置向左还是向右自动旋转
         */
        mSwitchLeftright.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mLoopRotarySwitchView.setAutoScrollDirection(isChecked ? LoopViewLayout.AutoScrollDirection.LEFT
                        : LoopViewLayout.AutoScrollDirection.RIGHT);
            }
        });

    }

    /**
     * 设置LoopRotarySwitchView
     */
    private void initLoopRotarySwitchView() {
        mLoopRotarySwitchView
                .setR(width / 3)//设置R的大小
                .setMultiple(2f)
                .setAutoRotation(false)//是否自动切换
                .setAutoScrollDirection(LoopViewLayout.AutoScrollDirection.LEFT)
                .setAutoRotationTime(1500);//自动切换的时间  单位毫秒

    }

    /**
     * 初始化布局
     */
    private void initView() {
        mLoopRotarySwitchView = (LoopViewLayout) findViewById(R.id.mLoopRotarySwitchView);
        mSeekBarX = (SeekBar) findViewById(R.id.seekBarX);
        mSeekBarZ = (SeekBar) findViewById(R.id.seekBarZ);
        mCheckbox = (CheckBox) findViewById(R.id.checkbox);
        mSwitchLeftright = (Switch) findViewById(R.id.switchLeftright);
        mSeekBarX.setProgress(mSeekBarX.getMax() / 2);
        mSeekBarZ.setProgress(mSeekBarZ.getMax() / 2);

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;

        myAdapter = new MyAdapter();
        mLoopRotarySwitchView.setAdapter(myAdapter);
        mLoopRotarySwitchView.setLoopRotationX(-50);

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        myAdapter.setDatas(list);
    }


    public class MyAdapter extends LoopViewAdapter<String> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loopview_item_view0, null);
            TextView tv = (TextView) view.findViewById(R.id.loopView0_tv1);
            tv.setText(String.valueOf(position+1));
            return view;
        }
    }

}
