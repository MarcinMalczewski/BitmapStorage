package pl.twogeeks.bitmapstorage;

import android.graphics.Bitmap;

/**
 * Created by marcim on 15.03.14.
 */
public interface BitmapLoadingListener {

    public void onBitmapLoadingStarted(String key);
    public void onBitmapLoaded(String key, Bitmap bitmap);
    public void onBitmapFailed(String key);
}
