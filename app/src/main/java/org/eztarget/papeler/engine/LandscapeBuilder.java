package org.eztarget.papeler.engine;

/**
 * Created by michelsievers on 19.07.17.
 */

public class LandscapeBuilder implements BeingBuilder {

    @Override
    public Being build(float x, float y) {
        return new Landscape(y, x * 2f, y * 1.25f);
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
