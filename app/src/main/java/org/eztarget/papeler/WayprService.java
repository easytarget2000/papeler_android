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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by michelsievers on 23/01/2017.
 */

public class WayprService extends WallpaperService {

    private static final String TAG = WayprService.class.getSimpleName();

    private static final boolean CLEAR_FRAME = false;

    private static final int PAINT_1_ALPHA = CLEAR_FRAME ? 200 : 30;

    private static final int PAINT_2_ALPHA = CLEAR_FRAME ? 180 : 10;

    private static final long MAX_TOUCH_AGE_MILLIS = 3L * 1000L;

    private static final float MAX_TOUCH_AGE_FLOATIES = (float) MAX_TOUCH_AGE_MILLIS;

    private static final int DENSITY = 10;

    private static final boolean VERBOSE = false;

    @Override
    public Engine onCreateEngine() {
        Log.d(TAG, "onCreateEngine()");
        return new WayprEngine();
    }

    private class WayprEngine extends Engine {

        private final Handler mHandler = new Handler();

        private final Runnable mUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                draw();
            }

        };

        private ArrayList<Being> mBeings = new ArrayList<>();

        private Paint mPaint1 = new Paint();

        private Paint mPaint2 = new Paint();

        private Paint mBitmapPaint = new Paint();

        private int mAlphaOffset = 0;

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

        WayprEngine() {

            if (VERBOSE) {
                Log.d(TAG, "WayprEngine()");
            }

            mPaint1.setAntiAlias(true);
            mPaint1.setColor(Color.WHITE);
            mPaint1.setStyle(Paint.Style.STROKE);
            mPaint1.setStrokeWidth(1f);

            mPaint2.setAntiAlias(true);
            mPaint2.setStyle(Paint.Style.STROKE);
            mPaint2.setStrokeWidth(1f);

            mBitmapPaint.setColor(Color.WHITE);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            mFirstTouchMillis = 0L;

            if (VERBOSE) {
                Log.d(TAG, "WayprEngine.onVisibilityChanged(" + mVisible + ")");
            }

            nextStep();

            if (PreferenceAccess.with(getApplicationContext()).getAndUnsetIsFirstTime()) {
                final String welcomeMessage = getString(R.string.main_welcome_msg);
                Toast.makeText(WayprService.this, welcomeMessage, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);

            Log.d(TAG, "WayprEngine.onSurfaceDestroyed(" + mVisible + ")");

            mVisible = false;
            stopAllPerformances();
            cancelDrawSchedule();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            mWidth = width;
            mHeight = height;

            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(mBackgroundColor);

            mSymmetric = new Random().nextBoolean();
            if (mSymmetric) {
                mAlphaOffset += 20;
            } else {
                mAlphaOffset -= 10;
            }

//            if (new Random().nextInt(8) % 8 != 0) {
//            if (true) {
//                final float blurRadius = width * 0.1f;
//                final MaskFilter blur;
//                blur = new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL);
//                mPaint1.setMaskFilter(new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL));
//                mPaint2.setMaskFilter(new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL));
//                mAlphaOffset += 50;
//            }

            if (mBeings != null) {
                stopAllPerformances();
                mBeings = new ArrayList<>();

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
                        64 + rnd.nextInt(192),
                        64 + rnd.nextInt(192),
                        64 + rnd.nextInt(192)
                );

                if (mFirstTouchMillis < 100L) {
                    mFirstTouchMillis = System.currentTimeMillis();
                }

                mIsTouching = true;
                mPaint1.setAlpha(PAINT_1_ALPHA + mAlphaOffset);
                mPaint2.setAlpha(PAINT_2_ALPHA + mAlphaOffset);

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

            final long ageMinutes = (System.currentTimeMillis() - mFirstTouchMillis) / 1000L / 60L;
            if (ageMinutes > 30) {
                if (mAlphaOffset > -(PAINT_1_ALPHA * 0.75)) {
                    if (mAlphaOffset > (-PAINT_2_ALPHA * 0.75)) {
                        mAlphaOffset -= 10;
                    }
                }
            }

            if (mBeings.size() > 10) {
                return;
            }

            final float canvasSize = Math.min(mWidth, mHeight);

//            final Foliage foliage;
//            switch ((int) (Math.random() * 3)) {
//                case 0:
//                    foliage = Foliage.lineInstance(x, y, mSymmetric, canvasSize);
//                    break;
//                default:
//                    foliage = Foliage.circleInstance(x, y, mSymmetric, canvasSize);
//            }

            final Being being = new FlowerStick(mHeight, x, y);
//            mAlphaOffset += 50;

            mPaint1.setAlpha(PAINT_1_ALPHA + mAlphaOffset);
            mPaint2.setAlpha(PAINT_2_ALPHA + mAlphaOffset);

            mBeings.add(being);
        }

        private void draw() {
            if (mDrawing) {
                return;
            }

            cancelDrawSchedule();
            mDrawing = true;

            final long startMillis = System.currentTimeMillis();
            if (VERBOSE) {
                Log.d(TAG, "WayprEngine.draw()");
            }

            final SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            boolean hadAnyMovement = false;

            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {

                    for (int i = 0; i < mBeings.size(); i++) {

                        if (mResetCanvasOnce) {
                            mResetCanvasOnce = false;
                            canvas.drawColor(mBackgroundColor);
                            mCanvas.drawColor(mBackgroundColor);
                            break;
                        }

                        final Being being = mBeings.get(i);
                        final boolean beingGrew = being.update(mIsTouching);
                        hadAnyMovement |= beingGrew;

                        if (!mIsTouching) {
                            being.draw(mCanvas, mPaint1, mPaint2);
                        }

                        if (!beingGrew) {
                            mBeings.remove(i);
                            Log.d(TAG, "WayprEngine.draw() removing Foliage " + i + ".");
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
                Log.d(TAG, "WayprEngine.draw() took " + (System.currentTimeMillis() - startMillis) + "ms.");
            }

            if (hadAnyMovement) {
                nextStep();
            } else {
                Log.d(TAG, "WayprEngine.draw() not calling nextStep().");
            }
        }

        private void nextStep() {

            if (VERBOSE) {
                Log.d(TAG, "WayprEngine.nextStep()");
            }

            cancelDrawSchedule();

            if (mVisible) {
                mHandler.postDelayed(mUpdateRunnable, 20L);
            } else if (mBeings != null) {
                stopAllPerformances();
            }
        }

        private void cancelDrawSchedule() {
            mHandler.removeCallbacks(mUpdateRunnable);
        }

        private void stopAllPerformances() {
            for (final Being being : mBeings) {
                being.stopPerforming();
            }
        }

    }
}