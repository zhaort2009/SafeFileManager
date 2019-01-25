package com.sdt.safefilemanager.activity;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.adapter.ChooseRootDirectoryAdapter;
import com.sdt.safefilemanager.adapter.HomeGridViewAdapter;
import com.sdt.safefilemanager.helper.ExpandableHeightGridView;
import com.sdt.safefilemanager.helper.ExternalStorage;
import com.sdt.safefilemanager.helper.HomeConstants;
import com.sdt.safefilemanager.model.HomeGridViewItem;
import com.sdt.safefilemanager.model.MediaFileListModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * @author: zrt
 * @date: 2018/11/13
 * @describe: 主界面--普通文件Fragment
 */

public class HomeUnencryptFragment extends Fragment {
    private ArrayList<HomeGridViewItem> mItems;
    private HomeGridViewAdapter mGridAdapter;
    private ExpandableHeightGridView mGridView;
    private LinearLayout mAllFileLayout;

    private int imgIds[] = {R.mipmap.audio, R.mipmap.video, R.mipmap.image, R.mipmap.doc, R.mipmap.apk, R.mipmap.rar};
    private String txtNames[] = {"音频", "视频", "图片", "文档", "安装包", "压缩包"};
    private ArrayList<String> txtSizes = new ArrayList<String>(Collections.nCopies(imgIds.length,"( " + " )"));
    private int imgPressedIds[] = {R.mipmap.audio_pressed, R.mipmap.video_pressed, R.mipmap.image_pressed, R.mipmap.doc_pressed, R.mipmap.apk_pressed, R.mipmap.rar_pressed};


    public HomeUnencryptFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        new NewAsyncTask().execute();
//        getAllCount();
//        Log.e("再次查询个数", "onResume：图片个数"+ txtSizes.get(2));
//        updateItemSize();
//        mGridAdapter.notifyDataSetChanged();
    }

    private void updateItemSize(){


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_unencrypt, container, false);
        mGridView = (ExpandableHeightGridView) view.findViewById(R.id.gridView);
        mGridView.setExpanded(true);
        mItems = new ArrayList<>();
        getAllCount();
        for (int i = 0; i < imgIds.length; i++) {

            HomeGridViewItem item = new HomeGridViewItem(imgIds[i], txtNames[i], txtSizes.get(i), imgPressedIds[i]);
            mItems.add(item);
        }
        mGridAdapter = new HomeGridViewAdapter(mItems, getContext());
        mGridView.setAdapter(mGridAdapter);
        mAllFileLayout = (LinearLayout)view.findViewById(R.id.allFilesLayout);
        /**所有文件layout的跳转**/
        mAllFileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent allFileIntent= new Intent(getActivity(),AllFilesActivity.class);
//                String root = Environment.getExternalStorageDirectory().getAbsolutePath();
//                allFileIntent.putExtra("showMode",HomeConstants.ALLFILEMODE_NORMAL);
//                allFileIntent.putExtra("rootPath",root);
//                startActivity(allFileIntent);
                 Intent  intent = new Intent(getActivity(), ChooseRootDirActivity.class);
                  startActivity(intent);

            }
        });

        return view;

    }

    private int getCount(String selection, Uri queryUri) {
        final Cursor mCursor = getActivity().getContentResolver().query(
                queryUri,
                null, selection, null,
                null);
        int count = mCursor.getCount();
        mCursor.close();
        return count;
    }


    private void getAllCount() {
        //music
        String selectionMusic = null;
        String musicCount = String.valueOf(getCount(selectionMusic, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI));
        txtSizes.set(0,"(" + musicCount + ")");


        //video
        String selectionVideo = null;
        String videoCount = String.valueOf(getCount(selectionVideo, MediaStore.Video.Media.EXTERNAL_CONTENT_URI));
        txtSizes.set(1,"(" + videoCount + ")");


        //image
        String selectionImage = null;
        String imageCount = String.valueOf(getCount(selectionImage, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

        Log.e("图片个数", "getAllCount：imageCount："+ imageCount);
        txtSizes.set(2,"(" + imageCount + ")");

        //doc
        String[] typeList = new String[]{"doc", "docx", "txt", "xls", "xlsx", "ppt", "pptx", "pdf", "htm", "html"};
        String selectionDoc = "(";

        for (String fileType : typeList) {
            selectionDoc = selectionDoc + MediaStore.Files.FileColumns.DATA + " LIKE '%." + fileType + "' or ";
        }
        selectionDoc = selectionDoc.substring(0,selectionDoc.length() - 4);
        selectionDoc =  selectionDoc + ") and " + MediaStore.Files.FileColumns.SIZE + " >1 ";
        String docCount = String.valueOf(getCount(selectionDoc, MediaStore.Files.getContentUri("external")));

        txtSizes.set(3,"(" + docCount + ")");

        //apk
        String selectionApk = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.apk'" + ") and " + MediaStore.Files.FileColumns.SIZE + " >1 ";
        String apkCount = String.valueOf(getCount(selectionApk, MediaStore.Files.getContentUri("external")));

        txtSizes.set(4,"(" + apkCount + ")");

        //zip
        String selectionZip = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.zip'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.jar'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.rar'" + ") and " + MediaStore.Files.FileColumns.SIZE + " >1 ";
        String zipCount = String.valueOf(getCount(selectionZip, MediaStore.Files.getContentUri("external")));

        txtSizes.set(5,"(" + zipCount + ")");
    }



    class NewAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {

            mGridAdapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(String... strings) {
            getAllCount();
            for (int i = 0; i < imgIds.length; i++) {
                mItems.get(i).setCapacity(txtSizes.get(i));
            }


            return null;
        }
    }




}
