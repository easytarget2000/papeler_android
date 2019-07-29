package org.eztarget.papeler.old_engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;

/**
 * Created by michelsievers on 27/03/2017.
 */

public class Curtain extends Being {

    private static final int MAX_AGE = 800;

    private int mAge = 0;

    private Edge[] mStartEdges;

    Curtain(final double x, final double y, final double canvasSize) {
        mDoubleJitter = canvasSize * 0.05;

        final int numberOfStartEdges = 4;
        mStartEdges = new Edge[numberOfStartEdges];
        for (int i = 0; i < numberOfStartEdges; i++) {

            final double edgeStartX;
            final double edgeStartY;

            if (i == 0) {
                edgeStartX = x + getDoubleJitter();
                edgeStartY = y + getDoubleJitter();
            } else {
                final Edge lastEdge = mStartEdges[i - 1];
                edgeStartX = lastEdge.mStartX + (Math.cos(lastEdge.mAngle) * lastEdge.mLength);
                edgeStartY = lastEdge.mStartY + (Math.sin(lastEdge.mAngle) * lastEdge.mLength);
            }

            final double edgeAngle = TWO_PI * mRandom.nextDouble();
            final double edgeLength = mRandom.nextDouble() * canvasSize * 0.2;

            mStartEdges[i] = new Edge(edgeStartX, edgeStartY, edgeAngle, edgeLength);
        }

        for (int i = 0; i < numberOfStartEdges; i++) {

            final double edgeStartX;
            final double edgeStartY;

            if (i == 0) {
                edgeStartX = x + getDoubleJitter();
                edgeStartY = y + getDoubleJitter();
            } else {
                final Edge lastEdge = mStartEdges[i - 1].mOther;
                edgeStartX = lastEdge.mStartX + (Math.cos(lastEdge.mAngle) * lastEdge.mLength);
                edgeStartY = lastEdge.mStartY + (Math.sin(lastEdge.mAngle) * lastEdge.mLength);
            }

            final double edgeAngle = TWO_PI * mRandom.nextDouble();
            final double edgeLength = mRandom.nextDouble() * canvasSize * 0.2;

            mStartEdges[i].mOther = new Edge(edgeStartX, edgeStartY, edgeAngle, edgeLength);
        }
    }

    @Override
    public boolean update(boolean isTouching) {
        return mAge++ < MAX_AGE;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint1) {
        for (final Edge edge : mStartEdges) {
            canvas.drawLine(
                    edge.getX1(),
                    edge.getY1(),
                    edge.getX2(),
                    edge.getY2(),
                    paint1
            );

//            canvas.drawPoint(edge.getX1(), edge.getY1(), paint1);
//            paint1.setAlpha(50);
//            canvas.drawPoint(edge.getX2(), edge.getY2(), paint1);
        }

    }

    private class Edge {

        private Edge mOther;

        private double mStartX;

        private double mStartY;

        private double mAngle;

        private double mLength;

        private Edge(
                final double startX,
                final double startY,
                final double angle,
                final double length
        ) {
            mStartX = startX;
            mStartY = startY;
            mAngle = angle;
            mLength = length;
        }

        private float getX1() {
            return (float) (mStartX + (Math.cos(mAngle) * mRandom.nextFloat() * mLength));
        }

        private float getY1() {
            return (float) (mStartY + (Math.sin(mAngle) * mRandom.nextFloat() * mLength));
        }

        private float getX2() {
            return (float) (mOther.mStartX +
                    (Math.cos(mOther.mAngle) * mRandom.nextFloat() * mOther.mLength));
        }

        private float getY2() {
            return (float) (mOther.mStartX +
                    (Math.sin(mOther.mAngle) * mRandom.nextFloat() * mOther.mLength));
        }
    }
}
