package org.eztarget.papeler.data;

import java.util.Random;

/**
 * Created by michelsievers on 04/03/2017.
 */

public abstract class Being implements UpdateAndDrawable {

    protected static final float TWO_PI = (float) Math.PI * 2f;

    protected boolean mStopped = false;

    protected int mAge = 0;

    protected float mJitter;

    protected Random mRandom = new Random();

    public void stopPerforming() {
        mStopped = true;
    }

    protected float getJitterValue() {
        return mJitter * 0.5f - random(mJitter);
    }

    protected float random(final float maxValue) {
        return mRandom.nextFloat() * maxValue;
    }
}
