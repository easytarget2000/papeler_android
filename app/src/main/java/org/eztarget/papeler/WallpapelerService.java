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

    private static final int PAINT_1_ALPHA = CLEAR_FRAME ? 200 : 36;

    private static final int PAINT_2_ALPHA = CLEAR_FRAME ? 180 : 16;

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

        private ArrayList<Foliage> mFoliages = new ArrayList<>();

        private Paint mPaint1 = new Paint();

        private Paint mPaint2 = new Paint();

        private Paint mBitmapPaint = new Paint();

        private int mBackgroundColor = Color.BLACK;

        private int mWidth;

        private int mHeight;

        private boolean mVisible = true;

        private boolean mIsTouching = false;

        private long mLastTouchMillis;

        private boolean mSymmetric;

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
            mHeight = height;

//            mWidth = Math.max(width, height);
//            mHeight = mWidth;

            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(mBackgroundColor);

            mSymmetric = new Random().nextBoolean();

            if (mFoliages != null) {
                stopAllLinePerformances();
                mFoliages = new ArrayList<>();

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

//            Log.d(TAG, "onTouchEvent: " + event.getAction() + ": " + event.getDownTime());

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                addFoliage(event.getX(), event.getY());

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
                mPaint1.setAlpha(PAINT_1_ALPHA);
                mPaint2.setAlpha(PAINT_2_ALPHA);

                mLastTouchMillis = System.currentTimeMillis();

                mResetCanvasOnce = false;
                nextStep();

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsTouching = false;
                return;
            }

            super.onTouchEvent(event);
        }

        private void addFoliage(final float x, final float y) {

            if (mFoliages.size() > 10) {
                return;
            }

            final float canvasSize = Math.min(mWidth, mHeight);

            final Foliage foliage;
//            switch (0) {
            switch ((int) (Math.random() * 2)) {
                case 0:
                    foliage = Foliage.circleInstance(x, y, mSymmetric, canvasSize);
                    break;
                default:
                    foliage = Foliage.lineInstance(x, y, mSymmetric, canvasSize);
            }

            mFoliages.add(foliage);
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

                    for (int i = 0; i < mFoliages.size(); i++) {

                        if (mResetCanvasOnce) {
                            mResetCanvasOnce = false;
                            canvas.drawColor(mBackgroundColor);
                            mCanvas.drawColor(mBackgroundColor);
                            break;
                        }

                        final Foliage foliage = mFoliages.get(i);
                        final boolean lineMoved = foliage.update(mIsTouching);
                        hadAnyMovement |= lineMoved;

                        if (!mIsTouching) {
                            foliage.draw(mCanvas, mPaint1, mPaint2);
                        }

                        if (!lineMoved) {
                            mFoliages.remove(i);
                            Log.d(TAG, "Pengine.draw() removing Foliage " + i + ".");
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
            } else {
                Log.d(TAG, "Pengine.draw() not calling nextStep().");
            }
        }

        private void nextStep() {

            if (VERBOSE) {
                Log.d(TAG, "Pengine.nextStep()");
            }

            cancelDrawSchedule();

            if (mVisible) {
                mHandler.postDelayed(mUpdateRunnable, 20L);
            } else if (mFoliages != null){
                stopAllLinePerformances();
            }
        }

        private void cancelDrawSchedule() {
            mHandler.removeCallbacks(mUpdateRunnable);
        }

        private void stopAllLinePerformances() {
            for (final Foliage foliage : mFoliages) {
                foliage.stopPerforming();
            }
        }

    }
}