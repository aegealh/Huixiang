package com.pybeta.huixiang.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;

import com.pybeta.huixiang.BuildConfig;

public class FileUtils {

	private static final String INTERNAL_COMMON_PATH = "files";

	public static void saveInternalFile(Context ctx, String dataStr, String fileName) {
		if (dataStr != null) {
			File dir = ctx.getDir(INTERNAL_COMMON_PATH, Context.MODE_PRIVATE);
			try {
				if (dir != null) {
					byte[] data = dataStr.getBytes("utf-8");
					saveFile(dir, fileName, data);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	public static String saveExternalFile(Context ctx, String dataStr, String fileName) {
		String filePath = null;
		File externalDir = Environment.getExternalStorageDirectory();
		if (externalDir != null) {
			if (dataStr != null) {
				try {
					byte[] data = dataStr.getBytes("utf-8");
					saveFile(externalDir, fileName, data);
					filePath = externalDir.getAbsolutePath() + "/" + fileName;
					if (BuildConfig.DEBUG) {
						System.out.println("backup data to " + filePath);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return filePath;
	}

	public static String savePicture(Context ctx, Bitmap bitmap) throws IOException {
		String imageDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(System.currentTimeMillis()));

		String cacheDir = Environment.getExternalStorageDirectory().getPath();
		File externalDir = new File(cacheDir + File.separator + "DayMatter");
		if (!externalDir.exists()) {
			externalDir.mkdir();
		}

		String fileName = String.format("%s.png", imageDate);
		File file = new File(externalDir, fileName);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 70, out)) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file.getPath();
	}

	private static void saveFile(File dir, String fileName, byte[] data) {
		File file = new File(dir, fileName);
		BufferedOutputStream bos = null;

		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			bos = new BufferedOutputStream(new FileOutputStream(file));
			bos.write(data);
			bos.flush();

			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public static String loadInternalFile(Context ctx, String fileName) {
		File dir = ctx.getDir(INTERNAL_COMMON_PATH, Context.MODE_PRIVATE);
		try {
			if (dir != null) {
				byte[] data = getDataFromFile(dir, fileName);
				return new String(data, "utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String loadExternalFile(Context ctx, String fileName) {
		File file = new File(fileName);
		try {
			if (file != null && file.exists() && file.isFile()) {
				byte[] data = readDataFromStream(new FileInputStream(file));
				return new String(data, "utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getAssertsFile(Context context, String fileName) {

		InputStream inputStream = null;
		AssetManager assetManager = context.getAssets();
		try {
			inputStream = assetManager.open(fileName);
			byte[] data = readDataFromStream(inputStream);
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static byte[] getDataFromFile(File dir, String fileName) {
		try {
			File file = new File(dir, fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			return readDataFromStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		}
		return null;
	}

	public static byte[] readDataFromStream(InputStream inputStream) {
		if (inputStream == null)
			return null;

		byte[] data = null;
		BufferedInputStream bis;
		bis = new BufferedInputStream(inputStream);

		int length;
		try {
			length = bis.available();
			data = new byte[length];
			bis.read(data);
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null)
					bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
