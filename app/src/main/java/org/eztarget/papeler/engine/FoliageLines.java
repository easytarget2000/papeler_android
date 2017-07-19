package org.eztarget.papeler.engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

class FoliageLines extends Being {

    private static final String TAG = FoliageLines.class.getSimpleName();

    private static final int NUM_OF_INITIAL_NODES = 48;

    private static final int MAX_AGE = 80;

    private static final int ADD_NODE_LIMIT = 80;

    private static final double PUSH_FORCE = 16f;

    private Line mFirstLine;

    private int mNumberOfNodes;

    private double mCanvasSize;

    private boolean mSymmetric;

    private int mPaintMode;

    private boolean mChangeAlpha = true;

    private double mNodeDensity;

    private double mNodeRadius;

    private double mNeighbourGravity;

    private double mPreferredNeighbourDistance;

    private double mPreferredNeighbourDistanceHalf;

    private double mMaxPushDistance;

    FoliageLines(final double canvasSize, final boolean canChangeAlpha) {
        mCanvasSize = canvasSize;
        final double nodeSize = canvasSize / 300f;
        mNodeRadius = nodeSize * 0.5f;
        mNodeDensity = 10 + mRandom.nextInt(30);
        mNeighbourGravity = mNodeRadius * 0.5f;
        mMaxPushDistance = canvasSize * 0.1f;
        mDoubleJitter = mCanvasSize * 0.002f;
        mChangeAlpha = canChangeAlpha;

        mNumberOfNodes = NUM_OF_INITIAL_NODES;

        Log.d(
                TAG,
                "Initialized: node size: " + nodeSize
                        + ", rect mode: " + mPaintMode
                        + ", node density: " + mNodeDensity
        );
    }

    void setSymmetric(final boolean symmetric) {
        mSymmetric = symmetric;
    }

    void setRectMode(final int paintMode) {
        mPaintMode = paintMode;
    }

    FoliageLines initLine(final double x, final double y) {
        final double lineLength = random(mCanvasSize / 2);
        final double lineLengthHalf = lineLength / 2;
        final double lineAngle = 0;
        final float lineCos = (float) Math.cos(lineAngle);
        final float lineSin = (float) Math.sin(lineAngle);

        Log.d("Foliage()", "Line init. Line density: " + mNodeDensity);

        Line lastLine = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final double currentLength = (lineLength * ((i + 1f) / NUM_OF_INITIAL_NODES));

            final double lineX = x - lineLengthHalf + (lineCos * currentLength);
            final double lineY = y + (lineSin * currentLength);

            final Line line = new Line(lineX, lineY, lineLength / 2, lineAngle + (TWO_PI / 4));

            if (mFirstLine == null) {
                mFirstLine = line;
                lastLine = line;
            } else if (i == NUM_OF_INITIAL_NODES - 1) {
                mPreferredNeighbourDistance = line.distance(lastLine);
                mPreferredNeighbourDistanceHalf = mPreferredNeighbourDistance / 2;
                lastLine.mNext = line;
            } else {
                lastLine.mNext = line;
                lastLine = line;
            }

        }
        return this;
    }

    FoliageLines initCircle(final double x, final double y) {
        final double initialRadius = random(mCanvasSize * 0.01) + mCanvasSize * 0.05;

//        final boolean closedCircle = mRandom.nextInt(4) > 1;
        final boolean closedCircle = true;
        final double arcStart;
        final double arcEnd;
        if (closedCircle) {
            arcStart = 0f;
            arcEnd = TWO_PI;
        } else {
            arcStart = random(Math.PI);
            arcEnd = arcStart + random(Math.PI);
        }

        final double squeezeFactor = random(0.66) + 0.66;

        final double lineLength = initialRadius * 2;

        Log.d(TAG, "initCircle(): From " + arcStart + " to " + arcEnd + ", radius: " + initialRadius);

        Line lastLine = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {

            final double angleOfNode = arcStart + (arcEnd * ((i + 1.0) / NUM_OF_INITIAL_NODES));

            final double lineX = x
                    + (((float) Math.cos(angleOfNode) * initialRadius) * squeezeFactor)
                    + getDoubleJitter();
            final double lineY = y
                    + ((float) Math.sin(angleOfNode) * initialRadius)
                    + getDoubleJitter();

            final Line line = new Line(lineX, lineY, lineLength, TWO_PI / 4);

            if (mFirstLine == null) {
                mFirstLine = line;
                lastLine = line;
            } else if (i == NUM_OF_INITIAL_NODES - 1) {
                mPreferredNeighbourDistance = line.distance(lastLine);
                mPreferredNeighbourDistanceHalf = mPreferredNeighbourDistance / 2;
                lastLine.mNext = line;
                if (closedCircle) {   // Only connect full circles.
                    line.mNext = mFirstLine;
                }
            } else {
                lastLine.mNext = line;
                lastLine = line;
            }

        }

        return this;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint) {
        Line currentLine = mFirstLine;
        Line nextLine;

        do {
            nextLine = currentLine.mNext;
            if (nextLine == null) {
                break;
            }

//            debugDraw(canvas, paint, currentLine, nextLine);
            draw(canvas, paint, currentLine, nextLine);

            currentLine = nextLine;
        } while (!mStopped && currentLine != mFirstLine);
    }

    private void debugDraw(
            @NonNull Canvas canvas,
            @NonNull Paint paint,
            @NonNull Line line1,
            @NonNull Line line2
    ) {

    }

    private void draw(
            @NonNull Canvas canvas,
            @NonNull Paint paint,
            @NonNull Line line1,
            @NonNull Line line2
    ) {

//        canvas.drawLine(
//                (float) line1.mX[0],
//                (float) line1.mY[0],
//                (float) line1.mX[1],
//                (float) line1.mY[1],
//                paint
//        );
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x09444444);
        canvas.drawRect((float) line1.mX[0],(float)  line2.mY[0],(float)  line2.mX[1],(float)  line1.mY[1], paint);
    }

    @Override
    public boolean update(final boolean isTouching) {

        ++mAge;
        mStopped = false;

        int nodeCounter = 0;
        Line currentLine = mFirstLine;
        do {
            if (currentLine == null) {
                break;
            }

            currentLine.update(!isTouching);

            if (nodeCounter < ADD_NODE_LIMIT && (++nodeCounter % mNodeDensity == 0)) {
                addNodeNextTo(currentLine);
            }

            currentLine = currentLine.mNext;
        } while (!mStopped && currentLine != mFirstLine);

        return mAge < MAX_AGE;
    }

    private void addNodeNextTo(final Line line) {
        final Line oldNeighbour = line.mNext;
        if (oldNeighbour == null) {
            return;
        }

        final double newNeighbourX[] = new double[2];
        final double newNeighbourY[] = new double[2];

        for (int i = 0; i < 2; i++) {
            newNeighbourX[i] = (line.mX[i] + oldNeighbour.mX[i]) / 2;
            newNeighbourY[i] = (line.mY[i] + oldNeighbour.mY[i]) / 2;
        }

        final Line newNeighbour = new Line(newNeighbourX, newNeighbourY);

        line.mNext = newNeighbour;
        newNeighbour.mNext = oldNeighbour;
    }

