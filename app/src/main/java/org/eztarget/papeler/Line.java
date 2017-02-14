package org.eztarget.papeler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Random;

/**
 * Created by michelsievers on 08/02/2017.
 */

public class Line {

    private static final int NUM_OF_INITIAL_NODES = 40;

//    private ArrayList<Node> mNodes = new ArrayList<>();

    private Node mFirstNode;

    private int mAge = 0;

    private float mCanvasSize;

    private float mNodeSize;

    private float mNodeRadius;

    private float mFavouriteNeighbourDistance;

    private float mMaxPushDistance;

    private float mJitter;

    private int mInsertionIndex = NUM_OF_INITIAL_NODES / 2;

    private Random mRandom = new Random();

    Line(final float x, final float y, final float canvasSize) {

        mCanvasSize = canvasSize;
        mNodeSize = 8f;
        mNodeRadius = mNodeSize * 0.5f;
        mFavouriteNeighbourDistance = mNodeSize * 1.5f;
        mMaxPushDistance = canvasSize * 0.2f;

        Log.d("Line()", "Node radius: " + mNodeRadius);
        Log.d("Line()", "Favourite neighbour distance: " + mFavouriteNeighbourDistance);

        final float initialRadius = random(mCanvasSize * 0.05f) + mCanvasSize * 0.03f;
//        final float initialAngle = random((float) Math.PI * 2);
        mJitter = mCanvasSize * 0.001f;

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();

            final float angleOfNode = (float) Math.PI * 2f * ((i + 1f) / NUM_OF_INITIAL_NODES);

            node.mX = x
                    + ((float) Math.cos(angleOfNode) * initialRadius)
                    + mJitter * 0.5f - random(mJitter);
            node.mY = y
                    + ((float) Math.sin(angleOfNode) * initialRadius)
                    + mJitter * 0.5f - random(mJitter);

//            Log.d("Line()", "New: " + node);

            if (mFirstNode == null) {
                mFirstNode = node;
                lastNode = node;
//                Log.d("Line()", "First node.");
            } else if (i == NUM_OF_INITIAL_NODES - 1) {
                lastNode.mNext = node;
                node.mNext = mFirstNode;
//                Log.d("Line()", "Connected last node to " + node.mNext + ".");
            } else {
                lastNode.mNext = node;
//                Log.d("Line()", "Connected node " + lastNode + " to " + lastNode.mNext + ".");
                lastNode = node;
            }

        }

    }

    void draw(
            @NonNull final Canvas canvas,
            @NonNull final Paint paint1,
            @NonNull final Paint paint2
    ) {
        Node currentNode = mFirstNode;
        do {
            final Node nextNode = currentNode.mNext;

            canvas.drawLine(currentNode.mX, currentNode.mY, nextNode.mX, nextNode.mY, paint1);
            canvas.drawCircle(currentNode.mX, currentNode.mY, mNodeRadius, paint2);

            currentNode = nextNode;
        } while (currentNode != mFirstNode);
    }

    boolean update(final boolean isTouching) {

        ++mAge;

        Node currentNode = mFirstNode;
        do {
            currentNode.update(!isTouching);
            currentNode = currentNode.mNext;
        } while (currentNode != mFirstNode);

//        if (!isTouching && mAge % 2 == 0) {
//            final Node node1 = mNodes.get(mInsertionIndex);
//            final Node node2 = mNodes.get(node1.mNeighbour2Id);
//
//            final Node newMiddleNode = new Node();
//
//            newMiddleNode.mId = mNodes.size();
//            newMiddleNode.mNeighbor1Id = node1.mId;
//            newMiddleNode.mNeighbour2Id = node2.mId;
//
//            node1.mNeighbour2Id = newMiddleNode.mId;
//            node2.mNeighbor1Id = newMiddleNode.mId;
//
//            newMiddleNode.mX = (node1.mX + node2.mX) / 2f;
//            newMiddleNode.mY = (node1.mY + node2.mY) / 2f;
//
//            mNodes.add(node1.mId, node1);
//            mNodes.add(node2.mId, node2);
//            mNodes.add(newMiddleNode.mId, newMiddleNode);
//
//            mInsertionIndex = node1.mNeighbour2Id;
//            Log.d("Line.update()", mInsertionIndex + " -> New: " + newMiddleNode + " between " + node1 + " and " + node2 + ".");
//        }

        return mAge < 20;
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

            return (float) Math.atan2(otherNode.mY - mY, otherNode.mX - mX);
        }

        private void update(final boolean applyForces) {

            mX += mJitter * 0.5f - random(mJitter);
            mY += mJitter * 0.5f - random(mJitter);

            if (!applyForces) {
                return;
            }

            Node otherNode = mNext;

            do {

                if (otherNode.mNext == this) {
                    return;
                }

                final float distance = distance(otherNode);
                final float angle = (float) Math.PI;

                final float force;
                if (otherNode == mNext || otherNode.mNext == this) {

                    force = mFavouriteNeighbourDistance - distance;
                    Log.d("Line.Node.update()", this.toString() + " is neighbour of " + otherNode.toString() + ".");

                    mX += (float) Math.cos(angle) * force;
                    mY += (float) Math.sin(angle) * force;

                    Log.d("Line.Node.update()", "Distance: " + distance + "; Force: " + force + "; Angle: " + angle + " --> " + mX + ", " + mY);

                } else if (distance > mMaxPushDistance) {
//                        Log.d("Line.update()", this.toString() + " is too far away from " + otherNode.toString() + ".");
                    force = 0f;
                } else {
                    force = 0f;
                }

                otherNode = otherNode.mNext;
            } while (true);
        }

    }

}
