package org.eztarget.papeler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by michelsievers on 08/02/2017.
 */

public class Line {

    private static final int NUM_OF_INITIAL_NODES = 20;

    private ArrayList<Node> mNodes = new ArrayList<>();

    private int mAge = 0;

    private float mCanvasSize;

    private float mJitter;

    private Random mRandom = new Random();

    Line(final float x, final float y, final float canvasSize) {

        mCanvasSize = canvasSize;

        final float initialRadius = random(mCanvasSize * 0.01f) + mCanvasSize * 0.01f;
//        final float initialAngle = random((float) Math.PI * 2);
        mJitter = mCanvasSize * 0.001f;

        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node(mCanvasSize);

            node.mId = i;

            // Close the circle.
            node.mNeighbor1Id = i > 0 ? i - 1 : (NUM_OF_INITIAL_NODES - 1) - 1;
            node.mNeighbour2Id = i;

            final float angleOfNode = (float) Math.PI * 2f * ((i + 1f) / NUM_OF_INITIAL_NODES);
//            Log.d("Line()", angleOfNode + "");

            node.mX = x + ((float) Math.cos(angleOfNode) * initialRadius);
            node.mY = y + ((float) Math.sin(angleOfNode) * initialRadius);
            mNodes.add(i, node);
        }

        Log.d("Line()", "Initial Nodes: " + Arrays.toString(mNodes.toArray()));
    }

    void draw(
            @NonNull final Canvas canvas,
            @NonNull final Paint paint1,
            @NonNull final Paint paint2
    ) {

//        canvas.drawCircle(100f, 100f, 100f, paint);

        for (final Node node : mNodes) {
            final Node neighborNode = mNodes.get(node.mNeighbor1Id);
            canvas.drawLine(node.mX, node.mY, neighborNode.mX, neighborNode.mY, paint1);
            canvas.drawCircle(node.mX, node.mY, 4f, paint2);
//            canvas.drawPoint(lastNode.mX, lastNode.mY, paint2);
//            canvas.drawCircle(node.mX, node.mY, 4f, paint1);
        }
    }

    boolean update(final boolean isTouching) {

        ++mAge;

        final int numberOfPoints = mNodes.size();

        final Node[] pointsArray = new Node[numberOfPoints];
        mNodes.toArray(pointsArray);

        for (final Node node : mNodes) {
            node.update(pointsArray, mJitter, !isTouching);
        }

        if (!isTouching && mAge % 5 == 0) {
            final int randomIndex = mRandom.nextInt(mNodes.size() - 1);
            final Node node1 = mNodes.get(randomIndex);
            final Node node2 = mNodes.get(randomIndex + 1);

            final Node newMiddleNode = new Node(mCanvasSize);

            newMiddleNode.mId = mNodes.size();
            newMiddleNode.mNeighbor1Id = randomIndex;
            newMiddleNode.mNeighbour2Id = randomIndex + 1;

            newMiddleNode.mX = (node1.mX + node2.mX) / 2f;
            newMiddleNode.mY = (node1.mY + node2.mY) / 2f;

            mNodes.add(newMiddleNode.mId, newMiddleNode);
//            Log.d("Line.update()", "New: " + newMiddleNode.toString() + " between " + node1 + " and " + node2 + ".");
        }

        return mAge < 100;
    }

    private static float random(final float maxValue) {
        return (float) Math.random() * maxValue;
    }

    private class Node {

        private float mMaxPushDistance;

        private float mFavouriteNeighbourDistance;

        private int mId;

        private int mNeighbor1Id;

        private int mNeighbour2Id;

        private float mX;

        private float mY;

        private Node(final float canvasSize) {
            mMaxPushDistance = canvasSize * 0.3f;
            mFavouriteNeighbourDistance = canvasSize * 0.1f;
        }

        @Override
        public String toString() {
            return "[Node " + mId + " at " + mX + ", " + mY + "]";
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

        private void update(@NonNull final Node[] nodes, final float jitter, final boolean push) {
            for (final Node otherNode : nodes) {

                if (mId == otherNode.mId) {
                    continue;
                }

//                mX += jitter * 0.5f - random(jitter);
//                mY += jitter * 0.5f - random(jitter);

                if (push) {

                    final float distance = distance(otherNode);

//                    Log.d("Line.update()", "Before: " + mId + ": " + mX + ", " + mY + ", " + otherNode.mId + ": " + otherNode.mX + ", " + otherNode.mY + ", distance: " + distance);

                    final float force;
                    if (otherNode.mId == mNeighbor1Id || otherNode.mId == mNeighbour2Id) {
                        if (distance > mFavouriteNeighbourDistance) {
                            force = -distance * 0.1f;
                        } else {
                            return;
                        }
                    } else if (distance > mMaxPushDistance) {
                        return;
                    } else {
                        force = 1f / distance;
                    }

//                    Log.d("Line.update()", "Before: force: " + force);

                    if (otherNode.mX - mX > 0) {
                        mX -= force;
                    } else {
                        mX += force;
                    }

                    if (otherNode.mY - mY > 0) {
                        mY -= force;
                    } else {
                        mY += force;
                    }
                }

//                Log.d("Line.update()", "After: " + mId +": " + mX + ", " + mY);
            }
        }

    }

}
