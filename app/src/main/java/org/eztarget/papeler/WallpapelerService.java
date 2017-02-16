package org.eztarget.papeler;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.NonNull;
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
                update();
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

        private float mSpreadHalf;

        private boolean mVisible = true;

        private boolean mIsTouching = false;

        private long mLastTouchMillis;

        private boolean mDrawing = false;

        private long mFirstTouchMillis;

        private int mMaxNumber;

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

            scheduleDrawIfReady();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);

            Log.d(TAG, "Pengine.onSurfaceDestroyed(" + mVisible + ")");

            mVisible = false;
            mHandler.removeCallbacks(mUpdateRunnable);
        }

        private int mDebugDrawCounter;

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            mWidth = width;
            mSpread = mWidth * 0.4f;
            mSpreadHalf = mSpread * 0.5f;
            mHeight = height;

            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(Color.BLACK);

            mDebugDrawCounter = 0;

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

                scheduleDrawIfReady();

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

                    for (final Line line : mLines) {
                        final boolean lineMoved = line.update(mIsTouching);
                        hadAnyMovement |= lineMoved;

                        line.draw(mCanvas, mPaint1, mPaint2);
                    }
                    canvas.drawBitmap(mBitmap, 0f, 0f, null);

                    if (CLEAR_FRAME) {
                        mCanvas.drawColor(Color.BLACK);
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
                mDrawing = false;
            }

            if (VERBOSE) {
                Log.d(TAG, "Pengine.draw() took " + (System.currentTimeMillis() - startMillis) + "ms.");
            }

            Log.d(TAG, "Pengine.draw().movement: " + hadAnyMovement);

            if (hadAnyMovement) {
                scheduleDrawIfReady();
            }
        }

        private void scheduleDrawIfReady() {

            if (VERBOSE) {
                Log.d(TAG, "Pengine.scheduleDrawIfReady()");
            }

            mHandler.removeCallbacks(mUpdateRunnable);

            if (mVisible) {
                mHandler.postDelayed(mUpdateRunnable, 20L);
            }
        }

        private void update() {

        }

        private float getAgeFactor() {

            if (mIsTouching) {
                return 1f;
            }

            final long ageMillis =  System.currentTimeMillis() - mLastTouchMillis;

            if (ageMillis >= MAX_TOUCH_AGE_MILLIS) {
                if (VERBOSE) {
                    Log.d(TAG, "Pengine.getAgeFactor() -> 0");
                }
                return 0f;
            } else {
                if (VERBOSE) {
                    Log.d(TAG, "Pengine.getAgeFactor() -> " + (1f - (ageMillis / MAX_TOUCH_AGE_FLOATIES)));
                }
                return 1f - (ageMillis / MAX_TOUCH_AGE_FLOATIES);
            }
        }

        private void drawDrop(@NonNull Canvas canvas, @NonNull final Drop drop) {

//            Log.d(TAG, "drawDrop " + mDrop.x + ", " + mDrop.y);

//            final float spread = (float) Math.random() * mWidth * 0.1f;

//            final int brightness = (int) (50 * getAgeFactor());
//            mPaint1.setAlpha(brightness);
//
//            double angle;
//            float radius;
//            for (int i = 0; i < DENSITY; i++) {
//                angle =  Math.random() * Math.PI * 2;
//                radius = mSpread * (float) Math.random() + 0.2f;
//                canvas.drawPoint(
//                        mDrop.x + (float) Math.cos(angle) * radius,
//                        mDrop.y + (float) Math.sin(angle) * radius,
//                        mPaint1
//                );
//            }
        }

        // Surface view requires that all elements are drawn completely
//        private void drawCircles(Canvas canvas) {
////            canvas.drawColor(Color.BLACK);
//            if (mPoints.size() > 100) {
//                mPoints.clear();
//            }
//
//            for (final Drop point : mPoints) {
//                canvas.drawCircle(
//                        point.x,
//                        point.y,
//                        (float) Math.random() * mWidth * 0.1f,
//                        mPaint1
//                );
//            }
//        }
    }
}