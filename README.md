[![](https://jitpack.io/v/XiaogegeChen/TrackView.svg)](https://jitpack.io/#XiaogegeChen/TrackView)
# TrackView(仿知乎可拖动悬停按钮)
![0](https://github.com/XiaogegeChen/TrackView/blob/master/screenshot/device-2019-05-11-203831.gif)
## 主要功能
1. 随手拖动<br>
2. 响应点击事件<br>
3. 全屏拖动，也可以限定位置<br>
4. 可通过xml配置颜色和内部样式<br>
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
2.在工程目录的build.gradle中添加依赖(x.y查看最上面的版本号进行替换)
```
implementation 'com.github.XiaogegeChen:TrackView:x.y'
```
3.在xml中配置
```
<com.github.xiaogegechen.library.TrackView
        android:id="@+id/track_view"
        android:layout_marginTop="50dp"
        android:layout_width="50dp"
        android:layout_height="100dp" />
```

## 可配置的属性
```
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
## 监听点击事件
```
        final TrackView trackView = findViewById (R.id.track_view);

        trackView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                
                // 点击逻辑
                Toast.makeText (MainActivity.this, "点击了拖动按钮",  Toast.LENGTH_SHORT).show ();
            }
        });
```
## 原理分析
可参考 [仿知乎可拖动悬停按钮](https://blog.csdn.net/qq_40909351/article/details/90116874)


