[![](https://jitpack.io/v/XiaogegeChen/TrackView.svg)](https://jitpack.io/#XiaogegeChen/TrackView)
# TrackView(仿知乎可拖动悬停按钮)
![0](https://github.com/XiaogegeChen/TrackView/blob/master/screenshot/v2.0.gif)
## 主要功能
* 随手拖动<br>
* 展开闭合<br>
* 动态更改文字<br>
* 全屏拖动，也可以限定位置<br>
* 响应点击事件<br>
* 可通过xml配置颜色和内部样式<br>
## 快速使用
1.在工程根目录的build.gradle中添加依赖
```
allprojects {
    repositories {
        google()
        jcenter()
        
        maven { url 'https://jitpack.io' } 
    }
}
```
2.在工程目录的build.gradle中添加依赖(查看最上面的版本号进行替换)
```
implementation 'com.github.XiaogegeChen:TrackView:2.0'
```
3.在xml中配置
```
<com.github.xiaogegechen.library.TrackView
        android:id="@+id/track_view"
        android:layout_width="150dp"
        android:layout_height="50dp"
        android:padding="3dp"
        android:layout_margin="30dp"
        app:inner_text="下一个回答"/>
```

## 可配置的属性（请更新至最新版本）
```     
app:inner_text 文字，可动态更改
app:inner_text_color 文字颜色
app:inner_text_size 文字尺寸，单位sp
 
app:inner_distance 是两个箭头之间的间距
app:inner_length 是每个箭头的边长
app:inner_stroke_width 是两个箭头的线条宽
app:blank_bottom 是底部留白的高度
app:blank_left 是左侧留白的高度
app:blank_right 是右侧留白的高度
app:blank_top 是顶部留白的高度
app:inner_content_color 是圆形内部的填充色
app:inner_stroke_color 是两个箭头的线条颜色
app:out_stroke_color 是外圆线条的颜色
app:out_stroke_width 是外圆线条的线宽
```

## 展开与闭合、动态更改文字
```
        findViewById (R.id.open).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                // 展开
                trackView.open ();
            }
        });
        
        findViewById (R.id.close).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                // 闭合
                trackView.close ();
            }
        });
        
        findViewById (R.id.change).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                // 动态更改文字
                trackView.setText ("num:" + num);
                num++;
            }
        });
```

## 监听点击事件
```
        final TrackView trackView = findViewById (R.id.track_view);

        trackView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                textView.setText ("WORLD");
                Toast.makeText (MainActivity.this,  "click", Toast.LENGTH_SHORT).show ();
            }
        });
```
## 原理分析
可参考 [仿知乎可拖动悬停按钮](https://blog.csdn.net/qq_40909351/article/details/90116874)
## 更新日志

v2.0
增加新功能：
* 展开和关闭功能
* 文字显示功能
bug修复：
* 更改v1.1版本尺寸错误的bug
* 删除默认的padding
优化：
* 默认值优化，更见美观

v1.1
* 吸附到侧面时增加动画效果
* 修复之前版本中手指离开点在边界之外时悬浮球错误的移动的bug
* 优化细节


