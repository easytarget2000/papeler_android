package org.eztarget.papeler.old_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import android.util.Log;

/**
 * Created by michelsievers on 04.04.17.
 */

public class Cubicle extends Being {

    private static final String TAG = Cubicle.class.getSimpleName();

    private static final int MAX_AGE = 800;

    private static final int MAX_ROUNDS_PER_DRAW_CALL = 512;

    private int mRoundsPerDrawCall;

    private int mAge = 0;

    private float mTopVerticesX[] = new float[4];

    private float mTopVerticesY[] = new float[4];

    private float mBottomVerticesX[] = new float[4];

    private float mBottomVerticesY[] = new float[4];

    Cubicle(final float centerX, final float centerY, final float canvasSize) {
//        mDoubleJitter = canvasSize * 0.2;
//        mFloatJitter = (float) mDoubleJitter;

        final double maxSize = canvasSize / 10;
        final double sizeFactor = 0.3 + random(0.7);

        final double width = sizeFactor * maxSize;
        mRoundsPerDrawCall = (int) (MAX_ROUNDS_PER_DRAW_CALL * sizeFactor);

        final float horizontalShift = randomF(canvasSize / 16f);
        final float height = (float) width * 1.5f;

        final int numberOfVertices = mTopVerticesX.length;

        final double minAngle = TWO_PI / (numberOfVertices * 0.9);
        final double maxAngle = (TWO_PI / ((double) numberOfVertices * 1.7)) - minAngle;

//        Log.d(TAG, "Constructor: minAngle: " + minAngle + ", maxAngle: " + maxAngle);

        final double angleOffset = random(TWO_PI / 8.0);

        double angle = angleOffset;
        for (int i = 0; i < numberOfVertices; i++) {
            if (i < numberOfVertices - 1) {
                angle += minAngle + random(maxAngle);
            } else {
                angle = TWO_PI + angleOffset;
            }

            Log.d(TAG, "Constructor: Edge " + i + ": Angle:" + angle);

            mTopVerticesX[i] = centerX + (float) (Math.cos(angle) * width);
            mBottomVerticesX[i] = mTopVerticesX[i] + horizontalShift;

            mTopVerticesY[i] = (centerY + (height / 2))
                    + (float) (Math.sin(angle) * width);
            mBottomVerticesY[i] = mTopVerticesY[i] + height;
        }

    }

    @Override
    public boolean update(boolean isTouching) {
        return mAge++ < MAX_AGE;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint1) {

        for (int rounds = 0; rounds < mRoundsPerDrawCall; rounds++) {
            drawPoints(canvas, paint1);
        }

    }

    private void drawPoints(@NonNull Canvas canvas, @NonNull Paint paint) {
        for (int i = 0; i < mTopVerticesX.length; i++) {

            final int nextIndex = (i + 1) % mTopVerticesX.length;

            final float relativeDist = randomF(1f);

            final float topPointX;
            topPointX = (mTopVerticesX[i] + ((mTopVerticesX[nextIndex] - mTopVerticesX[i]) * relativeDist));
            final float topPointY;
            topPointY = (mTopVerticesY[i] + ((mTopVerticesY[nextIndex] - mTopVerticesY[i]) * relativeDist));

            final float bottomPointX;
            bottomPointX = (mBottomVerticesX[i] + ((mBottomVerticesX[nextIndex] - mBottomVerticesX[i]) * relativeDist));
            final float bottomPointY;
            bottomPointY = (mBottomVerticesY[i] + ((mBottomVerticesY[nextIndex] - mBottomVerticesY[i]) * relativeDist));

            canvas.drawPoint(
                    topPointX + randomF(bottomPointX - topPointX),
                    topPointY + randomF(bottomPointY - topPointY),
                    paint
            );
//            canvas.drawPoint(randomPointX + 4, randomPointY + 3, paint);
//            canvas.drawPoint(randomPointX - 4, randomPointY, paint);
//            canvas.drawPoint(randomPointX, randomPointY - 3, paint);
        }
    }
}
