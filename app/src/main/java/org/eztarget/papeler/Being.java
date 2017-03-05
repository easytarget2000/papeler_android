package org.eztarget.papeler;

import java.util.Random;

/**
 * Created by michelsievers on 04/03/2017.
 */

abstract class Being implements UpdateAndDrawable {

    protected static final float TWO_PI = (float) Math.PI * 2f;

    protected boolean mStopped = false;

    protected int mAge = 0;

    protected float mJitter;

    protected Random mRandom = new Random();

    void stopPerforming() {
        mStopped = true;
    }

    protected float getJitterValue() {
        return mJitter * 0.5f - random(mJitter);
    }

    static float random(final float maxValue) {
        return (float) Math.random() * maxValue;
    }
}
