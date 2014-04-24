package pl.twogeeks.bitmapstorage.sample;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import pl.twogeeks.bitmapstorage.BitmapLoadingListener;
import pl.twogeeks.bitmapstorage.BitmapStorage;
import pl.twogeeks.bitmapstorage.R;


public class LinearLayoutActivity extends ActionBarActivity implements BitmapLoadingListener {

    private String TARGET_DIR;

    private LinearLayout mLinearLayout;
    private TextView mTotalMemory;
    private TextView mUsedMemory;
    private TextView mStatsSize;
    private TextView mStatsHits;
    private TextView mStatsMisses;
    private TextView mStatsEvictions;
    private Button mAddBtn;
    private CheckBox mCacheCheckBox;

    private long availMemoryAtStart;

    private int mMaxImageWidth;
    private int mMaxImageHeight;
    private int mTotalBitmaps;

    private BitmapStorage mBitmapStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_layout);
        mLinearLayout = (LinearLayout) this.findViewById(R.id.linearLayout);
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
                addBitmaps(10);
            }
        });
        Button btn = (Button) this.findViewById(R.id.clearBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCache();
            }
        });
        btn = (Button) this.findViewById(R.id.removeBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBitmaps();
            }
        });
        btn = (Button) this.findViewById(R.id.gcBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runtime.getRuntime().gc();
                updateInfo();
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

        // określenie max szerokości i wysokości obrazka
        // uzyskanie szerokości ekranu
        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mMaxImageWidth = metrics.widthPixels;
        mMaxImageHeight = 150;

        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        availMemoryAtStart = memoryInfo.availMem;

        mTotalBitmaps = 0;

        TARGET_DIR = this.getFilesDir().getAbsolutePath() + File.separator;

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

    private void clearCache() {
        if (mBitmapStorage.isMemCacheUsed()) {
            mBitmapStorage.getMemCache().evictAll();
            updateInfo();
        }
    }

    private void removeBitmaps() {
        mLinearLayout.removeAllViews();
        mTotalBitmaps = 0;
        mAddBtn.setText(String.format("Add 10 bitmaps (%d)", mTotalBitmaps));
        updateInfo();
    }

    private void addBitmaps(int bitmapsNumber) {
        int counter = 0;

        for (int ii = 0; ii < bitmapsNumber; ii++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mMaxImageHeight));
            imageView.setBackgroundColor(Color.LTGRAY);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(8, 8, 8, 8);

            int number = (counter++ % 4) + 1;
            String fileName = String.format(MainActivity.IMAGE_FILE_TEMPLATE, number);
            mBitmapStorage.getBitmap(Integer.toString(number), TARGET_DIR + fileName, imageView,
                    mMaxImageWidth, mMaxImageHeight);

            mLinearLayout.addView(imageView);
        }

        mTotalBitmaps += bitmapsNumber;

        mAddBtn.setText(String.format("Add 10 bitmaps (%d)", mTotalBitmaps));
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
