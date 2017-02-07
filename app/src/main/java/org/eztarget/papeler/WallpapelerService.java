package org.eztarget.papeler;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;

/**
 * Created by michelsievers on 23/01/2017.
 */

public class WallpapelerService extends WallpaperService {

    private static final String TAG = WallpapelerService.class.getSimpleName();

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
//                if (VERBOSE) {
//                    Log.d(TAG, "mUpdateRunnable.run()");
//                }
                update();
                draw();
            }

        };

        private ArrayList<Drop> mDrops = new ArrayList<>();

        private Paint mPaint = new Paint();

        private Paint mBitmapPaint = new Paint();

        private int mWidth;

        private int mHeight;

        private float mSpread;

        private float mSpreadHalf;

        private boolean mVisible = true;

        private boolean mTouching = false;

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

            final SharedPreferences prefs;
            prefs = PreferenceManager.getDefaultSharedPreferences(WallpapelerService.this);

            mMaxNumber = Integer.valueOf(prefs.getString("numberOfCircles", "4"));
//            mHandleTouch = prefs.getBoolean("touch", false);

            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);
//            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeWidth(1f);
            mHandler.post(mUpdateRunnable);

            mBitmapPaint.setColor(Color.WHITE);
            //mBitmapPaint.setAntiAlias(true);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;

            if (VERBOSE) {
                Log.d(TAG, "Pengine.onVisibilityChanged(" + mVisible + ")");
            }

            if (mVisible) {
                mHandler.post(mUpdateRunnable);
            } else {
                mHandler.removeCallbacks(mUpdateRunnable);
            }
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

            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ALPHA_8);
            mCanvas = new Canvas(mBitmap);


//            mPaint.setStrokeWidth(mWidth * 0.01f);


            mDebugDrawCounter = 0;

            if (VERBOSE) {
                Log.d(TAG, "New Surface: " + format + ": " + mWidth + " x " + mHeight);
            }

            super.onSurfaceChanged(holder, format, mWidth, mHeight);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                final Drop firstDrop;
                if (mDrops.isEmpty()) {
                    firstDrop = null;
                } else {
                    firstDrop = mDrops.get(0);
                }

                final Drop dropUnderTouch = new Drop(event.getX(), event.getY(), mWidth, firstDrop);
                mDrops.add(dropUnderTouch);

                if (mFirstTouchMillis < 100L) {
                    mFirstTouchMillis = System.currentTimeMillis();
                }

                mTouching = true;
                mLastTouchMillis = System.currentTimeMillis();

                scheduleDraw();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mTouching = false;
                return;
            }



            super.onTouchEvent(event);
        }

        private void draw() {
            if (mDrawing) {
                return;
            }

            mDrawing = true;

            if (VERBOSE) {
                Log.d(TAG, "Pengine.draw()");
            }

            final SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
//            final long now = System.currentTimeMillis();
//            mPaint.setColor((int) (now - mFirstTouchMillis));
//            mPaint.setAlpha((int) (50 * getAgeFactor()));

            boolean scheduleDraw = false;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    for (final Drop drop : mDrops) {
                        if (drop.update()) {
                            scheduleDraw = true;
                            drop.draw(mCanvas, mPaint);
                            canvas.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint);
                        }
                    }

                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
                mDrawing = false;
            }

            if (scheduleDraw) {
                scheduleDraw();
            }
        }

        private void scheduleDraw() {

            if (VERBOSE) {
                Log.d(TAG, "Pengine.scheduleDraw()");
            }

            mHandler.removeCallbacks(mUpdateRunnable);

            if (mVisible) {
                mHandler.postDelayed(mUpdateRunnable, 20L);
            }
        }

        private void update() {

        }

        private float getAgeFactor() {

            if (mTouching) {
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
//            mPaint.setAlpha(brightness);
//
//            double angle;
//            float radius;
//            for (int i = 0; i < DENSITY; i++) {
//                angle =  Math.random() * Math.PI * 2;
//                radius = mSpread * (float) Math.random() + 0.2f;
//                canvas.drawPoint(
//                        mDrop.x + (float) Math.cos(angle) * radius,
//                        mDrop.y + (float) Math.sin(angle) * radius,
//                        mPaint
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
//                        mPaint
//                );
//            }
//        }
    }
}