package com.useful.utils;

import java.io.File;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 本地文件帮助类，比如排序文件
 * <p>Created by xueyy on 2015/12/4 12:25.</p>
 */
public class LocalFileUtil {

    public static class ImageItemData {
        public static final int MEDIA_TYPE_IMAGE = 0;

        public static final int MEDIA_TYPE_VIDEO = 1;

        /**
         * 文件的全路径/mnt/sdcard/100.jpg
         */
        public String filePath;

        /**
         * 文件类型
         */
        public int fileType;

        /**
         * 缩略图的id
         */
        public long thumbnailsId;

        /**
         * 文件创建时间
         */
        public String createDate;


        public static ImageItemData copy(ImageItemData imageItemData) {
            if (imageItemData != null) {
                ImageItemData itemData = new ImageItemData();
                itemData.filePath = imageItemData.filePath;
                itemData.createDate = imageItemData.createDate;
                itemData.thumbnailsId = imageItemData.thumbnailsId;
                itemData.fileType = imageItemData.fileType;
                return itemData;
            }
            return null;
        }
    }

    /**
     *
     * 从数据库加载视频文件数据
     *
     * @param toFileList 保存数据的集合
     * @param context Context
     * @param srcFolderPath 文件夹路径
     */
    public static void loadVideoFile(List<ImageItemData> toFileList, Context context, String srcFolderPath) {
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr =context.getContentResolver();
        // "_id DESC"的作用是使新拍摄的照片可以在前面显示
        // folderPath.replaceAll("'", "''") 防止sql注入
        Cursor videoCursor =
                cr.query(videoUri, null, "_data like '%" + srcFolderPath.replaceAll("'", "''") + "%'",
                        null, "date_added DESC");
        if (videoCursor == null) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            while (videoCursor.moveToNext()) {
                Date date = new Date(Long.parseLong(videoCursor.getString(videoCursor.getColumnIndex("date_added"))) * 1000);

                String imageCreateDate = sdf.format(date);
                String imageFullPath = videoCursor.getString(videoCursor.getColumnIndex("_data"));
                String imageFolderPath = imageFullPath.substring(0, imageFullPath.lastIndexOf("/"));
                if (imageFolderPath.equals(srcFolderPath)) {
                    ImageItemData imageItemData = new ImageItemData();
                    imageItemData.createDate = imageCreateDate;
                    imageItemData.filePath = imageFullPath;
                    imageItemData.fileType = ImageItemData.MEDIA_TYPE_VIDEO;
                    long thumdnailsId = videoCursor.getLong(videoCursor.getColumnIndex("_id"));
                    imageItemData.thumbnailsId = thumdnailsId;
                    toFileList.add(imageItemData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (videoCursor != null) {
                videoCursor.close();
            }
        }
    }

    /**
     * 从数据库加载图片文件数据
     *
     * @param toFileList 保存数据的集合
     * @param context Context
     * @param srcFolderPath 文件夹路径
     */
    public static void loadImageFile(List<ImageItemData> toFileList, Context context, String srcFolderPath) {
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        // "_id DESC"的作用是使新拍摄的照片可以在前面显示
        // folderPath.replaceAll("'", "''") 防止sql注入
        Cursor imageCursor =
                cr.query(imageUri, null, "_data like '%" + srcFolderPath.replaceAll("'", "''") + "%'",
                        null, "date_added DESC");
        if (imageCursor == null) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            while (imageCursor.moveToNext()) {
                Date date = new Date(Long.parseLong(imageCursor.getString(imageCursor.getColumnIndex("date_added"))) * 1000);

                String imageCreateDate = sdf.format(date);
                String imageFullPath = imageCursor.getString(imageCursor.getColumnIndex("_data"));
                String imageFolderPath = imageFullPath.substring(0, imageFullPath.lastIndexOf("/"));
                if (imageFolderPath.equals(srcFolderPath)) {
                    ImageItemData imageItemData = new ImageItemData();
                    imageItemData.createDate = imageCreateDate;
                    imageItemData.filePath = imageFullPath;
                    imageItemData.fileType = ImageItemData.MEDIA_TYPE_IMAGE;
                    long thumdnailsId = imageCursor.getLong(imageCursor.getColumnIndex("_id"));
                    imageItemData.thumbnailsId = thumdnailsId;
                    toFileList.add(imageItemData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageCursor != null) {
                imageCursor.close();
            }
        }
    }

    /**
     * 加载一个图片文件数据
     *
     * @param context Context
     * @param imagePath 图片路径
     */
    public static ImageItemData loadImageFile(Context context, String imagePath) {
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        // "_id DESC"的作用是使新拍摄的照片可以在前面显示
        // folderPath.replaceAll("'", "''") 防止sql注入
        Cursor imageCursor =
                cr.query(imageUri, null, "_data = '" + imagePath + "'",
                        null, "date_added DESC");
        if (imageCursor == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            while (imageCursor.moveToNext()) {
                Date date = new Date(Long.parseLong(imageCursor.getString(imageCursor.getColumnIndex("date_added"))) * 1000);

                String imageCreateDate = sdf.format(date);
                String imageFullPath = imageCursor.getString(imageCursor.getColumnIndex("_data"));

                ImageItemData imageItemData = new ImageItemData();
                imageItemData.createDate = imageCreateDate;
                imageItemData.filePath = imageFullPath;
                imageItemData.fileType = ImageItemData.MEDIA_TYPE_IMAGE;
                long thumdnailsId = imageCursor.getLong(imageCursor.getColumnIndex("_id"));
                imageItemData.thumbnailsId = thumdnailsId;
                return imageItemData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageCursor != null) {
                imageCursor.close();
            }
        }
        return null;
    }

    /**
     * 对本地文件或文件夹排序，先目录，在文件；目录和文件按名称顺序排序
     *
     * @param files 需要排序的文件或文件夹
     * @return 排序后输出
     */
    public static List<File> orderFiles(File[] files) {
        if (files == null) {
            return null;
        }
        List<File> outputList = new ArrayList<File>();
        ArrayList<File> fileList = new ArrayList<File>();
        ArrayList<File> folderList = new ArrayList<File>();

        //区分文件夹和文件,排除以“.”开头的文件
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().charAt(0) == '.') {
                continue;
            }
            if (files[i].isDirectory()) {
                folderList.add(files[i]);
            } else {
                fileList.add(files[i]);
            }
        }

        //分别排序
        Comparator comp = new FileComparator();
        Collections.sort(folderList, comp);
        Collections.sort(fileList, comp);

        //排序结果输出
        for (File file : folderList) {
            outputList.add(file);
        }
        for (File file : fileList) {
            outputList.add(file);
        }

        return outputList;
    }

    public static class FileComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs == null && rhs == null) {
                return 0;
            }
            if (lhs == null) {
                return -1;
            }
            if (rhs == null) {
                return 1;
            }
            Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
            return cmp.compare(lhs.getName(), rhs.getName());
        }
    }
}
