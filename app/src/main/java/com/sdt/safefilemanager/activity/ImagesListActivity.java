package com.sdt.safefilemanager.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.adapter.ImagesListAdapter;
import com.sdt.safefilemanager.helper.FileOperationHelper;
import com.sdt.safefilemanager.helper.HomeConstants;
import com.sdt.safefilemanager.helper.ImageResizer;
import com.sdt.safefilemanager.helper.IntentBuilder;
import com.sdt.safefilemanager.helper.Utils;
import com.sdt.safefilemanager.model.CopyFilesDataHolder;
import com.sdt.safefilemanager.model.MediaFileListModel;
import com.sdt.safefilemanager.helper.FileOperationHelper.IOperationProgressListener;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 *  目前只有Image的分类浏览可以进行复制等操作
 */
public class ImagesListActivity extends AppCompatActivity implements IOperationProgressListener {
    private ArrayList<MediaFileListModel> imageListModelsArray;
    private ArrayList<MediaFileListModel> mCheckedFileNameList = new ArrayList<MediaFileListModel>();
    private LinearLayout noMediaLayout;
    private ImageResizer mImageResizer;
    private int mImageThumbSize;
    private ImagesListAdapter audioListAdapter;
    private String TAG = "ImagesListActivity";
    private LinearLayout toolbarSelectModeTop;
    private LinearLayout toolbarSelectModeBottom;

