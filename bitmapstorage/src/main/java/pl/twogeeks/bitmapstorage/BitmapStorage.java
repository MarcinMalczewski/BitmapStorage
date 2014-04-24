package pl.twogeeks.bitmapstorage;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by marcim on 15.03.14.
 */
public class BitmapStorage implements BitmapLoadingListener {

    private HashMap<String, String> mPathMap;
    private HashMap<String, WeakReference<BitmapWorkerTask>> mBitmapWorkerTaskMap;
    private BitmapLoadingListener mBitmapLoadingListener;

    private LruCache<String, Bitmap> mMemCache;
    private boolean mUseMemCache;
    // domyslna wielkosc memory cache to 1/4 dostepnej pamieci
    private final int DEFAULT_MEM_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory()) / 4;
    /*
        wielkosc memory cache w bajtach
        jesli podana ponizej 1 MB, to uzywana jest DEFAULT_MEM_CACHE_SIZE
     */
    private int mMemCacheSize;
    
    private String mDiskStoragePath;

    public BitmapStorage() {
        mPathMap = new HashMap<String, String>();
        mBitmapWorkerTaskMap = new HashMap<String, WeakReference<BitmapWorkerTask>>();

        // domyslnie memory cache jest wlaczony
        setMemCacheSize(DEFAULT_MEM_CACHE_SIZE);
        setUseMemCache(true);
    }

    public void getBitmap(String key, String path) {
        getBitmap(key, path, null, 0, 0, null);
    }

    public void getBitmap(String key, String path,
    		BitmapLoadingListener localListener) {
        getBitmap(key, path, null, 0, 0, localListener);
    }

    public void getBitmap(String key, String path, ImageView imageView,
                          int maxImageWidth, int maxImageHeight) {
        getBitmap(key, path, imageView, maxImageWidth, maxImageHeight, null);
    }

    public void getBitmap(String key, String path, ImageView imageView,
                          int maxImageWidth, int maxImageHeight,
                          BitmapLoadingListener localListener) {
        mPathMap.put(key, path);

        // ustawienie listenera
        BitmapLoadingListener listener = (localListener != null) ? localListener : this;
        
        // sprawdzenie czy bitmapa o podanych kluczu jest w memory cache
        if (isMemCacheUsed()) {
            Bitmap bitmap = mMemCache.get(key);
            if (bitmap != null) {
                if (imageView != null)
                    imageView.setImageBitmap(bitmap);
                listener.onBitmapLoaded(key, bitmap);
                return;
            }
        }

        // jesli w memory cache nie bylo bitmapy, to pobranie z pliku
        BitmapWorkerTask task = new BitmapWorkerTask(listener);
        int width = 0;
        int height = 0;
        if (imageView != null) {
            task.setImageView(imageView);
            width = imageView.getWidth();
            height = imageView.getHeight();
        }
        width = (width > 0) ? width : maxImageWidth;
        height = (height > 0) ? height : maxImageHeight;
        mBitmapWorkerTaskMap.put(key, new WeakReference<BitmapWorkerTask>(task));
        task.execute(key, path, Integer.toString(width), Integer.toString(height));
    }

    public Bitmap getBitmapDirectly(String key, String path) {
    	mPathMap.put(key, path);
 
    	Bitmap bitmap;
        // sprawdzenie czy bitmapa o podanych kluczu jest w memory cache
        if (isMemCacheUsed()) {
            bitmap = mMemCache.get(key);
            if (bitmap != null) {
                return bitmap;
            }
        }
        
        bitmap = BitmapLoader.loadBitmapFromFile(path);
        if (isMemCacheUsed())
            mMemCache.put(key, bitmap);
        return bitmap;
    }
    
    public void removeBitmap(String key) {
        mPathMap.remove(key);
        mBitmapWorkerTaskMap.remove(key);
        if (isMemCacheUsed()) {
            mMemCache.remove(key);
        }
    }

    public void setBitmapLoadingListener(BitmapLoadingListener bitmapLoadingListener) {
        this.mBitmapLoadingListener = bitmapLoadingListener;
    }

    public void setUseMemCache(boolean useMemCache) {
        this.mUseMemCache = useMemCache;
        if ((mUseMemCache) && (mMemCache == null)) {
            mMemCache = new LruCache<String, Bitmap>(mMemCacheSize) {
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();
                }
            };
        } else if ((!mUseMemCache) && (mMemCache != null)) {
            mMemCache.evictAll();
            mMemCache = null;
        }
    }

    public void setMemCacheSize(int memCacheSize) {
        if (memCacheSize < 1024 * 1024)
            this.mMemCacheSize = DEFAULT_MEM_CACHE_SIZE;
        else
            this.mMemCacheSize = memCacheSize;
    }

    public boolean isMemCacheUsed() {
        return (mMemCache != null);
    }

    public LruCache<String, Bitmap> getMemCache() {
        return mMemCache;
    }

    public boolean prepareDiskStorage(String dirName) {
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
    		return false;
    	
    	File path;
    	if ((dirName == null) || (dirName.trim().equals(""))) 
    		path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    	else {
    		String spath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
    				+ File.separator + dirName;
    		path = new File(spath);
    	}    	
    	path.mkdirs();

    	mDiskStoragePath = path.getAbsolutePath();
    	
    	return true;
    }
    
    public String createImageFileName() {
    	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "IMG_"+ timeStamp + ".jpg";
    }
    
    public String createImagePath() {
    	if (mDiskStoragePath != null) 
        	return mDiskStoragePath + File.separator + createImageFileName();
    	else
    		return null;
    }

    public String getDiskStoragePath() {
        return mDiskStoragePath;
    }

    /*
            implementacja interfejsu BitmapLoadingListener
        */
    @Override
    public void onBitmapLoadingStarted(String key) {
        if (mBitmapLoadingListener != null)
            mBitmapLoadingListener.onBitmapLoadingStarted(key);
    }

    @Override
    public void onBitmapLoaded(String key, Bitmap bitmap) {
        mBitmapWorkerTaskMap.remove(key);
        if (isMemCacheUsed())
            mMemCache.put(key, bitmap);
        if (mBitmapLoadingListener != null)
            mBitmapLoadingListener.onBitmapLoaded(key, bitmap);
    }

    @Override
    public void onBitmapFailed(String key) {
        mBitmapWorkerTaskMap.remove(key);
        if (mBitmapLoadingListener != null)
            mBitmapLoadingListener.onBitmapFailed(key);
    }
}
