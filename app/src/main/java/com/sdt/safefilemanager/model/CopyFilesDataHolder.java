package com.sdt.safefilemanager.model;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: zrt
 * @date: 2019/1/7
 * @describe:
 */

public class CopyFilesDataHolder {
    private ArrayList<MediaFileListModel> mCopyFilesList;
    private boolean bCopy;
    private boolean bMove;
    private static volatile CopyFilesDataHolder onlyHolder = null;
    private ReentrantReadWriteLock rwLock;
    private boolean bwaitForPaste;

    private CopyFilesDataHolder() {
        mCopyFilesList = new ArrayList<>();
        rwLock = new ReentrantReadWriteLock();
        bwaitForPaste = false;
        bCopy = false;
        bMove = false;
    }

    public Boolean isBwaitForPasteByCopy() {
        rwLock.readLock().lock();
        try{
            return bwaitForPaste&&bCopy;
        }finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean isBwaitForPasteByMove() {
        rwLock.readLock().lock();
        try{
            return bwaitForPaste&&bMove;
        }finally {
            rwLock.readLock().unlock();
        }
    }


    //判断当前是不是其它没有完成的操作
    public boolean canStartAction(){
        rwLock.readLock().lock();
        try{
            if(this.bCopy == false && this.bMove == false && this.bwaitForPaste == false){
                return true;
            }else{
                return false;
            }
        }finally {
            rwLock.readLock().unlock();
        }
    }

    //复制完成
    public void stopCopy(){
        rwLock.writeLock().lock();
        try{
            this.bCopy = false;
            this.bwaitForPaste = false;
        }finally {
            rwLock.writeLock().unlock();
        }
    }

    //移动完成
    public void stopMove(){
        rwLock.writeLock().lock();
        try{
            this.bMove = false;
            this.bwaitForPaste = false;
        }finally {
            rwLock.writeLock().unlock();
        }
    }
    // 开始复制
    public void startCopy() {
        rwLock.writeLock().lock();
        try{
            this.bCopy = true;
            this.bwaitForPaste = true;
        }finally {
            rwLock.writeLock().unlock();
        }
    }

    // 开始移动
    public void startMove() {
        rwLock.writeLock().lock();
        try{
            this.bMove = true;
            this.bwaitForPaste = true;
        }finally {
            rwLock.writeLock().unlock();
        }
    }


    public static CopyFilesDataHolder getInstance(){
        if(onlyHolder==null){
            synchronized (CopyFilesDataHolder.class){
                if(onlyHolder == null){
                    onlyHolder = new CopyFilesDataHolder();
                }
            }
        }
        return  onlyHolder;
    }

    public void clearCopyFiles(){
        rwLock.writeLock().lock();
        try{
            mCopyFilesList.clear();
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            rwLock.writeLock().unlock();
        }

    }

    public void addCopyFiles(ArrayList<MediaFileListModel> fileList){
        rwLock.writeLock().lock();
        try{
            mCopyFilesList.clear();
            for(MediaFileListModel f: fileList){
                mCopyFilesList.add(f);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            rwLock.writeLock().unlock();
        }
    }

    public ArrayList<MediaFileListModel> getCopyFiles(){
        ArrayList<MediaFileListModel> tempFileList = null;
        rwLock.readLock().lock();
        try{
            tempFileList = new ArrayList<>();
            for(MediaFileListModel f: mCopyFilesList){
                tempFileList.add(f);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            rwLock.readLock().unlock();
            return  tempFileList;
        }

    }
}