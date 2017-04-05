package org.eztarget.papeler.data;

/**
 * Created by michelsievers on 05/03/2017.
 */

public interface BeingBuilder {

    Being build(final float x, final float y);

    int getRecommendedAlpha();

}
