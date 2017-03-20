package org.eztarget.papeler.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by michelsievers on 20/03/2017.
 */

public class LineArtist extends Being {

    private static final int MAX_AGE = 5;

    private int mAge = 0;

    private Line[] mLines;

    public LineArtist(final double x, final double y, final double canvasSize) {
        mLines = new Line[1 + mRandom.nextInt(10)];
        mLines = new Line[1];

        for (int i = 0; i < mLines.length; i++) {
            mLines[i] = new Line(x, y, random(canvasSize * 0.4) + 64, TWO_PI / 4.0);
        }
        mJitter = canvasSize * 0.01;
    }

    @Override
    public boolean update(boolean isTouching) {

//        mLine.update();
        boolean updatedOne = false;
        for (final Line line : mLines) {
            updatedOne |= line.update();
        }
        return updatedOne;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint1) {
//        paint1.setAlpha(50);
        for (final Line line : mLines) {
            canvas.drawLine(
                    line.getStartX(),
                    line.getStartY(),
                    line.getEndX(),
                    line.getEndY(),
                    paint1
            );
        }

    }

    private class Line {

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

        private Line(final double x, final double y, final double radius, final double angle) {


            mCenterX = x;
            mCenterY = y;

            mRadius = radius;
            mInitialLength = radius * 0.33;
            mCurrentLength = mInitialLength;

            mMaxAge = mRandom.nextInt(1000) + 100;
            mMaxAge = 200;

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
            return "[LineArtist.Line around " + mCenterX + ", " + mCenterY
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
//            for (final Line otherLine : mLines) {
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
                mCurrentLength = mInitialLength + ((mInitialLength - mCurrentLength) / 8);
            }

            mCurrentLength += getJitterValue();
            if (mCurrentLength < 0) {
                mCurrentLength = 4;
            } else if (mCurrentLength > mRadius) {
                mCurrentLength = mRadius;
            }

            mCurrentAngle = mInitialAngle + (mAge++ * mAngleStep);
//            mCurrentAngle += ((double) mMaxAge / ++mAge) / 10.0;

//            mCenterX += getJitterValue();
//            mCenterY += getJitterValue();

            return mAge <= mMaxAge;
        }
    }
}
