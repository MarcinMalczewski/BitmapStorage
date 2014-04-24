package pl.twogeeks.bitmapstorage.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pl.twogeeks.bitmapstorage.R;

public class MainActivity extends ActionBarActivity {

    public static final String IMAGE_FILE_TEMPLATE = "img_%d.jpg";
    public static final int IMAGE_COUNT = 4;
    private String TARGET_DIR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.loaderBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BitmapLoaderActivity.class);
                startActivity(intent);
            }
        });

        btn = (Button) findViewById(R.id.gridBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GridActivity.class);
                startActivity(intent);
            }
        });

        btn = (Button) findViewById(R.id.linearBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LinearLayoutActivity.class);
                startActivity(intent);
            }
        });

        TARGET_DIR = this.getFilesDir().getAbsolutePath() + File.separator;
        prepareTestImage(R.raw.img_1, 1);
        prepareTestImage(R.raw.img_2, 2);
        prepareTestImage(R.raw.img_3, 3);
        prepareTestImage(R.raw.img_4, 4);
    }

    private void prepareTestImage(int resId, int imageNumber) {
        String fileName = String.format(IMAGE_FILE_TEMPLATE, imageNumber);
        // check if image file exists
        File file = new File(TARGET_DIR + fileName);
        if (file.exists())
            return;

        // copy default image file from raw dir to app's dir
        byte[] buffer = new byte[1024];
        int read;

        InputStream is = this.getResources().openRawResource(resId);
        BufferedInputStream bis = new BufferedInputStream(is);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(TARGET_DIR + fileName);
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
}
