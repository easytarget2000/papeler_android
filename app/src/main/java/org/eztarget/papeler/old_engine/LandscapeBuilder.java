package org.eztarget.papeler.old_engine;

/**
 * Created by michelsievers on 19.07.17.
 */

public class LandscapeBuilder implements BeingBuilder {

    private float mCanvasHeight = 1920f;

    public void setCanvasHeight(final float canvasHeight) {
        mCanvasHeight = canvasHeight;
    }

    @Override
    public Being build(float x, float y) {
        return new Landscape(y, x * 2f, mCanvasHeight / 16f);
    }

    @Override
    public int getRecommendedAlpha() {
        return 32;
    }

    @Override
    public int getRecommendedMaxNumber() {
        return 8;
    }
}
