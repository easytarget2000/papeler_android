package org.eztarget.papeler.engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.NonNull;
import android.util.Log;

class Jellyfish extends Being {

    private static final String TAG = Jellyfish.class.getSimpleName();

    private static final int NUM_OF_INITIAL_NODES = 64;

    private static final int MAX_AGE = 240;

    private static final int ADD_NODE_LIMIT = 24;

    private static final double PUSH_FORCE = 8f;

    private static final int INITIAL_FILLING_ALPHA = 5;

    private Node mFirstNode;

    private double mCanvasSize;

    private boolean mSymmetric;

    private int mPaintMode;

    private boolean mChangeAlpha = true;

    private double mNodeDensity;

    private double mNodeRadius;

    private double mNeighbourGravity;

    private double mPreferredNeighbourDistance;

    private double mMaxPushDistance;

    private boolean mPaintedInitialFilling = false;

//    private NewNode mSpecialNode;

    Jellyfish(final double canvasSize, final boolean canChangeAlpha) {
        mCanvasSize = canvasSize;
        final double nodeSize = canvasSize / 300f;
        mNodeRadius = nodeSize * 0.5f;
        mNodeDensity = 10 + mRandom.nextInt(30);
        mNeighbourGravity = mNodeRadius * 0.5f;
        mMaxPushDistance = canvasSize * 0.1f;
        mDoubleJitter = mCanvasSize * 0.002f;
        mChangeAlpha = canChangeAlpha;

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
//        if (mPaintMode == FLARE_MODE) {
//            mDoubleJitter *= 3;
//        }
    }

    Jellyfish initSquare(final float x, final float y) {
        final double sideLength = random(mCanvasSize * 0.15) + (mCanvasSize * 0.01);
        final double sideLengthHalf = sideLength * 0.5;

        Log.d(TAG, "initSquare()");

        final int quarterOfInitialNodes = NUM_OF_INITIAL_NODES / 4;

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();

            if (i < quarterOfInitialNodes) {
                node.mX = (x - sideLengthHalf)
                        + (sideLength * ((double) i / quarterOfInitialNodes));
                node.mY = y - sideLengthHalf;
            } else if (i < quarterOfInitialNodes * 2) {
                node.mX = x + sideLengthHalf;
                node.mY = (y - sideLengthHalf)
                        + (sideLength *
                        (((double) i - quarterOfInitialNodes) / quarterOfInitialNodes));
            } else if (i < quarterOfInitialNodes * 3) {
                node.mX = (x + sideLengthHalf)
                        - (sideLength *
                        (((double) i - (quarterOfInitialNodes * 2)) / quarterOfInitialNodes));
                node.mY = y + sideLengthHalf;
            } else {
                node.mX = x - sideLengthHalf;
                node.mY = (y + sideLengthHalf)
                        - (sideLength * (
                        ((double) i - (quarterOfInitialNodes * 3)) / quarterOfInitialNodes)
                );
            }

            if (mFirstNode == null) {
                mFirstNode = node;
                lastNode = node;
            } else if (i == NUM_OF_INITIAL_NODES - 1) {
                mPreferredNeighbourDistance = node.distance(lastNode);
                node.mNext = mFirstNode;
                lastNode.mNext = node;
            } else {
                lastNode.mNext = node;
                lastNode = node;
            }

        }

