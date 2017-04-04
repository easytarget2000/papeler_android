package org.eztarget.papeler.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * Created by michelsievers on 04.04.17.
 */

public class Cubicle extends Being {

    private static final String TAG = Cubicle.class.getSimpleName();

    private static final int MAX_AGE = 2000;

    private int mAge = 0;

    private double mTopVerticesX[] = new double[4];

    private double mTopVerticesY[] = new double[4];

    private double mBottomVerticesX[] = new double[4];

    private double mBottomVerticesY[] = new double[4];

    Cubicle(final double centerX, final double centerY, final double canvasSize) {

        mDoubleJitter = canvasSize * 0.2;
        mFloatJitter = (float) mDoubleJitter;

        final double width = random(canvasSize / 10) + (canvasSize / 20);
        final double horizontalShift = random(canvasSize / 16);
        final double height = random(canvasSize / 8) + (canvasSize / 30);

        final int numberOfVertices = mTopVerticesX.length;

        double angle = 0.0;
        for (int i = 0; i < numberOfVertices; i++) {
            if (i < numberOfVertices - 1) {
                angle += random(TWO_PI / (double) numberOfVertices);
            } else {
                angle = TWO_PI - angle;
            }

            mTopVerticesX[i] = centerX + (Math.cos(angle) * width);
            mBottomVerticesX[i] = mTopVerticesX[i] + horizontalShift;
            mTopVerticesY[i] = centerY + (Math.sin(angle) * width);
            mBottomVerticesY[i] = mTopVerticesY[i] + height;
        }

    }

    @Override
    public boolean update(boolean isTouching) {
        return mAge++ < MAX_AGE;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint1) {

        for (int rounds = 0; rounds < 16; rounds++) {
            drawPoints(canvas, paint1);
        }
    }

    private void drawPoints(@NonNull Canvas canvas, @NonNull Paint paint) {
        for (int i = 0; i < mTopVerticesX.length; i++) {

            final int nextIndex = (i + 1) % mTopVerticesX.length;

            final float relativeDist = randomF(1f);

            final float topPointX;
            topPointX = (float) (mTopVerticesX[i] + ((mTopVerticesX[nextIndex] - mTopVerticesX[i]) * relativeDist));
            final float topPointY;
            topPointY = (float) (mTopVerticesY[i] + ((mTopVerticesY[nextIndex] - mTopVerticesY[i]) * relativeDist));

            final float bottomPointX;
            bottomPointX = (float) (mBottomVerticesX[i] + ((mBottomVerticesX[nextIndex] - mBottomVerticesX[i]) * relativeDist));
            final float bottomPointY;
            bottomPointY = (float) (mBottomVerticesY[i] + ((mBottomVerticesY[nextIndex] - mBottomVerticesY[i]) * relativeDist));

            final float randomPointX = topPointX + randomF(bottomPointX - topPointX);
            final float randomPointY = topPointY + randomF(bottomPointY - topPointY);

            canvas.drawPoint(randomPointX, randomPointY, paint);

            canvas.drawLine(
                    (float) mTopVerticesX[i],
                    (float) mTopVerticesY[i],
                    (float) mTopVerticesX[nextIndex],
                    (float) mTopVerticesY[nextIndex],
                    paint
            );

            canvas.drawLine(
                    (float) mBottomVerticesX[i],
                    (float) mBottomVerticesY[i],
                    (float) mBottomVerticesX[nextIndex],
                    (float) mBottomVerticesY[nextIndex],
                    paint
            );
        }
    }
}
