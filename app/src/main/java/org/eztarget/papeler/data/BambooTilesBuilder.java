package org.eztarget.papeler.data;

import java.util.Random;

/**
 * Created by michelsievers on 19.04.17.
 */

public class BambooTilesBuilder implements BeingBuilder {

    private float mTileSize;

    public BambooTilesBuilder(final float canvasSize) {
        mTileSize = canvasSize / (3f + (float) new Random().nextInt(5));
    }

    @Override
    public Being build(float x, float y) {
        return new BambooTile(mTileSize, x, y);
    }

    @Override
    public int getRecommendedAlpha() {
        return 16;
    }
}
