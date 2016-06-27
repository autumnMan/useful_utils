package com.useful.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.widget.Toast;

public class CommonUtils {

    public static final String TAG = "CommonUtils";

    /**
     * 判断快捷方式是否已经创建
     *
     * @param context      上下文
     * @param shortcutName 快捷方式名称
     * @return true 已经创建；false 为创建
     */
    public static boolean isShortCutAdded(Context context, String shortcutName) {
        boolean isInstallShortcut = false;

        // 2.2以上系统是”com.android.launcher2.settings”,其他的为"com.android.launcher.settings"
        final String AUTHORITY = "com.android.launcher.settings";
        final String AUTHORITY2 = "com.android.launcher2.settings";

        ContentResolver cr = context.getContentResolver();
        Cursor cursor1 = null;
        Cursor cursor2 = null;

        try {
            Uri contentUri = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
            cursor1 = cr.query(contentUri, new String[]{"title", "iconResource"}, "title=?",
                    new String[]{shortcutName}, null);
            if (cursor1 != null && cursor1.getCount() > 0) {
                isInstallShortcut = true;
            } else {
                contentUri = Uri.parse("content://" + AUTHORITY2 + "/favorites?notify=true");
                cursor2 = cr.query(contentUri, new String[]{"title", "iconResource"}, "title=?",
                        new String[]{shortcutName}, null);
                if (cursor2 != null && cursor2.getCount() > 0) {
                    isInstallShortcut = true;
                }
            }
        } finally {
            if (cursor1 != null) {
                cursor1.close();
            }
            if (cursor2 != null) {
                cursor2.close();
            }
        }
        return isInstallShortcut;
    }

    /**
     * 截取超过字节限制的字符串
     * @param str
     * @param maxLength (byte)
     * @return 截取后的字符串
     * @throws UnsupportedEncodingException
     */
    public static String subString(String str, int maxLength)
            throws UnsupportedEncodingException {
        if (str == null) {
            return "";
        } else {
            int tempMaxLength = maxLength;//截取字节数
            String subStr = str.substring(0, str.length() < maxLength ? str.length() : maxLength);//截取的子串
            int subStrByetsLen = subStr.getBytes("UTF-8").length;//截取子串的字节长度
            // 说明截取的字符串中包含有汉字
            while (subStrByetsLen > tempMaxLength) {
                int tempSubLength = --maxLength;
                subStr = str.substring(0, tempSubLength > str.length() ? str.length() : tempSubLength);
                subStrByetsLen = subStr.getBytes("UTF-8").length;
            }
            return subStr;
        }
    }

    /**
     * 修改文件权限
     *
     * @param filePath 要修改的文件全路径
     * @param mode     权限模式，如“777”；
     */
    public static void chMod(String filePath, String mode) {
        try {
            if (TextUtils.isEmpty(filePath)) {
                return;
            }
            if (TextUtils.isEmpty(mode)) {
                mode = "777";
            }
            String command = "chmod " + mode + " " + filePath;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否存在sd卡
     *
     * @return true :存在 false: 不存在
     */
    public static boolean isSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 判断服务是否在运行,该方法不可靠
     *
     * @param context   上下文
     * @param className 服务名称
     * @return true 在运行；false 不在运行
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> serviceList = activityManager.getRunningServices(100);
        if (serviceList != null) {
            for (RunningServiceInfo info : serviceList) {
                if (info.service.getClassName().equalsIgnoreCase(className)) {
                    isRunning = true;
                    break;
                }
            }
        } else {
        }
        return isRunning;
    }

    /**
     * 获取跟目录
     *
     * @param context 上下文
     * @return 根据系统的版本返回，<=2.1返回“/sdcard”,>2.1返回“/mnt”或者“/storage”
     */
    public static String getStorageRootPath(Context context) {
        String rootPath = "";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
            return "/sdcard";
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1 &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return "/mnt";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            StorageManager storageMgr = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            String[] volumePaths = (String[]) ReflectionHelper.invokeMethod(storageMgr, "getVolumePaths", null);
            if (volumePaths != null && volumePaths.length > 0) {
                String path = volumePaths[0];
                int firstIndex = path.indexOf("/") + 1;
                rootPath = path.substring(0, path.indexOf("/", firstIndex));
            }
        }
        return rootPath;
    }

