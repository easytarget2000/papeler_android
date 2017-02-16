package org.eztarget.papeler;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by michelsievers on 23/01/2017.
 */

public class WallpapelerService extends WallpaperService {

    private static final String TAG = WallpapelerService.class.getSimpleName();

    private static final boolean CLEAR_FRAME = false;

    private static final int PAINT_1_ALPHA = CLEAR_FRAME ? 200 : 40;

    private static final int PAINT_2_ALPHA = CLEAR_FRAME ? 180 : 20;

    private static final long MAX_TOUCH_AGE_MILLIS = 3L * 1000L;

    private static final float MAX_TOUCH_AGE_FLOATIES = (float) MAX_TOUCH_AGE_MILLIS;

    private static final int DENSITY = 10;

    private static final boolean VERBOSE = false;

    @Override
    public Engine onCreateEngine() {
        Log.d(TAG, "onCreateEngine()");
        return new Pengine();
    }

    private class Pengine extends Engine {

        private final Handler mHandler = new Handler();

        private final Runnable mUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                draw();
            }

        };

        private ArrayList<Line> mLines = new ArrayList<>();

        private Paint mPaint1 = new Paint();

        private Paint mPaint2 = new Paint();

        private Paint mBitmapPaint = new Paint();

        private int mWidth;

        private int mHeight;

        private float mSpread;

        private boolean mVisible = true;

        private boolean mIsTouching = false;

        private long mLastTouchMillis;

        private boolean mDrawing = false;

        private long mFirstTouchMillis;

        private boolean mResetCanvasOnce = false;

        private Bitmap mBitmap;

        private Canvas mCanvas;

        Pengine() {

            if (VERBOSE) {
                Log.d(TAG, "Pengine()");
            }

            mPaint1.setAntiAlias(true);
            mPaint1.setColor(Color.WHITE);
            mPaint1.setAlpha(PAINT_1_ALPHA);
            mPaint1.setStyle(Paint.Style.STROKE);
            mPaint1.setStrokeWidth(1f);

            mPaint2.setAntiAlias(true);
            mPaint2.setAlpha(PAINT_2_ALPHA);
            mPaint2.setStyle(Paint.Style.STROKE);
            mPaint2.setStrokeWidth(1f);

            mBitmapPaint.setColor(Color.WHITE);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;

            if (VERBOSE) {
                Log.d(TAG, "Pengine.onVisibilityChanged(" + mVisible + ")");
            }

            nextStep();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);

            Log.d(TAG, "Pengine.onSurfaceDestroyed(" + mVisible + ")");

            mVisible = false;
            stopAllLinePerformances();
            cancelDrawSchedule();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            mWidth = width;
            mSpread = mWidth * 0.4f;
            mHeight = height;

            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(Color.BLACK);

            if (mLines != null) {
                stopAllLinePerformances();
                mLines = new ArrayList<>();

                // Draw once to clear everything.
                mResetCanvasOnce = true;
                nextStep();
            }

            if (VERBOSE) {
                Log.d(TAG, "New Surface: " + format + ": " + mWidth + " x " + mHeight);
            }

            super.onSurfaceChanged(holder, format, mWidth, mHeight);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {

            Log.d(TAG, "onTouchEvent: " + event.getAction() + ": " + event.getDownTime());

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                addLine(event.getX(), event.getY());

                final Random rnd = new Random();
                mPaint2.setARGB(
                        PAINT_2_ALPHA,
                        rnd.nextInt(256),
                        rnd.nextInt(256),
                        rnd.nextInt(256)
                );

                if (mFirstTouchMillis < 100L) {
                    mFirstTouchMillis = System.currentTimeMillis();
                }

                mIsTouching = true;
                mLastTouchMillis = System.currentTimeMillis();

                mResetCanvasOnce = false;
                nextStep();

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsTouching = false;
                return;
            }

            super.onTouchEvent(event);
        }

        private void addLine(final float x, final float y) {

            if (mLines.size() > 10) {
                return;
            }

            final float canvasSize = Math.min(mWidth, mHeight);
            mLines.add(new Line(x, y, canvasSize));
        }

        private void draw() {
            if (mDrawing) {
                return;
            }

            cancelDrawSchedule();
            mDrawing = true;

            final long startMillis = System.currentTimeMillis();
            if (VERBOSE) {
                Log.d(TAG, "Pengine.draw()");
            }

            final SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            boolean hadAnyMovement = false;

            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {

                    for (int i = 0; i < mLines.size(); i++) {

                        if (mResetCanvasOnce) {
                            mResetCanvasOnce = false;
                            canvas.drawColor(Color.BLACK);
                            mCanvas.drawColor(Color.BLACK);
                            break;
                        }

                        final Line line = mLines.get(i);
                        final boolean lineMoved = line.update(mIsTouching);
                        hadAnyMovement |= lineMoved;

                        line.draw(mCanvas, mPaint1, mPaint2);

                        if (!hadAnyMovement) {
                            mLines.remove(i);
                        }


                    }
                    canvas.drawBitmap(mBitmap, 0f, 0f, null);

                    if (CLEAR_FRAME) {
                        mCanvas.drawColor(Color.BLACK);
                    }
                }
            } finally {
                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
                mDrawing = false;
            }

            if (VERBOSE) {
                Log.d(TAG, "Pengine.draw() took " + (System.currentTimeMillis() - startMillis) + "ms.");
            }

            if (hadAnyMovement) {
                nextStep();
            }
        }

        private void nextStep() {

            if (VERBOSE) {
                Log.d(TAG, "Pengine.nextStep()");
            }

            cancelDrawSchedule();

            if (mVisible) {
                mHandler.postDelayed(mUpdateRunnable, 20L);
            } else if (mLines != null){
                stopAllLinePerformances();
            }
        }

        private void cancelDrawSchedule() {
            mHandler.removeCallbacks(mUpdateRunnable);
        }

        private void stopAllLinePerformances() {
            for (final Line line : mLines) {
                line.stopPerforming();
            }
        }

    }
}