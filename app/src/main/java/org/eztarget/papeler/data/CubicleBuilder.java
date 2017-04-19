package org.eztarget.papeler.data;

/**
 * Created by michelsievers on 05.04.17.
 */

public class CubicleBuilder implements BeingBuilder {

    private float mCanvasSize;

    public CubicleBuilder(final float canvasSize) {
        mCanvasSize = canvasSize;
    }

    @Override
    public Being build(float x, float y) {
        return new Cubicle(x, y, mCanvasSize);
    }

    @Override
    public int getRecommendedAlpha() {
        return 24;
    }

    @Override
    public int getRecommendedMaxNumber() {
        return 16;
    }
}
