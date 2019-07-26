package org.eztarget.papeler.engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;

/**
 * Created by michelsievers on 04/03/2017.
 */

interface UpdateAndDrawable {

    boolean update(final boolean isTouching);

    void draw(@NonNull final Canvas canvas, @NonNull final Paint paint1);

}
