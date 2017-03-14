package org.eztarget.papeler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by michelsievers on 14/03/2017.
 */

class BackgroundImageOpener {

    private static final String TAG = BackgroundImageOpener.class.getSimpleName();

    static Bitmap load() throws Exception {

        final File imageFile = getFile();
        if (imageFile == null) {
            Log.e(TAG, "Image file is null.");
            throw new FileNotFoundException("Image File is null");
        }

        final InputStream imageStream = new FileInputStream(getFile());
        return BitmapFactory.decodeStream(imageStream);
    }

    static Bitmap loadRatioPreserved(final int targetWidth, final int targetHeight)
            throws Exception {

        final Bitmap bitmap = load();
        final int sourceWidth = bitmap.getWidth();
        final int sourceHeight = bitmap.getHeight();

        final float xScale = (float) targetWidth / sourceWidth;
        final float yScale = (float) targetHeight / sourceHeight;
        final float scale = Math.max(xScale, yScale);

        final float scaledWidth = scale * sourceWidth;
        final float scaledHeight = scale * sourceHeight;

        final float left = (targetWidth - scaledWidth) / 2;
        final float top = (targetHeight - scaledHeight) / 2;

        final RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        final Bitmap dest = Bitmap.createBitmap(targetWidth, targetHeight, bitmap.getConfig());
        final Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(bitmap, null, targetRect, null);

        return dest;
    }

    static File getFile() {
        final File destinationDir;
        destinationDir = new File(Environment.getExternalStorageDirectory().getPath() + "/waypr/");
        if (destinationDir.exists()) {
            Log.d(TAG, "Directory " + destinationDir.getAbsolutePath() + " exists.");
        } else {
            Log.d(TAG, "Creating " + destinationDir.getAbsolutePath() + ".");
            if (!destinationDir.mkdirs()) {
                Log.e(TAG, "Could not create " + destinationDir.getAbsolutePath() + ".");
                return null;
            }
        }

        return new File(destinationDir, "w.png");
    }

}
