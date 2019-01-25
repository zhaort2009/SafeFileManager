package com.sdt.safefilemanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.activity.AllFilesActivity;
import com.sdt.safefilemanager.activity.ChooseRootDirActivity;
import com.sdt.safefilemanager.helper.FileUtils;
import com.sdt.safefilemanager.helper.HomeConstants;
import com.sdt.safefilemanager.helper.Utilities;
import com.sdt.safefilemanager.model.MediaFileListModel;

import java.util.ArrayList;

/**
 * @author: zrt
 * @date: 2019/1/2
 * @describe:
 */
public class ChooseRootDirectoryAdapter extends BaseAdapter {
    private final ArrayList<MediaFileListModel> mediaFileListModelsArray;
    private ChooseRootDirActivity mActivity;
    private Context mContext;
    private int  mAllFileMode;

    private LayoutInflater mlayoutInflater;

    public ChooseRootDirectoryAdapter(ArrayList<MediaFileListModel> mediaFileListModelsArray, ChooseRootDirActivity mActivity) {
        this.mediaFileListModelsArray = mediaFileListModelsArray;
        this.mActivity = mActivity;
        this.mContext = mActivity;
    }
    /**1.8.为ChooseRoot界面添加mAllFileMode模式**/
    public int getmAllFileMode() {
        return mAllFileMode;
    }

    public void setmAllFileMode(int mAllFileMode) {
        this.mAllFileMode = mAllFileMode;
    }
    /**1.8.为ChooseRoot界面添加mAllFileMode模式**/
    @Override
    public int getCount() {
        return mediaFileListModelsArray.size();
    }

    @Override
    public Object getItem(int i) {
        return mediaFileListModelsArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if(view == null)
            mlayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null)
            view = mlayoutInflater.inflate(R.layout.root_directory_item,null);
        /**1.获取item中的所有控件**/
        TextView lblRootDirName = (TextView) view.findViewById(R.id.rootDir_name);
        TextView lblRootDirFreeSpace = (TextView) view.findViewById(R.id.rootDir_freeSpace);
        TextView lblRootDirAllSpace = (TextView) view.findViewById(R.id.rootDir_allSpace);
        ImageView imgRootDirIcon = (ImageView) view.findViewById(R.id.rootDir_icon);

        FileUtils.SDCardInfo  sdCardInfo = FileUtils.getSDCardInfo();
        lblRootDirFreeSpace.setText("可用:"+FileUtils.getDynamicSpace(sdCardInfo.free));
        lblRootDirAllSpace.setText("总共:"+FileUtils.getDynamicSpace(sdCardInfo.total));
        if(true){
            imgRootDirIcon.setImageResource(R.mipmap.apk);
        }
        if(true){
            lblRootDirName.setText(mediaFileListModelsArray.get(i).getFilePath());
        }
        final int positionF = i;
        view.setOnClickListener(new View.OnClickListener() {
            //跳转AllFilesActivity的模式1：浏览模式
            @Override
            public void onClick(View view) {
                Intent allFileIntent= new Intent(mActivity,AllFilesActivity.class);
                //String root = Environment.getExternalStorageDirectory().getAbsolutePath();
                /**1.8.为ChooseRoot界面跳转AllFileActivity添加showMode参数，原来也有，现在根据传入来定**/
                allFileIntent.putExtra("showMode",mAllFileMode);
                allFileIntent.putExtra("rootPath",mediaFileListModelsArray.get(positionF).getFilePath());   //这后面要改成传进来的
                mContext.startActivity(allFileIntent);
                mActivity.finish();
            }
        });
        return view;
    }
}