    /**
     * <p>
     * 简单判断是否为手机号
     * </p>
     * 11位数字，1开头
     *
     * @param input 輸入的手機號
     * @return true 手機號，false 非手机号
     */
    public static boolean isPhoneNumber(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        return input.startsWith("1") && input.length() == 11 && input.matches("[0-9]*");
    }

    /**
     * 判断是否是邮箱
     *
     * @param input 邮箱
     * @return 通过正则表示判断regularExpression =
     * "^[_a-z\\d\\-\\./]+@[_a-z\\d\\-]+(\\.[_a-z\\d\\-]+)*(\\.(info|biz|com|edu|gov|net|am|bz|cn|cx|hk|jp|tw|vc|vn))$"
     * ;
     */
    public static boolean validateEmail(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        String regularExpression =
                "^[_a-z\\d\\-\\./]+@[_a-z\\d\\-]+(\\.[_a-z\\d\\-]+)*(\\.(info|biz|com|edu|gov|net|am|bz|cn|cx|hk|jp|tw|vc|vn))$";
        return input.matches(regularExpression);
    }

    /**
     * 截取email地址的前缀，如xxx@aaa.com,则返回xxx
     *
     * @param email 地址
     * @return 返回email地址的前缀，如xxx@aaa.com,则返回xxx
     */
    public static String getEmailWithoutSuffix(String email) {
        if (TextUtils.isEmpty(email)) {
            return "";
        }
        String retVal = "";
        if (email.contains("@")) {
            retVal = email.substring(0, email.lastIndexOf("@"));
        } else {
            retVal = email;
        }
        return retVal;
    }

    /**
     * 如果input没有@后缀，则添加后缀intput+“@189.cn”
     *
     * @param input email地址
     * @return 有@189.cn後綴的地址
     */
    public static String getEmailWithSuffix(String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }
        String retVal = "";
        if (input.contains("@")) {
            retVal = input;
        } else {
            retVal = input + "@189.cn";
        }
        return retVal;
    }

    /**
     * 普通文本Toast提示
     *
     * @param context 上下文。最好给application
     * @param text    提示文字
     */
    public static void toastText(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density);
    }

    /**
     * 获取通知栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 剔除字符串中一些特殊字符 ：\n 回车( )，\t 水平制表符( )，\s 空格(\u0008)，\r 换行( )
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 判断app是否已经安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return true 已经安装；false 未安装
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取手机号码，中间四位用星号替换
     *
     * @param phoneNumber 原手机号
     * @return 替换后的手机号
     */
    public static String getStuffPhoneNumber(String phoneNumber) {
        String stuffPhone = phoneNumber;

        if (phoneNumber.length() == 11 && isPhoneNumber(phoneNumber)) {
            stuffPhone = phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
        }

        return stuffPhone;
    }

    /**
     * 是否在主进程调用
     *
     * @param context 上下文
     * @return true 该方法在主进程被调用;false 在其他进程调用
     */
    public static boolean isCalledOnMainProcess(Context context) {
        boolean flag = false;

        String curProcessName = getCurrentProcessName(context);
        String mainProcessName = context.getPackageName();
        if (!TextUtils.isEmpty(curProcessName)) {
            if (curProcessName.equalsIgnoreCase(mainProcessName)) {
                flag = true;
            }
        } else {
            // FIXME: 2015/12/2 这种情况怎么判断？
            flag = true;
        }
        return flag;
    }

    /**
     * 获取当前进程名称
     *
     * @param context 上下文
     * @return 当前进程名称，可能为null
     */
    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();

        if (runningApps != null && !runningApps.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo appProcess : runningApps) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }

        return null;
    }

    /**
     * 判断App是否在后台
     * @param context
     * @return
     */
    public static boolean isAppBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
