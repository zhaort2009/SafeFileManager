package com.sdt.safefilemanager.activity;

import android.content.ActivityNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
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

import java.util.ArrayList;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.adapter.VideoListAdapter;
import com.sdt.safefilemanager.helper.IntentBuilder;
import com.sdt.safefilemanager.helper.Utils;
import com.sdt.safefilemanager.helper.VideoThumbnailResizer;
import com.sdt.safefilemanager.model.MediaFileListModel;


public class VideosListActivity extends AppCompatActivity {
    private ArrayList<MediaFileListModel> videoListModelsArray;
    private LinearLayout noMediaLayout;
    private VideoThumbnailResizer mVideoImageResizer;
    private int mImageThumbSize;
    private VideoListAdapter audioListAdapter;
    private String TAG = "VideoListActivity";

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
        mVideoImageResizer = new VideoThumbnailResizer(this, mImageThumbSize);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("视频");
        videoListModelsArray = new ArrayList<>();
        getVideoList();
        audioListAdapter = new VideoListAdapter(this, videoListModelsArray, mVideoImageResizer);
        listview.setAdapter(audioListAdapter);


        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mVideoImageResizer.setPauseWork(true);
                    }
                } else {
                    mVideoImageResizer.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MediaFileListModel model = videoListModelsArray.get(position);
                viewFile(model.getFilePath());
            }
        });


    }

    private void getVideoList() {
        Cursor mCursor = null;
        try{
             mCursor = getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA}, null, null,
                    "LOWER(" + MediaStore.Video.Media.TITLE + ") ASC");
            if (mCursor.getCount() == 0)
                noMediaLayout.setVisibility(View.VISIBLE);
            if (mCursor.moveToFirst()) {
                do {
                    MediaFileListModel mediaFileListModel = new MediaFileListModel();
                    mediaFileListModel.setFileName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                    mediaFileListModel.setFilePath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                    videoListModelsArray.add(mediaFileListModel);
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

    @Override
    public void onResume() {
        super.onResume();
        mVideoImageResizer.setExitTasksEarly(false);
        audioListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoImageResizer.setPauseWork(false);
        mVideoImageResizer.setExitTasksEarly(true);
        mVideoImageResizer.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideoImageResizer.closeCache();
    }


    private void viewFile(String filePath) {
        try {
            IntentBuilder.viewFile(this, filePath);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "fail to view file: " + e.toString());
            Toast.makeText(getApplicationContext(), "无法打开此文件", Toast.LENGTH_SHORT).show();
        }
    }

}

