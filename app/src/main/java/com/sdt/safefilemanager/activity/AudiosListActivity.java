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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.adapter.AudioListAdapter;
import com.sdt.safefilemanager.helper.IntentBuilder;
import com.sdt.safefilemanager.helper.Utilities;
import com.sdt.safefilemanager.model.MediaFileListModel;


public class AudiosListActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private LinearLayout noMediaLayout;
    private ArrayList<MediaFileListModel> mediaFileListModelsArray;
    private String TAG = "AudiosListActivity";

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
        getSupportActionBar().setTitle("音乐");
        mediaFileListModelsArray = new ArrayList<>();
        getMusicList();
        AudioListAdapter audioListAdapter = new AudioListAdapter(this, mediaFileListModelsArray);
        listview.setAdapter(audioListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaFileListModel model = mediaFileListModelsArray.get(position);
                viewFile(model.getFilePath());
            }
        });
    }


    private void getMusicList() {
        Cursor mCursor = null;
        try {
            mCursor = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null,
                    "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");
            Log.d("audio list", "" + mCursor.getCount());
            if (mCursor.getCount() == 0)
                noMediaLayout.setVisibility(View.VISIBLE);
            if (mCursor.moveToFirst()) {
                do {
                    MediaFileListModel mediaFileListModel = new MediaFileListModel();
                    mediaFileListModel.setFileName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                    mediaFileListModel.setFilePath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                    mediaFileListModelsArray.add(mediaFileListModel);
                } while (mCursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "query error");

        } finally {
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
}