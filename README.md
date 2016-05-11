#介绍
1: LoopRotarySwitchView1.0<br />
可以无限循环，自动旋转，停靠的3D旋转布局控件,无需编写代码，直接在布局中加入自己的布局即可。<br />
控件继承相对布局RelativeLayout直接可以当做布局使用。

## 效果图
![image](https://github.com/dalong982242260/LoopRotarySwitch/blob/master/img/dalong.gif)

##优势
*1.3d旋转RelativeLayout <br />
*2.支持自动旋转<br />
*3.可直接在xml添加元素即可添加列数据。也可以动态代码添加view<br />
*4.优良的兼容性，和可以自己尺寸控制<br />
*5.添加了点击切换和点击中间监听，适合目前app的需求<br />
*6.优化控件点击切换效果，使用更加的方法实用<br />

## 如何使用


在你的项目Gradle 添加

     compile 'com.dalong:loopview:1.0.1'


后者直接引入库文件
 
 
## 1.0.1版本
 1、优化代码。
 2、增加自动旋转方向设置
 
## 1.0.0版本
 1、支持3d旋转。
 2、支持自定义旋转。
 3、点击3d切换，触摸滑动3d切换
 
## 配置view 
    mLoopRotarySwitchView
                   .setR(300)//设置R的大小
                   .setAutoRotation(true)//是否自动切换
                   .setAutoScrollDirection(LoopRotarySwitchView.AutoScrollDirection.left)//切换方向
                   .setAutoRotationTime(2000);//自动切换的时间  单位毫秒 
 
 
 
