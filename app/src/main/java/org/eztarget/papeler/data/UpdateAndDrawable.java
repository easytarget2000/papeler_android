package org.eztarget.papeler.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * Created by michelsievers on 04/03/2017.
 */

public interface UpdateAndDrawable {

    public boolean update(final boolean isTouching);

    public void draw(@NonNull final Canvas canvas, @NonNull final Paint paint1);
}
