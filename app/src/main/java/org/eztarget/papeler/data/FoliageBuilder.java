package org.eztarget.papeler.data;

import android.util.Log;

import java.util.Random;

/**
 * Created by michelsievers on 05/03/2017.
 */

public class FoliageBuilder implements BeingBuilder {

    private static final String TAG = FoliageBuilder.class.getSimpleName();

    private float mCanvasSize;

    private boolean mSymmetric;

    private int mRectMode;

    public FoliageBuilder(final float canvasSize) {
        final Random random = new Random();

        this.mCanvasSize = canvasSize;
        this.mSymmetric = random.nextInt(4) % 4 == 0;
        if (this.mSymmetric) {
            this.mRectMode = (random.nextInt(2) % 2 == 0) ? Foliage.LINE_MODE : Foliage.CIRCLE_MODE;
        } else {
            if (random.nextBoolean()) {
                this.mRectMode = Foliage.LINE_MODE;
            } else {
                this.mRectMode = random.nextInt(4);
            }
        }

        Log.d(
                TAG,
                "Initialized. Canvas size: " + canvasSize
                        + ", symmetry: " + mSymmetric
                        + ", mode: " + mRectMode
        );
    }

    @Override
    public Being build(final float x, final float y) {
        final Random random = new Random();

        final Foliage foliage = new Foliage(mCanvasSize);
        foliage.setSymmetric(mSymmetric);
        foliage.setRectMode(mRectMode);
        switch (random.nextInt(6)) {
            case 0:
                foliage.initSine(x, y);
                break;
            case 1:
                foliage.initLine(x, y);
                break;
            default:
                foliage.initInCircleShape(x, y);
        }

        Log.d(TAG, "Built Foliage at " + x + ", " + y + ".");

        return foliage;
    }
}