package org.eztarget.papeler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by michelsievers on 08/02/2017.
 */

public class Line {

    private static final int NUM_OF_INITIAL_NODES = 10;

    private ArrayList<Node> mNodes = new ArrayList<>();

    private int mAge = 0;

    private float mJitter;

    private Random mRandom = new Random();

    Line(final float x, final float y, final float canvasSize) {

        final float initialLength = random(canvasSize * 0.8f);
        final float initialAngle = random((float) Math.PI * 2);
        mJitter = canvasSize * 0.03f;

        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node(i);
            final float posOnLine = initialLength * ((float) i / NUM_OF_INITIAL_NODES);
            node.mX = x + (float) Math.cos(initialAngle) * posOnLine;
            node.mY = y + (float) Math.sin(initialAngle) * posOnLine;
            mNodes.add(node);
        }

    }

    void draw(
            @NonNull final Canvas canvas,
            @NonNull final Paint paint1,
            @NonNull final Paint paint2
    ) {

//        canvas.drawCircle(100f, 100f, 100f, paint);

        Node lastNode = null;
        for (final Node node : mNodes) {
            if (lastNode != null) {
                canvas.drawLine(lastNode.mX, lastNode.mY, node.mX, node.mY, paint1);
            }
            lastNode = node;
            canvas.drawCircle(lastNode.mX, lastNode.mY, random(mJitter), paint2);
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

        if (mAge % 10 == 0 && !isTouching) {
            final int randomIndex = mRandom.nextInt(mNodes.size() - 1);
            final Node node1 = mNodes.get(randomIndex);
            final Node node2 = mNodes.get(randomIndex + 1);

            final Node newMiddleNode = new Node(numberOfPoints);
            newMiddleNode.mX = (node1.mX + node2.mX) / 2f;
            newMiddleNode.mY = (node1.mY + node2.mY) / 2f;
            mNodes.add(newMiddleNode);
//            Log.d("Line.update()", "New: " + newMiddleNode.mId);
        }

        return mAge < 500;
    }

    private static float random(final float maxValue) {
        return (float) Math.random() * maxValue;
    }

    private class Node {

        private float mMaxPushDistance = 100f;

        private float mFavouriteNeighbourDistance = 10f;

        private int mId;

        private float mX;

        private float mY;

        private Node(final int id) {
            mId = id;
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

                if (push) {

                    final float distance = distance(otherNode);

//                    Log.d("Line.update()", "Before: " + mId + ": " + mX + ", " + mY + ", " + otherNode.mId + ": " + otherNode.mX + ", " + otherNode.mY + ", distance: " + distance);

                    final float force;
                    if (otherNode.mId == mId + 1 || otherNode.mId == mId - 1) {
                        force = (mFavouriteNeighbourDistance - distance) / 10f;
                    } else if (distance > mMaxPushDistance) {
                        return;
                    } else {
                        force = 2f / distance;
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
//
                mX += jitter * 0.5f - random(jitter);
                mY += jitter * 0.5f - random(jitter);

//                Log.d("Line.update()", "After: " + mId +": " + mX + ", " + mY);
            }
        }

    }

}
