package com.sdt.safefilemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.activity.AllFilesActivity;
import com.sdt.safefilemanager.activity.ImagesListActivity;
import com.sdt.safefilemanager.helper.ApkIconResizer;
import com.sdt.safefilemanager.helper.FileUtils;
import com.sdt.safefilemanager.helper.HomeConstants;
import com.sdt.safefilemanager.helper.ImageResizer;
import com.sdt.safefilemanager.helper.IntentBuilder;
import com.sdt.safefilemanager.helper.VideoThumbnailResizer;
import com.sdt.safefilemanager.model.MediaFileListModel;
import com.sdt.safefilemanager.model.ParamHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author: zrt
 * @date: 2018/12/31
 * @describe:
 */
public class AllFilesListAdapter extends BaseAdapter {
    private final ArrayList<MediaFileListModel>  mFileListModelsArray;  //final表示指向的数组不会再变化地址
    private final Activity activity;

    private LayoutInflater mlayoutInflater;
    private Context mContext;

    private ImageResizer mImageResizer;
    private VideoThumbnailResizer mVideoImageResizer;
    private ApkIconResizer mApkIconResizer;

    private AllFilesActivity allFilesActivity;
    private int allFileMode;
    private String TAG = "AllFilesListAdapter" ;

    //模式1：普通模式
    //模式2：可多选模式
    AllFilesActivity.AllSelectListner allSelectListner;
    AllFilesActivity.CurrentFilePathChanged mFilePathChangedListner;
    private HashMap<Integer,Boolean> isSelectedMap = new HashMap<>();
    //模式3：单选模式，选择路径
    private HashMap<Integer,Boolean> isChoosePathMap = new HashMap<>();




    /***********方法*****************************/

    /***1.构造方法****/
    public AllFilesListAdapter(ArrayList<MediaFileListModel> mediaFileListModelsArray, AllFilesActivity allFilesActivity) {
        this.mFileListModelsArray = mediaFileListModelsArray;
        this.allFilesActivity = allFilesActivity;
        this.mContext = allFilesActivity;
        this.activity = allFilesActivity;

    }
    /***2.所有模式：属性set方法****/
    public void setmImageResizer(ImageResizer mImageResizer, VideoThumbnailResizer mVideoImageResizer,ApkIconResizer mApkIconResizer ) {
        this.mImageResizer = mImageResizer;
        this.mVideoImageResizer = mVideoImageResizer;
        this.mApkIconResizer = mApkIconResizer;
    }

    /***3.模式2：属性set方法****/
    public void setAllSelectListner(AllFilesActivity.AllSelectListner allSelectListner) {
        this.allSelectListner = allSelectListner;
    }

    public void setmFilePathChangedListner(AllFilesActivity.CurrentFilePathChanged mFilePathChangedListner) {
        this.mFilePathChangedListner = mFilePathChangedListner;
    }

    /***4.属性set方法****/
    public void setAllFileMode(int allFileMode) {
        this.allFileMode = allFileMode;
    }

    /***5.模式2：选择模式：属性get方法****/
    public HashMap<Integer, Boolean> getIsSelectedMap() {
        return isSelectedMap;
    }

    public int getAllFileMode() {
        return allFileMode;
    }

    /***6.模式3：路径模式：属性get方法****/
    public HashMap<Integer, Boolean> getIsChoosePathMap() {
        return isChoosePathMap;
    }


    /*************Adapter必须实现的适配器接口********************/

