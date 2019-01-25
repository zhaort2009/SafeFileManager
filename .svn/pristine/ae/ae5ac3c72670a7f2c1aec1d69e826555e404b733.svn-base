package com.sdt.safefilemanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.activity.AudiosListActivity;
import com.sdt.safefilemanager.activity.HomeGridViewItemLayout;
import com.sdt.safefilemanager.activity.ImagesListActivity;
import com.sdt.safefilemanager.activity.VideosListActivity;
import com.sdt.safefilemanager.helper.HomeConstants;
import com.sdt.safefilemanager.model.HomeGridViewItem;

import java.util.ArrayList;

/**
 * @author: zrt
 * @date: 2018/11/13
 * @describe: 主界面-加密文件Fragment-GridView-Adapter
 */
public class HomeGridViewEncryptAdapter extends BaseAdapter {
    private ArrayList<HomeGridViewItem> mGridItemList;
    private Context mContext;

    public HomeGridViewEncryptAdapter(ArrayList<HomeGridViewItem> mGridItemList, Context mContext) {
        this.mGridItemList = mGridItemList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mGridItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGridItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HomeGridViewEncryptAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item, parent, false);
            holder=new HomeGridViewEncryptAdapter.ViewHolder();
            holder.imgView=(ImageView)convertView.findViewById(R.id.imageView);
            holder.txtViewName=(TextView)convertView.findViewById(R.id.textViewName);
            holder.txtViewSize=(TextView)convertView.findViewById(R.id.textViewSize);
            convertView.setTag(holder);

        } else {
            holder=(HomeGridViewEncryptAdapter.ViewHolder)convertView.getTag();
        }

        holder.imgView.setImageResource(mGridItemList.get(position).getImageResourceID());
        holder.txtViewName.setText(mGridItemList.get(position).getImageName());
        holder.txtViewSize.setText(mGridItemList.get(position).getCapacity());
        holder.txtViewName.setTextColor(mContext.getResources().getColor(R.color.home_grid_normal));

        final int postionF = position;
        HomeGridViewItemLayout llBackground = (HomeGridViewItemLayout)convertView.findViewById(R.id.gridItemLayout);
        llBackground.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent;
                switch(postionF){
                    case HomeConstants.MUSIC:
//                        intent = new Intent(mContext, AudiosListActivity.class);
//                        mContext.startActivity(intent);
                        break;
                    case HomeConstants.VIDEO:
//                        intent = new Intent(mContext, VideosListActivity.class);
//                        mContext.startActivity(intent);
                        break;
                    case HomeConstants.IMAGE:
//                        intent = new Intent(mContext, ImagesListActivity.class);
//                        mContext.startActivity(intent);
                        break;
                    case HomeConstants.DOC:
                        break;
                    case HomeConstants.RAR:
                        break;
                    default:
                        break;
                }
            }
        });
        llBackground.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        holder.imgView.setImageResource(mGridItemList.get(postionF).getImagePressedID());
                        holder.txtViewName.setTextColor(mContext.getResources().getColor(R.color.home_grid_pressed));
                        break;
                    case MotionEvent.ACTION_UP:
                        holder.imgView.setImageResource(mGridItemList.get(postionF).getImageResourceID());
                        holder.txtViewName.setTextColor(mContext.getResources().getColor(R.color.home_grid_normal));
                        view.performClick();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        holder.imgView.setImageResource(mGridItemList.get(postionF).getImageResourceID());
                        holder.txtViewName.setTextColor(mContext.getResources().getColor(R.color.home_grid_normal));
                    default:
                        break;
                }

                return false;
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView imgView;
        TextView txtViewName;
        TextView txtViewSize;
    }
}
