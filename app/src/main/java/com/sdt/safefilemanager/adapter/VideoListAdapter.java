package com.sdt.safefilemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.activity.VideosListActivity;
import com.sdt.safefilemanager.helper.VideoThumbnailResizer;
import com.sdt.safefilemanager.model.MediaFileListModel;


public class VideoListAdapter extends BaseAdapter {
    private final ArrayList<MediaFileListModel> mediaFileListModelsArray;
    private final Activity activity;
    private LayoutInflater layoutInflater;
    private VideoThumbnailResizer mVideoImageResizer;

    public VideoListAdapter(VideosListActivity audiosListActivity, ArrayList<MediaFileListModel> mediaFileListModelsArray, VideoThumbnailResizer resizer) {
        this.activity = audiosListActivity;
        this.mediaFileListModelsArray = mediaFileListModelsArray;
        this.mVideoImageResizer = resizer;
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
        mVideoImageResizer.loadImage(mediaFileListModel.getFilePath(), imgItemIcon);
        return view;
    }
}
