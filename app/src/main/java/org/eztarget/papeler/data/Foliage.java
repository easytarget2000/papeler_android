package org.eztarget.papeler.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

class Foliage extends Being {

    private static final String TAG = Foliage.class.getSimpleName();

    private static final int NUM_OF_INITIAL_NODES = 48;

    private static final float PI = (float) Math.PI;

    private static final float TWO_PI = PI * 2f;

    private static final int MAX_AGE = 80;

    private static final int ADD_NODE_LIMIT = 80;

    private static final float PUSH_FORCE = 16f;

    private Node mFirstNode;

    private float mCanvasSize;

    private boolean mSymmetric;

    static final int LINE_MODE = 0;

    static final int RECT_MODE = 1;

    static final int CIRCLE_MODE = 2;

    private int mPaintMode;

    private float mNodeDensity;

    private float mNodeRadius;

    private float mNeighbourGravity;

    private float mPreferredNeighbourDistance;

    private float mPreferredNeighbourDistanceHalf;

    private float mMaxPushDistance;

    Foliage(final float canvasSize) {
        mCanvasSize = canvasSize;
        final float nodeSize = canvasSize / 300f;
        mNodeRadius = nodeSize * 0.5f;
        mNodeDensity = 10 + mRandom.nextInt(30);
        mNeighbourGravity = mNodeRadius * 0.5f;
        mMaxPushDistance = canvasSize * 0.1f;
        mJitter = mCanvasSize * 0.001f;

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

    void setRectMode(final int rectMode) {
        mPaintMode = rectMode;
    }

    Foliage initSquare(final float x, final float y) {
        final float sideLength = random(mCanvasSize * 0.15f) + (mCanvasSize * 0.01f);
        final float sideLengthHalf = sideLength * 0.5f;

        Log.d(TAG, "initSquare()");

        final int quarterOfInitialNodes = NUM_OF_INITIAL_NODES / 4;

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();

            if (i < quarterOfInitialNodes) {
                node.mX = (x - sideLengthHalf) + (sideLength * ((float) i / quarterOfInitialNodes));
                node.mY = y - sideLengthHalf;
            } else if (i < quarterOfInitialNodes * 2) {
                node.mX = x + sideLengthHalf;
                node.mY = (y - sideLengthHalf)
                        + (sideLength * (((float) i - quarterOfInitialNodes) / quarterOfInitialNodes));
            } else if (i < quarterOfInitialNodes * 3) {
                node.mX = (x + sideLengthHalf) - (sideLength * (((float) i - (quarterOfInitialNodes * 2)) / quarterOfInitialNodes));
                node.mY = y + sideLengthHalf;
            } else {
                node.mX = x - sideLengthHalf;
                node.mY = (y + sideLengthHalf)
                        - (sideLength * (
                        ((float) i - (quarterOfInitialNodes * 3)) / quarterOfInitialNodes)
                );
            }

            if (mFirstNode == null) {
                mFirstNode = node;
                lastNode = node;
            } else if (i == NUM_OF_INITIAL_NODES - 1) {
                mPreferredNeighbourDistance = node.distance(lastNode);
                mPreferredNeighbourDistanceHalf = mPreferredNeighbourDistance * 0.5f;
                node.mNext = mFirstNode;
                lastNode.mNext = node;
            } else {
                lastNode.mNext = node;
                lastNode = node;
            }

        }

        return this;
    }

