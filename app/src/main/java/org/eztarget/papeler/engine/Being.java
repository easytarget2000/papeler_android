package org.eztarget.papeler.engine;

/**
 * Created by michelsievers on 04/03/2017.
 */

public abstract class Being implements UpdateAndDrawable {

    protected static final double TWO_PI = Math.PI * 2;

    protected boolean mStopped = false;

    protected int mAge = 0;

    protected double mDoubleJitter;

    protected float mFloatJitter;

    protected java.util.Random mRandom = new java.util.Random();

    public void stopPerforming() {
        mStopped = true;
    }

    protected double getDoubleJitter() {
        return mDoubleJitter * 0.5 - random(mDoubleJitter);
    }

    protected float getFloatJitter() {
        return mFloatJitter * 0.5f - randomF(mFloatJitter);
    }

    protected double random(final double maxValue) {
        return Random.nextDouble(maxValue);
    }

    protected float randomF(final float maxValue) {
        return Random.nextFloat(maxValue);
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