//    protected static double angle(
//            final double x1,
//            final double y1,
//            final double x2,
//            final double y2
//    ) {
//        final double calcAngle = Math.atan2(
//                -(y1 - y2),
//                x2 - x1
//        );
//
//        if (calcAngle < 0) {
//            return calcAngle + TWO_PI;
//        } else {
//            return calcAngle;
//        }
//    }

    private class Line {

        private Line mNext;

        private double[] mX = new double[2];

        private double[] mY = new double[2];

        private Line(final double[] x, final double[] y) {
            mX = x;
            mY = y;
        }

        private Line(final double x, final double y, final double length, final double angle) {

            mX[0] = x;
            mY[0] = y;

            mX[1] = x + (Math.cos(angle) * length);
            mY[1] = y + (Math.sin(angle) * length);
        }

        @Override
        public String toString() {
            return "[Line " + super.toString() + " at " + mX[0] + ", " + mY[0]
                    + " to " + mX[1] + ", " + mY[1] + "]";
        }

        private double distance(final Line otherLine) {
            return (
                    Math.sqrt(
                    Math.pow((otherLine.mX[0] - mX[0]), 2) + Math.pow((otherLine.mY[0] - mY[0]), 2)
                ) + Math.sqrt(
                    Math.pow((otherLine.mX[1] - mX[1]), 2) + Math.pow((otherLine.mY[1] - mY[1]), 2)
                )
            ) / 2.0;
        }

        private double angle(final Line otherLine) {
            return (FoliageLines.angle(mX[0], mY[0], otherLine.mX[0], otherLine.mY[0])
                    + FoliageLines.angle(mX[1], mY[1], otherLine.mX[1], otherLine.mY[1])) / 2.0;
        }

        private void update(final boolean applyForces) {

            for (int i = 0; i < 2; i++) {
                mX[i] += getDoubleJitter();
                mY[i] += getDoubleJitter();
            }

            if (!applyForces) {
                return;
            }

            Line otherLine = mNext;

            double force = 0;
            double angle = 0;

            do {

                if (otherLine == null || otherLine.mNext == this) {
                    return;
                }

                final double distance = distance(otherLine);

                if (distance > mMaxPushDistance) {
                    otherLine = otherLine.mNext;
                    continue;
                }

                angle = angle(otherLine) + (angle * 0.05);

                force *= 0.05;

                if (otherLine == mNext) {

                    if (distance > mPreferredNeighbourDistance) {
//                        force = mPreferredNeighbourDistanceHalf;
                        force += (distance / PUSH_FORCE);
                    } else {
                        force -= mNeighbourGravity;
                    }

                } else {

                    if (distance < mNodeRadius) {
                        force -= mNodeRadius;
                    } else {
                        force -= (PUSH_FORCE / distance);
                    }

                }

                for (int i = 0; i < 2; i++) {
                    mX[i] += Math.cos(angle) * force;
                    mY[i] += Math.sin(angle) * force;
                }

                otherLine = otherLine.mNext;
            } while (!mStopped);
        }

    }

}
