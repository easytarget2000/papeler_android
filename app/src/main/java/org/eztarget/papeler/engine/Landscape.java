package org.eztarget.papeler.engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import android.util.Log;

/**
 * Created by michelsievers on 19.07.17.
 */

public class Landscape extends Being {

    private static final String TAG = Landscape.class.getSimpleName();

    private static final boolean VERBOSE = false;

    private static final int MAX_AGE = 256;

    private Slope[] mFirstSlopes;

    private float mMaxWidth;

    private float mMaxHeight;

    public Landscape(final float y, final float maxWidth, final float maxHeight) {
        mMaxWidth = maxWidth;
        final float maxSpeed = maxWidth / 128f;
        mMaxHeight = maxHeight;
        mFirstSlopes = new Slope[2 + (int) random(4.0)];
        for (int i = 0; i < mFirstSlopes.length; i++) {
            final float speedFactor = ((i + 1f) / (float) mFirstSlopes.length);
            mFirstSlopes[i] = new Slope(maxWidth, y, maxSpeed * speedFactor);

            if (VERBOSE) {
                Log.d(
                        TAG,
                        "New Slope: speedFactor: " + speedFactor
                                + " -> speed: " + maxSpeed * speedFactor
                );
            }
        }
    }

    @Override
    public boolean update(boolean isTouching) {

        for (Slope firstSlope : mFirstSlopes) {
            Slope currentSlope = firstSlope;

            while (currentSlope != null) {

                if (currentSlope.update()) {
//                    updatedOneSlope = true;
                } else {
//                    firstSlope = currentSlope.mNextSlope;
                }

                currentSlope = currentSlope.mNextSlope;
            }
        }

        return ++mAge < MAX_AGE;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint) {
        for (final Slope firstSlope : mFirstSlopes) {
            Slope currentSlope = firstSlope;

            while (currentSlope != null) {

                currentSlope.draw(canvas, paint);
                currentSlope = currentSlope.mNextSlope;
            }
        }
    }


    private class Slope {

        private float mLeftX;

        private float mLeftY;

        private float mRightX;

        private float mRightY;

        private float mSpeed;

        private Slope mNextSlope;

        private Slope(final float leftX, final float leftY, final float speed) {
            mLeftX = leftX;
            mLeftY = leftY;
            mSpeed = speed;

            mRightX = leftX + randomF(mMaxWidth * 0.75f);
            mRightY = leftY + (mMaxHeight / 2f - randomF(mMaxHeight));
        }

        private boolean update() {
            mLeftX += -mSpeed;
            mRightX += -mSpeed;

            if (mRightY > mMaxWidth || mRightY < 0f) {
                mRightY -= Random.nextFloat(mRightY - mMaxWidth);
                mSpeed = -mSpeed;
            }

            if (mLeftX < mMaxWidth && mNextSlope == null) {
//                if (mRightY > mMaxHeight) {
//                    mNextSlope = new Slope(mMaxWidth, 0f, mSpeed);
//                } else {
                mNextSlope = new Slope(mRightX + mSpeed, mRightY, mSpeed * 1.2f);
//                }
                return false;
            } else if (mRightX < 0f) {
                return false;
            } else {
                return true;
            }
        }

        private void draw(@NonNull Canvas canvas, @NonNull Paint paint) {
            canvas.drawLine(mLeftX, mLeftY, mRightX, mRightY, paint);
        }
    }
}
