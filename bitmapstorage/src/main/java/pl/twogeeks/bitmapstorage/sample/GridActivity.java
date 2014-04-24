package pl.twogeeks.bitmapstorage.sample;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import pl.twogeeks.bitmapstorage.BitmapLoadingListener;
import pl.twogeeks.bitmapstorage.BitmapStorage;
import pl.twogeeks.bitmapstorage.R;

public class GridActivity extends ActionBarActivity implements BitmapLoadingListener {

    private GridView mGrid;
    private TextView mTotalMemory;
    private TextView mUsedMemory;
    private TextView mStatsSize;
    private TextView mStatsHits;
    private TextView mStatsMisses;
    private TextView mStatsEvictions;
    private Button mAddBtn;
    private CheckBox mCacheCheckBox;

    private int counter;
    private long availMemoryAtStart;

    private ImageAdapter mImageAdapter;
    private ArrayList<Integer> mList;

    private BitmapStorage mBitmapStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        mGrid = (GridView) this.findViewById(R.id.grid);
        mTotalMemory = (TextView) this.findViewById(R.id.totalMemory);
        mUsedMemory = (TextView) this.findViewById(R.id.usedMemory);
        mStatsSize = (TextView) this.findViewById(R.id.statsSize);
        mStatsHits = (TextView) this.findViewById(R.id.statsHits);
        mStatsMisses = (TextView) this.findViewById(R.id.statsMisses);
        mStatsEvictions = (TextView) this.findViewById(R.id.statsEvictions);
        mAddBtn = (Button) this.findViewById(R.id.addBtn);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = (counter++ % 4) + 1;
                mList.add(new Integer(number));
                mImageAdapter.notifyDataSetChanged();

                ((Button)v).setText(String.format("Add bitmap (%d)", mList.size()));
            }
        });
        mCacheCheckBox = (CheckBox) this.findViewById(R.id.cacheCheckBox);
        mCacheCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBitmapStorage.setUseMemCache(isChecked);
                updateInfo();
            }
        });

        mBitmapStorage = new BitmapStorage();
        mBitmapStorage.setBitmapLoadingListener(this);
        mCacheCheckBox.setChecked(mBitmapStorage.isMemCacheUsed());

        mList = new ArrayList<Integer>();

        // określenie max szerokości i wysokości obrazka
        // uzyskanie szerokości ekranu
        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int maxImageWidth = metrics.widthPixels;
        int maxImageHeight = 150;
        mImageAdapter = new ImageAdapter(this, mList, mBitmapStorage, maxImageWidth, maxImageHeight);
        mGrid.setAdapter(mImageAdapter);

        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        availMemoryAtStart = memoryInfo.availMem;

        updateInfo();
    }

    private void updateInfo() {
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final int memoryClassBytes = am.getMemoryClass();
        mTotalMemory.setText("Total memory: "
                        + Formatter.formatFileSize(this, memoryClassBytes * 1024 * 1024)
                        + " ; " + Formatter.formatFileSize(this, Runtime.getRuntime().maxMemory()));

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        mUsedMemory.setText("Used memory: "
                        + Formatter.formatFileSize(this, availMemoryAtStart - memoryInfo.availMem)
                        + " ; " + Formatter.formatFileSize(this, Runtime.getRuntime().totalMemory()));

        mStatsSize.setEnabled(mBitmapStorage.isMemCacheUsed());
        mStatsHits.setEnabled(mBitmapStorage.isMemCacheUsed());
        mStatsMisses.setEnabled(mBitmapStorage.isMemCacheUsed());
        mStatsEvictions.setEnabled(mBitmapStorage.isMemCacheUsed());

        if (mBitmapStorage.isMemCacheUsed()) {
            mStatsSize.setText("Cache size: " + Formatter.formatFileSize(this, mBitmapStorage.getMemCache().size()));
            mStatsHits.setText("Cache hits: " + Integer.toString(mBitmapStorage.getMemCache().hitCount()));
            mStatsMisses.setText("Cache misses: " + Integer.toString(mBitmapStorage.getMemCache().missCount()));
            mStatsEvictions.setText("Cache evictions: " + Integer.toString(mBitmapStorage.getMemCache().evictionCount()));
        }
    }

    public void onClearCacheButtonClick(View view) {
        if (mBitmapStorage.isMemCacheUsed()) {
            mBitmapStorage.getMemCache().evictAll();
            updateInfo();
        }
    }

    /*
        BitmapLoadingListener implementation
    */

    @Override
    public void onBitmapLoadingStarted(String key) {
        updateInfo();
    }

    @Override
    public void onBitmapLoaded(String key, Bitmap bitmap) {
        updateInfo();
    }

    @Override
    public void onBitmapFailed(String key) {
        updateInfo();
    }
}