    @Override
    public int getCount() {
        return  mFileListModelsArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileListModelsArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null)
            mlayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null)
            view = mlayoutInflater.inflate(R.layout.allfiles_item_view,null);
        /**1.获取item中的所有控件**/
        TextView lblFileName = (TextView) view.findViewById(R.id.file_name);
        TextView lblFileTime = (TextView) view.findViewById(R.id.file_time);
        TextView lblFileSize = (TextView) view.findViewById(R.id.file_size);
        ImageView imgDirRightArrow = (ImageView) view.findViewById(R.id.imageDirRightArrow);
        ImageView imgItemIcon = (ImageView) view.findViewById(R.id.icon);

        /**2.根据File设置item中的所有控件**/
        MediaFileListModel fileListModel = mFileListModelsArray.get(position);
        final File file = new File(fileListModel.getFilePath());

        lblFileName.setText(fileListModel.getFileName());
        lblFileTime.setText(FileUtils.getModifiedTime(file));
        if(file.isDirectory()){
            int nums = FileUtils.getItemsNumInDir(file);
            if(nums!=-1){
                lblFileSize.setText(nums+"项");
            }else{
                lblFileSize.setText("");
                Log.e(TAG, "获取文件夹子项数目出错");
            }

        }else{
            lblFileSize.setText(FileUtils.getDynamicSpace(file.length()));
        }

        //图标要根据文件类型来设置
        setIconByFileType(imgItemIcon,file);
        /**3.根据模式确定是否显示checkbox,根据是否是文件夹显示右侧箭头**/
        final CheckBox checkBox = (CheckBox)view.findViewById(R.id.ckb);
        switch (allFileMode){
            case HomeConstants.ALLFILEMODE_NORMAL:
                checkBox.setVisibility(View.GONE);
                if(file.isDirectory()){
                    imgDirRightArrow.setVisibility(View.VISIBLE);
                }else{
                    imgDirRightArrow.setVisibility(View.GONE);
                }
                break;
            case HomeConstants.ALLFILEMODE_SELECT:
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setFocusable(false);
                checkBox.setClickable(false);
                checkBox.setFocusableInTouchMode(false);
                imgDirRightArrow.setVisibility(View.GONE);
                break;
            case HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION:
                imgDirRightArrow.setVisibility(View.GONE);
                if(file.isDirectory()){
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setFocusable(false);
                    checkBox.setClickable(true);
                    checkBox.setFocusableInTouchMode(false);
                }else{
                    checkBox.setVisibility(View.GONE);
                }
                break;
        }
        /**4.根据模式确定点击item后的行为**/
        final int  positionf = position;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allFileMode==HomeConstants.ALLFILEMODE_NORMAL){
                    if(file.isDirectory()){
                        mFilePathChangedListner.onCurrentFilePathChanged(file.getAbsolutePath());
                        //通知Activity中的listview更新
                    }else{
                        MediaFileListModel model = mFileListModelsArray.get(positionf);
                        allFilesActivity.viewFile(model.getFilePath());
                    }
                } else if(allFileMode==HomeConstants.ALLFILEMODE_SELECT){
                    if(!checkBox.isChecked()){
                        isSelectedMap.put(positionf,true);
                    }else{
                        isSelectedMap.remove(positionf);
                    }
                    if(isSelectedMap!=null&&isSelectedMap.containsKey(positionf)){
                        checkBox.setChecked(true);
                    }else {
                        checkBox.setChecked(false);
                    }
                    String numTitle="已选择"+isSelectedMap.size()+"项";
                    allFilesActivity.setSelectTitle(numTitle);

                    if(isSelectedMap.size()==0){
                        allFilesActivity.setSendButton(false);
                        allFilesActivity.setDeleteButton(false);
                        allFilesActivity.setCutButton(false);
                        allFilesActivity.setMoreButton(false);
                    }else{
                        allFilesActivity.setSendButton(true);
                        allFilesActivity.setDeleteButton(true);
                        allFilesActivity.setCutButton(true);
                        allFilesActivity.setMoreButton(true);
                    }
                    if (isSelectedMap.size()==mFileListModelsArray.size()){
                        allSelectListner.onAllSelectCheckedChanged(true);
                    }else{
                        allSelectListner.onAllSelectCheckedChanged(false);
                    }

                }else if(allFileMode==HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION){
                    if(file.isDirectory()){
                        //通知Activity中的listview更新
                        mFilePathChangedListner.onCurrentFilePathChanged(file.getAbsolutePath());
                    }else{
                        MediaFileListModel model = mFileListModelsArray.get(positionf);
                        allFilesActivity.viewFile(model.getFilePath());
                    }
                }
            }
        });
        /**5.根据模式确定点击checkbox点击后的行为**/
