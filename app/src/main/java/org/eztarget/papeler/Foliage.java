package org.eztarget.papeler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Random;

/**
 * Created by michelsievers on 08/02/2017.
 */

public class Foliage {

    private static final int NUM_OF_INITIAL_NODES = 40;

    private static final float TWO_PI = (float) Math.PI * 2f;

    private static final int MAX_AGE = 78;

    private static final int ADD_NODE_LIMIT = 128;

    private static final float PUSH_FORCE = 16f;

    private Node mFirstNode;

    private int mAge = 0;

    private float mCanvasSize;

    private boolean mSymmetric;

    private int mShape;

    private float mNodeSize;

    private float mNodeDensity;

    private float mNodeRadius;

    private float mNeighbourGravity;

    private float mPreferredNeighbourDistance;

    private float mPreferredNeighbourDistanceHalf;

    private float mMaxPushDistance;

    private float mJitter;

    private int mInsertionIndex = NUM_OF_INITIAL_NODES / 2;

    private boolean mStopped = true;

    private Random mRandom = new Random();

    private Foliage(final float canvasSize, final boolean symmetric) {
        mCanvasSize = canvasSize;
        mNodeSize = canvasSize / 300f;
        mNodeRadius = mNodeSize * 0.5f;
        mNeighbourGravity = mNodeRadius * 0.5f;
        mMaxPushDistance = canvasSize * 0.1f;
        mJitter = mCanvasSize * 0.001f;

        mSymmetric = symmetric;
    }

    static Foliage lineInstance(
            final float x,
            final float y,
            final boolean symmetric,
            final float canvasSize
    ) {
        return new Foliage(canvasSize, symmetric).initLine(x, y);
    }

    static Foliage circleInstance(
            final float x,
            final float y,
            final boolean symmetric,
            final float canvasSize
    ) {
        return new Foliage(canvasSize, symmetric).initInCircleShape(x, y);
    }

    private Foliage initLine(final float x, final float y) {

        final float lineLength = random(mCanvasSize * 0.3f);
        final float lineSinHeight = lineLength / 3f;

        mNodeDensity = 10 + mRandom.nextInt(30);

        Log.d("Foliage()", "Line init. Node density: " + mNodeDensity);

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
//                node.mNext = mFirstNode;
            } else {
                lastNode.mNext = node;
                lastNode = node;
            }

        }
        return this;
    }

    private Foliage initInCircleShape(final float x, final float y) {
        final float initialRadius = random(mCanvasSize * 0.01f) + mCanvasSize * 0.05f;
        mNodeDensity = 10 + mRandom.nextInt(30);

        Log.d("Foliage()", "Circle init. Node density: " + mNodeDensity);

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();

            final float angleOfNode = TWO_PI * ((i + 1f) / NUM_OF_INITIAL_NODES);

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
                node.mNext = mFirstNode;
            } else {
                lastNode.mNext = node;
                lastNode = node;
            }

        }

        return this;
    }

    void draw(
            @NonNull final Canvas canvas,
            @NonNull final Paint paint1,
            @NonNull final Paint paint2
    ) {
        Node currentNode = mFirstNode;
        do {
            final Node nextNode = currentNode.mNext;
            if (nextNode == null) {
                break;
            }

            if (mSymmetric) {
                canvas.drawPoint(currentNode.mX, currentNode.mY, paint1);
                canvas.drawPoint(currentNode.mX, currentNode.mY + 1, paint2);
                canvas.drawPoint(currentNode.mX + 1, currentNode.mY + 1, paint2);

                canvas.drawPoint(mCanvasSize - currentNode.mX, currentNode.mY, paint1);
                canvas.drawPoint(mCanvasSize - currentNode.mX, currentNode.mY + 1, paint1);
                canvas.drawPoint(mCanvasSize - currentNode.mX + 1, currentNode.mY + 1, paint2);
            } else {

//                if (mShape == 0) {
                    canvas.drawLine(
                            currentNode.mX,
                            currentNode.mY,
                            nextNode.mX,
                            nextNode.mY,
                            paint1
                    );
//                } else {
                    canvas.drawRect(
                            currentNode.mX,
                            currentNode.mY,
                            nextNode.mX,
                            nextNode.mY,
                            paint2
                    );
//                }
//                canvas.drawPoint(currentNode.mX, currentNode.mY, paint2);
//                canvas.drawPoint(currentNode.mX + 1, currentNode.mY + 1, paint2);
            }


            currentNode = nextNode;
        } while (!mStopped && currentNode != mFirstNode);
    }

    boolean update(final boolean isTouching) {

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

    void stopPerforming() {
        mStopped = true;
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

    private float getJitterValue() {
        return mJitter * 0.5f - random(mJitter);
    }

    private static float random(final float maxValue) {
        return (float) Math.random() * maxValue;
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
