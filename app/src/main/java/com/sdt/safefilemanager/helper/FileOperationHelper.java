package com.sdt.safefilemanager.helper;

/**
 * @author:
 * @date: 2018/12/4
 * @describe:
 */
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.sdt.safefilemanager.activity.ImagesListActivity;
import com.sdt.safefilemanager.model.CopyFilesDataHolder;
import com.sdt.safefilemanager.model.MediaFileListModel;

public class FileOperationHelper {
    public interface IOperationProgressListener {
        void onFinish();

        void onFileChanged(String path);
    }
    private static final String LOG_TAG = "FileOperation";
    private ArrayList<MediaFileListModel> mCurFileNameList = new ArrayList<MediaFileListModel>();
    private boolean mMoving;
    private IOperationProgressListener mOperationListener;

    private Context context;


    public FileOperationHelper(IOperationProgressListener l) {
        mOperationListener = l;
        //context = (ImagesListActivity) l;

    }



    /**共用的异步函数，每调用一次新建一个AsyncTask对象，并执行execute，开启子线程**/
    private void asnycExecute(Runnable r) {
        final Runnable _r = r;
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                synchronized(mCurFileNameList) {
                    _r.run();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                if (mOperationListener != null) {
                    mOperationListener.onFinish();
                }
                super.onPostExecute(o);
            }
        }.execute();
    }

    /**删除操作****************************Start******************/

    public boolean Delete(ArrayList<MediaFileListModel> files) {
        copyFileList(files);
        asnycExecute(new Runnable() {
            @Override
            public void run() {
                for (MediaFileListModel f : mCurFileNameList) {
                    DeleteFile(f);
                }
                clear();
            }
        });
        return true;
    }

    private void copyFileList(ArrayList<MediaFileListModel> files) {
        synchronized(mCurFileNameList) {
            mCurFileNameList.clear();
            for (MediaFileListModel f : files) {
                mCurFileNameList.add(f);
            }
        }
    }


    protected void DeleteFile(MediaFileListModel f) {
        if (f == null) {
            Log.e(LOG_TAG, "DeleteFile: null parameter");
            return;
        }

        File file = new File(f.getFilePath());
        boolean directory = file.isDirectory();
        if (directory) {
            for (File child : file.listFiles()) {
                if (FileUtils.isNormalFile(child.getAbsolutePath())) {
                    DeleteFile(FileUtils.GetFileInfo(child, true));
                }
            }
        }
        Uri deleteFileUri = Uri.fromFile(file);
        boolean bDeleteResult = file.delete();
        mOperationListener.onFileChanged(file.getAbsolutePath());



        Log.v(LOG_TAG, "DeleteFile >>> " + f.getFilePath());
    }

    public void clear() {
        synchronized(mCurFileNameList) {
            mCurFileNameList.clear();
        }
    }

    /**删除操作****************************End******************/


    /**粘贴操作****************************Start******************/
    public boolean Paste(String path) {
        ArrayList<MediaFileListModel> tempList = CopyFilesDataHolder.getInstance().getCopyFiles();
        if(tempList.size() == 0 || !CopyFilesDataHolder.getInstance().isBwaitForPasteByCopy()){
            return  false;
        }
        final String _path = path;
        asnycExecute(new Runnable() {
            @Override
            public void run() {
                final ArrayList<MediaFileListModel> tempFileList = CopyFilesDataHolder.getInstance().getCopyFiles();
                for (MediaFileListModel f : tempFileList) {
                    CopyFile(f, _path);
                }

                mOperationListener.onFileChanged(Environment
                        .getExternalStorageDirectory()
                        .getAbsolutePath());

                CopyFilesDataHolder.getInstance().stopCopy();  //在这里设置不让它等待复制了
                clear();
            }
        });

        return true;
    }




    private void CopyFile(MediaFileListModel f, String dest) {
        if (f == null || dest == null) {
            Log.e(LOG_TAG, "CopyFile: null parameter");
            return;
        }

        File file = new File(f.getFilePath());
        if (file.isDirectory()) {

            // directory exists in destination, rename it
            String destPath = FileUtils.makePath(dest, f.getFileName());
            File destFile = new File(destPath);
            int i = 1;
            while (destFile.exists()) {
                destPath = FileUtils.makePath(dest, f.getFileName() + " " + i++);
                destFile = new File(destPath);
            }

            for (File child : file.listFiles()) {
                if (!child.isHidden() && FileUtils.isNormalFile(child.getAbsolutePath())) {
                    CopyFile(FileUtils.GetFileInfo(child,true), destPath);
                }
            }
        } else {
            String destFile = FileUtils.copyFile(f.getFilePath(), dest);
            mOperationListener.onFileChanged(destFile);

        }
        Log.v(LOG_TAG, "CopyFile >>> " + f.getFilePath() + "," + dest);
    }



    /**粘贴操作****************************End******************/


    /**移动操作****************************Begin******************/

  //在AllFilesActivity的移动的paste函数中去调用呢
    public boolean canMove(String path) {
        for (MediaFileListModel f : CopyFilesDataHolder.getInstance().getCopyFiles()) {
            File file = new File(f.getFilePath());
            if (!file.isDirectory())
                continue;
            if (FileUtils.containsPath(file.getAbsolutePath(), path))
                return false;
        }
        return true;
    }

    public boolean Move(String path) {
        ArrayList<MediaFileListModel> tempList = CopyFilesDataHolder.getInstance().getCopyFiles();
        if(tempList.size() == 0 || !CopyFilesDataHolder.getInstance().isBwaitForPasteByMove()){
            return  false;
        }

        final String _path = path;
        asnycExecute(new Runnable() {
            @Override
            public void run() {
                final ArrayList<MediaFileListModel> tempFileList = CopyFilesDataHolder.getInstance().getCopyFiles();
                for (MediaFileListModel f : tempFileList){
                    MoveFile(f, _path);
                }

//                mOperationListener.onFileChanged(Environment
//                        .getExternalStorageDirectory()
//                        .getAbsolutePath());

                CopyFilesDataHolder.getInstance().stopMove();  //在这里设置不让它等待移动了
                clear();
            }
        });
        return true;
    }


    private boolean MoveFile(MediaFileListModel f, String dest) {
        Log.v(LOG_TAG, "MoveFile >>> " + f.getFilePath() + "," + dest);

        if (f == null || dest == null) {
            Log.e(LOG_TAG, "MoveFile: null parameter");
            return false;
        }

        File file = new File(f.getFilePath());
        String newPath = FileUtils.makePath(dest, f.getFileName());
        try {
            boolean result = file.renameTo(new File(newPath));
            if(result){
                mOperationListener.onFileChanged(file.getAbsolutePath());
                mOperationListener.onFileChanged(newPath);
            }
            Log.e(LOG_TAG, "心情很糟糕啦" + file.getAbsolutePath());
            return result ;
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Fail to move file," + e.toString());
        }
        return false;
    }

    /**移动操作****************************End******************/

}
