package com.sdt.safefilemanager.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.adapter.AllFilesListAdapter;
import com.sdt.safefilemanager.helper.ApkIconResizer;
import com.sdt.safefilemanager.helper.FileOperationHelper;
import com.sdt.safefilemanager.helper.FileUtils;
import com.sdt.safefilemanager.helper.HomeConstants;
import com.sdt.safefilemanager.helper.ImageResizer;
import com.sdt.safefilemanager.helper.IntentBuilder;
import com.sdt.safefilemanager.helper.Utils;
import com.sdt.safefilemanager.helper.VideoThumbnailResizer;
import com.sdt.safefilemanager.model.CopyFilesDataHolder;
import com.sdt.safefilemanager.model.MediaFileListModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AllFilesActivity extends AppCompatActivity implements FileOperationHelper.IOperationProgressListener {


    /***接口1：模式2：全选**/
    public interface  AllSelectListner{
        public void onAllSelectCheckedChanged(boolean b);
    }
    /**接口2：模式所有: 当crumb被按下时，或者adapter中文件夹被点击后，改变当前路径**/
    public interface CurrentFilePathChanged{
        public void onCurrentFilePathChanged(String filePath);
    }

    /**接口3：模式所有: 当crumb被按下时，改变当前路径**/
    public interface FilePathChangedByCrum{
        public void onFilePathChangedByCrum(String filePath);
    }
    /**重要属性**/
    private CrumbView mCrumbView;
    private String TAG = "AllFilesActivity";
    private LinearLayout noMediaLayout;
    private int mAllFileMode;
    private AllFilesListAdapter mAllFilesListAdapter;
    private CrumbView.EnterDirectory mEnterDirectoryListner;
    private CrumbView.GoBackDirectory mGoBackDirectoryListner;
    private ArrayList<MediaFileListModel> mFileListModelsArray = null;
    private String mInitRootPath;
    private String mCurrentFilePath;
    Toolbar toolbar;
    private SweetAlertDialog progressDialog;

    /**模式2：多选模式：用到的控件**/
    private ImageButton mDeleteButton;
    private ImageButton mSendButton;
    private ImageButton mCutButton;
    private ImageButton mMoreButton;
    private ImageButton mCloseSelectButton;
    private CheckBox checkBoxSelectAll;
    private boolean mFromItem = false;
    private FileOperationHelper mFileOperationHelper;
    private LinearLayout toolbarSelectModeTop;
    private LinearLayout toolbarSelectModeBottom;
    private ArrayList<MediaFileListModel> mCheckedFileNameList;
    private boolean mbChoosePathByCheckboxOrNot;
    /**模式3：路径模式：用到的控件**/
    private ImageButton mPasteButton;
    private ImageButton mPasteCancelButton;
    private LinearLayout  toolbarChoosePathModeTop;
    private LinearLayout  toolbarChoosePathModeBottom;
    private ImageButton button_pasteclose;
    private MediaFileListModel mChoosedFileModel;

    /**resizer**/
    private ImageResizer mImageResizer;
    private int mImageThumbSize;
    private ApkIconResizer mApkImageResizer;
    private VideoThumbnailResizer mVideoThumbnailResizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_files);

        /**从外面传进来intent，指定Activity的根路径和Activity的模式**/
        Intent intent = getIntent();
        String path = ((Intent) intent).getStringExtra("rootPath");
        int mode = intent.getIntExtra("showMode",HomeConstants.ALLFILEMODE_NORMAL);
        if (mode<HomeConstants.ALLFILEMODE_NORMAL || mode >HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION){
            Log.e(TAG,"指定的AllFilesActivity类型错误");
            return;  //模式错误
        }
        mAllFileMode = mode;
        mInitRootPath = path;
        mCurrentFilePath = path;


        mCrumbView = findViewById(R.id.crumb_view);
        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,100);
        params.addRule(RelativeLayout.BELOW, R.id.toolbar);
        mCrumbView.setLayoutParams(params);


        mCrumbView = (CrumbView) findViewById(R.id.crumb_view);
        mCrumbView.setActivity(this,mInitRootPath);

       /**1.初始必要属性**/
        initResizer();
        mFileListModelsArray = new ArrayList<>();   //最关键的属性，当前列表中的所有文件
        getDirectory(mInitRootPath);
        mAllFilesListAdapter = new AllFilesListAdapter(mFileListModelsArray,this);
        mAllFilesListAdapter.setmImageResizer(mImageResizer,mVideoThumbnailResizer,mApkImageResizer);
        mAllFilesListAdapter.setAllFileMode(mAllFileMode);
        /***2.初始化控件****/

        noMediaLayout = (LinearLayout) findViewById(R.id.noMediaLayout);

        mFileOperationHelper = new FileOperationHelper(this);

        /**模式1**/
        final ListView listview = (ListView) findViewById(R.id.audio_listview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("所有文件");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /**模式2**/
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

        toolbarSelectModeTop = (LinearLayout) findViewById(R.id.toolbarSelectMode);
        toolbarSelectModeBottom = (LinearLayout) findViewById(R.id.toolbarSelectModeBottom);

        /**模式3**/
        mPasteButton = (ImageButton)findViewById(R.id.button_paste_chooseMode);
        mPasteCancelButton = (ImageButton)findViewById(R.id.button_cancel_chooseMode);
        toolbarChoosePathModeTop = (LinearLayout) findViewById(R.id.toolbarChoosePathMode);
        toolbarChoosePathModeBottom = (LinearLayout) findViewById(R.id.toolbarChoosePathModeBottom);
        button_pasteclose = (ImageButton) findViewById(R.id.button_pasteclose);
        initLayoutByMode();  //很重要，初始化mode不同显示不同底部栏。


         /**所有模式都需要：设置传递给导航栏的监听器，当导航栏被点击时会发生下面的语句。**/
        mCrumbView.setmFilePathChangedByCrumListner(new FilePathChangedByCrum() {
            @Override
            public void onFilePathChangedByCrum(String filePath) {
                getDirectory(filePath);
                mCurrentFilePath = filePath;
                clearCheckBoxSelection();
                mAllFilesListAdapter.notifyDataSetChanged();

            }
        });
        /***设置传递给Adapter的文件路径改变监听器，对于全部文件列表最关键的，点击文件夹会刷新列表展示文件夹中的文件*/
        mAllFilesListAdapter.setmFilePathChangedListner(new CurrentFilePathChanged() {
            @Override
            public void onCurrentFilePathChanged(String filePath) {
                Log.e(TAG,filePath);
                if(!FileUtils.isNormalFile(filePath)){
                    Toast.makeText(getApplicationContext(), "无法访问系统安全文件", Toast.LENGTH_SHORT).show();
                    return;
                }
                getDirectory(filePath);
                mCurrentFilePath = filePath;
                clearCheckBoxSelection();
                mEnterDirectoryListner.onEnterDirectory(filePath);
                mAllFilesListAdapter.notifyDataSetChanged();
            }
        });
        /**设置传递给Adapter的全选监听器*模式1和模式2都需要设置这些监听器，因为由模式1可以转换为模式2**/
        if(mAllFileMode!=HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION){
            mAllFilesListAdapter.setAllSelectListner(new AllSelectListner() {
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
            checkBoxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    HashMap<Integer, Boolean> isSelectedMap = mAllFilesListAdapter.getIsSelectedMap();
                    if(mFromItem){
                        mFromItem = false;
                        return;
                    }
                    if(b){
                        for(int key=0;key<mFileListModelsArray.size();key++){
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

                    mAllFilesListAdapter.notifyDataSetChanged();
                }
            });

            /***********模式1 底部暂时没有按钮没有事件*********/

            /***********模式2 的按钮点击事件******************/
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
                    //onOperationDelete();
                }
            });

            /**第三个按钮：剪切按钮**/
            mCutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            /**第四个按钮：更多选项按钮**应该出来菜单**/
            mMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }else{
            /***********模式3 的按钮点击事件*********/
            mPasteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOperationPaste();
                }
            });

            mPasteCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            button_pasteclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }


        listview.setAdapter(mAllFilesListAdapter);
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageResizer.setPauseWork(true);
                        mVideoThumbnailResizer.setPauseWork(true);
                        mApkImageResizer.setPauseWork(true);
                    }
                } else {
                    mImageResizer.setPauseWork(false);
                    mVideoThumbnailResizer.setPauseWork(false);
                    mApkImageResizer.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

    }

    /****1.8.根据mode的不同设置不同的界面模式****************************/
    //**显示选择路径底部的工具栏**/
    private void initLayoutByMode(){
        switch (mAllFileMode){
            case HomeConstants.ALLFILEMODE_NORMAL:
                break;
            case HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION:
                toolbarChoosePathModeTop.setVisibility(View.VISIBLE);
                toolbarChoosePathModeBottom.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
    //获取复制的目标路径，有两种方式，一个是直接粘贴，一个是选择一个checkbox的路径进行黏贴
    public MediaFileListModel getChoosedFile(){
            if(mAllFilesListAdapter.getIsChoosePathMap().size()>1){
                Log.e(TAG,"选择路径只能选择一项");
                return null;
            }
            if(mAllFilesListAdapter.getIsChoosePathMap().size() == 0){
                File file = new File(mCurrentFilePath);
                mChoosedFileModel = new MediaFileListModel(file.getName(),file.getAbsolutePath());
                mbChoosePathByCheckboxOrNot = false;

            } else if(mAllFilesListAdapter.getIsChoosePathMap().size() == 1){
                Iterator<Integer> it = mAllFilesListAdapter.getIsChoosePathMap().keySet().iterator();
                while(it.hasNext()){
                    MediaFileListModel temp =  mFileListModelsArray.get(it.next());
                    mChoosedFileModel = new MediaFileListModel(temp.getFileName(),temp.getFilePath());
                }
                mbChoosePathByCheckboxOrNot = true;

            }else{
                Log.e(TAG,"isChoosePathMap不应该出现不是0或1的情况");
                mChoosedFileModel = null;
            }
            return mChoosedFileModel;
    }

    public int getmAllFileMode() {
        return mAllFileMode;
    }

    public void setmAllFileMode(int mAllFileMode) {
        this.mAllFileMode = mAllFileMode;
    }

    /****1.8.根据mode的不同设置不同的界面模式****************************/
    public void setmEnterDirectoryListner(CrumbView.EnterDirectory mEnterDirectoryListner) {
        this.mEnterDirectoryListner = mEnterDirectoryListner;
    }

    public void setmGoBackDirectoryListner(CrumbView.GoBackDirectory mGoBackDirectoryListner) {
        this.mGoBackDirectoryListner = mGoBackDirectoryListner;
    }

    private void initResizer(){
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        // The ImageResizer takes care of loading images into our ImageView children asynchronously
        mImageResizer = new ImageResizer(this, mImageThumbSize);
        mApkImageResizer = new ApkIconResizer(this, mImageThumbSize);
        mVideoThumbnailResizer = new VideoThumbnailResizer(this,mImageThumbSize);
        //mImageResizer.setLoadingImage(R.drawable.empty_photo);

    }
    /***************和其它一样******************/
    public void viewFile(String filePath) {
        try {
            IntentBuilder.viewFile(this, filePath);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "fail to view file: " + e.toString());
            Toast.makeText(getApplicationContext(), "无法打开此文件", Toast.LENGTH_SHORT).show();
        }
    }


    /**模式2：特有函数**/
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
    //由模式1进入模式2
    public void showToolbarSelectMode(boolean isShow) {
        if(isShow){
            toolbarSelectModeTop.setVisibility(View.VISIBLE);
            toolbarSelectModeBottom.setVisibility(View.VISIBLE);
        }else{
            toolbarSelectModeTop.setVisibility(View.GONE);
            toolbarSelectModeBottom.setVisibility(View.GONE);
        }

    }

    /*模式2转变为模式1*/
    public void closeSelectionMode() {
        Log.e("3:closeSelectionMode:","want notifyData");
        if(mAllFilesListAdapter.getAllFileMode()==HomeConstants.ALLFILEMODE_SELECT){
            showToolbarSelectMode(false);
            mAllFilesListAdapter.getIsSelectedMap().clear();
            mAllFilesListAdapter.setAllFileMode(HomeConstants.ALLFILEMODE_NORMAL);
            mAllFileMode = HomeConstants.ALLFILEMODE_NORMAL;
            mAllFilesListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageResizer.setExitTasksEarly(false);
        mVideoThumbnailResizer.setExitTasksEarly(false);
        mApkImageResizer.setExitTasksEarly(false);
        mAllFilesListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageResizer.setPauseWork(false);
        mVideoThumbnailResizer.setPauseWork(false);
        mApkImageResizer.setPauseWork(false);

        mImageResizer.setExitTasksEarly(true);
        mVideoThumbnailResizer.setExitTasksEarly(true);
        mApkImageResizer.setExitTasksEarly(true);

        mImageResizer.flushCache();
        mVideoThumbnailResizer.flushCache();
        mApkImageResizer.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        mImageResizer.closeCache();
        mVideoThumbnailResizer.closeCache();
        mApkImageResizer.closeCache();
    }
    /******真正要显示数据啦****/
    private void getDirectory(String directoryPath) {
        if(mFileListModelsArray != null){
            mFileListModelsArray.clear();
        }
        File directory = new File(directoryPath);
        if(!directory.exists()||!directory.isDirectory()){
            Log.e(TAG,"当前文件夹路径不存在或并不是一个文件夹");
            return;
        }

        File[] files = directory.listFiles();     //返回当前目录下的所有文件
        for(File file:files){
            MediaFileListModel model = new MediaFileListModel();
            model.setFileName(file.getName());
            model.setFilePath(file.getAbsolutePath());
            mFileListModelsArray.add(model);
        }
    }

    /*onBack不能直接退出activity，要更新列表**/

    @Override
    public void onBackPressed() {
        if(mCurrentFilePath.equals(mInitRootPath)){
            super.onBackPressed();
        }else{
            File file = new File(mCurrentFilePath);
            mGoBackDirectoryListner.onGoBackDirectory(mCurrentFilePath);
            File parentFile = file.getParentFile();
            mCurrentFilePath = parentFile.getAbsolutePath();
            getDirectory(mCurrentFilePath);
            clearCheckBoxSelection();
            mAllFilesListAdapter.notifyDataSetChanged();
        }

    }

    /**1.8.paste的相关操作****/
    public void onOperationPaste(){
        doOperationPaste(getChoosedFile());
    }

    /**1.21.paste的相关操作,加入移动文件的****/
    private void  doOperationPaste(MediaFileListModel fileModelDst){
        //分类浏览中的复制，对应这里的paste
        if(CopyFilesDataHolder.getInstance().isBwaitForPasteByCopy()){
            if(fileModelDst == null){
                Log.e(TAG,"复制过程中目标路径为空");
                return;
            }
            showProgress("正在复制");
            boolean result = mFileOperationHelper.Paste(fileModelDst.getFilePath());

        }else if(CopyFilesDataHolder.getInstance().isBwaitForPasteByMove()){
            //分类浏览中的移动，对应这里的paste
            if(!mFileOperationHelper.canMove(fileModelDst.getFilePath())){
                Toast.makeText(getApplicationContext(), "移动路径嵌套包含，请重新选择路径", Toast.LENGTH_SHORT).show();
                return;
            }
            if(fileModelDst == null){
                Log.e(TAG,"移动过程中目标路径为空");
                return;
            }
            showProgress("正在移动");
            boolean result = mFileOperationHelper.Move(fileModelDst.getFilePath());
        }



    }
    private void showProgress(String msg) {
        progressDialog =new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText(msg);
        progressDialog.setCancelable(false);
        progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
        progressDialog.show();
    }

    //由模式3变到模式1---变到普通模式,暂时没有从模式3变为模式1的可能，那可能是选择路径后新建了一个Activity
    public void closeChoosePathMode() {
        Log.e("3:closeChoosePathMode:","want notifyData");
        if(mAllFilesListAdapter.getAllFileMode()==HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION
                && mAllFileMode == HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION){
            showToolbarChoosePathMode(false);
            mAllFilesListAdapter.getIsChoosePathMap().clear();
            mAllFilesListAdapter.setAllFileMode(HomeConstants.ALLFILEMODE_NORMAL);
            mAllFilesListAdapter.notifyDataSetChanged();
        }
    }

    public void showToolbarChoosePathMode(boolean isShow) {
        if(isShow){
            toolbarChoosePathModeTop.setVisibility(View.VISIBLE);
            toolbarChoosePathModeBottom.setVisibility(View.VISIBLE);
        }else{
            toolbarChoosePathModeTop.setVisibility(View.GONE);
            toolbarChoosePathModeBottom.setVisibility(View.GONE);
        }

    }
    /*********** FileOperationHelper复制后，会回调的函数 ，这是异步执行完啦************/
    /***********FileOperationHelper移动后，会回调的函数，这是异步执行啦**************/
    /***********移动和复制都执行这两个，会不会有错呢，还有如果本Acitivity的删除操作也执行这个吗，这是个问题**/
    @Override
    public void onFinish() {
        if (progressDialog != null) {
            progressDialog.setTitleText("文件粘贴成功!")
                    .setConfirmText("确定")
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            progressDialog = null;
        }

        refreshFilesList();

        closeChoosePathMode();

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
            Log.d(TAG, "file changed, send broadcast:" + intent.toString());
        } else {
            intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(new File(path)));
            Log.d(TAG, "file changed, send broadcast:" + intent.toString());
        }
        this.sendBroadcast(intent);
    }

    private void refreshFilesList() {
        if(mChoosedFileModel == null){
            return;
        }
        mFileListModelsArray.clear();
        if(mbChoosePathByCheckboxOrNot){
            mCurrentFilePath = mChoosedFileModel.getFilePath();
            getDirectory(mCurrentFilePath);
            clearCheckBoxSelection();
            mEnterDirectoryListner.onEnterDirectory(mCurrentFilePath);
            mAllFilesListAdapter.notifyDataSetChanged();
            Log.d(TAG,"复制之后，通过checkbox选择的路径，就进入这个路径的文件列表");
        }else{
            getDirectory(mCurrentFilePath);
            clearCheckBoxSelection();
            mAllFilesListAdapter.notifyDataSetChanged();
            Log.d(TAG,"复制之后，未通过checkbox选择目的路径，就在当前路径，重新刷新");
        }
    }

    private void clearCheckBoxSelection(){
        if(mAllFilesListAdapter!=null){
            if(mAllFileMode == HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION){
                mAllFilesListAdapter.getIsChoosePathMap().clear();
            }
            if(mAllFileMode == HomeConstants.ALLFILEMODE_SELECT){
                mAllFilesListAdapter.getIsSelectedMap().clear();
            }
        }else{
            Log.e(TAG,"clearCheckBoxSelection()-no mAllFilesListAdapter ");
        }

    }


}
