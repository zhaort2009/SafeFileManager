package com.sdt.safefilemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.activity.ImagesListActivity;
import com.sdt.safefilemanager.helper.BitmapUtils;
import com.sdt.safefilemanager.helper.ImageResizer;
import com.sdt.safefilemanager.model.MediaFileListModel;

import com.sdt.safefilemanager.helper.FileUtils;

public class ImagesListAdapter extends BaseAdapter {
    private final ArrayList<MediaFileListModel> mediaFileListModelsArray;
    private final Activity activity;
    private LayoutInflater layoutInflater;
    private Context mContext;
    private ImageResizer mImageResizer;
    private  ImagesListActivity imagesListActivity ;
    private ImagesListActivity.AllSelectListner allSelectListner;

    private boolean mIsMultiSeleteMode = false;

    private HashMap<Integer,Boolean> isSelectedMap = new HashMap<>();

    public HashMap<Integer, Boolean> getIsSelectedMap() {
        return isSelectedMap;
    }

    public ImagesListAdapter(ImagesListActivity audiosListActivity, ArrayList<MediaFileListModel> mediaFileListModelsArray, ImageResizer resizer,ImagesListActivity.AllSelectListner allSelectListner) {
        this.activity = audiosListActivity;
        this.mediaFileListModelsArray = mediaFileListModelsArray;
        this.mContext = audiosListActivity;
        this.mImageResizer = resizer;
        this.imagesListActivity = audiosListActivity;
        this.allSelectListner = allSelectListner;

    }

    @Override
    public int getCount() {
        return mediaFileListModelsArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaFileListModelsArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            view = layoutInflater.inflate(R.layout.media_list_item_view, null);
        TextView lblFileName = (TextView) view.findViewById(R.id.file_name);
        ImageView imgItemIcon = (ImageView) view.findViewById(R.id.icon);
        TextView lblFileTime = (TextView) view.findViewById(R.id.file_time);
        TextView lblFileSize = (TextView) view.findViewById(R.id.file_size);

        MediaFileListModel mediaFileListModel = mediaFileListModelsArray.get(position);
        lblFileName.setText(mediaFileListModel.getFileName());
        File imgFile = new File(mediaFileListModel.getFilePath());
        lblFileTime.setText(FileUtils.getModifiedTime(imgFile));
        lblFileSize.setText(FileUtils.getDynamicSpace(imgFile.length()));
        if (imgFile.exists()) {
            mImageResizer.loadImage(imgFile.getAbsolutePath(), imgItemIcon);
        }


        final CheckBox checkBox = (CheckBox)view.findViewById(R.id.ckb);
        if(mIsMultiSeleteMode){
            checkBox.setVisibility(View.VISIBLE);
        }else{
            checkBox.setVisibility(View.GONE);
        }
        final int  positionf = position;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mIsMultiSeleteMode){
                    MediaFileListModel model = mediaFileListModelsArray.get(positionf);
                    imagesListActivity.viewFile(model.getFilePath());
                } else if(mIsMultiSeleteMode){
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
                    imagesListActivity.setSelectTitle(numTitle);

                    if(isSelectedMap.size()==0){
                        imagesListActivity.setSendButton(false);
                        imagesListActivity.setDeleteButton(false);
                        imagesListActivity.setCutButton(false);
                        imagesListActivity.setMoreButton(false);
                    }else{
                        imagesListActivity.setSendButton(true);
                        imagesListActivity.setDeleteButton(true);
                        imagesListActivity.setCutButton(true);
                        imagesListActivity.setMoreButton(true);
                    }
                    if (isSelectedMap.size()==mediaFileListModelsArray.size()){
                        allSelectListner.onAllSelectCheckedChanged(true);
                    }else{
                        allSelectListner.onAllSelectCheckedChanged(false);
                    }

                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!mIsMultiSeleteMode){
                    mIsMultiSeleteMode = true;
                    imagesListActivity.showToolbarSelectMode(true);
                    view.performClick();
                }else if(mIsMultiSeleteMode) {

                }
                return true;
            }
        });


        if(isSelectedMap!=null&&isSelectedMap.containsKey(positionf)){
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }
        String numTitle="已选择"+isSelectedMap.size()+"项";
        imagesListActivity.setSelectTitle(numTitle);

        return view;
    }

    public boolean ismIsMultiSeleteMode() {
        return mIsMultiSeleteMode;
    }

    public void setmIsMultiSeleteMode(boolean mIsMultiSeleteMode) {
        this.mIsMultiSeleteMode = mIsMultiSeleteMode;
    }

    public ArrayList<MediaFileListModel> getMediaFileListModelsArray() {
        return mediaFileListModelsArray;
    }
}
