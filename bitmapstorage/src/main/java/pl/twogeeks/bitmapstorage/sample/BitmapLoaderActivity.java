package pl.twogeeks.bitmapstorage.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pl.twogeeks.bitmapstorage.BitmapLoader;
import pl.twogeeks.bitmapstorage.R;

public class BitmapLoaderActivity extends ActionBarActivity {

    private final String DEFAULT_IMAGE_FILE = "default_img.jpg";
    private String DEFAULT_PATH;
    private final int DEFAULT_WIDTH_HEIGHT = 400;

    private String mPath;

    private ImageView mImage1;
    private ImageView mImage2;
    private TextView mSizeDefault;
    private EditText mReqWidth;
    private EditText mReqHeight;
    private TextView mSize1;
    private TextView mSize2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_loader);

        DEFAULT_PATH = this.getFilesDir().getAbsolutePath() + File.separator + DEFAULT_IMAGE_FILE;
        mPath = DEFAULT_PATH;

        mImage1 = (ImageView) findViewById(R.id.image1);
        mImage2 = (ImageView) findViewById(R.id.image2);
        mSizeDefault = (TextView) findViewById(R.id.defaultSize);
        mReqWidth = (EditText) findViewById(R.id.reqWidth);
        mReqWidth.setText(Integer.toString(DEFAULT_WIDTH_HEIGHT));
        mReqHeight = (EditText) findViewById(R.id.reqHeight);
        mReqHeight.setText(Integer.toString(DEFAULT_WIDTH_HEIGHT));
        mSize1 = (TextView) findViewById(R.id.size1);
        mSize2 = (TextView) findViewById(R.id.size2);

        Button btn = (Button) findViewById(R.id.changeSize);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRequestedSize();
            }
        });

        prepareDefaultImage();
        loadImages(mPath, DEFAULT_WIDTH_HEIGHT, DEFAULT_WIDTH_HEIGHT);
    }

    private void prepareDefaultImage() {
        // check if image file exists
        File file = new File(DEFAULT_PATH);
        if (file.exists())
            return;

        // copy default image file from raw dir to app's dir
        byte[] buffer = new byte[1024];
        int read;

        InputStream is = this.getResources().openRawResource(R.raw.default_img);
        BufferedInputStream bis = new BufferedInputStream(is);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(DEFAULT_PATH);
            while ((read = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeRequestedSize() {
        int reqWidth = 0, reqHeight = 0;

        try {
            reqWidth = Integer.parseInt(mReqWidth.getText().toString());
            reqHeight = Integer.parseInt(mReqHeight.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Wrong value!", Toast.LENGTH_LONG).show();
            return;
        }

        loadImages(mPath, reqWidth, reqHeight);
    }

    private void loadImages(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        mSizeDefault.setText(String.format("Original image size (W x H): %d x %d",
                options.outWidth, options.outHeight));

        bm = BitmapLoader.loadScaledBitmapFromFile(path, reqWidth, reqHeight, BitmapLoader.SCALE_TYPE_NOT_SMALLER);
        mImage1.setImageBitmap(bm);
        mSize1.setText(String.format("Image size (W x H): %d x %d",
                bm.getWidth(), bm.getHeight()));

        bm = BitmapLoader.loadScaledBitmapFromFile(path, reqWidth, reqHeight, BitmapLoader.SCALE_TYPE_SMALLER);
        mImage2.setImageBitmap(bm);
        mSize2.setText(String.format("Image size (W x H): %d x %d",
                bm.getWidth(), bm.getHeight()));

    }
}
