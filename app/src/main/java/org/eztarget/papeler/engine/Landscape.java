package org.eztarget.papeler.engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * Created by michelsievers on 19.07.17.
 */

public class Landscape extends Being {

    private Slope[] mFirstSlopes;

    private float mMaxWidth;

    private float mMaxHeight;

    public Landscape(final float y, final float maxWidth, final float maxHeight) {
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mFirstSlopes = new Slope[64];
        for (int i = 0; i < mFirstSlopes.length; i++) {
            final float speed = 20f * ((mFirstSlopes.length - i + 1f) / (float) mFirstSlopes.length);
            mFirstSlopes[i] = new Slope(0f, y, speed);
        }
    }

    @Override
    public boolean update(boolean isTouching) {

        boolean updatedOneSlope = false;

        for (Slope firstSlope : mFirstSlopes) {
            Slope currentSlope = firstSlope;

            while (currentSlope != null) {

                if (currentSlope.update()) {
                    updatedOneSlope = true;
                } else {
                    firstSlope = currentSlope.mNextSlope;
                }

                currentSlope = currentSlope.mNextSlope;
            }
        }

        return updatedOneSlope;
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

            mRightX = leftX + (float) random(128);
            mRightY = (leftY + (float) random(20 - 10));
        }

        private boolean update() {
            mLeftX += -mSpeed;
            mRightX += -mSpeed;

            if (mLeftX < mMaxWidth && mNextSlope == null) {
                if (mRightY > mMaxHeight) {
                    mNextSlope = new Slope(mMaxWidth, 0f, mSpeed);
                } else {
                    mNextSlope = new Slope(mRightX + mSpeed, mRightY, mSpeed);
                }
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
