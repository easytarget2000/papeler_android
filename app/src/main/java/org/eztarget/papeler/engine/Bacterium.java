package org.eztarget.papeler.engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.util.Log;

class Bacterium extends Being {

    private static final String TAG = Bacterium.class.getSimpleName();

    private static final int NUM_OF_INITIAL_NODES = 48;

    private static final int MAX_AGE = 100;

    private static final int ADD_NODE_LIMIT = 80;

    private static final double MAX_PUSH_FORCE = 16f;

    private Node mFirstNode;

    private double mCanvasSize;

    private double mNodeDensity;

    private double mNodeRadius;

    private double mNeighbourGravity;

    private double mPreferredNeighbourDistance;

    private double mMaxPushDistance;

    private double mPushForce;

    Bacterium(final double canvasSize) {
        mCanvasSize = canvasSize;
        final double nodeSize = canvasSize / 300f;
        mNodeRadius = nodeSize * 0.5f;
        mNodeDensity = 10 + mRandom.nextInt(30);
        mNeighbourGravity = mNodeRadius * 0.5f;
        mMaxPushDistance = canvasSize * 0.1f;
        mDoubleJitter = mCanvasSize * 0.002f;

        Log.d(
                TAG,
                "Initialized: node size: " + nodeSize
                        + ", node density: " + mNodeDensity
        );
    }

    Bacterium initCircle(final double x, final double y) {
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

        initSpecialNode(x, y);

        final double squeezeFactor = random(0.66) + 0.66;

        Log.d(TAG, "initCircle(): From " + arcStart + " to " + arcEnd + ", radius: " + initialRadius);

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {
            final Node node = new Node();

            final double angleOfNode = arcStart + (arcEnd * ((i + 1.0) / NUM_OF_INITIAL_NODES));

            node.mX = x
                    + (((float) Math.cos(angleOfNode) * initialRadius) * squeezeFactor)
                    + getDoubleJitter();
            node.mY = y
                    + ((float) Math.sin(angleOfNode) * initialRadius)
                    + getDoubleJitter();

            if (mFirstNode == null) {
                mFirstNode = node;
                lastNode = node;
            } else if (i == NUM_OF_INITIAL_NODES - 1) {
                mPreferredNeighbourDistance = node.distance(lastNode);
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

    Bacterium initPolygon(final double x, final double y) {

        final int numberOfEdges = mRandom.nextInt(5) + 3;
        final int nodesPerEdge = NUM_OF_INITIAL_NODES / numberOfEdges;
        final double size = random(mCanvasSize / 8.0);

        final double polygonAngle = random(TWO_PI);

        initSpecialNode(x, y);

        Node lastNode = null;
        for (int i = 0; i < NUM_OF_INITIAL_NODES; i++) {

            final double edge = i / nodesPerEdge;
            final double angleOfEdge1 = polygonAngle + (TWO_PI * edge / numberOfEdges);
            final double angleOfEdge2 = polygonAngle + (TWO_PI * (edge + 1) / numberOfEdges);

            final double edge1X = x + (size * Math.cos(angleOfEdge1));
            final double edge1Y = y + (size * Math.sin(angleOfEdge1));

            final double edge2X = x + (size * Math.cos(angleOfEdge2));
            final double edge2Y = y + (size * Math.sin(angleOfEdge2));

            final double angleBetweenEdges = angle(edge1X, edge1Y, edge2X, edge2Y);
            final double nodeRelativeToEdge1;
            nodeRelativeToEdge1 = (i - (edge * (double) nodesPerEdge)) / (double) nodesPerEdge;

            final Node node = new Node();
            node.mX = edge1X + (Math.cos(angleBetweenEdges) * nodeRelativeToEdge1 * size);
            node.mY = edge1Y + (Math.sin(angleBetweenEdges) * nodeRelativeToEdge1 * size);

            if (mFirstNode == null) {
                mFirstNode = node;
                lastNode = node;
            } else if (i == NUM_OF_INITIAL_NODES - 1) {
                mPreferredNeighbourDistance = node.distance(lastNode);
                lastNode.mNext = node;
                node.mNext = mFirstNode;
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
        path.moveTo((float) currentNode.mX, (float) currentNode.mY);

        do {
            nextNode = currentNode.mNext;
            if (nextNode == null) {
                break;
            }

//            debugDraw(canvas, paint, currentNode, nextNode);
            path.lineTo((float) nextNode.mX, (float) nextNode.mY);

            currentNode = nextNode;
        } while (!mStopped && currentNode != mFirstNode);

        path.close();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
    }

    private void debugDraw(
            @NonNull Canvas canvas,
            @NonNull Paint paint,
            @NonNull Node node1,
            @NonNull Node node2
    ) {

    }

    @Override
    public boolean update(final boolean isTouching) {

        ++mAge;
//        mPushForce = MAX_PUSH_FORCE * ((MAX_AGE - (double) mAge) / MAX_AGE);
        mPushForce = MAX_PUSH_FORCE * ((mAge + 1.0) / (double) MAX_AGE);
        mStopped = false;

        int nodeCounter = 0;
        Node currentNode = mFirstNode;
        do {
            if (currentNode == null) {
                break;
            }

            currentNode.update(!isTouching);

            if (!isTouching && nodeCounter < ADD_NODE_LIMIT && (++nodeCounter % mNodeDensity == 0)) {
//                addNodeNextTo(currentNode);
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

    private class Node {

        private Node mNext;

        private double mX;

        private double mY;

        private Node() {

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
            return Bacterium.distance(mX, mY, otherNode.mX, otherNode.mY);
        }

        private double angle(final Node otherNode) {
            return Bacterium.angle(mX, mY, otherNode.mX, otherNode.mY);
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
                        force += (distance / mPushForce);
                    } else {
                        force -= mNeighbourGravity;
                    }

                } else {

                    if (distance < mNodeRadius) {
                        force -= mNodeRadius;
                    } else {
                        force -= (mPushForce / distance);
                    }

                }

                mX += Math.cos(angle) * force;
                mY += Math.sin(angle) * force;

                otherNode = otherNode.mNext;
            } while (!mStopped);
        }

    }

}
