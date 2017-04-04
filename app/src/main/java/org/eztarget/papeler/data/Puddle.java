package org.eztarget.papeler.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * Created by michel@easy-target.org on 24/03/2017.
 */

public class Puddle extends Being {

    private static final int MAX_AGE = 800;

    private int mMage = 0;

    private double mCanvasSize = 64;

    public Puddle(final double canvasSize) {
        mCanvasSize = canvasSize;
    }

    @Override
    public boolean update(boolean isTouching) {

        return ++mAge < MAX_AGE;
    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint1) {
        for (double y = -mCanvasSize; y < mCanvasSize; y += 5.0) {
            for (double x = -mCanvasSize; x < mCanvasSize; x += 2.0) {
                final double z = Math.cos(Math.sqrt(x * x + y * y * 2.0) / 40 - mAge) * 6;
                paint1.setColor((int) (x * y * mAge));
                canvas.drawPoint((float) (mCanvasSize + x), (float) (mCanvasSize + y - z), paint1);
            }
        }


//        r = 64
//        t = 0
//                ::a::
//        cls()
//        for y = -r, r, 5 do
//            for x =- r, r, 2 do
//            z = (cos(sqrt(x * x + y * y * 2) / 40 - t) * 6) / 2
//        pset(r + x, r + y - z, x * y + t)
//        end
//                end
//        flip()
//        t += 2 / r
//
//          goto a
    }
}
