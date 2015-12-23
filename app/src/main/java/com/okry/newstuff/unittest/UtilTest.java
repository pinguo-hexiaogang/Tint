package com.okry.newstuff.unittest;

import android.test.InstrumentationTestCase;

import com.okry.newstuff.util.Util;

/**
 * Created by hexiaogang on 12/11/15.
 */
public class UtilTest extends InstrumentationTestCase{

    public void testUtil(){
        int dp = Util.test();
        assertEquals(dp,4);
    }
}
