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
import android.widget.AbsListView;
import android.widget.AdapterView;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.adapter.ApkListAdapter;

import com.sdt.safefilemanager.helper.ApkIconResizer;
import com.sdt.safefilemanager.helper.IntentBuilder;

import com.sdt.safefilemanager.helper.Utils;
import com.sdt.safefilemanager.model.MediaFileListModel;

import java.io.IOException;
import java.util.ArrayList;


public class ApksListActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private LinearLayout noMediaLayout;
    private ArrayList<MediaFileListModel> mediaFileListModelsArray;
    private String TAG = "ApksListActivity";
    private ApkListAdapter apkListAdapter;
    //异步完成apk图标缩放
    private ApkIconResizer mApkImageResizer;
    private int mImageThumbSize;


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

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);

        mApkImageResizer = new ApkIconResizer(this, mImageThumbSize);
        //mApkImageResizer.setLoadingImage(R.drawable.empty_photo);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("安装包");
        mediaFileListModelsArray = new ArrayList<>();
        getApkList();
        apkListAdapter = new ApkListAdapter(this, mediaFileListModelsArray, mApkImageResizer);
        listview.setAdapter(apkListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaFileListModel model = mediaFileListModelsArray.get(position);
                viewFile(model.getFilePath());
            }
        });


        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mApkImageResizer.setPauseWork(true);
                    }
                } else {
                    mApkImageResizer.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });


    }


    private void getApkList() {
        Cursor mCursor = null;
        String selection = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.apk'" + ") and " + MediaStore.Files.FileColumns.SIZE + " >1 ";
        try{
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
                    mediaFileListModel.setFileName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)) + ".apk");
                    mediaFileListModel.setFilePath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)));
                    mediaFileListModelsArray.add(mediaFileListModel);
                } while (mCursor.moveToNext());
            }
        }catch(Exception e){
               e.printStackTrace();
               Log.e(TAG,"query error");
        }finally{
            if(mCursor!=null)
            mCursor.close();
        }
    }


    private void viewFile(String filePath) {
        try {
            IntentBuilder.viewFile(this, filePath);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "fail to view file: " + e.toString());
            Toast.makeText(getApplicationContext(), "无法打开此文件", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mApkImageResizer.setExitTasksEarly(false);
        apkListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mApkImageResizer.setPauseWork(false);
        mApkImageResizer.setExitTasksEarly(true);
        mApkImageResizer.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApkImageResizer.closeCache();
    }


}