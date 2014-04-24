package pl.twogeeks.bitmapstorage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/**
 * Helper class for loading images from files
 * @author MarcinM.
 */
public final class BitmapLoader {

    /**
     * Indicates that scaled image must not be smaller than given width and height
     */
    public static final int SCALE_TYPE_NOT_SMALLER = 0;
    /**
     * Indicates that scaled image must be smaller than given width and height.
     * In other words image must fit in rectangle made using given width and height.
     */
    public static final int SCALE_TYPE_SMALLER = 1;

    /*
    method taken from: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html#load-bitmap
     */
	private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight, int scaleType) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

        if (scaleType == SCALE_TYPE_NOT_SMALLER) {
            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
        } else if (scaleType == SCALE_TYPE_SMALLER) {
            while ((height / inSampleSize) > reqHeight
                    || (width / inSampleSize) > reqWidth) {
                inSampleSize++;
            }
        }
	
	    return inSampleSize;
	}

//    private static int calculatePreciseInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//	    int height = options.outHeight;
//	    int width = options.outWidth;
//	    int inSampleSize = 1;
//
//	    while ((height / inSampleSize) > reqHeight
//	    		|| (width / inSampleSize) > reqWidth) {
//	    	inSampleSize++;
//	    }
//
//	    return inSampleSize;
//	}

    /**
     * Load bitmap from path.
     * It uses BitmapFactory.decodeFile (http://developer.android.com/reference/android/graphics/BitmapFactory.html#decodeFile%28java.lang.String,%20android.graphics.BitmapFactory.Options%29)
     * @param path Path for the file to be loaded.
     * @param reqWidth Require width.
     * @param reqHeight Require height.
     * @param scaleType Scale type. Can be SCALE_TYPE_NOT_SMALLER or SCALE_TYPE_SMALLER.
     * @return Bitmap object or null if image could not be loaded.
     */
	public static Bitmap loadScaledBitmapFromFile(String path, int reqWidth, int reqHeight, int scaleType) {
		if (reqWidth == 0 || reqHeight == 0)
			return null;
		
		File imgFile = new File(path);
		if (imgFile.exists()) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, scaleType);
            options.inJustDecodeBounds = false;

            if (scaleType == SCALE_TYPE_NOT_SMALLER) {
                return BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            } else if (scaleType == SCALE_TYPE_SMALLER) {
                int dstHeight = (options.outHeight / options.inSampleSize);
                int dstWidth = (options.outWidth / options.inSampleSize);

                Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options), dstWidth, dstHeight, false);
                return bm;
            }
		}
		return null;
	}
	
//	public static Bitmap loadScaledBitmapForSizeFromFile(String path, int reqWidth, int reqHeight) {
//		if (reqWidth == 0 || reqHeight == 0)
//			return null;
//
//		File imgFile = new File(path);
//		if (imgFile.exists()) {
//			final BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inJustDecodeBounds = true;
//		    BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
//
//		    options.inSampleSize = calculatePreciseInSampleSize(options, reqWidth, reqHeight);
//		    options.inJustDecodeBounds = false;
//
//		    int dstHeight = (options.outHeight / options.inSampleSize);
//		    int dstWidth = (options.outWidth / options.inSampleSize);
//
//		    Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options), dstWidth, dstHeight, false);
//			return bm;
//		}
//		else
//			return null;
//	}

    /**
     * Load bitmap from path.
     * It uses <a href="http://developer.android.com/reference/android/graphics/BitmapFactory.html#decodeFile%28java.lang.String,%20android.graphics.BitmapFactory.Options%29">BitmapFactory.decodeFile</a>
     * @param path Path for the file to be loaded.
     * @return Bitmap object or null if image could not be loaded.
     */
	public static Bitmap loadBitmapFromFile(String path) {
		File imgFile = new File(path);
		if (imgFile.exists()) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
		}
		else
			return null;
	}

}
