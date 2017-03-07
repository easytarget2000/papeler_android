package org.eztarget.papeler.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

class Foliage extends Being {

    private static final String TAG = Foliage.class.getSimpleName();

    private static final int NUM_OF_INITIAL_NODES = 48;

    private static final int MAX_AGE = 80;

    private static final int ADD_NODE_LIMIT = 80;

    private static final double PUSH_FORCE = 16f;

    private Node mFirstNode;

    private double mCanvasSize;

    private boolean mSymmetric;

    static final int LINE_MODE = 0;

    static final int RECT_MODE = 1;

    static final int CIRCLE_MODE = 2;

    private int mPaintMode;

    private double mNodeDensity;

    private double mNodeRadius;

    private double mNeighbourGravity;

    private double mPreferredNeighbourDistance;

    private double mPreferredNeighbourDistanceHalf;

    private double mMaxPushDistance;

    Foliage(final double canvasSize) {
        mCanvasSize = canvasSize;
        final double nodeSize = canvasSize / 300f;
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
        final double sideLength = random(mCanvasSize * 0.15) + (mCanvasSize * 0.01);
        final double sideLengthHalf = sideLength * 0.5;

        Log.d(TAG, "initSquare()");

        final int quarterOfInitialNodes = NUM_OF_INITIAL_NODES / 4;

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();

            if (i < quarterOfInitialNodes) {
                node.mX = (x - sideLengthHalf) + (sideLength * ((double) i / quarterOfInitialNodes));
                node.mY = y - sideLengthHalf;
            } else if (i < quarterOfInitialNodes * 2) {
                node.mX = x + sideLengthHalf;
                node.mY = (y - sideLengthHalf)
                        + (sideLength * (((double) i - quarterOfInitialNodes) / quarterOfInitialNodes));
            } else if (i < quarterOfInitialNodes * 3) {
                node.mX = (x + sideLengthHalf) - (sideLength * (((double) i - (quarterOfInitialNodes * 2)) / quarterOfInitialNodes));
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
                mPreferredNeighbourDistanceHalf = mPreferredNeighbourDistance / 2;
                node.mNext = mFirstNode;
                lastNode.mNext = node;
            } else {
                lastNode.mNext = node;
                lastNode = node;
            }

        }

