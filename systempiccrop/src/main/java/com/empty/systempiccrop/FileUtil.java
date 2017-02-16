package com.empty.systempiccrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

	private static final String TAG     = FileUtil.class.getSimpleName();
	/**
	 * 每兆多少字节
	 */
	private static final long   MB_UNIT = 1024 * 1024;
	/**
	 * 可用空间最低5兆
	 */
	private static final int    MIN     = 5;


	/**
	 * 创建缓存目录
	 *
	 * @param context
	 * @param dirName 目录名
	 * @return
	 */
	public static String getAppCache(Context context, String dirName) {
		String savePath = context.getCacheDir().getAbsolutePath() + File.separator + dirName + File.separator;
		File   saveDir  = new File( savePath );
		if ( !saveDir.exists() ) {
			saveDir.mkdirs();
		}
		return savePath;
	}

	public static boolean isEnoughSpace() {
		if ( Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) == false ) {
//			LogUtil.w( TAG, "MEDIA not mounted" );
			return false;
		}

		File   file           = Environment.getExternalStorageDirectory();
		StatFs stat           = new StatFs( file.getAbsolutePath() );
		long   bytesAvailable = ( long ) stat.getBlockSize() * ( long ) stat.getBlockCount();
		long   megAvailable   = bytesAvailable / MB_UNIT;
		return megAvailable > MIN;
	}

	/**
	 * 根据文件绝对路径获取文件名
	 *
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if ( TextUtils.isEmpty( filePath ) ) {
			return "";
		} else {
			return filePath.substring( filePath.lastIndexOf( File.separator ) + 1 );
		}
	}

	/**
	 * 创建临时文件
	 *
	 * @return
	 */
	public static String getImageCache() {
		String parent = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + ".dayitea" + File.separatorChar;
		File   file   = new File( parent );
		if ( !file.exists() ) {
			if ( !file.mkdirs() ) {
				throw new NullPointerException( parent + " make dir failed" );
			}
		}
		return parent;
	}

	/* 图片压缩方法一
	 *
	 * 计算 bitmap大小，如果超过64kb，则进行压缩
	 *
	 * @param bitmap
	 * @return
	 */
	private static Bitmap ImageCompressL(Bitmap bitmap) {
		double targetwidth = Math.sqrt( 64.00 * 1000 );
		if ( bitmap.getWidth() > targetwidth || bitmap.getHeight() > targetwidth ) {
			// 创建操作图片用的matrix对象
			Matrix matrix = new Matrix();
			// 计算宽高缩放率
			double x = Math.max( targetwidth / bitmap.getWidth(), targetwidth
				/ bitmap.getHeight() );
			// 缩放图片动作
			matrix.postScale( ( float ) x, ( float ) x );
			bitmap = Bitmap.createBitmap( bitmap, 0, 0, 400,
			                              400, matrix, true );
		}
		return bitmap;
	}

	/**
	 * 把图片压缩到200K
	 *
	 * @param oldpath 压缩前的图片路径
	 * @param newPath 压缩后的图片路径
	 * @return
	 */
	public static File compressFile(String oldpath, String newPath) {
		Bitmap                compressBitmap = FileUtil.decodeFile( oldpath );
		Bitmap                newBitmap      = ratingImage( oldpath, compressBitmap );
		ByteArrayOutputStream os             = new ByteArrayOutputStream();
		newBitmap.compress( Bitmap.CompressFormat.PNG, 60, os );
		byte[] bytes = os.toByteArray();

		File file = null;
		try {
			file = FileUtil.getFileFromBytes( bytes, newPath );
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {

			if ( newBitmap != null ) {
				if ( !newBitmap.isRecycled() ) {
					newBitmap.recycle();
				}
				newBitmap = null;
			}
			if ( compressBitmap != null ) {
				if ( !compressBitmap.isRecycled() ) {
					compressBitmap.recycle();
				}
				compressBitmap = null;
			}
		}
		return file;
	}

	/**
	 * 图片压缩
	 *
	 * @param fPath
	 * @return
	 */
	public static Bitmap decodeFile(String fPath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		opts.inDither = false; // Disable Dithering mode
		opts.inPurgeable = true; // Tell to gc that whether it needs free
		opts.inInputShareable = true; // Which kind of reference will be used to
		BitmapFactory.decodeFile( fPath, opts );
		final int REQUIRED_SIZE = 300;
		int       scale         = 1;
		if ( opts.outHeight > REQUIRED_SIZE || opts.outWidth > REQUIRED_SIZE ) {
			final int heightRatio = Math.round( ( float ) opts.outHeight
				                                    / ( float ) REQUIRED_SIZE );
			final int widthRatio = Math.round( ( float ) opts.outWidth
				                                   / ( float ) REQUIRED_SIZE );
			scale = heightRatio < widthRatio ? heightRatio : widthRatio;//
		}
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;
		Bitmap bm = BitmapFactory.decodeFile( fPath, opts ).copy( Bitmap.Config.ARGB_8888, false );
		return bm;
	}

	private static Bitmap ratingImage(String filePath, Bitmap bitmap) {
		int degree = readPictureDegree( filePath );
		return rotaingImageView( degree, bitmap );
	}

	/**
	 * 读取图片属性：旋转的角度
	 *
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface( path );
			int           orientation   = exifInterface.getAttributeInt( ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL );
			switch ( orientation ) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 旋转图片
	 *
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		//旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate( angle );
		System.out.println( "angle2=" + angle );
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap( bitmap, 0, 0,
		                                            bitmap.getWidth(), bitmap.getHeight(), matrix, true );
		return resizedBitmap;
	}

	/**
	 * 把字节数组保存为一个文件
	 *
	 * @param b
	 * @param outputFile
	 * @return
	 */
	public static File getFileFromBytes(byte[] b, String outputFile) {
		File                 ret    = null;
		BufferedOutputStream stream = null;
		try {
			ret = new File( outputFile );
			FileOutputStream fstream = new FileOutputStream( ret );
			stream = new BufferedOutputStream( fstream );
			stream.write( b );
		} catch ( Exception e ) {
			// log.error("helper:get file from byte process error!");
			e.printStackTrace();
		} finally {
			if ( stream != null ) {
				try {
					stream.close();
				} catch ( IOException e ) {
					// log.error("helper:get file from byte process error!");
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	public static File saveFile(Bitmap bitmap, String path, String fileName) throws IOException {
		File dirFile = new File( path );
		if ( !dirFile.exists() ) {
			dirFile.mkdir();
		}
		File                 myCaptureFile = new File( path, fileName );
		BufferedOutputStream bos           = new BufferedOutputStream( new FileOutputStream( myCaptureFile ) );
		bitmap.compress( Bitmap.CompressFormat.JPEG, 80, bos );
		bos.flush();
		bos.close();
		return myCaptureFile;
	}
}

