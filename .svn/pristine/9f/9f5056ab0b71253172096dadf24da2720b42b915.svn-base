package com.sdt.safefilemanager.activity;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.adapter.AudioListAdapter;
import com.sdt.safefilemanager.adapter.DocListAdapter;
import com.sdt.safefilemanager.helper.FileUtils;
import com.sdt.safefilemanager.model.FileInfoBean;
import com.sdt.safefilemanager.model.MediaFileListModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DocListActivity extends AppCompatActivity {

    private LinearLayout noMediaLayout;
    private ArrayList<FileInfoBean> docFileListArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_list);

        ListView listview = (ListView) findViewById(R.id.doc_listview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        noMediaLayout = (LinearLayout) findViewById(R.id.noMediaLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("文档");
        docFileListArray = new ArrayList<>();

        DocListAdapter docListAdapter = new DocListAdapter(this, docFileListArray);
        listview.setAdapter(docListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfoBean fileInfo = docFileListArray.get(position);
                FileUtils.openFiles(DocListActivity.this, fileInfo.getFilePath());
//                try {
//                    getAudioPlayer(model.getFileName(), model.getFilePath());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });

        String[] typeList = new String[]{"doc", "docx", "txt", "xls", "xlsx", "ppt", "pptx", "pdf", "htm", "html"};
        FileUtils.getDocFileList(this, typeList, docFileListArray);
        docListAdapter.notifyDataSetChanged();
        //获取所有文档列表
//        List<FileInfoBean> files = new ArrayList<FileInfoBean>();
        // 扫描files文件库
//        Cursor c = null;
//        try {
//            c = this.getContentResolver().query(MediaStore.Files.getContentUri("external"), new String[]{"_id", MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE}, null, null, null);
//            int dataindex = c.getColumnIndex(MediaStore.Files.FileColumns.DATA);
//            int sizeindex = c.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
//
//            while (c.moveToNext()) {
//                String path = c.getString(dataindex);
//                String name = path.substring(path.lastIndexOf('/') + 1, path.length());
//
//                if (FileUtils.getFileType(path) == 0) {
//                    if (!FileUtils.isExists(path)) {
//                        continue;
//                    }
//                    long size = c.getLong(sizeindex);
//                    FileInfoBean fileInfo = new FileInfoBean(path, name, FileUtils.getFileIconByPath(path), size);
////                    files.add(fileInfo);
//                    docFileListArray.add(fileInfo);
//                    docListAdapter.notifyDataSetChanged();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (c != null) {
//                c.close();
//            }
//        }
    }
}
