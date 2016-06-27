package com.useful.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import android.os.Environment;
import android.os.StatFs;

public class FileCache {
	private static final int KB = 1024;
	private static final int MB = 1024 * 1024;
	/** 期望的最小缓存空间 */
	private static final int MIX_CACHE_SIZE = 20 * MB;
	/** 最大缓存大小 */
	private static final int MAX_CACHE_SIZE = 200 * MB;

	/**
	 * 计算sdcard上的剩余空间
	 * 
	 * @return sdcard 剩余空间 单位Kb。
	 */
	public static long freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeKB = (double) stat.getAvailableBlocks()
				* (double) stat.getBlockSize() / KB;
		return (long) sdFreeKB;
	}

	/**
	 * 计算本机内部存储的剩余存储空间
	 * 
	 * @return 内部存储的剩余存储空间 单位Kb。
	 */
	public static long freeSpaceOnThis() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		// 手机内存剩余容量
		long memoryAvail = (availableBlocks * blockSize) / KB;
		// long totalBlocks = stat.getBlockCount();
		// // 手机内存总容量
		// long memorySize = totalBlocks * blockSize;
		return availableBlocks;
	}

	/**
	 * 剩余存储空间
	 * @return 剩余存储空间 单位Kb
	 */
	public static long freeSpace() {
		if (CommonUtils.isSDCard()) {
			return freeSpaceOnSd();
		} else {
			return freeSpaceOnThis();
		}
	}

	/**
	 * 计算存储目录下的文件大小， 当文件总大小大于规定的{@link FileCache#MAX_CACHE_SIZE} 或者sdcard剩余空间小于
	 * {@link FileCache#MIX_CACHE_SIZE}的规定 那么删除40%最近没有被使用的文件
	 * 
	 * @param dirPath
	 *            缓存目录
	 * @param size
	 *            未使用该值
	 */
	public static void removeCache(String dirPath, int size) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		long dirSize = getSize(files);
		if (dirSize > MAX_CACHE_SIZE || MIX_CACHE_SIZE > freeSpaceOnSd() * KB) {
			int removeFactor = (int) ((0.4 * files.length) + 1);
			Arrays.sort(files, new FileLastModifSort());
			for (int i = 0; i < removeFactor; i++) {
				try {
					files[i].delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 计算文件总大小
	 * 
	 * @param files
	 *            文件
	 * @return 总大小 单位byte
	 */
	public static long getSize(File[] files) {
		long size = 0;
		for (int i = 0; i < files.length; i++) {
			size += files[i].length();
		}
		return size;
	}

	/**
	 * TODO 根据文件的最后修改时间进行排序 *
	 */
	static class FileLastModifSort implements Comparator<File> {
		@Override
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}
}
