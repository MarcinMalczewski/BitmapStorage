package pl.twogeeks.bitmapstorage.sample;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import pl.twogeeks.bitmapstorage.BitmapStorage;

/**
 * Created by marcim on 16.03.14.
 */
public class ImageAdapter extends BaseAdapter {

    private String TARGET_DIR;

    private Context mContext;
    private ArrayList<Integer> mList;
    private BitmapStorage mBitmapStorage;
    private int mMaxImageWidth;
    private int mMaxImageHeight;

    public ImageAdapter(Context context, ArrayList<Integer> list, BitmapStorage bitmapStorage,
                        int maxImageWidth, int maxImageHeight) {
        mContext = context;
        mList = list;
        mBitmapStorage = bitmapStorage;
        mMaxImageWidth = maxImageWidth;
        mMaxImageHeight = maxImageHeight;

        TARGET_DIR = mContext.getFilesDir().getAbsolutePath() + File.separator;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mMaxImageHeight));
            imageView.setBackgroundColor(Color.LTGRAY);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
            imageView.setImageBitmap(null);
        }

        int number = mList.get(position).intValue();
        String fileName = String.format(MainActivity.IMAGE_FILE_TEMPLATE, number);
        mBitmapStorage.getBitmap(Integer.toString(number), TARGET_DIR + fileName, imageView, mMaxImageWidth, mMaxImageHeight);

        return imageView;
    }

}