    private ImageButton mDeleteButton;
    private ImageButton mSendButton;
    private ImageButton mCutButton;
    private ImageButton mMoreButton;
    private ImageButton mCloseSelectButton;
    private CheckBox checkBoxSelectAll;
    private boolean mFromItem = false;
    private SweetAlertDialog progressDialog;
    private FileOperationHelper mFileOperationHelper;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);
        final ListView listview = (ListView) findViewById(R.id.audio_listview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        checkBoxSelectAll = (CheckBox)findViewById(R.id.ckbSelectAll);
        mSendButton = (ImageButton)findViewById(R.id.button_send);
        mDeleteButton = (ImageButton)findViewById(R.id.button_delete);
        mCutButton = (ImageButton)findViewById(R.id.button_cut);
        mMoreButton = (ImageButton)findViewById(R.id.button_more);
        mCloseSelectButton = (ImageButton)findViewById(R.id.button_close);

        mSendButton.setEnabled(false);
        mDeleteButton.setEnabled(false);
        mCutButton.setEnabled(false);
        mMoreButton.setEnabled(false);


        noMediaLayout = (LinearLayout) findViewById(R.id.noMediaLayout);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        // The ImageResizer takes care of loading images into our ImageView children asynchronously
        mImageResizer = new ImageResizer(this, mImageThumbSize);
        //mImageResizer.setLoadingImage(R.drawable.empty_photo);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("图片");



        toolbarSelectModeTop = (LinearLayout) findViewById(R.id.toolbarSelectMode);
        toolbarSelectModeBottom = (LinearLayout) findViewById(R.id.toolbarSelectModeBottom);
        mFileOperationHelper = new FileOperationHelper(this);

        imageListModelsArray = new ArrayList<>();
        getImagesList();
        audioListAdapter = new ImagesListAdapter(this, imageListModelsArray, mImageResizer, new AllSelectListner() {
            @Override
            public void onAllSelectCheckedChanged(boolean b) {
                if (!b&&!checkBoxSelectAll.isChecked()){
                    return;
                }else if(!b&&checkBoxSelectAll.isChecked()){
                    mFromItem = true;
                    checkBoxSelectAll.setChecked(false);
                }else if(b){
                    mFromItem = true;
                    checkBoxSelectAll.setChecked(true);
                }
            }
        });

        listview.setAdapter(audioListAdapter);



        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageResizer.setPauseWork(true);
                    }
                } else {
                    mImageResizer.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        checkBoxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                HashMap<Integer, Boolean> isSelectedMap = audioListAdapter.getIsSelectedMap();
                if(mFromItem){
                    mFromItem = false;
                    return;
                }
                if(b){
                    for(int key=0;key<imageListModelsArray.size();key++){
                        isSelectedMap.put(key,true);
                    }
                    mSendButton.setEnabled(true);
                    mDeleteButton.setEnabled(true);
                    mCutButton.setEnabled(true);
                    mMoreButton.setEnabled(true);
                }else{
                    isSelectedMap.clear();
                    mSendButton.setEnabled(false);
                    mDeleteButton.setEnabled(false);
                    mCutButton.setEnabled(false);
                    mMoreButton.setEnabled(false);
                }

                audioListAdapter.notifyDataSetChanged();


            }
        });
        mCloseSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSelectionMode();
            }
        });
        /**第一个按钮：发送按钮**/
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /**第二个按钮：删除按钮**/
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOperationDelete();
            }
        });

        /**第三个按钮：剪切按钮**/
        mCutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOperationMove();
            }
        });

        /**第四个按钮：更多选项按钮**应该出来菜单**/
        mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOperationCopy();
            }
        });
    }

    private void getImagesList() {
        if(imageListModelsArray!= null){
            imageListModelsArray.clear();
        }

        Cursor mCursor = null;
        try{
            mCursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA}, null, null,
                    "LOWER(" + MediaStore.Images.Media.TITLE + ") ASC");
            if (mCursor.getCount() == 0)
                noMediaLayout.setVisibility(View.VISIBLE);
            if (mCursor.moveToFirst()) {
                do {
                    //加入删除的判断逻辑
                    File imgFile = new File(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                    if(!imgFile.exists()){
                            continue;
                    }
                    MediaFileListModel mediaFileListModel = new MediaFileListModel();
                    mediaFileListModel.setFileName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
                    mediaFileListModel.setFilePath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                    Log.e(TAG,mediaFileListModel.getFilePath());
                    imageListModelsArray.add(mediaFileListModel);
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


    @Override
    public void onResume() {
        super.onResume();

        closeSelectionMode();
        refreshImagesList();
        audioListAdapter.notifyDataSetChanged();
        mImageResizer.setExitTasksEarly(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageResizer.setPauseWork(false);
        mImageResizer.setExitTasksEarly(true);
        mImageResizer.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        mImageResizer.closeCache();
    }

    public void viewFile(String filePath) {
        try {
            IntentBuilder.viewFile(this, filePath);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "fail to view file: " + e.toString());
            Toast.makeText(getApplicationContext(), "无法打开此文件", Toast.LENGTH_SHORT).show();
        }
    }


    public void showToolbarSelectMode(boolean isShow) {
        if(isShow){
            toolbarSelectModeTop.setVisibility(View.VISIBLE);
            toolbarSelectModeBottom.setVisibility(View.VISIBLE);
        }else{
            toolbarSelectModeTop.setVisibility(View.GONE);
            toolbarSelectModeBottom.setVisibility(View.GONE);
        }

    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater=getMenuInflater();
//        inflater.inflate(R.menu.menu_category, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    public void setSelectTitle(String title){
        TextView seletTitle=(TextView)findViewById(R.id.toolbar_title_select);
        seletTitle.setText(title);
    }

    public void setSendButton(Boolean isEnabled){
        mSendButton.setEnabled(isEnabled);
    }

    public void setDeleteButton(Boolean isEnabled){
        mDeleteButton.setEnabled(isEnabled);
    }

    public void setCutButton(Boolean isEnabled){
        mCutButton.setEnabled(isEnabled);
    }
    public void setMoreButton(Boolean isEnabled){
        mMoreButton.setEnabled(isEnabled);
    }



    public interface AllSelectListner{

        public void onAllSelectCheckedChanged(boolean b);
    }

    /**activity中删除的功能**/

    //获取选中的文件详细信息列表
    public ArrayList<MediaFileListModel> getSelectedFileList() {
        mCheckedFileNameList.clear();
        for(int key:audioListAdapter.getIsSelectedMap().keySet()){
            mCheckedFileNameList.add(imageListModelsArray.get(key));
        }
        return mCheckedFileNameList;
    }

    public void onOperationDelete() {
        doOperationDelete(getSelectedFileList());
    }

    private void doOperationDelete(final ArrayList<MediaFileListModel> selectedFileList) {
        //这是副本
        final ArrayList<MediaFileListModel> selectedFiles = new ArrayList<MediaFileListModel>(selectedFileList);
        SweetAlertDialog sweetAlertDialog= new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("确定删除")
                .setContentText("删除后文件将无法恢复")
                .setCancelText("取消")
                .setConfirmText("确定")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        closeSelectionMode();
                        sDialog.dismiss();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        showProgress("正在删除");
                        boolean result = mFileOperationHelper.Delete(selectedFiles);
                        //这地方很快返回因为delete只是开启了一个execute异步执行，何时执行完，就是调用onfinish执行完

                    }
                });
        sweetAlertDialog.show();
    }


    public void closeSelectionMode() {
        if(audioListAdapter.ismIsMultiSeleteMode()){
                showToolbarSelectMode(false);
                audioListAdapter.getIsSelectedMap().clear();
                audioListAdapter.setmIsMultiSeleteMode(false);
                audioListAdapter.notifyDataSetChanged();
        }
    }

    private void showProgress(String msg) {
        progressDialog =new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText(msg);
        progressDialog.setCancelable(false);
        progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
        progressDialog.show();
    }
    /*********** FileOperationHelper删除后，会回调的函数 ，这是异步执行完啦************/
    @Override
    public void onFinish() {
        if (progressDialog != null) {
            progressDialog.setTitleText("文件删除成功!")
                    .setConfirmText("确定")
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            progressDialog = null;
        }

        refreshImagesList();

        closeSelectionMode();

    }

    @Override
    public void onFileChanged(String path) {
        notifyFileSystemChanged(path);
    }

    public static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";
    private void notifyFileSystemChanged(String path) {
        if (path == null)
            return;
        final File f = new File(path);
        if(f==null){
            return;
        }
        final Intent intent;
        if (f.isDirectory()) {
            intent = new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR);
            intent.setData(Uri.fromFile(new File(path)));

        } else {
            intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(new File(path)));

        }
        this.sendBroadcast(intent);
    }

    private void refreshImagesList() {
        imageListModelsArray.clear();
        getImagesList();
    }

    /**复制文件的操作***/
    public void onOperationCopy() {
        CopyFilesDataHolder.getInstance().stopCopy();   //先停止当前的进行了一半的操作
        CopyFilesDataHolder.getInstance().stopMove();
        doOperationCopy(getSelectedFileList());
    }

    private void doOperationCopy(final ArrayList<MediaFileListModel> selectedFileList) {
        if(CopyFilesDataHolder.getInstance().canStartAction()){
            CopyFilesDataHolder.getInstance().startCopy();
            CopyFilesDataHolder.getInstance().addCopyFiles(selectedFileList);
            Intent  intent = new Intent(this, ChooseRootDirActivity.class);
            intent.putExtra("showMode",HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION);
            startActivity(intent);
            closeSelectionMode();
        }else{
            Log.e(TAG,"当前无法完成复制，又其余操作正在进行");
        }
    }

    /**1.21.复制文件的操作***/
    public void onOperationMove() {
        CopyFilesDataHolder.getInstance().stopCopy();   //先停止当前的进行了一半的操作
        CopyFilesDataHolder.getInstance().stopMove();
        doOperationMove(getSelectedFileList());
    }

    private void doOperationMove(final ArrayList<MediaFileListModel> selectedFileList) {

        if(CopyFilesDataHolder.getInstance().canStartAction()){
            CopyFilesDataHolder.getInstance().startMove();
            CopyFilesDataHolder.getInstance().addCopyFiles(selectedFileList);
            Intent  intent = new Intent(this, ChooseRootDirActivity.class);
            intent.putExtra("showMode",HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION);
            startActivity(intent);
            closeSelectionMode();
        }else{
            Log.e(TAG,"当前无法完成移动，又其余操作正在进行");
        }
    }
}