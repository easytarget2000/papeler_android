package org.eztarget.papeler.engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.NonNull;

/**
 * Created by michelsievers on 21/03/2017.
 */

public class SwingBrush extends Being {

    private int mMaxAge = mRandom.nextInt(600) + 100;

    private int mAge = 0;

    private double mSharedMidX;

    private double mSharedMidY;

    private float mCanvasSize;

    private Line mFirstLine;

    public SwingBrush(final float x, final float y, final double canvasSize) {
        mSharedMidX = x;
        mSharedMidY = y;
//        mCanvasSize = canvasSize;
        mDoubleJitter = canvasSize * 0.01f;
        mFloatJitter = (float) mDoubleJitter;

        final int numberOfLines = mRandom.nextInt(10) + 2;
        Line lastLine = null;

        for (int i = 0; i < numberOfLines; i++) {

            final Line newLine = new Line(canvasSize);

            if (mFirstLine == null) {
                mFirstLine = newLine;
                lastLine = newLine;
            } else if (i == numberOfLines - 1) {
                newLine.mNextLine = mFirstLine;
                lastLine.mNextLine = newLine;
            } else {
                lastLine.mNextLine = newLine;
                lastLine = newLine;
            }

        }
    }

    @Override
    public boolean update(boolean isTouching) {
//        mSharedMidX += getDoubleJitter();
//        mSharedMidY += getDoubleJitter();

        Line currentLine = mFirstLine;
        do {
            currentLine.update();
            currentLine = currentLine.mNextLine;
        } while (currentLine != null && currentLine != mFirstLine);

        return mAge++ < mMaxAge;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint1) {
        Path path = null;
        Line currentLine = mFirstLine;

        do {
            final float[] coordinates = currentLine.getCurveCoordinates();
            if (path == null) {
                path = new Path();
                path.moveTo(coordinates[0], coordinates[1]);
            }

            path.quadTo(
                    coordinates[2], coordinates[3],
                    coordinates[4], coordinates[5]
            );
            currentLine = currentLine.mNextLine;
        } while (currentLine != null && currentLine != mFirstLine);


//        canvas.drawLine(coordinates[0], coordinates[1], coordinates[4], coordinates[5], paint1);

        canvas.drawPath(path, paint1);
    }

    private class Line {

        private static final double MIN_LINE_LENGTH_FACTOR = 0.2;

        private static final double MAX_LINE_LENGTH_FACTOR = 0.6;

        private static final float MID_DEVIATION_FACTOR = 32f;

        private double mMaxPushDistance;

        private double mPreferredNeighbourDistance;

        private double mCurrentAngle;

        private double mMaxAngleStep = TWO_PI / 360.0;

        private double mCurrentLengthHalf;

        private float mMidX;

        private float mMidY;

        private double mStartToMidRatio = 0.5;

        private Line mNextLine;

        private Line(final double canvasSize) {
            mMaxPushDistance = canvasSize * 0.2;
            mPreferredNeighbourDistance = canvasSize * 0.001;
            mCurrentAngle = random(TWO_PI);
            final double minLength = canvasSize * MIN_LINE_LENGTH_FACTOR;
            final double maxLength = canvasSize * MAX_LINE_LENGTH_FACTOR;
            mCurrentLengthHalf = (minLength + random(maxLength - minLength)) / 2.0;

            mMidX = (float) mSharedMidX + (MID_DEVIATION_FACTOR * getFloatJitter());
            mMidY = (float) mSharedMidY + (MID_DEVIATION_FACTOR * getFloatJitter());
        }

        private void update() {
            mCurrentAngle += mMaxAngleStep - random(mMaxAngleStep * 2.0);
            mCurrentLengthHalf += getDoubleJitter();
            mMidX += getFloatJitter();
            mMidY += getFloatJitter();

            Line otherLine = mNextLine;
            do {
                final double distance = distance(mMidX, mMidY, otherLine.mMidX, otherLine.mMidY);

                if (distance > mMaxPushDistance) {
                    otherLine = otherLine.mNextLine;
                    continue;
                }

                final double angle = angle(mMidX, mMidY, otherLine.mMidX, otherLine.mMidY);

                final double force;

                if (otherLine == mNextLine) {

                    if (distance > mPreferredNeighbourDistance) {
//                        force = mPreferredNeighbourDistanceHalf;
                        force = (distance / 6);
                    } else {
                        force = -6;
                    }

                } else {

                    force = (6 / distance);

                }

                mMidX += Math.cos(angle) * force;
                mMidY += Math.sin(angle) * force;

                otherLine = otherLine.mNextLine;
            } while (otherLine != null && otherLine != this);
        }

        private float[] getCurveCoordinates() {
            final float[] coordinates = new float[6];
            // Starting point:
            coordinates[0] = mMidX - (float) (Math.cos(mCurrentAngle) * mCurrentLengthHalf);
            coordinates[1] = mMidY - (float) (Math.sin(mCurrentAngle) * mCurrentLengthHalf);
            // Mid control point:
            coordinates[2] = (float) mSharedMidX;
            coordinates[3] = (float) mSharedMidY;
            // The opposite of the start coordinates.
            coordinates[4] = mMidX + (float) (Math.cos(mCurrentAngle) * mCurrentLengthHalf);
            coordinates[5] = mMidY + (float) (Math.sin(mCurrentAngle) * mCurrentLengthHalf);

            return coordinates;
        }

    }
}
