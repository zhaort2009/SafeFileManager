package com.sdt.safefilemanager.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.adapter.AudioListAdapter;
import com.sdt.safefilemanager.adapter.ZipListAdapter;
import com.sdt.safefilemanager.helper.IntentBuilder;
import com.sdt.safefilemanager.helper.Utilities;
import com.sdt.safefilemanager.model.MediaFileListModel;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 毕业僧
 * www.biyeseng.cn
 */
public class ZipsListActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private LinearLayout noMediaLayout;
    private ArrayList<MediaFileListModel> mediaFileListModelsArray;
    private String TAG = "ZipsListActivity";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);
        ListView listview = (ListView) findViewById(R.id.audio_listview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        noMediaLayout = (LinearLayout) findViewById(R.id.noMediaLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("压缩包");
        mediaFileListModelsArray = new ArrayList<>();
        getZipList();
        ZipListAdapter zipListAdapter = new ZipListAdapter(this, mediaFileListModelsArray);
        listview.setAdapter(zipListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaFileListModel model = mediaFileListModelsArray.get(position);
                viewFile(model.getFilePath());
            }
        });
    }

    private void getZipList() {
        Cursor mCursor = null;
        try{
            String selection = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.zip'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.jar'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.rar'" + ") and " + MediaStore.Files.FileColumns.SIZE + " >1 ";
            mCursor = getContentResolver().query(
                    MediaStore.Files.getContentUri("external"),
                    new String[]{MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.DATA}, selection, null,
                    "LOWER(" + MediaStore.Files.FileColumns.TITLE + ") ASC");
            Log.d("audio list", "" + mCursor.getCount());
            if (mCursor.getCount() == 0)
                noMediaLayout.setVisibility(View.VISIBLE);
            if (mCursor.moveToFirst()) {
                do {
                    MediaFileListModel mediaFileListModel = new MediaFileListModel();
                    String filepath = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                    String fileName = filepath.substring(filepath.lastIndexOf("/") + 1);
                    mediaFileListModel.setFileName(fileName);
                    mediaFileListModel.setFilePath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)));
                    mediaFileListModelsArray.add(mediaFileListModel);
                } while (mCursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"query error");
        }finally {
            if(mCursor!=null)
                mCursor.close();
        }

    }


    private void viewFile(String filePath) {
        try {
            IntentBuilder.viewFile(this, filePath);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "无法打开此文件", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "fail to view file: " + e.toString());
        }
    }
}