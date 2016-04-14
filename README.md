#介绍
1: LoopRotarySwitchView2.0<br />
可以无限循环，自动旋转，停靠的3D旋转布局控件,无需编写代码，直接在布局中加入自己的布局即可。<br />
仅使用三角函数,使用ValueAnimation,继承FrameLayout.兼容所有滑动组件

## 效果图
![image](https://github.com/dalong982242260/LoopRotarySwitch/blob/master/img/dalong.gif)

##优势
*1.3d旋转GrallyView<br />
*2.支持自动旋转<br />
*3.可直接在xml添加元素即可添加列数据。也可以动态代码添加view<br />
*4.优良的兼容性，和可以自己尺寸控制<br />
*5.添加了点击切换和点击中间监听，适合目前app的需求<br />
*6.优化控件点击切换效果，使用更加的方法实用<br />

##如何使用？
 <com.example.looprotaryswitch.view.LoopRotarySwitchView<br />
        android:id="@+id/mLoopRotarySwitchView" <br />
        android:layout_width="fill_parent" <br />
        android:gravity="center_vertical"<br />
        android:layout_height="fill_parent"> <br />
        
  <!--  此处添加你的View元素，也可以用layout包裹 --!><br />
       <include android:id="@+id/item1" layout="@layout/item_view0"></include><br />
       <include android:id="@+id/item2" layout="@layout/item_view1"></include> <br />
       <include android:id="@+id/item3" layout="@layout/item_view2"></include> <br />
       
 </com.example.looprotaryswitch.view.LoopRotarySwitchView> <br />
 
 
 或者直接代码添加  LoopRotarySwitchView.addView(view);<br />
 
 
 
 ##配置view
    mLoopRotarySwitchView.setMultiple(1f)                      
            .setR(width/3+20)//设置R的大小                          
            .setAutoRotation(false)//是否自动切换                    
            .setAutoRotationTime(2000);//自动切换的时间  单位毫秒         
 
 
 
