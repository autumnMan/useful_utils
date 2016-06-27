package com.useful.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "Crash";

    private UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类

    private static CrashHandler INSTANCE = new CrashHandler();// CrashHandler实例

    private Context mAppCxt;// 程序的Context对象

    private String mCrashLogDirPath;

    private StringBuilder mBuilder = new StringBuilder();

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());// 用于格式化日期,作为日志文件名的一部分

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler(){}

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     */
    public void init(Context appCxt, String crashLogDirPath) {
        mAppCxt = appCxt.getApplicationContext();
        mCrashLogDirPath = crashLogDirPath;
        final UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (defaultHandler == null || this != defaultHandler) {
            Log.i(TAG, "old default handler : " + defaultHandler);
            mDefaultHandler = defaultHandler;
            Log.i(TAG, "new default handler : " + this);
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            if (ex != null) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     */
    private void handleException(Throwable ex) {
        if (ex == null) {
            return;
        }
        // 收集设备参数信息
        collectDeviceInfo(mAppCxt);
        // 保存日志文件
        final String logPath = saveCrashInfo2File(ex);
        Log.i(TAG, "save crash file to " + logPath);
    }

    /**
     * 收集设备参数信息
     */
    private void collectDeviceInfo(Context context) {
        mBuilder.setLength(0);
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            if (pi != null) {
                mBuilder.append("AppInfo = ").append(String.format(Locale.getDefault(),
                        "pkg:%s, vn:%s, vc:%s", context.getPackageName(),
                        pi.versionName, pi.versionCode)).append("\r\n");
            }
        } catch (Exception e) {
        }
        mBuilder.append("OS_VERSION = ").append("Android ").append(Build.VERSION.RELEASE).append("\r\n");
        mBuilder.append("SDK_INT = ").append(Build.VERSION.SDK_INT + "").append("\r\n");

        mBuilder.append("ID = ").append(Build.ID).append("\r\n");
        mBuilder.append("BRAND = ").append(Build.BRAND).append("\r\n");
        mBuilder.append("BOARD = ").append(Build.BOARD).append("\r\n");
        mBuilder.append("MODEL = ").append(Build.MODEL).append("\r\n");
        mBuilder.append("TYPE = ").append(Build.TYPE).append("\r\n");
        mBuilder.append("DEVICE = ").append(Build.DEVICE).append("\r\n");
        mBuilder.append("FINGERPRINT = ").append(Build.FINGERPRINT).append("\r\n");
        mBuilder.append("MANUFACTURER = ").append(Build.MANUFACTURER).append("\r\n");
        mBuilder.append("\r\n");
    }

    private String saveCrashInfo2File(Throwable ex) {
        String crashDir = mCrashLogDirPath;
        Writer writer = new StringWriter();

        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.flush();
        pw.close();// 记得关闭

        String result = writer.toString();
        mBuilder.append(result);

        // 保存文件
        String fileName = format.format(new Date()) + ".txt";
        OutputStream fos = null;
        try {
            final File logDir = new File(crashDir);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            final File logFile = new File(logDir, fileName);
            fos = new BufferedOutputStream(new FileOutputStream(logFile));
            fos.write(mBuilder.toString().getBytes());
            fos.flush();
            return logFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
}
