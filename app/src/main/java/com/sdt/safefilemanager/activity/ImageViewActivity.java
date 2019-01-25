package com.sdt.safefilemanager.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import com.sdt.safefilemanager.R;


public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        ImageView imageFile = (ImageView) findViewById(R.id.image_file);
        TextView lblImageName = (TextView) findViewById(R.id.lbl_image_name);
        Intent imagePath = getIntent();
        String imageFilePath = imagePath.getStringExtra("imagePath");
        lblImageName.setText(imagePath.getStringExtra("imageName"));
        File imgFile = new File(imageFilePath);
        if (imgFile.exists()) {
            Log.d("action", imageFilePath);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageFile.setImageBitmap(myBitmap);
        }
    }
}
