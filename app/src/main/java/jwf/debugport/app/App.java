package jwf.debugport.app;

import android.app.Application;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

import jwf.debugport.DebugPortService;

/**
 *
 */
public class App extends Application {
    HashMap<String, Integer> mStringIntHashmap = new HashMap<>();
    public Class<?>[] mClassArray;
    public int[] mIntArray;
    public boolean[] mBooleanArray;
    public char[] mCharArray;
    public short[] mShortArray;
    public byte[] mByteArray;
    public long[] mLongArray;

    @Override
    public void onCreate() {
        super.onCreate();
        mStringIntHashmap.put("Test", 1);
    }

    public Map<String, Integer> getStringIntHashmap() {
        return mStringIntHashmap;
    }

    public void setStringIntHashmap(HashMap<String, Integer> map) {
        mStringIntHashmap = map;
    }

    public int[] getIntArray() {
        return mIntArray;
    }

    public void setIntArray(int[] arry) {
        mIntArray = arry;
    }
}
