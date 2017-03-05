package org.eztarget.papeler.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

class Foliage extends Being {

    private static final String TAG = Foliage.class.getSimpleName();

    private static final int NUM_OF_INITIAL_NODES = 48;

    private static final float TWO_PI = (float) Math.PI * 2f;

    private static final int MAX_AGE = 64;

    private static final int ADD_NODE_LIMIT = 96;

    private static final float PUSH_FORCE = 16f;

    private Node mFirstNode;

    private float mCanvasSize;

    private boolean mSymmetric;

    static final int LINE_MODE = 0;

    static final int RECT_MODE = 1;

    static final int CIRCLE_MODE = 2;

    private int mRectMode;

    private float mNodeSize;

    private float mNodeDensity;

    private float mNodeRadius;

    private float mNeighbourGravity;

    private float mPreferredNeighbourDistance;

    private float mPreferredNeighbourDistanceHalf;

    private float mMaxPushDistance;

    Foliage(final float canvasSize) {
        mCanvasSize = canvasSize;
        mNodeSize = canvasSize / 300f;
        mNodeRadius = mNodeSize * 0.5f;
        mNeighbourGravity = mNodeRadius * 0.5f;
        mMaxPushDistance = canvasSize * 0.1f;
        mJitter = mCanvasSize * 0.001f;

        Log.d(TAG, "Initialized: node size: " + mNodeSize + ", rect mode: " + mRectMode);
    }

    void setSymmetric(final boolean symmetric) {
        mSymmetric = symmetric;
    }

    void setRectMode(final int rectMode) {
        mRectMode = rectMode;
    }

    Foliage initSine(final float x, final float y) {

        final float lineLength = random(mCanvasSize * 0.5f);
        final float lineSinHeight = lineLength / 6f;

        mNodeDensity = 10 + mRandom.nextInt(30);

        Log.d("Foliage()", "Sine init. Node density: " + mNodeDensity);

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

    Foliage initInCircleShape(final float x, final float y) {
        final float initialRadius = random(mCanvasSize * 0.01f) + mCanvasSize * 0.05f;
        mNodeDensity = 10 + mRandom.nextInt(30);

        final boolean closedCircle = mRandom.nextBoolean();
        final float arcStart;
        final float arcEnd;
        if (closedCircle) {
            arcStart = 0f;
            arcEnd = TWO_PI;
        } else {
            arcStart = random(TWO_PI);
            arcEnd = random(TWO_PI);
        }

        Log.d("Foliage()", "Circle init. Node density: " + mNodeDensity);

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();

            final float angleOfNode = arcStart + (arcEnd * ((i + 1f) / NUM_OF_INITIAL_NODES));

            node.mX = x
                    + ((float) Math.cos(angleOfNode) * initialRadius)
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

        mNodeDensity = 10 + mRandom.nextInt(30);

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

            if (mSymmetric) {


                switch (mRectMode) {
                    case CIRCLE_MODE:
                        paint.setAlpha(20);

                        canvas.drawCircle(
                                currentNode.mX,
                                currentNode.mY,
                                mNodeRadius,
                                paint
                        );
                        canvas.drawCircle(
                                currentNode.mX,
                                currentNode.mY,
                                random(mNodeRadius * 8f),
                                paint
                        );
                        break;

                    default:
                        paint.setAlpha(32);

                        canvas.drawPoint(currentNode.mX, currentNode.mY, paint);
                        canvas.drawPoint(currentNode.mX, currentNode.mY + 1, paint);
                        canvas.drawPoint(currentNode.mX + 1, currentNode.mY + 1, paint);

                        canvas.drawPoint(mCanvasSize - currentNode.mX, currentNode.mY, paint);
                        canvas.drawPoint(mCanvasSize - currentNode.mX, currentNode.mY + 1, paint);
                        canvas.drawPoint(
                                mCanvasSize - currentNode.mX + 1,
                                currentNode.mY + 1,
                                paint
                        );
                }

            } else {


                switch (mRectMode) {
                    case RECT_MODE:
                        paint.setAlpha(20);
                        canvas.drawRect(
                                currentNode.mX,
                                currentNode.mY,
                                nextNode.mX,
                                nextNode.mY,
                                paint
                        );
                        break;
                    case CIRCLE_MODE:
                        paint.setAlpha(16);
                        canvas.drawCircle(
                                currentNode.mX,
                                currentNode.mY,
                                random(mNodeRadius * 8f),
                                paint
                        );
                        break;
                    default:
                        paint.setAlpha(40);

                        canvas.drawLine(
                                currentNode.mX,
                                currentNode.mY,
                                nextNode.mX,
                                nextNode.mY,
                                paint
                        );
                }

            }


            currentNode = nextNode;
        } while (!mStopped && currentNode != mFirstNode);
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
