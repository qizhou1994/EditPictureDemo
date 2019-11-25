# 解决多个图片需要缩放、移动、旋转问题
当前采用的是在一个视图当中进行多个图片的处理
# 作用
对于一个视图中多个bitmap每一个进行缩放，移动，旋转等操作且互不干扰。  
主要是对画布中的bitmap进行操作（并没有改变bitmap数据源，而是根据bitmap与matrix属性值进行操作）
# 基本信息
1. 主要视图代码：  
[EditPictureView](app/src/main/java/com/zq/editpicturedemo/editpictureview/EditPictureView.java)设置行列属性，有默认值，然后在java代码中去设置数据源（一个bitmap列表）。  
2. 主要存放每一个bitmap信息的载体：  
[PictureData](app/src/main/java/com/zq/editpicturedemo/editpictureview/PictureData.java)主要用于存放每一张图在视图中的位置，缩放信息等属性数据。

[Activity](app/src/main/java/com/zq/editpicturedemo/MainActivity.java):
```
    EditPictureView editView = findViewById(R.id.editView);
    //一个bitmap的数据源 也就是多张图
    List<Bitmap> list = new ArrayList<>();
    list.add(ImgUtil.getBitmapFormResources(this, R.drawable.index_3));
    list.add(ImgUtil.getBitmapFromVectorDrawable(this, R.drawable.index_2));
    list.add(ImgUtil.getBitmapFromVectorDrawable(this, R.drawable.index_ic_1));
    //直接set到视图中
    editView.setDataSourceList(list);
```
[xml](app/src/main/res/layout/activity_main.xml)
```
 <com.zq.editpicturedemo.editpictureview.EditPictureView
    android:id="@+id/editView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:normal_rank="1"
    app:normal_row="1"/>
```
用来设置行列的，然后会去将图片自适应控制他的大小  
`app:normal_rank="1" app:normal_row="1"`

# 知识点
1. Matrix进行缩放，平移，旋转（只可以使用一次set其它的都用post不然会相互覆盖，看到的效果只会是最后一个post）  
2. 触摸事件的分类，当拦截触摸事件的时候记得返回true.
3. 手指的区分 
4. 当前demo有一个额外点，是关于图像处理的，对Bitmap的像素按位修改数值而让bitmap变成一张两个颜色的图，详情可见：  
[ImgUtil](app/src/main/java/com/zq/editpicturedemo/editpictureview/ImgUtil.java)的getBitmapFromVectorDrawable下。
    
# demo效果示例:
<iframe height=500 width=320 src="gif.gif"/>  

  


