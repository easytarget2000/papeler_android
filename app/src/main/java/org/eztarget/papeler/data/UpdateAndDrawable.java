package org.eztarget.papeler.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * Created by michelsievers on 04/03/2017.
 */

interface UpdateAndDrawable {

    boolean update(final boolean isTouching);

    void draw(@NonNull final Canvas canvas, @NonNull final Paint paint1);

}
