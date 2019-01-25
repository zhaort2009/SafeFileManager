package com.sdt.safefilemanager.model;

import android.content.Context;

/**
 * @author:zrt
 * @date: 2018/11/19
 * @describe: 用于向ApkIconResizer传递参数
 */
public class ParamHolder {

        Context paramContext;
        String apkFilePath;

    public ParamHolder(Context paramContext, String apkFilePath) {
        this.paramContext = paramContext;
        this.apkFilePath = apkFilePath;
    }

    public Context getParamContext() {
        return paramContext;
    }

    public void setParamContext(Context paramContext) {
        this.paramContext = paramContext;
    }

    public String getApkFilePath() {
        return apkFilePath;
    }

    public void setApkFilePath(String apkFilePath) {
        this.apkFilePath = apkFilePath;
    }
}
