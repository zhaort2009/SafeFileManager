package com.sdt.safefilemanager.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.adapter.ChooseRootDirectoryAdapter;
import com.sdt.safefilemanager.helper.HomeConstants;
import com.sdt.safefilemanager.model.MediaFileListModel;

import java.util.ArrayList;

public class ChooseRootDirActivity extends AppCompatActivity {

    private String TAG = "ChooseRootDirActivity";
    private ChooseRootDirectoryAdapter mChooseRootDirAdapter;
    private ArrayList<MediaFileListModel> mRootDirModelsArray = null;
    private int mAllFileMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_root_directory);
        /**1.8.获取intent**/
        Intent intent = getIntent();
        int mode = ((Intent)intent).getIntExtra("showMode",HomeConstants.ALLFILEMODE_NORMAL);
        if (mode<HomeConstants.ALLFILEMODE_NORMAL || mode >HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION){
            Log.e(TAG,"指定的AllFilesActivity类型错误");
            return;  //模式错误
        }
        mAllFileMode = mode;
        /**1.8.获取intent**/
        final ListView listview = (ListView) findViewById(R.id.rootDirectory_listview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("选择目录");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRootDirModelsArray = new ArrayList<>();
        getRootDirs();
        mChooseRootDirAdapter = new ChooseRootDirectoryAdapter(mRootDirModelsArray,this);
        mChooseRootDirAdapter.setmAllFileMode(mAllFileMode);
        listview.setAdapter(mChooseRootDirAdapter);
    }
    public void getRootDirs(){
        MediaFileListModel model = new MediaFileListModel();
        model.setFileName("内部存储设备");
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        model.setFilePath(root);
        mRootDirModelsArray.add(model);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
