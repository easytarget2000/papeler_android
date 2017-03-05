package org.eztarget.papeler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

class FlowerStick extends Being {

    private static final String TAG = FlowerStick.class.getSimpleName();

    private static final int NUM_OF_INITIAL_NODES = 48;

    private float mBlossomX;

    private float mBlossomY;

    private float mCanvasHeight;

    private boolean mGrowing = true;

    private Branch[] mBranches;

    private int mInsertionIndex = NUM_OF_INITIAL_NODES / 2;

    private float mLastLineX;

    private float mLastLineY;

    FlowerStick(final float canvasHeight, final float x, final float y) {
        mCanvasHeight = canvasHeight;
        mJitter = mCanvasHeight * 0.001f;
        mBlossomX = x;
        mBlossomY = y;
        mLastLineX = mBlossomX;
        mLastLineY = mCanvasHeight;
        mJitter = 3f;
    }

    @Override
    public boolean update(boolean isTouching) {
        return mGrowing;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint1, @NonNull Paint paint2) {

        final float currentY = mCanvasHeight - mAge;

        if (currentY < mBlossomY) {

            if (mBranches == null) {
                final int numberOfBranches = 8 + mRandom.nextInt(24);
                mBranches = new Branch[numberOfBranches];

                final int numberOfPods = mRandom.nextInt(64 - 16) + 16;

                final float sharedLength = mRandom.nextFloat() * mCanvasHeight * 0.5f * ((float) numberOfPods / 64);

                for (int i = 0; i < numberOfBranches; i++) {
                    final float angle = (TWO_PI * ((i + 1f) / (float) numberOfBranches)) + (mRandom.nextFloat() * (TWO_PI / 90f));
                    final float length = sharedLength + (mRandom.nextFloat() * 0.01f * mCanvasHeight);
                    mBranches[i] = new Branch(angle, length);
                }
            }

            boolean somethingGrew = false;
            for (final Branch branch : mBranches) {
                if (branch.drawAndUpdate(canvas, paint1, paint2)) {
                    somethingGrew = true;
                }
            }

            if (!somethingGrew) {
                mGrowing = false;
            }
            return;

        } else {
            final float newLineX = mBlossomX + getJitterValue();
            canvas.drawLine(mLastLineX, mLastLineY, newLineX, currentY, paint1);
            mLastLineX = newLineX;
            mLastLineY = currentY;
        }

        mAge += mRandom.nextInt((int) mCanvasHeight / 32);
    }

    private class Branch {

        private float mCurrentLength = 0f;

        private float mFinalLength;

        private float mSpeed;

        private float mAngle;

        private float mMaxPodRadius;

        private Foliage mFoliage;

        private int mNumberOfPodsLeft;

        private Branch(final float angle, final float length) {
            mFinalLength = length;
            mSpeed = mCanvasHeight * 0.01f * mRandom.nextFloat();
            mAngle = angle;
            mMaxPodRadius = mRandom.nextFloat() * mCanvasHeight * 0.01f;
            mNumberOfPodsLeft = mRandom.nextInt(6);
        }

        private boolean drawAndUpdate(@NonNull Canvas canvas, @NonNull Paint paint1, @NonNull Paint paint2) {

            final float x = mBlossomX + ((float) Math.cos(mAngle) * mCurrentLength);
            final float y = mBlossomY + ((float) Math.sin(mAngle) * mCurrentLength);

            if (mCurrentLength > mFinalLength) {
                if (mNumberOfPodsLeft > 0) {
                    paint2.setAlpha(mRandom.nextInt(255));

                    canvas.drawCircle(x, y, mRandom.nextFloat() * mMaxPodRadius, paint2);
                    --mNumberOfPodsLeft;
                    return true;
                } else {
                    return false;
                }
            }

            mCurrentLength += mSpeed;

            final float newX = mBlossomX + ((float) Math.cos(mAngle) * mCurrentLength);
            final float newY = mBlossomY + ((float) Math.sin(mAngle) * mCurrentLength);

            canvas.drawLine(x, y, newX, newY, paint1);

            return true;
        }

    }

}
