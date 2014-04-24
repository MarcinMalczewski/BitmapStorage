package pl.twogeeks.bitmapstorage.test;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.io.File;

import pl.twogeeks.bitmapstorage.BitmapLoader;

/**
 * Created by marcim on 2014-03-31.
 */
public class BitmapLoaderTest extends AndroidTestCase {

    private final String IMAGE_FILE = "default.jpg";
    private String IMAGE_PATH;
    private final int IMAGE_WIDTH = 3264;
    private final int IMAGE_HEIGHT = 1836;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        IMAGE_PATH = this.getContext().getFilesDir().getAbsolutePath() + File.separator + IMAGE_FILE;
        File file = new File(IMAGE_PATH);
        if (!file.exists())
            throw new Exception("File needed to make tests doesn't exist!");
    }

    public void testLoadBitmapFromFile() {
        Bitmap bm = BitmapLoader.loadBitmapFromFile("path");
        Assert.assertNull(bm);

        bm = BitmapLoader.loadBitmapFromFile(IMAGE_PATH);
        Assert.assertNotNull(bm);
        Assert.assertTrue(bm.getWidth() == IMAGE_WIDTH);
        Assert.assertTrue(bm.getHeight() == IMAGE_HEIGHT);
    }

    public void testLoadScaledBitmapFromFile() {
        Bitmap bm = BitmapLoader.loadScaledBitmapFromFile(IMAGE_PATH, 0, 0, BitmapLoader.SCALE_TYPE_NOT_SMALLER);
        Assert.assertNull(bm);

        bm = BitmapLoader.loadScaledBitmapFromFile(IMAGE_PATH, 0, 100, BitmapLoader.SCALE_TYPE_NOT_SMALLER);
        Assert.assertNull(bm);

        bm = BitmapLoader.loadScaledBitmapFromFile(IMAGE_PATH, 100, 0, BitmapLoader.SCALE_TYPE_NOT_SMALLER);
        Assert.assertNull(bm);

        bm = BitmapLoader.loadScaledBitmapFromFile(IMAGE_PATH, 100, 100, BitmapLoader.SCALE_TYPE_NOT_SMALLER);
        Assert.assertNotNull(bm);
        Assert.assertTrue(bm.getWidth() > 100);
        Assert.assertTrue(bm.getHeight() > 100);

        bm = BitmapLoader.loadScaledBitmapFromFile(IMAGE_PATH, 100, 100, BitmapLoader.SCALE_TYPE_SMALLER);
        Assert.assertNotNull(bm);
        Assert.assertTrue(bm.getWidth() < 100);
        Assert.assertTrue(bm.getHeight() < 100);
    }

}
