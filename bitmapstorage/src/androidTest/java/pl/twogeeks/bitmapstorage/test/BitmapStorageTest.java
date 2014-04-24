package pl.twogeeks.bitmapstorage.test;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import pl.twogeeks.bitmapstorage.BitmapStorage;

/**
 * Created by marcim on 2014-04-01.
 */
public class BitmapStorageTest extends AndroidTestCase {

    private final int DEFAULT_MEM_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory()) / 4;

    private BitmapStorage mBitmapStorage;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mBitmapStorage = new BitmapStorage();
    }

    public void testInitWithMemCache() {
        Assert.assertTrue(mBitmapStorage.isMemCacheUsed());
        Assert.assertNotNull(mBitmapStorage.getMemCache());
        Assert.assertEquals(0, mBitmapStorage.getMemCache().size());
        Assert.assertEquals(DEFAULT_MEM_CACHE_SIZE, mBitmapStorage.getMemCache().maxSize());
    }

    public void testInitWithoutMemCache() {
        mBitmapStorage.setUseMemCache(false);
        Assert.assertTrue(!mBitmapStorage.isMemCacheUsed());
        Assert.assertNull(mBitmapStorage.getMemCache());
    }
}
