package org.eztarget.papeler.engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

class FlowerStick extends Being {

    private static final String TAG = FlowerStick.class.getSimpleName();

    private static final int NUM_OF_INITIAL_NODES = 48;

    private double mBlossomX;

    private double mBlossomY;

    private double mCanvasHeight;

    private boolean mGrowing = true;

    private Branch[] mBranches;

    private Paint mLinePaint = new Paint();

    private int mInsertionIndex = NUM_OF_INITIAL_NODES / 2;

    private float mLastLineX;

    private float mLastLineY;

    FlowerStick(final double canvasHeight, final double x, final double y) {
        mCanvasHeight = canvasHeight;
        mDoubleJitter = mCanvasHeight * 0.001f;
        mBlossomX = x;
        mBlossomY = y;
        mLastLineX = (float) mBlossomX;
        mLastLineY = (float) mCanvasHeight;
        mDoubleJitter = 3f;
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.WHITE);
    }

    @Override
    public boolean update(boolean isTouching) {
        return mGrowing;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint) {

        final float currentY = (float) mCanvasHeight - mAge;

        if (currentY < mBlossomY) {

            if (mBranches == null) {
                final int numberOfBranches = 8 + mRandom.nextInt(64);
                mBranches = new Branch[numberOfBranches];

                final int numberOfPods = mRandom.nextInt(64 - 16) + 16;

                final double sharedLength = random(
                        mCanvasHeight / 3 * (numberOfPods / 64.0)
                );

                for (int i = 0; i < numberOfBranches; i++) {
                    final double angle;
                    angle = (TWO_PI * ((i + 1.0) / numberOfBranches)) + random(TWO_PI / 90);
                    final double length = sharedLength + random(mCanvasHeight / 100.0);

                    mBranches[i] = new Branch(angle, length);
                }
            }

            boolean somethingGrew = false;
            for (final Branch branch : mBranches) {
                if (branch.drawAndUpdate(canvas, paint)) {
                    somethingGrew = true;
                }
            }

            if (!somethingGrew) {
                mGrowing = false;
            }
            return;

        } else {
            final float newLineX = (float) (mBlossomX + getDoubleJitter());
            mLinePaint.setAlpha(paint.getAlpha());
            canvas.drawLine(
                    mLastLineX,
                    mLastLineY,
                    newLineX,
                    currentY,
                    mLinePaint
            );
            mLastLineX = newLineX;
            mLastLineY = currentY;
        }

        mAge += mRandom.nextInt((int) mCanvasHeight / 32);
    }

    private class Branch {

        private double mCurrentLength = 0f;

        private double mFinalLength;

        private double mSpeed;

        private double mAngle;

        private float mMaxPodRadius;

        private int mNumberOfPodsLeft;

        private Branch(final double angle, final double length) {
            mFinalLength = length;
            mSpeed = mCanvasHeight * 0.01f * mRandom.nextFloat();
            mAngle = angle;
            mMaxPodRadius = mRandom.nextFloat() * (float) mCanvasHeight * 0.01f;
            mNumberOfPodsLeft = mRandom.nextInt(6);
        }

        private boolean drawAndUpdate(@NonNull Canvas canvas, @NonNull Paint paint) {

            final float x = (float) (mBlossomX + (Math.cos(mAngle) * mCurrentLength));
            final float y = (float) (mBlossomY + (Math.sin(mAngle) * mCurrentLength));

            if (mCurrentLength > mFinalLength) {
                if (mNumberOfPodsLeft > 0) {
                    paint.setAlpha(mRandom.nextInt(255));

                    canvas.drawCircle(x, y, mRandom.nextFloat() * mMaxPodRadius, paint);
                    --mNumberOfPodsLeft;
                    return true;
                } else {
                    return false;
                }
            }

            mCurrentLength += mSpeed;

            final float newX = (float) (mBlossomX + (Math.cos(mAngle) * mCurrentLength));
            final float newY = (float) (mBlossomY + (Math.sin(mAngle) * mCurrentLength));

            mLinePaint.setAlpha(paint.getAlpha());
            canvas.drawLine(x, y, newX, newY, mLinePaint);

            return true;
        }

    }

}
