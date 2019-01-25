package com.sdt.safefilemanager.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.adapter.HomeFragmentsAdapter;
import com.sdt.safefilemanager.helper.ExternalStorage;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: zrt
 * @date: 2018/11/13
 * @describe: 主界面Home
 */
public class MainHomeActivity extends AppCompatActivity {

    //第一次点击事件发生的时间
    private long mExitTime;
    private long mExitInterval = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        initToolbar();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        HomeFragmentsAdapter adapter = new HomeFragmentsAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeUnencryptFragment(), "本机存储");
        adapter.addFragment(new HomeEncryptFragment(), "加密存储卡");
        viewPager.setAdapter(adapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        /*根据tab页更换标题*/
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        changeToolbar("本机存储");
                        break;

                    case 1:
                        changeToolbar("加密存储卡");
                        break;

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        new NewsAsyncTask().execute("hello");
//        List<String> list = getSdCardPaths(this,false);
//        for(String s:list){
//           Log.e("lastlast",s);
//        }

    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView textView = (TextView) findViewById(R.id.toolbar_title_home);
        textView.setTextColor(getResources().getColor(R.color.home_toolbar_title));
        textView.setText("本机存储");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }

    }

    /**
     * 点击两次返回退出app
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > mExitInterval) {
                Object mHelperUtils;
                Toast.makeText(this, "再按一次退出APP", Toast.LENGTH_SHORT).show();
                //System.currentTimeMillis()系统当前时间
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void changeToolbar(String title) {
        TextView textView = (TextView) findViewById(R.id.toolbar_title_home);
        textView.setText(title);
    }

    class NewsAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... strings) {
            final String state = Environment.getExternalStorageState();
            if ( Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {  // we can read the External Storage...
                //Retrieve the primary External Storage:
                final File primaryExternalStorage = Environment.getExternalStorageDirectory();

                //Retrieve the External Storages root directory:
                final String externalStorageRootDir;
                if ( (externalStorageRootDir = primaryExternalStorage.getParent()) == null ) {  // no parent...
                    Log.e("AAAAAAA:", "External Storage: " + primaryExternalStorage + "\n");
                }
                else {
                    final File externalStorageRoot = new File( externalStorageRootDir );
                    final File[] files = externalStorageRoot.listFiles();

                    for ( final File file : files ) {
                        if ( file.isDirectory() && file.canRead() && (file.listFiles().length > 0) ) {  // it is a real directory (not a USB drive)...
                            Log.e("HHHHHHHHHH:", "External Storage: " + file.getAbsolutePath() + "\n");
                        }
                    }
                }
            }
            Map<String, File> externalLocations = ExternalStorage.getAllStorageLocations();
            File sdCard = externalLocations.get(ExternalStorage.SD_CARD);
            File externalSdCard = externalLocations.get(ExternalStorage.EXTERNAL_SD_CARD);
            Log.e("CCCCCCCCCCC:", "External Storage: " + sdCard.getAbsolutePath() + "\n");
//            Log.d("DDDDDDDDDDD:", "External Storage: " + externalSdCard.getAbsolutePath() + "\n");

            Log.e("hello1",System.getenv("EXTERNAL_STORAGE"));
          //  Log.e("hello2",System.getenv("SECONDARY_STORAGE"));



            return null;

        }
    }





    /**
     * returns a list of all available sd cards paths, or null if not found.
     *
     * @param includePrimaryExternalStorage set to true if you wish to also include the path of the primary external storage
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static List<String> getSdCardPaths(final Context context, final boolean includePrimaryExternalStorage)
    {
        final File[] externalCacheDirs=ContextCompat.getExternalCacheDirs(context);
        if(externalCacheDirs==null||externalCacheDirs.length==0)
            return null;
        if(externalCacheDirs.length==1)
        {
            if(externalCacheDirs[0]==null)
                return null;
            final String storageState=EnvironmentCompat.getStorageState(externalCacheDirs[0]);
            if(!Environment.MEDIA_MOUNTED.equals(storageState))
                return null;
            boolean a =  !includePrimaryExternalStorage;
            boolean b=Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB;
            boolean c=Environment.isExternalStorageEmulated();
            if(!includePrimaryExternalStorage && b &&c)
                return null;
        }
        final List<String> result=new ArrayList<>();
        if(includePrimaryExternalStorage||externalCacheDirs.length==1)
            result.add(getRootOfInnerSdCardFolder(externalCacheDirs[0]));
        for(int i=1;i<externalCacheDirs.length;++i)
        {
            final File file=externalCacheDirs[i];
            if(file==null)
                continue;
            final String storageState=EnvironmentCompat.getStorageState(file);
            if(Environment.MEDIA_MOUNTED.equals(storageState))
                result.add(getRootOfInnerSdCardFolder(externalCacheDirs[i]));
        }
        if(result.isEmpty())
            return null;
        return result;
    }

    /** Given any file/folder inside an sd card, this will return the path of the sd card */
    private static String getRootOfInnerSdCardFolder(File file)
    {
        if(file==null)
            return null;
        final long totalSpace=file.getTotalSpace();
        while(true)
        {
            final File parentFile=file.getParentFile();
            if(parentFile==null||parentFile.getTotalSpace()!=totalSpace)
                return file.getAbsolutePath();
            file=parentFile;
        }
    }
}
