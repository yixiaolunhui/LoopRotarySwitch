#介绍
1: LoopRotarySwitchView1.0<br />
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

## 如何使用


在你的项目Gradle 添加

dependencies {<br />
     compile 'com.dalong:loopview:1.0.0'<br />
}<br />

后者直接引入库文件<br />
 
 
## 配置view 
   mLoopRotarySwitchView<br />
                  .setR(350)//设置R的大小<br />
                  .setAutoRotation(false)//是否自动切换<br />
                  .setAutoRotationTime(2000);//自动切换的时间  单位毫秒  <br />     
 
 
 
