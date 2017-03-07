package org.eztarget.papeler.data;

import java.util.Random;

/**
 * Created by michelsievers on 04/03/2017.
 */

public abstract class Being implements UpdateAndDrawable {

    protected static final double TWO_PI = Math.PI * 2;

    protected boolean mStopped = false;

    protected int mAge = 0;

    protected double mJitter;

    protected Random mRandom = new Random();

    public void stopPerforming() {
        mStopped = true;
    }

    protected double getJitterValue() {
        return mJitter * 0.5 - random(mJitter);
    }

    protected double random(final double maxValue) {
        return mRandom.nextDouble() * maxValue;
    }
}
