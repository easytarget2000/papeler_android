package org.eztarget.papeler.engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * Created by michelsievers on 18.04.17.
 */

public class BambooTile extends Being {

    private static final float NON_LINEAR_JITTER_BREAK = 0.85f;

    private static final int NUM_OF_STROKES = 4;

    private int MAX_AGE = 600;

    private int mGrowthDirection = mRandom.nextInt(4);

    private boolean mDrawLines = true;

    private int mAge = 0;

    private float mLeftX[] = new float[NUM_OF_STROKES];

    private float mRightX[] = new float[NUM_OF_STROKES];

    private float mTopY[] = new float[NUM_OF_STROKES];

    private float mBottomY[] = new float[NUM_OF_STROKES];

    BambooTile(final float tileSize, final float x, final float y, final boolean drawLines) {
        mFloatJitter = tileSize;
        mDrawLines = drawLines;

        final float column = (float) Math.floor((double) x / (double) tileSize);
        final float row = (float) Math.floor((double) y / (double) tileSize);

        final boolean growsHorizontally = mGrowthDirection % 2 == 0;

        float offset = 0;
        for (int i = 0; i < NUM_OF_STROKES; i++) {
            mLeftX[i] = (column * tileSize) + (growsHorizontally ? offset : 0f);
            mRightX[i] = ((column + 1) * tileSize) + (growsHorizontally ? offset : 0f);
            mTopY[i] = (row * tileSize) + (!growsHorizontally ? offset : 0f);
            mBottomY[i] = ((row + 1) * tileSize) + (!growsHorizontally ? offset : 0f);

            offset += (getFloatJitter() * 0.1f);
        }

    }

    @Override
    public boolean update(boolean isTouching) {
        return mAge++ < MAX_AGE;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint1) {

        for (int i = 0; i < NUM_OF_STROKES; i++) {

            final float startX;
            final float startY;
            final float endX;
            final float endY;

            switch (mGrowthDirection) {
                case 0:
                    // Right to left:
                    startX = mRightX[i] - (i > 0 ? getFloatJitter() : 0f);
                    startY = mTopY[i] + getEvenFloatJitter();
                    endX = mRightX[i] - getFloatJitter();
                    endY = startY;
                    break;

                case 1:
                    // Bottom to top:
                    startX = mLeftX[i] + getEvenFloatJitter();
                    startY = mBottomY[i] - (i > 0 ? getFloatJitter() : 0f);
                    endX = startX;
                    endY = mBottomY[i] - getFloatJitter();
                    break;

                case 2:
                    // Left to right:
                    startX = mLeftX[i] + (i > 0 ? getFloatJitter() : 0f);
                    startY = mTopY[i] + getEvenFloatJitter();
                    endX = mLeftX[i] + getFloatJitter();
                    endY = startY;
                    break;

                default:
                    // Top to bottom:
                    startX = mLeftX[i] + getEvenFloatJitter();
                    startY = mTopY[i] + (i > 0 ? getFloatJitter() : 0f);
                    endX = startX;
                    endY = mTopY[i] + getFloatJitter();
            }

            // CONTINUE HERE.

            if (i % 2 == 1) {
                final int lastColor = paint1.getColor();
                paint1.setColor(Color.BLACK);
                canvas.drawLine(startX, startY, endX, endY, paint1);
                paint1.setColor(lastColor);
            } else {
                canvas.drawLine(startX, startY, endX, endY, paint1);
            }

        }
    }

    @Override
    protected float getFloatJitter() {
        return mRandom.nextFloat() * mFloatJitter;
    }

    private float getEvenFloatJitter() {
        return (int) ((mRandom.nextFloat() * mFloatJitter) / 2f) * 2f;
    }

    private float getNonLinearJitter() {
        if (mRandom.nextBoolean()) {
            return getFloatJitter();
        } else {
            return getFloatJitter() * NON_LINEAR_JITTER_BREAK;
        }
    }
}
