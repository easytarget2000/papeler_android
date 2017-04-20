package org.eztarget.papeler.data;

import android.util.Log;

/**
 * Created by michelsievers on 05/03/2017.
 */

public class FlowerStickBuilder implements BeingBuilder {

    private static final String TAG = FlowerStickBuilder.class.getSimpleName();

    private float mCanvasHeight;

    public FlowerStickBuilder(final float canvasHeight) {
        Log.d(TAG, "Initialized with canvas height " + canvasHeight + ".");
        mCanvasHeight = canvasHeight;
    }

    @Override
    public Being build(float x, float y) {
        Log.d(TAG, "New Flower Stick at " + x + ", " + y + ".");
        return new FlowerStick(mCanvasHeight, x, y);
    }

    @Override
    public int getRecommendedAlpha() {
        return 64;
    }

    @Override
    public int getRecommendedMaxNumber() {
        return 24;
    }
}
