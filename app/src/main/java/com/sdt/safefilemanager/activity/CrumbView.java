package com.sdt.safefilemanager.activity;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sdt.safefilemanager.R;

import java.io.File;

/**
 * @author: zrt
 * @date: 2018/12/30
 * @describe:
 */
public class CrumbView extends HorizontalScrollView {
    /**接口1：模式所有: 当crumb被按下时，改变当前路径**/
    public interface EnterDirectory{
        public void onEnterDirectory(String currentFilePath);
    }
    public interface GoBackDirectory{
        public void onGoBackDirectory(String deletFilePath);
    }
    public class ViewDataModel{
        int mIndex;
        String mFilePath;
    }

    //原有属性
    private int LIGHT_COLOR, DARK_COLOR;
    private Resources mRes;
    private LinearLayout mContainer;
    private FragmentManager mFragmentManager;
    private AllFilesActivity mActivity;
    private AllFilesActivity.FilePathChangedByCrum mFilePathChangedByCrumListner;
    private String mRootPath;
    public CrumbView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRes = context.getResources();
        TypedArray typedArray = mRes.obtainAttributes(attrs, R.styleable.CrumbViewAttrs);
        try{
            LIGHT_COLOR = typedArray.getColor(R.styleable.CrumbViewAttrs_light_color, mRes.getColor(R.color.light_color));
            DARK_COLOR = typedArray.getColor(R.styleable.CrumbViewAttrs_dark_color, mRes.getColor(R.color.dark_color));
        }finally {
            typedArray.recycle();
        }

        initView(context);
    }
    //mContainer是在外面Activity上那个crumbview内部的layout，是的！
    private void initView(Context context) {
        mContainer = new LinearLayout(context);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);
        mContainer.setPadding(mRes.getDimensionPixelOffset(R.dimen.crumb_view_padding), 0,
                mRes.getDimensionPixelOffset(R.dimen.crumb_view_padding), 0);
        mContainer.setGravity(Gravity.CENTER_VERTICAL);
        addView(mContainer);   //addView是父类HorizontalScrollView的函数，添加一个孩子Layout
    }

    public void setmFilePathChangedByCrumListner(AllFilesActivity.FilePathChangedByCrum mFilePathChangedByCrumListner) {
        this.mFilePathChangedByCrumListner = mFilePathChangedByCrumListner;
    }

    //activity是外面的Activity，将父亲Activity的指针传递进来，进而获得父亲activity管理的fragments
    public void setActivity(AllFilesActivity activity,String rootPath){
        mActivity = activity;
        mRootPath = rootPath;
        activity.setmGoBackDirectoryListner(new GoBackDirectory() {
            @Override
            public void onGoBackDirectory(String deletFilePath) {
                minusOneCrumb(deletFilePath);
            }
        });
        activity.setmEnterDirectoryListner(new EnterDirectory() {
            @Override
            public void onEnterDirectory(String currentFilePath) {
                addOneCrumb(currentFilePath);
            }
        });
        addOneCrumb(rootPath);   //第一个crumb不是由进入目录的原因而产生的，而是Activity初始化时带过来的根目录
//        mFragmentManager = activity.getSupportFragmentManager();
//        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                updateCrumbs();
//            }
//        });
        //updateCrumbs();
//        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.crumb_item_layout, null);
//        TextView tv = (TextView) itemView.findViewById(R.id.crumb_name);
//        tv.setText("hello"); // ft.setBreadCrumbTitle(getString(R.string.crumb_title, firstLevel));ft（子类指针）就是backStackEntry（父类指针）
//        //itemView.setTag(backStackEntry);               //把栈中的第一个ft绑在view1上（前面是空，后面是第一个）
//        mContainer.addView(itemView);
//        itemView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int fragmentCounts = mFragmentManager.getBackStackEntryCount();
//                for(int i =0;i<fragmentCounts;i++){
//                    mFragmentManager.popBackStack();
//                }
//
//            }
//        });
    }    //setActivity结束
    private void minusOneCrumb(String deletFilePath){
        int numCrumbs = mContainer.getChildCount();
        View deleteView = mContainer.getChildAt(numCrumbs-1);
        if (!(deleteView.getTag() instanceof ViewDataModel)){
            return;
        }
        ViewDataModel dataModelGet =(ViewDataModel)deleteView.getTag();
        if(dataModelGet == null){
            return;
        }

        if(deletFilePath.equals(dataModelGet.mFilePath)){
            mContainer.removeView(deleteView);
        }
    }
    private void addOneCrumb(String currentFilePath){
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.crumb_item_layout, null);
        TextView tv = (TextView) itemView.findViewById(R.id.crumb_name);
        File file = new File(currentFilePath);
        if(currentFilePath.equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
            tv.setText("内部存储设备");
        }else{
            tv.setText(file.getName());
        }

        ViewDataModel dataModel = new ViewDataModel();
        dataModel.mIndex = mContainer.getChildCount();  //将index设置为当前新增的index
        dataModel.mFilePath = currentFilePath;
        showRightArrow(itemView,false);  //最后一个不显示右侧箭头
        itemView.setTag(dataModel);   //将路径数据绑定在View
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(view.getTag() instanceof ViewDataModel)){
                    return;
                }
                ViewDataModel dataModelGet =(ViewDataModel)view.getTag();
                if(dataModelGet == null){
                    return;
                }
                File file = new File(dataModelGet.mFilePath);
                if(!file.exists()){
                    return;
                }
                if(dataModelGet.mIndex >= mContainer.getChildCount()){
                    return;
                }
                mFilePathChangedByCrumListner.onFilePathChangedByCrum(dataModelGet.mFilePath);
                //删除所有右侧的View;
                int numCrumbs = mContainer.getChildCount();
                int deleteCount = numCrumbs - dataModelGet.mIndex -1;
                mContainer.removeViews(dataModelGet.mIndex+1, deleteCount);
                showRightArrow(view,false);


            }
        });
        mContainer.addView(itemView);
        //除了最后一个view，前面的view都需要变成有箭头的
        for(int i =0; i<mContainer.getChildCount()-1; i++){
            showRightArrow(mContainer.getChildAt(i),true);
        }
        focusToRight();
    }
