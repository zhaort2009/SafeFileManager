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
import com.sdt.safefilemanager.activity.DocListActivity;
import com.sdt.safefilemanager.model.FileInfoBean;

import java.util.ArrayList;

public class DocListAdapter extends BaseAdapter {

    private final ArrayList<FileInfoBean> docFileListArray;
    private final Activity activity;
    private LayoutInflater layoutInflater;

    public DocListAdapter(DocListActivity docListActivity, ArrayList<FileInfoBean> docFileListArray) {
        this.activity = docListActivity;
        this.docFileListArray = docFileListArray;
    }

    @Override
    public int getCount() {
        return docFileListArray.size();
    }

    @Override
    public Object getItem(int position) {
        return docFileListArray.get(position);
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
            view = layoutInflater.inflate(R.layout.doc_list_item_view, null);
        TextView lblFileName = (TextView) view.findViewById(R.id.file_name);
        ImageView imgItemIcon = (ImageView) view.findViewById(R.id.icon);
        FileInfoBean fileInfo = docFileListArray.get(position);
        lblFileName.setText(fileInfo.getFileName());
        imgItemIcon.setImageResource(fileInfo.getIconId());
        return view;
    }
}
