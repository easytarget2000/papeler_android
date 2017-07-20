package org.eztarget.papeler.engine;

/**
 * Created by michelsievers on 20.07.17.
 */

class Random {

    private static java.util.Random sRandom = new java.util.Random();

    static float nextFloat(final float maxValue) {
        return sRandom.nextFloat() * maxValue;
    }

    static double nextDouble(final double maxValue) {
        return sRandom.nextDouble() * maxValue;
    }
}
