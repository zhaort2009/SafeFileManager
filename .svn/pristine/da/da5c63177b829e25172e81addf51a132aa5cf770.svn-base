package com.sdt.safefilemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.activity.AudiosListActivity;
import com.sdt.safefilemanager.activity.ZipsListActivity;
import com.sdt.safefilemanager.model.MediaFileListModel;

import java.util.ArrayList;


@SuppressWarnings("DefaultFileTemplate")
public class ZipListAdapter extends BaseAdapter {
    private final ArrayList<MediaFileListModel> mediaFileListModelsArray;
    private final Activity activity;
    private LayoutInflater layoutInflater;

    public ZipListAdapter(ZipsListActivity zipListActivity, ArrayList<MediaFileListModel> mediaFileListModelsArray) {
        this.activity = zipListActivity;
        this.mediaFileListModelsArray = mediaFileListModelsArray;
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
        imgItemIcon.setImageResource(R.mipmap.ic_mp3);
        return view;
    }
}
