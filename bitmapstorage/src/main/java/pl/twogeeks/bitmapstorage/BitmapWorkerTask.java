package pl.twogeeks.bitmapstorage;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by marcim on 15.03.14.
 */
public class BitmapWorkerTask extends AsyncTask<String, Integer, Bitmap> {

    public static final int BITMAP_KEY = 0;
    public static final int BITMAP_PATH = 1;
    public static final int BITMAP_WIDTH = 2;
    public static final int BITMAP_HEIGHT = 3;

    private BitmapLoadingListener mListener;
    private String mKey;
    private WeakReference<ImageView> mImageView;

    public BitmapWorkerTask(BitmapLoadingListener listener) {
        mListener = listener;
    }

    public void setImageView(ImageView imageView) {
        this.mImageView = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        mKey = params[BITMAP_KEY];
        String path = params[BITMAP_PATH];

        Bitmap bm;
        try {
            int width = Integer.parseInt(params[BITMAP_WIDTH]);
            int height = Integer.parseInt(params[BITMAP_HEIGHT]);

            if ((path == null) || (mKey == null))
                    return null;

            if ((width == 0) && (height == 0))
                bm = BitmapLoader.loadBitmapFromFile(path);
            else
                bm = BitmapLoader.loadScaledBitmapFromFile(path, width, height, BitmapLoader.SCALE_TYPE_NOT_SMALLER);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bm;
    }

    @Override
    protected void onPreExecute() {
        if (mListener != null)
            mListener.onBitmapLoadingStarted(mKey);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mImageView != null) {
            ImageView imageView = mImageView.get();
            if (imageView != null)
                imageView.setImageBitmap(bitmap);
        }

        if (bitmap == null) {
            if (mListener != null)
                mListener.onBitmapFailed(mKey);
        } else {
            if (mListener != null)
                mListener.onBitmapLoaded(mKey, bitmap);
        }
    }

}
