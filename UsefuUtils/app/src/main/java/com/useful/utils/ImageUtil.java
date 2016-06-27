package com.useful.utils;

import java.io.ByteArrayOutputStream;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class ImageUtil {
    /**
     * 缩放srcBitmap的宽度为w，高度为宽度缩放比（wRadio）*srcBitmap.getWidth()。如“过高”，“过宽”则另外处理。
     *
     * @param srcBitmap 目标位图，缩放成功后，根据参数recycle参数，判断是否会被回收
     * @param w         目标宽
     * @param h         目标高
     * @param recycle   是否回收目标位图标志
     * @return
     */
    public static Bitmap fitBitmap(Bitmap srcBitmap, int w, int h, boolean recycle) {
        if (srcBitmap != null) {
            Bitmap resizedBitmap = null;
            try {
                int srcWidth = srcBitmap.getWidth();
                int srcHeight = srcBitmap.getHeight();

                float wRadio = ((float) w) / srcWidth;
                float hRadio = ((float) h) / srcHeight;

                if (srcWidth / srcHeight > 4) { // "过宽"
                    if (hRadio > 1.0f) {
                        hRadio = 1.0f;
                    }
                } else if (srcHeight / srcWidth > 4) {// “过高”
                    if (wRadio > 1.0f) {
                        wRadio = 1.0f;
                    }
                } else {// 根据宽比率缩放高
                    hRadio = wRadio;
                }

                Matrix matrix = new Matrix();
                matrix.postScale(wRadio, hRadio);
                // DLog.i("fitBitmap", "original bitmap size "+srcBitmap.getByteCount()+"original width : "+ srcWidth +
                // " original height : " + srcHeight+"\n"
                // +"scaleWidth : "+wRadio+" scaleHeight : "+hRadio);

                resizedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcWidth, srcHeight, matrix, true);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (recycle) { // 标记为真，则回收目标位图。
                if (resizedBitmap != null && srcBitmap != resizedBitmap) {
                    srcBitmap.recycle();
                    srcBitmap = null;
                } else {
                    resizedBitmap = srcBitmap;
                }
            }
            // DLog.i("fitBitmap", "scaled bitmap size "+resizedBitmap.getByteCount()+"scaled width : "+
            // resizedBitmap.getWidth() + " scaled height : " + resizedBitmap.getHeight());
            return resizedBitmap;
        } else {
            return null;
        }
    }

    /**
     * 除了按w、h缩放，再乘以缩放因子ratio
     *
     * @param path  图片路径
     * @param w     显示宽度
     * @param h     显示高度
     * @param ratio 缩放比例 （》1 缩小，《1放大）
     * @return
     */
    public static Bitmap scaleBitmap(String path, int w, int h, int ratio) {
        Log.i("ImageUtil", "expected image width= " + w + " height= " + h + " ratio= " + ratio);
        Bitmap bit = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        bit = BitmapFactory.decodeFile(path, opts);
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;
        Log.i("ImageUtil", "source image width= " + imageWidth + " height= " + imageHeight);
        int heightRatio = Math.round((float) imageHeight / (float) h);
        int widthRatio = Math.round((float) imageWidth / ((float) w));
        opts.inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;

        if (ratio != 1) {
            opts.inSampleSize = opts.inSampleSize * ratio;
        }

        Log.i("ImageUtil", "picture inSampleSize " + opts.inSampleSize);
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Config.RGB_565;
        try {
            bit = BitmapFactory.decodeFile(path, opts);
            if (bit != null) {
                Log.i("ImageUtil", "scaled image width= " + bit.getWidth() + " height= " + bit.getHeight());
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bit;
    }

    /**
     * 创建bitmap，会缩放，w，h只作为缩放参考值，不是bitmap的宽高值
     *
     * @param path 图片路径
     * @param w    参考宽
     * @param h    参考高
     * @return
     */
    public static Bitmap getBitmapWithScale(String path, int w, int h) {
        Bitmap bit = null;
        bit = scaleBitmap(path, w, h, 1);
        return bit;
    }

    /**
     * 创建bitmap，不缩放
     *
     * @param path 文件路径
     * @return bitmap
     */
    public static Bitmap getBitmapNoScale(String path) {
        Bitmap bit = null;
        try {
            bit = BitmapFactory.decodeFile(path);
        } catch (Exception e) {
            Log.w("ImageUtil", "getBitmapNoScale failed.");
            e.printStackTrace();
        }
        return bit;
    }

    /**
     * 获取正确旋转方向的bitmap，先处理本地文件，若无属性再对云端file处理，成功则回收srcBitmap，返回新bitmap。
     *
     * @param path      图片路径，may null
     * @param srcBitmap 不能为null
     * @return
     */
    public static Bitmap getBitmapWithOrientation(String path, Bitmap srcBitmap) {
        if (srcBitmap == null) {
            return null;
        }
        Bitmap retBitmap = null;

        int display = 0;
        try {
            // 根据本地文件exif值旋转图片
            if (!TextUtils.isEmpty(path)) {
                ExifInterface ei = new ExifInterface(path);
                String orientation = ei.getAttribute(ExifInterface.TAG_ORIENTATION);
                switch (Integer.valueOf(orientation)) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        display = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        display = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        display = 180;
                        break;
                    default:
                        break;
                }
            }

            String orientationStr = null;
            // 根据文件旋转类型描述旋转图片
            if (display == 0 && orientationStr != null) {
                String rotateStr =
                        orientationStr.substring(orientationStr.indexOf("(") + 1, orientationStr.indexOf(")"));
                String[] rotateInfos = rotateStr.split(" ");
                if (rotateInfos[2].equals("CW")) {
                    display = Integer.parseInt(rotateInfos[1]);
                } else {
                    display = 0 - Integer.parseInt(rotateInfos[1]);
                }
            }

            Matrix matrix = new Matrix();
            matrix.setRotate(display);
            retBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (retBitmap == null) {
            retBitmap = srcBitmap;
        }

        return retBitmap;
    }

    /**
     * 合并两张bitmap为一张
     *
     * @param background
     * @param foreground
     * @return Bitmap
     */
    public static Bitmap combineBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, (bgWidth - fgWidth) / 2, (bgHeight - fgHeight) / 2, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newmap;
    }

    /**
     * 给图片加上效果，比如半透明
     *
     * @param src   原图片
     * @param color 颜色，如0xffff0000;
     * @return
     */
    public static Bitmap maskBitmap(Bitmap src, int color) {
        if (src == null) {
            return null;
        }
        Bitmap output = null;
        try {
            output = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_4444);
            Canvas canvas = new Canvas(output);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.drawColor(color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }


    /**
     * 获得圆边图片的方法
     *
     * @param bitmap
     * @param needEdge  是否需要边框
     * @param edgeColor 边框颜色
     * @param edgeWidth 边框宽度
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, boolean needEdge, int edgeColor, float edgeWidth) {
        if (null != bitmap) {
            Bitmap output = null;
            try {
                output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_4444);
                Canvas canvas = new Canvas(output);

                final int color = 0xffff0000;
                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                final RectF rectF = new RectF(rect);

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(color);
                float radius =
                        bitmap.getWidth() < bitmap.getHeight() ? (rectF.right - rectF.left) / 2 : (rectF.bottom - rectF.top) / 2;
                canvas.drawCircle(rectF.left + (rectF.right - rectF.left) / 2, rectF.top + (rectF.bottom - rectF.top) /
                        2, radius, paint);

                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);

                if (needEdge) {
                    paint.setColor(edgeColor);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(edgeWidth);
                    canvas.drawCircle(rectF.left + (rectF.right - rectF.left) / 2, rectF.top +
                                    (rectF.bottom - rectF.top) / 2,
                            radius, paint);
                    paint.setColor(Color.GRAY);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(1f);
                    canvas.drawCircle(rectF.left + (rectF.right - rectF.left) / 2, rectF.top +
                                    (rectF.bottom - rectF.top) / 2,
                            radius, paint);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return output;
        } else {
            return null;
        }
    }

    /**
     * 得到圆角正方形图片，用于选择照片上传模块
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getCornerBitmap(Bitmap bitmap, int radius) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = null;
        try {
            output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Config.ARGB_4444);
            Canvas canvas = new Canvas(output);

            final int color = 0xffff0000;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            float cx = rectF.left + (rectF.right - rectF.left) / 2;
            float cy = rectF.top + (rectF.bottom - rectF.top) / 2;

            float tempX = (rectF.right - rectF.left) / 2 - radius;
            float tempY = (rectF.bottom - rectF.top) / 2;

            float radiusDraw = (float) Math.sqrt(tempX * tempX + tempY * tempY);
            canvas.drawCircle(cx, cy, radiusDraw, paint);

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }

    public static boolean addToGallery(Context context, String filePath) {
        try {
            String extension = null;
            if (filePath.contains(".")) {
                extension = filePath.substring(filePath.lastIndexOf(".") + 1);
            }
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

            if (!TextUtils.isEmpty(mimeType) && mimeType.startsWith("image")) {
                final Uri STORAGE_URI = Images.Media.EXTERNAL_CONTENT_URI;
                final String IMAGE_MIME_TYPE = "image/jpeg";

                ContentValues values = new ContentValues(4);

                values.put(Images.Media.TITLE, filePath.substring(filePath.lastIndexOf("/") + 1));
                values.put(Images.Media.DISPLAY_NAME, filePath.substring(filePath.lastIndexOf("/") + 1));
                values.put(Images.Media.MIME_TYPE, IMAGE_MIME_TYPE);
                values.put(Images.Media.DATA, filePath);

                context.getContentResolver().insert(STORAGE_URI, values);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void deleteFromGallery(Context context, long imageId) {
        try {
            Uri contentUri = Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, imageId);
            int count = context.getContentResolver().delete(uri, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建黑色背景，大小是140x140的图片，用户用户头像设置
     *
     * @param bitmap
     * @return
     */
    public static Bitmap createBlackBgBitmap(Bitmap bitmap) {
        Bitmap bmp = null;
        if (bitmap != null && !bitmap.isRecycled()) {
            bmp = Bitmap.createBitmap(140, 140, Config.ARGB_8888);
            float left = (140 - bitmap.getWidth()) * 0.5f;
            float top = (140 - bitmap.getHeight()) * 0.5f;
            Canvas canvas = new Canvas(bmp);
            canvas.drawRGB(0, 0, 0);
            canvas.drawBitmap(bitmap, left, top, null);
        }
        return bmp;
    }

    /**
     * bitmap 转化为byte[]
     *
     * @param bmp         输入的bitmap
     * @param needRecycle 是否需要回收
     * @return 转化后的byte[]
     */
    public static byte[] bmpToByteArray(Bitmap bmp, boolean needRecycle) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] outputBytes;
        try {
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            outputBytes = bos.toByteArray();
            if (needRecycle) {
                bmp.recycle();
            }
        } finally {
            try {
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return outputBytes;
    }

    /**
     * bitmap 转化为byte[]
     *
     * @param sentBitmap         输入的bitmap
     * @param radius 模糊程度
     * @param canReuseInBitmap 是否能重用
     * @return 转化后的Bitmap
     */


    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

}
