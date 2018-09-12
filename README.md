# DragView
[ ![Download](https://api.bintray.com/packages/xiong955/maven/dragview/images/download.svg) ](https://bintray.com/xiong955/maven/dragview/_latestVersion)
<br>
一个类似IOS小圆点的可拖动控件

## 使用方式
    implementation 'com.xiong.widget:dragview:1.0.1'

## 属性相关
<table>
    <tr>
        <td>animationTime</td>
        <td>动画时间</td>
    </tr>
     <tr>
        <td>backgroundColor</td>
        <td>背景颜色</td>
    </tr>
     <tr>
        <td>foregroundImage</td>
        <td>前景图</td>
    </tr>
     <tr>
        <td>bounce</td>
        <td>是否开启反弹动画</td>
    </tr>
     <tr>
        <td>welt</td>
        <td>是否贴边</td>
    </tr>
     <tr>
        <td>dragWidth</td>
        <td>拖动控件的宽</td>
    </tr>
     <tr>
        <td>dragHeight</td>
        <td>拖动控件的高</td>
    </tr>
</table>

## 布局文件

    <FrameLayout>

        //可拖动区域的布局
        <LinearLayout...>

        <com.xiong.DragView
            android:id="@+id/drag"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:animationTime="200"
            app:backgroundColor="#f9f035"
            app:bounce="true"
            app:dragHeight="60"
            app:dragWidth="60"
            app:foregroundImage="@mipmap/ic_launcher"
            app:welt="true" />

    </FrameLayout>

## Java代码

    mDragView = findViewById(R.id.drag);
    mDragView.onClickListener(new DragView.OnClickListener() {
        @Override
        public void onClick() {
            Toast.makeText(activity,"Touch",Toast.LENGTH_SHORT).show();
        }
    });


效果如图：
<br>
![贴边](https://github.com/xiong955/DragView/blob/master/gif/1.gif)
![回弹](https://github.com/xiong955/DragView/blob/master/gif/2.gif)
![时间](https://github.com/xiong955/DragView/blob/master/gif/3.gif)
<br>
使用中遇到什么问题请issues反馈<br>
或者QQ联系我:619291607,备注Git