        return this;
    }

    Foliage initSine(final double x, final double y) {

        final double lineLength = random(mCanvasSize / 2);
        final double lineSinHeight = lineLength / 6;

        Log.d(TAG, "initSine()");

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();

            node.mX = x + (lineLength * ((i + 1.0) / NUM_OF_INITIAL_NODES));

            final double angleOfNode = TWO_PI * ((i + 1.0) / NUM_OF_INITIAL_NODES);
            node.mY = y + getJitterValue() + (Math.sin(angleOfNode) * lineSinHeight);

            if (mFirstNode == null) {
                mFirstNode = node;
                lastNode = node;
            } else if (i == NUM_OF_INITIAL_NODES - 1) {
                mPreferredNeighbourDistance = node.distance(lastNode);
                mPreferredNeighbourDistanceHalf = mPreferredNeighbourDistance / 2;
                lastNode.mNext = node;
            } else {
                lastNode.mNext = node;
                lastNode = node;
            }

        }
        return this;
    }

    Foliage initCircle(final double x, final double y) {
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

        Log.d(TAG, "initCircle(): From " + arcStart + " to " + arcEnd + ", radius: " + initialRadius);

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();

            final double angleOfNode = arcStart + (arcEnd * ((i + 1.0) / NUM_OF_INITIAL_NODES));

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
                mPreferredNeighbourDistanceHalf = mPreferredNeighbourDistance / 2;
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

    Foliage initLine(final double x, final double y) {
        final double lineLength = random(mCanvasSize / 2);
        final double lineAngle = random(TWO_PI);
        final float lineCos = (float) Math.cos(lineAngle);
        final float lineSin = (float) Math.sin(lineAngle);

        Log.d("Foliage()", "Line init. Node density: " + mNodeDensity);

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();
            final double currentLength = (lineLength * ((i + 1f) / NUM_OF_INITIAL_NODES));

            node.mX = x + (lineCos * currentLength);
            node.mY = y + (lineSin * currentLength);

            if (mFirstNode == null) {
                mFirstNode = node;
                lastNode = node;
            } else if (i == NUM_OF_INITIAL_NODES - 1) {
                mPreferredNeighbourDistance = node.distance(lastNode);
                mPreferredNeighbourDistanceHalf = mPreferredNeighbourDistance / 2;
                lastNode.mNext = node;
            } else {
                lastNode.mNext = node;
                lastNode = node;
            }

        }
        return this;
    }

    Foliage initPolygon(final double x, final double y) {

        final int numberOfEdges = 4;
        final int nodesPerEdge = NUM_OF_INITIAL_NODES / numberOfEdges;
        final double size = mCanvasSize / 5.0;

//        for (int edge = 1; edge <= numberOfEdges; edge++) {
//            final double angle = TWO_PI * edge / numberOfEdges;
//            final float x = (float) (x + radius * Math.cos(angle));
//            final float y = (float) (y + radius * Math.sin(angle));
//
//            if (edge == 1) {
//                polygonPath.moveTo(x, y);
//            } else {
//                polygonPath.lineTo(x, y);
//            }
//
//        }

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {

            final double edge = (i + 1) / nodesPerEdge;
            final double angleOfEdge1 = TWO_PI * edge / numberOfEdges;
            final double angleOfEdge2 = TWO_PI * (edge + 1) / numberOfEdges;

            final double edge1X = x + (size * Math.cos(angleOfEdge1));
            final double edge1Y = y + (size * Math.sin(angleOfEdge1));

            final double edge2X = x + (size * Math.cos(angleOfEdge2));
            final double edge2Y = y + (size * Math.sin(angleOfEdge2));

            final double angleBetweenEdges = angle(edge1X, edge1Y, edge2X, edge2Y);
            final double nodeRelativeToEdge1 = (i - ((edge - 1) * (double) nodesPerEdge)) / (double) nodesPerEdge;

            Log.d(TAG, "i: " + i + ", edge: " + edge + ", angleBetweenEdges: " + angleBetweenEdges + ", nodeRelativeToEdge1: " + nodeRelativeToEdge1);

            final Node node = new Node();
            node.mX = edge1X + (Math.cos(angleBetweenEdges) * nodeRelativeToEdge1);
            node.mY = edge1Y + (Math.sin(angleBetweenEdges) * nodeRelativeToEdge1);

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
                            (float) node1.mX,
                            (float) node1.mY,
                            (float) node2.mX,
                            (float) node2.mY,
                            paint
                    );
                    canvas.drawRect(
                            (float) (mCanvasSize - node1.mX),
                            (float) node1.mY,
                            (float) (mCanvasSize - node2.mX),
                            (float) node2.mY,
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

        } else {

            switch (mPaintMode) {
                case RECT_MODE:
                    paint.setAlpha(20);
                    canvas.drawRect(
                            (float) node1.mX,
                            (float) node1.mY,
                            (float) node2.mX,
                            (float) node2.mY,
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
                            (float) node1.mX,
                            (float) node1.mY,
                            (float) node2.mX,
                            (float) node2.mY,
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

        newNeighbour.mX = (node.mX + oldNeighbour.mX) / 2;
        newNeighbour.mY = (node.mY + oldNeighbour.mY) / 2;

        node.mNext = newNeighbour;
        newNeighbour.mNext = oldNeighbour;
    }

    private static double angle(
            final double x1,
            final double y1,
            final double x2,
            final double y2
    ) {
        final double calcAngle = Math.atan2(
                -(y1 - y2),
                x2 - x1
        );

        if (calcAngle < 0) {
            return calcAngle + TWO_PI;
        } else {
            return calcAngle;
        }
    }

    private class Node {

        private Node mNext;

        private double mX;

        private double mY;

        private Node() {
        }

        @Override
        public String toString() {
            return "[Node " + super.toString() + " at " + mX + ", " + mY + "]";
        }

        private double distance(final Node otherNode) {
            return Math.sqrt(
                    Math.pow((otherNode.mX - mX), 2) + Math.pow((otherNode.mY - mY), 2)
            );
        }

        private double angle(final Node otherNode) {
            return Foliage.angle(mX, mY, otherNode.mX, otherNode.mY);
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

                final double distance = distance(otherNode);

                if (distance > mMaxPushDistance) {
                    otherNode = otherNode.mNext;
                    continue;
                }

                final double angle = angle(otherNode);

                final double force;
                if (otherNode == mNext) {

                    if (distance > mPreferredNeighbourDistance) {
                        force = mPreferredNeighbourDistanceHalf;
                    } else {
                        force = -mNeighbourGravity;
                    }

                } else {

                    if (distance < mNodeRadius) {
                        force = -mNodeRadius;
                    } else {
                        force = -PUSH_FORCE / distance;
                    }
                }

                mX += Math.cos(angle) * force;
                mY += Math.sin(angle) * force;

                otherNode = otherNode.mNext;
            } while (!mStopped);
        }

    }

}