        return this;
    }

    private void initSpecialNode(final double x, final double y) {
//        mSpecialNode = new NewNode(x, y);
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint) {
        Node currentNode = mFirstNode;
        Node nextNode;

        final Path path = new Path();
        if (!mSymmetric) {
            path.moveTo((float) currentNode.mX, (float) currentNode.mY);
        }

        do {
            nextNode = currentNode.mNext;
            if (nextNode == null) {
                break;
            }

//            debugDraw(canvas, paint, currentNode, nextNode);
            if (mSymmetric) {
                drawSymmetric(canvas, paint, currentNode);
            } else {
                path.lineTo((float) nextNode.mX, (float) nextNode.mY);
            }

            currentNode = nextNode;
        } while (!mStopped && currentNode != mFirstNode);

        if (!mSymmetric) {
            path.close();

            if (!mPaintedInitialFilling) {
                final int lastAlpha = paint.getAlpha();
                paint.setStyle(Paint.Style.FILL);
                paint.setAlpha(INITIAL_FILLING_ALPHA);
                canvas.drawPath(path, paint);

                paint.setAlpha(lastAlpha);
                paint.setStyle(Paint.Style.STROKE);

                mPaintedInitialFilling = true;
            } else {
                canvas.drawPath(path, paint);
            }

        }

    }

    private void debugDraw(
            @NonNull Canvas canvas,
            @NonNull Paint paint,
            @NonNull Node node1,
            @NonNull Node node2
    ) {

    }

    private void drawSymmetric(
            @NonNull Canvas canvas,
            @NonNull Paint paint,
            @NonNull Node node1
    ) {

            if (mChangeAlpha) {
                paint.setAlpha(32);
            }

            canvas.drawPoint((float) node1.mX, (float) node1.mY, paint);
            canvas.drawPoint((float) node1.mX, (float) node1.mY + 1, paint);
            canvas.drawPoint((float) node1.mX + 1, (float) node1.mY + 1, paint);

            canvas.drawPoint((float) (mCanvasSize - node1.mX), (float) node1.mY, paint);
            canvas.drawPoint((float) (mCanvasSize - node1.mX), (float) node1.mY + 1, paint);
            canvas.drawPoint(
                    (float) (mCanvasSize - node1.mX + 1),
                    (float) node1.mY + 1,
                    paint
            );
    }

    @Override
    public boolean update(final boolean isTouching) {

        ++mAge;
        mStopped = false;

        int nodeCounter = 0;
        Node currentNode = mFirstNode;
        do {
            if (currentNode == null) {
                break;
            }

            currentNode.update(!isTouching);

            if (!isTouching && nodeCounter < ADD_NODE_LIMIT && (++nodeCounter % mNodeDensity == 0))
            {
                addNodeNextTo(currentNode);
            }

            currentNode = currentNode.mNext;
        } while (!mStopped && currentNode != mFirstNode);

//        mSpecialNode.update(!isTouching);

        return mAge < MAX_AGE;
    }

    private void addNodeNextTo(final Node node) {
        final Node oldNeighbour = node.mNext;
        if (oldNeighbour == null) {
            return;
        }

        final Node newNeighbour = new Node();

        newNeighbour.mX = (node.mX + oldNeighbour.mX) / 2;
        newNeighbour.mY = (node.mY + oldNeighbour.mY) / 2;

        node.mNext = newNeighbour;
        newNeighbour.mNext = oldNeighbour;
    }


    private class Node {

        protected Node mNext;

        protected double mX;

        protected double mY;

        protected Node() {

        }

        protected Node(final double x, final double y) {
            mX = x;
            mY = y;
        }

        @Override
        public String toString() {
            return "[Line " + super.toString() + " at " + mX + ", " + mY + "]";
        }

        private double distance(final Node otherNode) {
            return Jellyfish.distance(mX, mY, otherNode.mX, otherNode.mY);
        }

        private double angle(final Node otherNode) {
            return Jellyfish.angle(mX, mY, otherNode.mX, otherNode.mY);
        }

        private void update(final boolean applyForces) {

            mX += getDoubleJitter();
            mY += getDoubleJitter();

            if (!applyForces) {
                return;
            }

            updateAcceleration();

        }

        protected void updateAcceleration() {
            Node otherNode = mNext;

            double force = 0;
            double angle = 0;

            do {

                if (otherNode == null || otherNode.mNext == this) {
                    return;
                }

                final double distance = distance(otherNode);

                if (distance > mMaxPushDistance) {
                    otherNode = otherNode.mNext;
                    continue;
                }

                angle = angle(otherNode) + (angle * 0.05);

                force *= 0.05;

                if (otherNode == mNext) {

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

                mX += Math.cos(angle) * force;
                mY += Math.sin(angle) * force;

                otherNode = otherNode.mNext;
            } while (!mStopped);
        }

    }

    private class NewNode extends Node {

        private NewNode(final double x, final double y) {
            mX = x;
            mY = y;

//            mAccelerationScale = mCanvasSize * 0.01 * mRandom.nextFloat();
//            mAccelerationAngle = mRandom.nextFloat() * TWO_PI;
        }

        @Override
        protected void updateAcceleration() {
            mX *= 0.9;
            mY += mCanvasSize * 0.01;
        }
    }

}
