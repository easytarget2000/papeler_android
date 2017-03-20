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

    protected static double angle(
            final double x1,
            final double y1,
            final double x2,
            final double y2
    ) {
        final double calcAngle = Math.atan2(
                -(y1 - y2),
                x2 - x1
        );

        if (calcAngle < 0) {
            return calcAngle + TWO_PI;
        } else {
            return calcAngle;
        }
    }

    protected static double distance(
            final double x1,
            final double y1,
            final double x2,
            final double y2
    ) {
        return Math.sqrt(
                Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2)
        );
    }
}
