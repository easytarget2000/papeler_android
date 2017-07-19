package org.eztarget.papeler.engine;

import android.util.Log;

import java.util.Random;

/**
 * Created by michelsievers on 05/03/2017.
 */

public class FoliageBuilder implements BeingBuilder {

    private static final String TAG = FoliageBuilder.class.getSimpleName();

    private float mCanvasSize;

    private boolean mSymmetric;

    private int mPaintMode;

    private boolean mCanChangeAlpha = true;

    public FoliageBuilder(final float canvasSize, final boolean canChangeAlpha) {
        final Random random = new Random();

        mCanvasSize = canvasSize;
        mSymmetric = random.nextInt(3) % 2 == 0;
        mCanChangeAlpha = canChangeAlpha;
//        if (this.mSymmetric) {
//            this.mPaintMode = (random.nextInt(2) % 2 == 0) ? Foliage.LINE_MODE : Foliage.CIRCLE_MODE;
//        } else {
        if (random.nextBoolean()) {
            mPaintMode = Foliage.LINE_MODE;
        } else {
            mPaintMode = random.nextInt(4);
        }
//        }

        Log.d(
                TAG,
                "Initialized. Canvas size: " + canvasSize
                        + ", symmetry: " + mSymmetric
                        + ", mode: " + mPaintMode
        );
    }

    @Override
    public Being build(final float x, final float y) {
        final Random random = new Random();

        final Foliage foliage = new Foliage(mCanvasSize, mCanChangeAlpha);
        foliage.setSymmetric(mSymmetric);
        foliage.setRectMode(mPaintMode);
        switch (random.nextInt(5)) {
            case 0:
            case 1:
                foliage.initPolygon(x, y);
                break;

            default:
                foliage.initCircle(x, y);
        }

        Log.d(TAG, "Built Foliage at " + x + ", " + y + ".");
        return foliage;
    }

    @Override
    public int getRecommendedAlpha() {
        return mSymmetric ? 22 : 16;
    }

    @Override
    public int getRecommendedMaxNumber() {
        return 8;
    }
}
