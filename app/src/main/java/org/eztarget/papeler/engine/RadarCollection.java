package org.eztarget.papeler.engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by michelsievers on 20/03/2017.
 */

public class RadarCollection extends Being {

    private static final int MAX_AGE = 5;

    private int mAge = 0;

    private Radar[] mRadars;

    public RadarCollection(final double x, final double y, final double canvasSize) {
        mRadars = new Radar[1 + mRandom.nextInt(10)];

        for (int i = 0; i < mRadars.length; i++) {
            mRadars[i] = new Radar(x, y, random(canvasSize * 0.4) + 64, random(TWO_PI));
        }
        mDoubleJitter = canvasSize * 0.01;
    }

    @Override
    public boolean update(boolean isTouching) {

//        mLine.update();
        boolean updatedOne = false;
        for (final Radar radar : mRadars) {
            updatedOne |= radar.update();
        }
        return updatedOne;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint1) {
//        paint1.setAlpha(50);
        for (final Radar radar : mRadars) {
            canvas.drawLine(
                    radar.getStartX(),
                    radar.getStartY(),
                    radar.getEndX(),
                    radar.getEndY(),
                    paint1
            );
        }

    }

    private class Radar {

        private double mCenterX;

        private double mCenterY;

        private double mRadius;

        private double mInitialLength;

        private double mCurrentLength;

        private double mInitialAngle;

        private double mCurrentAngle;

        private double mAngleStep;

        private double mTargetX;

        private double mTargetY;

        private double mInertiaX;

        private double mInertiaY;

        private int mAge;

        private int mMaxAge;

        private Radar(final double x, final double y, final double radius, final double angle) {


            mCenterX = x;
            mCenterY = y;

            mRadius = radius;
            mInitialLength = radius * 0.33;
            mCurrentLength = mInitialLength;

            mMaxAge = mRandom.nextInt(1000) + 100;

            mInitialAngle = angle;
            mAngleStep = TWO_PI / (double) mMaxAge;
//            final double maxInertia = length / 3.0;
//            final double minInertia = -maxInertia;
//            mInertiaX = minInertia + random(maxInertia * 2);
//            mInertiaY = minInertia + random(maxInertia * 2);

            Log.d(TAG, "Initializing " + toString());
        }

        @Override
        public String toString() {
            return "[RadarCollection.Radar around " + mCenterX + ", " + mCenterY
                    + ", radius " + mRadius + "]";
        }

        private void setTarget(final double x, final double y) {
            mTargetX = x;
            mTargetY = y;
        }

        private float getStartX() {
            return (float) (mCenterX + (Math.cos(mCurrentAngle) * mRadius));
        }

        private float getStartY() {
            return (float) (mCenterY + (Math.sin(mCurrentAngle) * mRadius));
        }

        private float getEndX() {
            return (float) (mCenterX + (Math.cos(mCurrentAngle) * (mRadius - mCurrentLength)));
        }

        private float getEndY() {
            return (float) (mCenterY + (Math.sin(mCurrentAngle) * (mRadius - mCurrentLength)));
        }

        private boolean update() {
//
//            for (final Radar otherLine : mRadars) {
//                if (otherLine == this) {
//                    continue;
//                }
//
//                final double distance = distance(mCenterX, mCenterY, otherLine.mCenterX, otherLine.mCenterY);
//                if (distance < 16) {
//                    mCurrentAngle = angle(mCenterX, mCenterY, otherLine.mCenterX, otherLine.mCenterY);
//                }
//            }

            if (mAge > mMaxAge * 0.9) {
                mCurrentLength = mInitialLength + ((mInitialLength - mCurrentLength) / 10);
            }

            mCurrentLength += getDoubleJitter();
            if (mCurrentLength < 0) {
                mCurrentLength = 4;
            } else if (mCurrentLength > mRadius) {
                mCurrentLength = mRadius;
            }

            mCurrentAngle = mInitialAngle + (mAge++ * mAngleStep);
//            mCurrentAngle += ((double) mMaxAge / ++mAge) / 10.0;

//            mCenterX += getDoubleJitter();
//            mCenterY += getDoubleJitter();

            return mAge <= mMaxAge;
        }
    }
}
