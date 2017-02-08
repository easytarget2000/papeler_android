package org.eztarget.papeler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by michelsievers on 08/02/2017.
 */

public class Line {

    private ArrayList<Point> mPoints;

    private int mAge = 0;

    private float mJitter;

    Line(final float x, final float y, final float canvasSize) {

        final float initialLengthHalf = random(canvasSize / 2f);
        final float initialAngle = random((float) Math.PI * 2);
        mJitter = canvasSize * 0.03f;

        final Point firstPoint = new Point(0);
        firstPoint.mX = x + (float) Math.cos(initialAngle) * initialLengthHalf;
        firstPoint.mY = y + (float) Math.sin(initialAngle) * initialLengthHalf;

        final Point lastPoint = new Point(1);
        lastPoint.mX = x + (float) Math.cos(initialAngle) * -initialLengthHalf;
        lastPoint.mY = y + (float) Math.sin(initialAngle) * -initialLengthHalf;

        mPoints = new ArrayList<>();
        mPoints.add(firstPoint);
        mPoints.add(lastPoint);
    }

    void draw(
            @NonNull final Canvas canvas,
            @NonNull final Paint paint1,
            @NonNull final Paint paint2
    ) {

//        canvas.drawCircle(100f, 100f, 100f, paint);

        Point lastPoint = null;
        for (final Point point : mPoints) {
            if (lastPoint != null && point.mId % 3 != 0) {
                canvas.drawLine(lastPoint.mX, lastPoint.mY, point.mX, point.mY, paint1);
            }
            lastPoint = point;
            canvas.drawCircle(lastPoint.mX, lastPoint.mY, random(mJitter), paint2);
//            canvas.drawPoint(point.mX, point.mY, paint);
        }
    }

    boolean update() {

        ++mAge;

        final int numberOfPoints = mPoints.size();

        final Point[] pointsArray = new Point[numberOfPoints];
        mPoints.toArray(pointsArray);

        for (final Point point : mPoints) {
            point.update(pointsArray, mJitter);
        }

        if (mAge % 2 == 0) {
            final Point firstPoint = mPoints.get(numberOfPoints - 2);
            final Point lastPoint = mPoints.get(numberOfPoints - 1);
            final Point newMiddlePoint = new Point(numberOfPoints);
            newMiddlePoint.mX = (firstPoint.mX + lastPoint.mX) / 2f;
            newMiddlePoint.mY = (firstPoint.mY + lastPoint.mY) / 2f;
            mPoints.add(newMiddlePoint);
            Log.d("Line.update()", "New: " + newMiddlePoint.mId);
        }

        return mAge < 200;
    }

    private static final float random(final float maxValue) {
        return (float) Math.random() * maxValue;
    }

    private class Point {

        private int mId;

        private float mX;

        private float mY;

        private Point(final int id) {
            mId = id;
        }

        private float distance(final Point otherPoint) {
            return (float) Math.sqrt(
                    Math.pow(
                            (double) (otherPoint.mX - mX),
                            2
                    )
                    + Math.pow(
                            (double) (otherPoint.mY - mY),
                            2
                    )
            );
        }

        private void update(@NonNull final Point[] points, final float jitter) {
            for (final Point otherPoint : points) {

                if (mId != otherPoint.mId) {
                    continue;
                }

                final float force = distance(otherPoint) / 2f;

//                Log.d("Line.update()", "Before: " + mId + ": " + mX + ", " + mY + ", " + otherPoint.mId + ": " + otherPoint.mX + ", " + otherPoint.mY + ", force: " + force);

                if (otherPoint.mX - mX > 0) {
                    mX -= force;
                } else {
                    mX += force;
                }

                if (otherPoint.mY - mY > 0) {
                    mY -= force;
                } else {
                    mY += force;
                }

                mX += jitter * 0.5f - random(jitter);
                mY += jitter * 0.5f - random(jitter);

//                Log.d("Line.update()", "After: " + mId +": " + mX + ", " + mY);
            }
        }

    }

}
