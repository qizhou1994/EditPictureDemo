package com.zq.editpicturedemo;

import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.zq.editpicturedemo.editpictureview.EditPictureView;
import com.zq.editpicturedemo.editpictureview.ImgUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    EditPictureView editView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditPictureView editView = findViewById(R.id.editView);

        //一个bitmap的数据源 也就是多张图
        List<Bitmap> list = new ArrayList<>();
        list.add(ImgUtil.getBitmapFormResources(this, R.drawable.index_3));
        list.add(ImgUtil.getBitmapFromVectorDrawable(this, R.drawable.index_2));
        list.add(ImgUtil.getBitmapFromVectorDrawable(this, R.drawable.index_ic_1));
        //直接set到视图中
        editView.setDataSourceList(list);

    }
}
