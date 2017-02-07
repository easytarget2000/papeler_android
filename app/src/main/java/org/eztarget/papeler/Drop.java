package org.eztarget.papeler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * Created by michelsievers on 23/01/2017.
 */

public class Drop {

    private float mLastX;

    private float mLastY;

    private float mX;

    private float mY;

    private float mSize;

    private float mMaxVelocity;

    private float mXVelocity;

    private float mYVelocity;

    private float mAge = 0;

    Drop(final float x, final float y, final float canvasSize, final Drop firstDrop) {
        mMaxVelocity = canvasSize * 0.1f;

        mX = x;
        mY = y;
        if (firstDrop == null) {
            mLastX = mX;
            mLastY = mY;
        } else {
            mLastX = firstDrop.mY;
            mLastY = firstDrop.mY;
        }
        mSize = mMaxVelocity * (float) Math.random() * 2f;
        mXVelocity = (mMaxVelocity * 0.5f) - (mMaxVelocity * (float) Math.random());
        mYVelocity = (mMaxVelocity * 0.5f) - (mMaxVelocity * (float) Math.random());
    }

    boolean update() {
        if (mAge++ > 30) {
            return false;
        }

        mX += mXVelocity;
        mY += mYVelocity;

        mXVelocity = (mXVelocity * 0.9f) + (float) Math.random();
        mYVelocity = (mYVelocity * 0.9f) + (float) Math.random();

        return true;
    }

    void draw(@NonNull final Canvas canvas, @NonNull final Paint paint) {
//        canvas.drawCircle(mX, mY, mSize, paint);
        canvas.drawLine(mLastX, mLastY, mX, mY, paint);

        mLastX = mX;
        mLastY = mY;
    }
}
