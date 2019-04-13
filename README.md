# MySexangleView
![github](https://github.com/zhongruiAndroid/MySexangleView/blob/master/app/src/main/res/drawable/sexangleview.gif "github")  


| 属性                | 类型      | 说明               |
|---------------------|-----------|--------------------|
| radius              | dimension | 六边形圆角半径     |
| sexangleColor       | color     | 六边形进度颜色     |
| sexangleSecondColor | color     | 六边形颜色         |
| sexangleWidth       | dimension | 六边形线条宽度     |
| progress            | float     | 进度               |
| max                 | float     | 总进度             |
| isRound             | boolean   | 进度条是否是圆角   |
| useAnimation        | boolean   | 是否设置动画       |
| duration            | integer   | 动画执行时间       |
| borderColor         | color     | 六边形边框颜色     |
| borderWidth         | dimension | 六边形边框线条宽度 |
| borderDashLength    | dimension | 虚线长度           |
| borderDashGap       | dimension | 虚线间隔           |
| borderRadius        | dimension | 六边形边框圆角     |
| borderPhase         | float     | 虚线偏移量         |
| borderMargin        | dimension | 六边形边框margin   |
| borderMarginLeft    | dimension |                    |
| borderMarginTop     | dimension |                    |
| borderMarginRight   | dimension |                    |
| borderMarginBottom  | dimension |                    |


#### 设置进度条颜色渐变
```java
//进度条边框颜色渐变
SexangleView sv=findViewById(R.id.sv);
SweepGradient shader = new SweepGradient(0, 0, new int[]{Color.parseColor("#34e8a6"), Color.parseColor("#06C1AE"), Color.parseColor("#34e8a6")}, null);
sv.setShader(shader);
```
#### 进度监听
```java
sv.setOnProgressChangeInter(new SexangleView.OnProgressChangeInter() {
   @Override
    public void progress(float scaleProgress, float progress, float max) {
	    //总进度：max
	    //当前进度：progress
	    //动画进度：scaleProgress
    
    }
});
```  

<br/> 
  

### 如果本库对您有帮助,还希望支付宝扫一扫下面二维码,你我同时免费获取奖励金(非常感谢 Y(^-^)Y)
![github](https://github.com/zhongruiAndroid/SomeImage/blob/master/image/small_ali.jpg?raw=true "github")  

  
  | 最新版本号 | [ ![Download](https://api.bintray.com/packages/zhongrui/mylibrary/MySexangleView/images/download.svg) ](https://bintray.com/zhongrui/mylibrary/MySexangleView/_latestVersion) |  
|--------|----|
<br/> 

```gradle
compile 'com.github:SexangleView:版本号看上面'
```  
<br/> 
<br/> 

#### Thanks https://github.com/HandGrab/MagicalProgress
