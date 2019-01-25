package com.sdt.safefilemanager.helper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.model.FileInfoBean;
import com.sdt.safefilemanager.model.MediaFileListModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FileUtils {

    /**文档类型*/
    public static final int TYPE_DOC = 0;
    /**apk类型*/
    public static final int TYPE_APK = 1;
    /**压缩包类型*/
    public static final int TYPE_ZIP = 2;
    /**安全文件的路径**/
  // private static String ANDROID_SECURE = "/mnt/sdcard/.android_secure";
    private static String ANDROID_SECURE = ".android_secure";

    private static String TAG=  "FileUtils";

    public static class SDCardInfo {
        public long total;

        public long free;
    }


    // if path1 contains path2
    /**1.21.增加的util***********/
    public static boolean containsPath(String path1, String path2) {
        String path = path2;
        while (path != null) {
            if (path.equalsIgnoreCase(path1))
                return true;

            if (path.equals(HomeConstants.ROOT_PATH))
                break;
            path = new File(path).getParent();
        }
        return false;
    }

    /**1.21.增加的util***********/



    /**1.8.增加的util***********/
    public static String makePath(String path1, String path2) {
        if (path1.endsWith(File.separator))
            return path1 + path2;

        return path1 + File.separator + path2;
    }

    // return new file path if successful, or return null
    public static String copyFile(String src, String dest) {
        File file = new File(src);
        if (!file.exists() || file.isDirectory()) {
            Log.v(TAG, "copyFile: file not exist or is directory, " + src);
            return null;
        }
        FileInputStream fi = null;
        FileOutputStream fo = null;
        try {
            fi = new FileInputStream(file);
            File destPlace = new File(dest);
            if (!destPlace.exists()) {
                if (!destPlace.mkdirs())
                    return null;
            }

            String destPath = FileUtils.makePath(dest, file.getName());
            File destFile = new File(destPath);
            int i = 1;
            while (destFile.exists()) {
                String destName = FileUtils.getNameFromFilename(file.getName()) + " " + i++ + "."
                        + FileUtils.getExtFromFilename(file.getName());
                destPath = FileUtils.makePath(dest, destName);
                destFile = new File(destPath);
            }

            if (!destFile.createNewFile())
                return null;

            fo = new FileOutputStream(destFile);
            int count = 102400;
            byte[] buffer = new byte[count];
            int read = 0;
            while ((read = fi.read(buffer, 0, count)) != -1) {
                fo.write(buffer, 0, read);
            }

            // TODO: set access privilege

            return destPath;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "copyFile: file not found, " + src);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "copyFile: " + e.toString());
        } finally {
            try {
                if (fi != null)
                    fi.close();
                if (fo != null)
                    fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
    /**1.8.增加的util*****************/


    /**
     * 判断文件是否存在
     * @param path 文件的路径
     * @return
     */
    public static boolean isExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static int getDocFileList(Activity activity, String[] fileTypeList, ArrayList<FileInfoBean> fileList) {

        String selection = "(";

        for (String fileType : fileTypeList) {
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%." + fileType + "' or ";
        }
        selection = selection.substring(0,selection.length() - 4);
        selection =  selection + ") and " + MediaStore.Files.FileColumns.SIZE + " >1 ";
        Cursor c = null;
        try {
            c = activity.getContentResolver().query(MediaStore.Files.getContentUri("external"), new String[]{"_id", MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE}, selection, null, null);
            int dataindex = c.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            int sizeindex = c.getColumnIndex(MediaStore.Files.FileColumns.SIZE);

            int a = c.getCount();
            while (c.moveToNext()) {
                String path = c.getString(dataindex);
                String name = path.substring(path.lastIndexOf('/') + 1, path.length());

                if (!FileUtils.isExists(path)) {
                    continue;
                }
                long size = c.getLong(sizeindex);
                FileInfoBean fileInfo = new FileInfoBean(path, name, FileUtils.getFileIconByPath(name), size);
                fileList.add(fileInfo);
            }
        } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (c != null) {
            c.close();
        }
    }
        return fileList.size();
    }

    public static int getFileType(String path) {
        path = path.toLowerCase();
        if (path.endsWith(".txt") || path.endsWith(".doc") || path.endsWith(".docx") || path.endsWith(".xls") || path.endsWith(".xlsx")
                || path.endsWith(".ppt") || path.endsWith(".pptx")) {
            return TYPE_DOC;
        }else if (path.endsWith(".apk")) {
            return TYPE_APK;
        }else if (path.endsWith(".zip") || path.endsWith(".rar") || path.endsWith(".tar") || path.endsWith(".gz")) {
            return TYPE_ZIP;
        }else{
            return -1;
        }
    }


    /**通过文件名获取文件图标*/
    public static int getFileIconByPath(String name){
        name = name.toLowerCase();
        int iconId = R.mipmap.ic_launcher;
        if (name.endsWith(".txt")){
            iconId = R.mipmap.ic_launcher;
        }else if(name.endsWith(".doc") || name.endsWith(".docx")){
            iconId = R.mipmap.ic_launcher;
        }else if(name.endsWith(".xls") || name.endsWith(".xlsx")){
            iconId = R.mipmap.ic_launcher;
        }else if(name.endsWith(".ppt") || name.endsWith(".pptx")){
            iconId = R.mipmap.ic_launcher;
        }else if(name.endsWith(".xml")){
            iconId = R.mipmap.ic_launcher;
        }else if(name.endsWith(".pdf")){
            iconId = R.mipmap.ic_launcher;
        }else if(name.endsWith(".htm") || name.endsWith(".html")){
            iconId = R.mipmap.ic_launcher;
        }else if(name.endsWith(".zip") || name.endsWith(".rar") || name.endsWith(".tar") || name.endsWith(".gz")) {
            iconId = R.mipmap.ic_launcher;
        }else if(name.endsWith(".apk")){
            iconId = R.mipmap.ic_launcher;
        }
        return iconId;
    }

    /**是否是图片文件*/
    public static boolean isPicFile(String path){
        path = path.toLowerCase();
        if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")){
            return true;
        }
        return false;
    }


    /** 判断SD卡是否挂载 */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }
    /** 判断是否是普通文件而非android系统的安全文件 */
    public static boolean isNormalFile(String fullName) {
        //return !fullName.equals(ANDROID_SECURE);
        String secureFilePath = makePath(Environment.getExternalStorageDirectory().getAbsolutePath(),ANDROID_SECURE);
        return !fullName.equals(secureFilePath);


    }

    /**
     * 从文件的全名得到文件的拓展名
     *
     * @param filename
     * @return
     */
    public static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }

    public static String getNameFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(0, dotPosition);
        }
        return "";
    }
    public static int  getItemsNumInDir(File dirFile){
        if(dirFile!=null &&dirFile.isDirectory()){
            File[] listfile = dirFile.listFiles();
            if(listfile !=null){
                return listfile.length;
            }else{
                return  -1;
            }

        }else{
            return -1;
        }

    }
    /**
     * 获取sdCard的大小和信息
     *
     * @param
     * @return SDCardInfo
     */

    public static SDCardInfo getSDCardInfo() {
        String sDcString = android.os.Environment.getExternalStorageState();

        if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            File pathFile = android.os.Environment.getExternalStorageDirectory();

            try {
                android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());

                // 获取SDCard上BLOCK总数
                long nTotalBlocks = statfs.getBlockCount();

                // 获取SDCard上每个block的SIZE
                long nBlocSize = statfs.getBlockSize();

                // 获取可供程序使用的Block的数量
                long nAvailaBlock = statfs.getAvailableBlocks();

                // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
                long nFreeBlock = statfs.getFreeBlocks();

                SDCardInfo info = new SDCardInfo();
                // 计算SDCard 总容量大小MB
                info.total = nTotalBlocks * nBlocSize;

                // 计算 SDCard 剩余大小MB
                info.free = nAvailaBlock * nBlocSize;

                return info;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, e.toString());
            }
        }

        return null;
    }
    /**
     * 读取文件的大小并格式化
     *
     * @param diskSpaceUsed --long
     * @return
     */
    public static String getDynamicSpace(long diskSpaceUsed)
    {
        if (diskSpaceUsed <= 0) {
            return "0";
        }

        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(diskSpaceUsed) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(diskSpaceUsed / Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }
    /**
     * 读取文件的修改时间
     *
     * @param f
     * @return
     */
    public static String getModifiedTime(File f) {
        Calendar cal = Calendar.getInstance();
        long time = f.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTimeInMillis(time);
        // System.out.println("修改时间[2] " + formatter.format(cal.getTime()));
        // 输出：修改时间[2] 2009-08-17 10:32:38
        return formatter.format(cal.getTime());
    }

    //打开文件时调用
    public static void openFiles(Activity activity, String filesPath) {
        Uri uri = Uri.parse("file://" + filesPath);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        String type = getMIMEType(filesPath);
        intent.setDataAndType(uri, type);
        if (!type.equals("*/*")) {
            try {
                activity.startActivity(intent);
            } catch (Exception e) {
                activity.startActivity(showOpenTypeDialog(filesPath));
            }
        } else {
            activity.startActivity(showOpenTypeDialog(filesPath));
        }
    }

//    //显示打开方式
//    public void show(String filesPath){
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(Intent.ACTION_VIEW);
//        startActivity(showOpenTypeDialog(filesPath));
//    }
    /**获取MediaFileListModel模型**/
    public static MediaFileListModel GetFileInfo(File f, boolean showHidden) {
        MediaFileListModel fileModel = new MediaFileListModel();
        String filePath = f.getPath();
        File file = new File(filePath);
        fileModel.setFileName(file.getName());
        fileModel.setFilePath(file.getAbsolutePath());
        return fileModel;
    }

    public static Intent showOpenTypeDialog(String param) {
        Log.e("ViChildError", "showOpenTypeDialog");
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    /**
     * --获取文件类型 --
     */
    public static String getMIMEType(String filePath) {
        String type = "*/*";
        String fName = filePath;

        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }

        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") {
            return type;
        }

        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
            }
        }
        return type;
    }

    /**
     * -- MIME 列表 --
     */
    public static final String[][] MIME_MapTable =
            {
                    // --{后缀名， MIME类型}   --
                    {".3gp", "video/3gpp"},
                    {".3gpp", "video/3gpp"},
                    {".aac", "audio/x-mpeg"},
                    {".amr", "audio/x-mpeg"},
                    {".apk", "application/vnd.android.package-archive"},
                    {".avi", "video/x-msvideo"},
                    {".aab", "application/x-authoware-bin"},
                    {".aam", "application/x-authoware-map"},
                    {".aas", "application/x-authoware-seg"},
                    {".ai", "application/postscript"},
                    {".aif", "audio/x-aiff"},
                    {".aifc", "audio/x-aiff"},
                    {".aiff", "audio/x-aiff"},
                    {".als", "audio/x-alpha5"},
                    {".amc", "application/x-mpeg"},
                    {".ani", "application/octet-stream"},
                    {".asc", "text/plain"},
                    {".asd", "application/astound"},
                    {".asf", "video/x-ms-asf"},
                    {".asn", "application/astound"},
                    {".asp", "application/x-asap"},
                    {".asx", " video/x-ms-asf"},
                    {".au", "audio/basic"},
                    {".avb", "application/octet-stream"},
                    {".awb", "audio/amr-wb"},
                    {".bcpio", "application/x-bcpio"},
                    {".bld", "application/bld"},
                    {".bld2", "application/bld2"},
                    {".bpk", "application/octet-stream"},
                    {".bz2", "application/x-bzip2"},
                    {".bin", "application/octet-stream"},
                    {".bmp", "image/bmp"},
                    {".c", "text/plain"},
                    {".class", "application/octet-stream"},
                    {".conf", "text/plain"},
                    {".cpp", "text/plain"},
                    {".cal", "image/x-cals"},
                    {".ccn", "application/x-cnc"},
                    {".cco", "application/x-cocoa"},
                    {".cdf", "application/x-netcdf"},
                    {".cgi", "magnus-internal/cgi"},
                    {".chat", "application/x-chat"},
                    {".clp", "application/x-msclip"},
                    {".cmx", "application/x-cmx"},
                    {".co", "application/x-cult3d-object"},
                    {".cod", "image/cis-cod"},
                    {".cpio", "application/x-cpio"},
                    {".cpt", "application/mac-compactpro"},
                    {".crd", "application/x-mscardfile"},
                    {".csh", "application/x-csh"},
                    {".csm", "chemical/x-csml"},
                    {".csml", "chemical/x-csml"},
                    {".css", "text/css"},
                    {".cur", "application/octet-stream"},
                    {".doc", "application/msword"},
                    {".dcm", "x-lml/x-evm"},
                    {".dcr", "application/x-director"},
                    {".dcx", "image/x-dcx"},
                    {".dhtml", "text/html"},
                    {".dir", "application/x-director"},
                    {".dll", "application/octet-stream"},
                    {".dmg", "application/octet-stream"},
                    {".dms", "application/octet-stream"},
                    {".dot", "application/x-dot"},
                    {".dvi", "application/x-dvi"},
                    {".dwf", "drawing/x-dwf"},
                    {".dwg", "application/x-autocad"},
                    {".dxf", "application/x-autocad"},
                    {".dxr", "application/x-director"},
                    {".ebk", "application/x-expandedbook"},
                    {".emb", "chemical/x-embl-dl-nucleotide"},
                    {".embl", "chemical/x-embl-dl-nucleotide"},
                    {".eps", "application/postscript"},
                    {".epub", "application/epub+zip"},
                    {".eri", "image/x-eri"},
                    {".es", "audio/echospeech"},
                    {".esl", "audio/echospeech"},
                    {".etc", "application/x-earthtime"},
                    {".etx", "text/x-setext"},
                    {".evm", "x-lml/x-evm"},
                    {".evy", "application/x-envoy"},
                    {".exe", "application/octet-stream"},
                    {".fh4", "image/x-freehand"},
                    {".fh5", "image/x-freehand"},
                    {".fhc", "image/x-freehand"},
                    {".fif", "image/fif"},
                    {".fm", "application/x-maker"},
                    {".fpx", "image/x-fpx"},
                    {".fvi", "video/isivideo"},
                    {".flv", "video/x-msvideo"},
                    {".gau", "chemical/x-gaussian-input"},
                    {".gca", "application/x-gca-compressed"},
                    {".gdb", "x-lml/x-gdb"},
                    {".gif", "image/gif"},
                    {".gps", "application/x-gps"},
                    {".gtar", "application/x-gtar"},
                    {".gz", "application/x-gzip"},
                    {".gif", "image/gif"},
                    {".gtar", "application/x-gtar"},
                    {".gz", "application/x-gzip"},
                    {".h", "text/plain"},
                    {".hdf", "application/x-hdf"},
                    {".hdm", "text/x-hdml"},
                    {".hdml", "text/x-hdml"},
                    {".htm", "text/html"},
                    {".html", "text/html"},
                    {".hlp", "application/winhlp"},
                    {".hqx", "application/mac-binhex40"},
                    {".hts", "text/html"},
                    {".ice", "x-conference/x-cooltalk"},
                    {".ico", "application/octet-stream"},
                    {".ief", "image/ief"},
                    {".ifm", "image/gif"},
                    {".ifs", "image/ifs"},
                    {".imy", "audio/melody"},
                    {".ins", "application/x-net-install"},
                    {".ips", "application/x-ipscript"},
                    {".ipx", "application/x-ipix"},
                    {".it", "audio/x-mod"},
                    {".itz", "audio/x-mod"},
                    {".ivr", "i-world/i-vrml"},
                    {".j2k", "image/j2k"},
                    {".jad", "text/vnd.sun.j2me.app-descriptor"},
                    {".jam", "application/x-jam"},
                    {".jnlp", "application/x-java-jnlp-file"},
                    {".jpe", "image/jpeg"},
                    {".jpz", "image/jpeg"},
                    {".jwc", "application/jwc"},
                    {".jar", "application/java-archive"},
                    {".java", "text/plain"},
                    {".jpeg", "image/jpeg"},
                    {".jpg", "image/jpeg"},
                    {".js", "application/x-javascript"},
                    {".kjx", "application/x-kjx"},
                    {".lak", "x-lml/x-lak"},
                    {".latex", "application/x-latex"},
                    {".lcc", "application/fastman"},
                    {".lcl", "application/x-digitalloca"},
                    {".lcr", "application/x-digitalloca"},
                    {".lgh", "application/lgh"},
                    {".lha", "application/octet-stream"},
                    {".lml", "x-lml/x-lml"},
                    {".lmlpack", "x-lml/x-lmlpack"},
                    {".log", "text/plain"},
                    {".lsf", "video/x-ms-asf"},
                    {".lsx", "video/x-ms-asf"},
                    {".lzh", "application/x-lzh "},
                    {".m13", "application/x-msmediaview"},
                    {".m14", "application/x-msmediaview"},
                    {".m15", "audio/x-mod"},
                    {".m3u", "audio/x-mpegurl"},
                    {".m3url", "audio/x-mpegurl"},
                    {".ma1", "audio/ma1"},
                    {".ma2", "audio/ma2"},
                    {".ma3", "audio/ma3"},
                    {".ma5", "audio/ma5"},
                    {".man", "application/x-troff-man"},
                    {".map", "magnus-internal/imagemap"},
                    {".mbd", "application/mbedlet"},
                    {".mct", "application/x-mascot"},
                    {".mdb", "application/x-msaccess"},
                    {".mdz", "audio/x-mod"},
                    {".me", "application/x-troff-me"},
                    {".mel", "text/x-vmel"},
                    {".mi", "application/x-mif"},
                    {".mid", "audio/midi"},
                    {".midi", "audio/midi"},
                    {".m4a", "audio/mp4a-latm"},
                    {".m4b", "audio/mp4a-latm"},
                    {".m4p", "audio/mp4a-latm"},
                    {".m4u", "video/vnd.mpegurl"},
                    {".m4v", "video/x-m4v"},
                    {".mov", "video/quicktime"},
                    {".mp2", "audio/x-mpeg"},
                    {".mp3", "audio/x-mpeg"},
                    {".mp4", "video/mp4"},
                    {".mpc", "application/vnd.mpohun.certificate"},
                    {".mpe", "video/mpeg"},
                    {".mpeg", "video/mpeg"},
                    {".mpg", "video/mpeg"},
                    {".mpg4", "video/mp4"},
                    {".mpga", "audio/mpeg"},
                    {".msg", "application/vnd.ms-outlook"},
                    {".mif", "application/x-mif"},
                    {".mil", "image/x-cals"},
                    {".mio", "audio/x-mio"},
                    {".mmf", "application/x-skt-lbs"},
                    {".mng", "video/x-mng"},
                    {".mny", "application/x-msmoney"},
                    {".moc", "application/x-mocha"},
                    {".mocha", "application/x-mocha"},
                    {".mod", "audio/x-mod"},
                    {".mof", "application/x-yumekara"},
                    {".mol", "chemical/x-mdl-molfile"},
                    {".mop", "chemical/x-mopac-input"},
                    {".movie", "video/x-sgi-movie"},
                    {".mpn", "application/vnd.mophun.application"},
                    {".mpp", "application/vnd.ms-project"},
                    {".mps", "application/x-mapserver"},
                    {".mrl", "text/x-mrml"},
                    {".mrm", "application/x-mrm"},
                    {".ms", "application/x-troff-ms"},
                    {".mts", "application/metastream"},
                    {".mtx", "application/metastream"},
                    {".mtz", "application/metastream"},
                    {".mzv", "application/metastream"},
                    {".nar", "application/zip"},
                    {".nbmp", "image/nbmp"},
                    {".nc", "application/x-netcdf"},
                    {".ndb", "x-lml/x-ndb"},
                    {".ndwn", "application/ndwn"},
                    {".nif", "application/x-nif"},
                    {".nmz", "application/x-scream"},
                    {".nokia-op-logo", "image/vnd.nok-oplogo-color"},
                    {".npx", "application/x-netfpx"},
                    {".nsnd", "audio/nsnd"},
                    {".nva", "application/x-neva1"},
                    {".oda", "application/oda"},
                    {".oom", "application/x-atlasMate-plugin"},
                    {".ogg", "audio/ogg"},
                    {".pac", "audio/x-pac"},
                    {".pae", "audio/x-epac"},
                    {".pan", "application/x-pan"},
                    {".pbm", "image/x-portable-bitmap"},
                    {".pcx", "image/x-pcx"},
                    {".pda", "image/x-pda"},
                    {".pdb", "chemical/x-pdb"},
                    {".pdf", "application/pdf"},
                    {".pfr", "application/font-tdpfr"},
                    {".pgm", "image/x-portable-graymap"},
                    {".pict", "image/x-pict"},
                    {".pm", "application/x-perl"},
                    {".pmd", "application/x-pmd"},
                    {".png", "image/png"},
                    {".pnm", "image/x-portable-anymap"},
                    {".pnz", "image/png"},
                    {".pot", "application/vnd.ms-powerpoint"},
                    {".ppm", "image/x-portable-pixmap"},
                    {".pps", "application/vnd.ms-powerpoint"},
                    {".ppt", "application/vnd.ms-powerpoint"},
                    {".pqf", "application/x-cprplayer"},
                    {".pqi", "application/cprplayer"},
                    {".prc", "application/x-prc"},
                    {".proxy", "application/x-ns-proxy-autoconfig"},
                    {".prop", "text/plain"},
                    {".ps", "application/postscript"},
                    {".ptlk", "application/listenup"},
                    {".pub", "application/x-mspublisher"},
                    {".pvx", "video/x-pv-pvx"},
                    {".qcp", "audio/vnd.qcelp"},
                    {".qt", "video/quicktime"},
                    {".qti", "image/x-quicktime"},
                    {".qtif", "image/x-quicktime"},
                    {".r3t", "text/vnd.rn-realtext3d"},
                    {".ra", "audio/x-pn-realaudio"},
                    {".ram", "audio/x-pn-realaudio"},
                    {".ras", "image/x-cmu-raster"},
                    {".rdf", "application/rdf+xml"},
                    {".rf", "image/vnd.rn-realflash"},
                    {".rgb", "image/x-rgb"},
                    {".rlf", "application/x-richlink"},
                    {".rm", "audio/x-pn-realaudio"},
                    {".rmf", "audio/x-rmf"},
                    {".rmm", "audio/x-pn-realaudio"},
                    {".rnx", "application/vnd.rn-realplayer"},
                    {".roff", "application/x-troff"},
                    {".rp", "image/vnd.rn-realpix"},
                    {".rpm", "audio/x-pn-realaudio-plugin"},
                    {".rt", "text/vnd.rn-realtext"},
                    {".rte", "x-lml/x-gps"},
                    {".rtf", "application/rtf"},
                    {".rtg", "application/metastream"},
                    {".rtx", "text/richtext"},
                    {".rv", "video/vnd.rn-realvideo"},
                    {".rwc", "application/x-rogerwilco"},
                    {".rar", "application/x-rar-compressed"},
                    {".rc", "text/plain"},
                    {".rmvb", "audio/x-pn-realaudio"},
                    {".s3m", "audio/x-mod"},
                    {".s3z", "audio/x-mod"},
                    {".sca", "application/x-supercard"},
                    {".scd", "application/x-msschedule"},
                    {".sdf", "application/e-score"},
                    {".sea", "application/x-stuffit"},
                    {".sgm", "text/x-sgml"},
                    {".sgml", "text/x-sgml"},
                    {".shar", "application/x-shar"},
                    {".shtml", "magnus-internal/parsed-html"},
                    {".shw", "application/presentations"},
                    {".si6", "image/si6"},
                    {".si7", "image/vnd.stiwap.sis"},
                    {".si9", "image/vnd.lgtwap.sis"},
                    {".sis", "application/vnd.symbian.install"},
                    {".sit", "application/x-stuffit"},
                    {".skd", "application/x-koan"},
                    {".skm", "application/x-koan"},
                    {".skp", "application/x-koan"},
                    {".skt", "application/x-koan"},
                    {".slc", "application/x-salsa"},
                    {".smd", "audio/x-smd"},
                    {".smi", "application/smil"},
                    {".smil", "application/smil"},
                    {".smp", "application/studiom"},
                    {".smz", "audio/x-smd"},
                    {".sh", "application/x-sh"},
                    {".snd", "audio/basic"},
                    {".spc", "text/x-speech"},
                    {".spl", "application/futuresplash"},
                    {".spr", "application/x-sprite"},
                    {".sprite", "application/x-sprite"},
                    {".sdp", "application/sdp"},
                    {".spt", "application/x-spt"},
                    {".src", "application/x-wais-source"},
                    {".stk", "application/hyperstudio"},
                    {".stm", "audio/x-mod"},
                    {".sv4cpio", "application/x-sv4cpio"},
                    {".sv4crc", "application/x-sv4crc"},
                    {".svf", "image/vnd"},
                    {".svg", "image/svg-xml"},
                    {".svh", "image/svh"},
                    {".svr", "x-world/x-svr"},
                    {".swf", "application/x-shockwave-flash"},
                    {".swfl", "application/x-shockwave-flash"},
                    {".t", "application/x-troff"},
                    {".tad", "application/octet-stream"},
                    {".talk", "text/x-speech"},
                    {".tar", "application/x-tar"},
                    {".taz", "application/x-tar"},
                    {".tbp", "application/x-timbuktu"},
                    {".tbt", "application/x-timbuktu"},
                    {".tcl", "application/x-tcl"},
                    {".tex", "application/x-tex"},
                    {".texi", "application/x-texinfo"},
                    {".texinfo", "application/x-texinfo"},
                    {".tgz", "application/x-tar"},
                    {".thm", "application/vnd.eri.thm"},
                    {".tif", "image/tiff"},
                    {".tiff", "image/tiff"},
                    {".tki", "application/x-tkined"},
                    {".tkined", "application/x-tkined"},
                    {".toc", "application/toc"},
                    {".toy", "image/toy"},
                    {".tr", "application/x-troff"},
                    {".trk", "x-lml/x-gps"},
                    {".trm", "application/x-msterminal"},
                    {".tsi", "audio/tsplayer"},
                    {".tsp", "application/dsptype"},
                    {".tsv", "text/tab-separated-values"},
                    {".ttf", "application/octet-stream"},
                    {".ttz", "application/t-time"},
                    {".txt", "text/plain"},
                    {".ult", "audio/x-mod"},
                    {".ustar", "application/x-ustar"},
                    {".uu", "application/x-uuencode"},
                    {".uue", "application/x-uuencode"},
                    {".vcd", "application/x-cdlink"},
                    {".vcf", "text/x-vcard"},
                    {".vdo", "video/vdo"},
                    {".vib", "audio/vib"},
                    {".viv", "video/vivo"},
                    {".vivo", "video/vivo"},
                    {".vmd", "application/vocaltec-media-desc"},
                    {".vmf", "application/vocaltec-media-file"},
                    {".vmi", "application/x-dreamcast-vms-info"},
                    {".vms", "application/x-dreamcast-vms"},
                    {".vox", "audio/voxware"},
                    {".vqe", "audio/x-twinvq-plugin"},
                    {".vqf", "audio/x-twinvq"},
                    {".vql", "audio/x-twinvq"},
                    {".vre", "x-world/x-vream"},
                    {".vrml", "x-world/x-vrml"},
                    {".vrt", "x-world/x-vrt"},
                    {".vrw", "x-world/x-vream"},
                    {".vts", "workbook/formulaone"},
                    {".wax", "audio/x-ms-wax"},
                    {".wbmp", "image/vnd.wap.wbmp"},
                    {".web", "application/vnd.xara"},
                    {".wav", "audio/x-wav"},
                    {".wma", "audio/x-ms-wma"},
                    {".wmv", "audio/x-ms-wmv"},
                    {".wi", "image/wavelet"},
                    {".wis", "application/x-InstallShield"},
                    {".wm", "video/x-ms-wm"},
                    {".wmd", "application/x-ms-wmd"},
                    {".wmf", "application/x-msmetafile"},
                    {".wml", "text/vnd.wap.wml"},
                    {".wmlc", "application/vnd.wap.wmlc"},
                    {".wmls", "text/vnd.wap.wmlscript"},
                    {".wmlsc", "application/vnd.wap.wmlscriptc"},
                    {".wmlscript", "text/vnd.wap.wmlscript"},
                    {".wmv", "video/x-ms-wmv"},
                    {".wmx", "video/x-ms-wmx"},
                    {".wmz", "application/x-ms-wmz"},
                    {".wpng", "image/x-up-wpng"},
                    {".wps", "application/vnd.ms-works"},
                    {".wpt", "x-lml/x-gps"},
                    {".wri", "application/x-mswrite"},
                    {".wrl", "x-world/x-vrml"},
                    {".wrz", "x-world/x-vrml"},
                    {".ws", "text/vnd.wap.wmlscript"},
                    {".wsc", "application/vnd.wap.wmlscriptc"},
                    {".wv", "video/wavelet"},
                    {".wvx", "video/x-ms-wvx"},
                    {".wxl", "application/x-wxl"},
                    {".x-gzip", "application/x-gzip"},
                    {".xar", "application/vnd.xara"},
                    {".xbm", "image/x-xbitmap"},
                    {".xdm", "application/x-xdma"},
                    {".xdma", "application/x-xdma"},
                    {".xdw", "application/vnd.fujixerox.docuworks"},
                    {".xht", "application/xhtml+xml"},
                    {".xhtm", "application/xhtml+xml"},
                    {".xhtml", "application/xhtml+xml"},
                    {".xla", "application/vnd.ms-excel"},
                    {".xlc", "application/vnd.ms-excel"},
                    {".xll", "application/x-excel"},
                    {".xlm", "application/vnd.ms-excel"},
                    {".xls", "application/vnd.ms-excel"},
                    {".xlt", "application/vnd.ms-excel"},
                    {".xlw", "application/vnd.ms-excel"},
                    {".xm", "audio/x-mod"},
                    {".xml", "text/xml"},
                    {".xmz", "audio/x-mod"},
                    {".xpi", "application/x-xpinstall"},
                    {".xpm", "image/x-xpixmap"},
                    {".xsit", "text/xml"},
                    {".xsl", "text/xml"},
                    {".xul", "text/xul"},
                    {".xwd", "image/x-xwindowdump"},
                    {".xyz", "chemical/x-pdb"},
                    {".yz1", "application/x-yz1"},
                    {".z", "application/x-compress"},
                    {".zac", "application/x-zaurus-zac"},
                    {".zip", "application/zip"},
                    {"", "*/*"}
            };


}