//    private void updateCrumbs() {
//
//        // 嵌套的fragment数量
//        int numFrags = mFragmentManager.getBackStackEntryCount();
//        // 面包屑的数量
//        int numCrumbs = mContainer.getChildCount();
//
//
//        //第一次调用就是numFrags=1，在一初始化crumbview控件结束，这时只有一个fragment
//        for(int i = 0; i < numFrags; i++){
//            final FragmentManager.BackStackEntry backStackEntry = mFragmentManager.getBackStackEntryAt(i);
//            if(i < numCrumbs-1){
//                int crumbIndex = i+1;
//                View view = mContainer.getChildAt(crumbIndex);
//                Object tag = view.getTag();
//                if(tag != backStackEntry){
//                    for(int j = crumbIndex; j < numCrumbs; j++){
//                        mContainer.removeViewAt(crumbIndex);
//                    }
//                    numCrumbs = crumbIndex;
//                }
//            }                      //你内存想的不错，但你没想到，这两步内存，一个是LayoutContainer的，一个是Fragmentmanagers的，同时你应该相当这两个内存应有两个类来管
//            if(i >= numCrumbs-1){    //这是那个一个文本和一个向右箭头的itemView
//                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.crumb_item_layout, null);
//                TextView tv = (TextView) itemView.findViewById(R.id.crumb_name);
//                tv.setText(backStackEntry.getBreadCrumbTitle());
//                itemView.setTag(backStackEntry);
//                itemView.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {      //每个item new出来都有自己的点击事件
//                        FragmentManager.BackStackEntry bse;
//                        if (v.getTag() instanceof FragmentManager.BackStackEntry) {
//                            bse = (FragmentManager.BackStackEntry) v.getTag();
//                            mFragmentManager.popBackStack(bse.getId(), 0);   //flag=0 表示不弹出自己
//                        } else {
//                            //全部回退
//                            int count = mFragmentManager.getBackStackEntryCount();   //itemview的tag不是Entry的类型，意外情况
//                            if (count > 0) {
//                                bse = mFragmentManager.getBackStackEntryAt(0);
//                                mFragmentManager.popBackStack(bse.getId(), 0);
//                            }
//                        }
//                    }
//                });
//                mContainer.addView(itemView);
//            }
//        }
//        numCrumbs = mContainer.getChildCount();
//        while(numCrumbs-1 > numFrags){
//            mContainer.removeViewAt(numCrumbs - 1);
//            numCrumbs--;
//        }
//
//        //调整可见性
//        for (int i = 0; i < numCrumbs; i++) {
//            final View child = mContainer.getChildAt(i);
//            // 高亮
//            highLightIndex(child, !(i < numCrumbs - 1));
//        }
//
//        // 滑动到最后一个
//        post(new Runnable() {
//            @Override
//            public void run() {
//                fullScroll(ScrollView.FOCUS_RIGHT);
//            }
//        });
//    }

    private  void focusToRight(){
        // 滑动到最后一个
        post(new Runnable() {
            @Override
            public void run() {
                fullScroll(ScrollView.FOCUS_RIGHT);
            }
        });
    }

     private void  showRightArrow(View view, boolean isShow){
         ImageView image = (ImageView) view.findViewById(R.id.crumb_icon);
         if(isShow){
             image.setVisibility(View.VISIBLE);
         }else{
             image.setVisibility(View.GONE);
         }

     }

//    public void highLightIndex(View view, boolean highLight) {
//        TextView text = (TextView) view.findViewById(R.id.crumb_name);
//        ImageView image = (ImageView) view.findViewById(R.id.crumb_icon);
//        if (highLight) {
//            text.setTextColor(LIGHT_COLOR);
//            image.setVisibility(View.GONE);
//        } else {
//            text.setTextColor(DARK_COLOR);
//            image.setVisibility(View.VISIBLE);
//        }
//    }
}
