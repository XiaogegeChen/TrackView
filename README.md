[![](https://jitpack.io/v/XiaogegeChen/TrackView.svg)](https://jitpack.io/#XiaogegeChen/TrackView)
# TrackView(仿知乎可拖动悬停按钮)
![0](https://github.com/XiaogegeChen/Android-customView/blob/master/screenshot/device-2019-05-11-203831.gif)

## 实现的主要功能有：
1. 随手拖动
2. 响应点击事件
3. 全屏拖动，也可以限定位置
4. 可通过xml配置颜色和内部样式

## 使用方法
1.可以直接复制[TrackView.java](https://github.com/XiaogegeChen/TrackView/blob/master/library/src/main/java/com/github/xiaogegechen/library/TrackView.java)到自己的工程中
2.在xml中配置属性（如果是复制的需要改成自己的包名,属性有默认值，也可以配置）

```
<com.xiaoegeg.arttest.TrackView
        android:id="@+id/track_view"
        android:layout_marginTop="50dp"
        android:layout_width="50dp"
        android:layout_height="100dp" />
```
3.通过findViewById()方法找到控件，如果需要响应点击事件，使用setOnClickListener()方法添加监听器即可。如：

```
        final TrackView trackView = findViewById (R.id.track_view);
        trackView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Toast.makeText (MainActivity.this, "点击了拖动按钮",  Toast.LENGTH_SHORT).show ();
            }
        });
```
## 属性
可以根据需要配置相应的属性

```
        app:inner_distance="10dp"
        app:inner_length="10dp"
        app:inner_stroke_width="2dp"
        app:blank_bottom="20dp"
        app:blank_left="15dp"
        app:blank_right="15dp"
        app:blank_top="60dp"
        app:inner_content_color="@color/colorAccent"
        app:inner_stroke_color="#000000"
        app:out_stroke_color="@color/colorAccent"
        app:out_stroke_width="1dp"

```

 ```app:inner_distance```是两个箭头之间的间距<br>
  ```app:inner_length```是每个箭头的边长<br>
   ```app:inner_stroke_width```是两个箭头的线条宽<br>
    ```app:blank_bottom```是底部留白的高度<br>
 ```app:blank_left```是左侧留白的高度<br>
  ```app:blank_right```是右侧留白的高度<br>
 ```app:blank_top```是顶部留白的高度<br>
  ```app:inner_content_color```是圆形内部的填充色<br>
    ```app:inner_stroke_color```是两个箭头的线条颜色<br>
 ```app:out_stroke_color```是外圆线条的颜色<br>
  ```app:out_stroke_width```是外圆线条的线宽<br>
  具体分析可以参考[仿知乎可拖动悬停按钮](https://blog.csdn.net/qq_40909351/article/details/90116874)