//        checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(allFileMode==HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION){
//                    if(!checkBox.isChecked()){
//                        isChoosePathMap.clear();    //选择路径模式只允许一个文件被选中
//                        isChoosePathMap.put(positionf,true);
//                    }else{
//                        isChoosePathMap.remove(positionf);
//                    }
//                    if(isChoosePathMap!=null&&isChoosePathMap.containsKey(positionf)){
//                        checkBox.setChecked(true);
//                    }else {
//                        checkBox.setChecked(false);
//                    }
//                }
//
//            }
//        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(allFileMode==HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION){
                    if(b){
                        isChoosePathMap.clear();    //选择路径模式只允许一个文件被选中
                        isChoosePathMap.put(positionf,true);
                    }else{
                        isChoosePathMap.remove(positionf);
                    }
                    if(isChoosePathMap!=null&&isChoosePathMap.containsKey(positionf)){
                        checkBox.setChecked(true);
                    }else {
                        checkBox.setChecked(false);
                    }
                    notifyDataSetChanged();
                }
            }
        });
        /**6.根据模式确定点击item长按后的行为**/
        /**暂时屏蔽，这样就无法在全部文件界面进行操作，无法进入模式2
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(allFileMode == HomeConstants.ALLFILEMODE_NORMAL){
                    allFileMode = HomeConstants.ALLFILEMODE_SELECT;
                    isSelectedMap.clear();
                    allFilesActivity.showToolbarSelectMode(true);
                    allFilesActivity.setmAllFileMode(HomeConstants.ALLFILEMODE_SELECT);
                }
                return true;
            }
        });*/

        //模式2中全选模式需要用到的,由外面更新的全选
        if(allFileMode == HomeConstants.ALLFILEMODE_SELECT){
            if(isSelectedMap!=null&&isSelectedMap.containsKey(positionf)){
                checkBox.setChecked(true);
            }else {
                checkBox.setChecked(false);
            }
            String numTitle="已选择"+isSelectedMap.size()+"项";
            allFilesActivity.setSelectTitle(numTitle);

        }
        if(allFileMode == HomeConstants.ALLFILEMODE_CHOOSEPATHOPERATION) {
            if (isChoosePathMap != null && isChoosePathMap.containsKey(positionf)) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }
        return view;
    }

    public void setIconByFileType(ImageView iconHolder,File file){
        if(file.isDirectory()){
            iconHolder.setImageResource(R.mipmap.ic_folder);
            return;
        }
        String mimetype = IntentBuilder.getMimeType(file.getPath());
        if(mimetype.contains("audio")){
            iconHolder.setImageResource(R.mipmap.ic_mp3);
        }else if(mimetype.contains("video")){
            mVideoImageResizer.loadImage(file.getAbsolutePath(),iconHolder);
        }else if(mimetype.contains("image")){
            mImageResizer.loadImage(file.getAbsolutePath(),iconHolder);
        }else if(FileUtils.getFileType(file.getPath())==FileUtils.TYPE_DOC){
            iconHolder.setImageResource(FileUtils.getFileIconByPath(file.getPath()));
        }else if(FileUtils.getFileType(file.getPath())==FileUtils.TYPE_APK){
            ParamHolder paramHolder = new ParamHolder(mContext, file.getAbsolutePath());
            mApkIconResizer.loadImage(paramHolder,iconHolder);
        }else if(FileUtils.getFileType(file.getPath())==FileUtils.TYPE_ZIP){
            iconHolder.setImageResource(R.mipmap.rar);
        }else{
            iconHolder.setImageResource(R.mipmap.ic_unknown_file);
        }

    }

}