    Foliage initSine(final float x, final float y) {

        final float lineLength = random(mCanvasSize * 0.5f);
        final float lineSinHeight = lineLength / 6f;

        Log.d(TAG, "initSine()");

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();

            node.mX = x + (lineLength * ((i + 1f) / NUM_OF_INITIAL_NODES));

            final float angleOfNode = 2f * TWO_PI * ((i + 1f) / NUM_OF_INITIAL_NODES);
            node.mY = y + getJitterValue() + ((float) Math.sin(angleOfNode) * lineSinHeight);

            if (mFirstNode == null) {
                mFirstNode = node;
                lastNode = node;
            } else if (i == NUM_OF_INITIAL_NODES - 1) {
                mPreferredNeighbourDistance = node.distance(lastNode);
                mPreferredNeighbourDistanceHalf = mPreferredNeighbourDistance * 0.5f;
                lastNode.mNext = node;
            } else {
                lastNode.mNext = node;
                lastNode = node;
            }

        }
        return this;
    }

    Foliage initCircle(final float x, final float y) {
        final float initialRadius = random(mCanvasSize * 0.01f) + mCanvasSize * 0.05f;

//        final boolean closedCircle = mRandom.nextInt(4) > 1;
        final boolean closedCircle = true;
        final float arcStart;
        final float arcEnd;
        if (closedCircle) {
            arcStart = 0f;
            arcEnd = TWO_PI;
        } else {
            arcStart = random(PI);
            arcEnd = arcStart + random(PI);
        }

        final float squeezeFactor = random(0.66f) + 0.66f;

        Log.d(TAG, "initCircle(): From " + arcStart + " to " + arcEnd + ", radius: " + initialRadius);

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();

            final float angleOfNode = arcStart + (arcEnd * ((i + 1f) / NUM_OF_INITIAL_NODES));

            node.mX = x
                    + (((float) Math.cos(angleOfNode) * initialRadius) * squeezeFactor)
                    + getJitterValue();
            node.mY = y
                    + ((float) Math.sin(angleOfNode) * initialRadius)
                    + getJitterValue();

            if (mFirstNode == null) {
                mFirstNode = node;
                lastNode = node;
            } else if (i == NUM_OF_INITIAL_NODES - 1) {
                mPreferredNeighbourDistance = node.distance(lastNode);
                mPreferredNeighbourDistanceHalf = mPreferredNeighbourDistance * 0.5f;
                lastNode.mNext = node;
                if (closedCircle) {   // Only connect full circles.
                    node.mNext = mFirstNode;
                }
            } else {
                lastNode.mNext = node;
                lastNode = node;
            }

        }

        return this;
    }

    Foliage initLine(final float x, final float y) {
        final float lineLength = random(mCanvasSize * 0.5f);
        final float lineAngle = random(TWO_PI);
        final float lineCos = (float) Math.cos(lineAngle);
        final float lineSin = (float) Math.sin(lineAngle);

        Log.d("Foliage()", "Line init. Node density: " + mNodeDensity);

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();
            final float currentLength = (lineLength * ((i + 1f) / NUM_OF_INITIAL_NODES));

            node.mX = x + (lineCos * currentLength);
            node.mY = y + (lineSin * currentLength);

            if (mFirstNode == null) {
                mFirstNode = node;
                lastNode = node;
            } else if (i == NUM_OF_INITIAL_NODES - 1) {
                mPreferredNeighbourDistance = node.distance(lastNode);
                mPreferredNeighbourDistanceHalf = mPreferredNeighbourDistance * 0.5f;
                lastNode.mNext = node;
            } else {
                lastNode.mNext = node;
                lastNode = node;
            }

        }
        return this;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint) {
        Node currentNode = mFirstNode;
        Node nextNode;

        do {
            nextNode = currentNode.mNext;
            if (nextNode == null) {
                break;
            }

            draw(canvas, paint, currentNode, nextNode);

            currentNode = nextNode;
        } while (!mStopped && currentNode != mFirstNode);
    }

    private void draw(
            @NonNull Canvas canvas,
            @NonNull Paint paint,
            @NonNull Node node1,
            @NonNull Node node2
    ) {

        if (mSymmetric) {

            switch (mPaintMode) {
                case RECT_MODE:
                    canvas.drawRect(
                            node1.mX,
                            node1.mY,
                            node2.mX,
                            node2.mY,
                            paint
                    );
                    canvas.drawRect(
                            mCanvasSize - node1.mX,
                            node1.mY,
                            mCanvasSize - node2.mX,
                            node2.mY,
                            paint
                    );
                    break;

//                case CIRCLE_MODE:
//                    paint.setAlpha(20);
//                    canvas.drawCircle(
//                            node1.mX,
//                            node1.mY,
//                            mNodeRadius,
//                            paint
//                    );
//                    canvas.drawCircle(
//                            node1.mX,
//                            node1.mY,
//                            random(mNodeRadius * 8f),
//                            paint
//                    );
//                    break;

                default:
                    paint.setAlpha(32);

                    canvas.drawPoint(node1.mX, node1.mY, paint);
                    canvas.drawPoint(node1.mX, node1.mY + 1, paint);
                    canvas.drawPoint(node1.mX + 1, node1.mY + 1, paint);

                    canvas.drawPoint(mCanvasSize - node1.mX, node1.mY, paint);
                    canvas.drawPoint(mCanvasSize - node1.mX, node1.mY + 1, paint);
                    canvas.drawPoint(
                            mCanvasSize - node1.mX + 1,
                            node1.mY + 1,
                            paint
                    );
            }

        } else {

            switch (mPaintMode) {
                case RECT_MODE:
                    paint.setAlpha(20);
                    canvas.drawRect(
                            node1.mX,
                            node1.mY,
                            node2.mX,
                            node2.mY,
                            paint
                    );
                    break;
//                case CIRCLE_MODE:
//                    paint.setAlpha(16);
//                    canvas.drawCircle(
//                            node1.mX,
//                            node1.mY,
//                            random(mNodeRadius * 8f),
//                            paint
//                    );
//                    break;

                default:
                    paint.setAlpha(40);

                    canvas.drawLine(
                            node1.mX,
                            node1.mY,
                            node2.mX,
                            node2.mY,
                            paint
                    );
            }

        }
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

            if (nodeCounter < ADD_NODE_LIMIT && (++nodeCounter % mNodeDensity == 0)) {
                addNodeNextTo(currentNode);
            }

            currentNode = currentNode.mNext;
        } while (!mStopped && currentNode != mFirstNode);

        return mAge < MAX_AGE;
    }

    private void addNodeNextTo(final Node node) {
        final Node oldNeighbour = node.mNext;
        if (oldNeighbour == null) {
            return;
        }

        final Node newNeighbour = new Node();

        newNeighbour.mX = (node.mX + oldNeighbour.mX) * 0.5f;
        newNeighbour.mY = (node.mY + oldNeighbour.mY) * 0.5f;

        node.mNext = newNeighbour;
        newNeighbour.mNext = oldNeighbour;
    }


    private class Node {

        private Node mNext;

        private float mX;

        private float mY;

        private Node() {
        }

        @Override
        public String toString() {
            return "[Node " + super.toString() + " at " + mX + ", " + mY + "]";
        }

        private float distance(final Node otherNode) {
            return (float) Math.sqrt(
                    Math.pow(
                            (double) (otherNode.mX - mX),
                            2
                    )
                            + Math.pow(
                            (double) (otherNode.mY - mY),
                            2
                    )
            );
        }

        private float angle(final Node otherNode) {

            final float calcAngle = (float) Math.atan2(
                    -(mY - otherNode.mY),
                    otherNode.mX - mX
            );

            if (calcAngle < 0) {
                return calcAngle + TWO_PI;
            } else {
                return calcAngle;
            }
        }

        private void update(final boolean applyForces) {

            mX += getJitterValue();
            mY += getJitterValue();

            if (!applyForces) {
                return;
            }

            Node otherNode = mNext;

            do {

                if (otherNode == null || otherNode.mNext == this) {
                    return;
                }

                final float distance = distance(otherNode);

                if (distance > mMaxPushDistance) {
                    otherNode = otherNode.mNext;
                    continue;
                }

                final float angle = angle(otherNode);

                final float force;
                if (otherNode == mNext) {

                    if (distance > mPreferredNeighbourDistance) {
                        force = mPreferredNeighbourDistanceHalf;
                    } else {
                        force = -mNeighbourGravity;
                    }

//                    Log.d("Foliage.Node.update()", this.toString() + " is neighbour of " + otherNode.toString() + ". Preferred distance: " + mPreferredNeighbourDistance);
//                    Log.d("Foliage.Node.update()", "Distance: " + distance + "; Force: " + force + "; Angle: " + angle + " --> " + mX + ", " + mY);

                } else {

                    if (distance < mNodeRadius) {
                        force = -mNodeRadius;
                    } else {
                        force = -PUSH_FORCE / distance;
                    }
//                    force = -mNodeRadius * 0.5f;
                }

                mX += (float) Math.cos(angle) * force;
                mY += (float) Math.sin(angle) * force;

                otherNode = otherNode.mNext;
            } while (!mStopped);
        }

    }

}
