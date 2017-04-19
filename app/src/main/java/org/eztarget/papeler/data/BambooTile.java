package org.eztarget.papeler.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * Created by michelsievers on 18.04.17.
 */

public class BambooTile extends Being {

    private static final float NON_LINEAR_JITTER_BREAK = 0.85f;

    private int MAX_AGE = 600;

    private int mGrowthDirection = mRandom.nextInt(4);

    private boolean mDrawLines = true;

    private int mAge = 0;

    private float mLeftX;

    private float mRightX;

    private float mTopY;

    private float mBottomY;

    BambooTile(final float tileSize, final float x, final float y, final boolean drawLines) {
        final float column = (float) Math.floor((double) x / (double) tileSize);
        final float row = (float) Math.floor((double) y / (double) tileSize);

        mLeftX = column * tileSize;
        mRightX = (column + 1) * tileSize;
        mTopY = row * tileSize;
        mBottomY = (row + 1) * tileSize;

        mFloatJitter = tileSize;

        mDrawLines = drawLines;
    }

    @Override
    public boolean update(boolean isTouching) {
        return mAge++ < MAX_AGE;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint1) {

        final float startX;
        final float startY;
        final float endX;
        final float endY;

        switch (mGrowthDirection) {
            case 0:
                // Right to left:
                startX = mRightX;
                startY = mTopY + getFloatJitter();
                endX = mRightX - getNonLinearJitter();
                endY = startY;
                break;

            case 1:
                // Bottom to top:
                startX = mLeftX + getFloatJitter();
                startY = mBottomY;
                endX = startX;
                endY = mBottomY - getNonLinearJitter();
                break;

            case 2:
                // Left to right:
                startX = mLeftX;
                startY = mTopY + getFloatJitter();
                endX = mLeftX + getNonLinearJitter();
                endY = startY;
                break;

            default:
                // Top to bottom:
                startX = mLeftX + getFloatJitter();
                startY = mTopY;
                endX = startX;
                endY = mTopY + getNonLinearJitter();
        }

        if (mDrawLines) {
            canvas.drawLine(startX, startY, endX, endY, paint1);
        } else {
            canvas.drawPoint(endX, endY, paint1);
        }
    }

    @Override
    protected float getFloatJitter() {
        return mRandom.nextFloat() * mFloatJitter;
    }

    private float getNonLinearJitter() {
        if (mRandom.nextBoolean()) {
            return getFloatJitter();
        } else {
            return getFloatJitter() * NON_LINEAR_JITTER_BREAK;
        }
    }
}
