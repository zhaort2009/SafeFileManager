package com.sdt.safefilemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.activity.ApksListActivity;
import com.sdt.safefilemanager.activity.ZipsListActivity;
import com.sdt.safefilemanager.helper.ApkIconResizer;
import com.sdt.safefilemanager.model.MediaFileListModel;
import com.sdt.safefilemanager.model.ParamHolder;

import java.util.ArrayList;


@SuppressWarnings("DefaultFileTemplate")
public class ApkListAdapter extends BaseAdapter {
    private final ArrayList<MediaFileListModel> mediaFileListModelsArray;
    private final Activity activity;
    private LayoutInflater layoutInflater;
    private Context mContext;

    private ApkIconResizer mApkImageResizer;


    public ApkListAdapter(ApksListActivity apkListActivity, ArrayList<MediaFileListModel> mediaFileListModelsArray, ApkIconResizer resizer) {
        this.activity = apkListActivity;
        this.mediaFileListModelsArray = mediaFileListModelsArray;

        this.mContext = apkListActivity;

        this.mApkImageResizer = resizer;
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

        MediaFileListModel mediaFileListModel = mediaFileListModelsArray.get(position);
        lblFileName.setText(mediaFileListModel.getFileName());


        ParamHolder paramHolder = new ParamHolder(mContext, mediaFileListModel.getFilePath());

        mApkImageResizer.loadImage(paramHolder, imgItemIcon);
        return view;
    }

}
