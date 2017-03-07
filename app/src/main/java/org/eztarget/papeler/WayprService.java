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

import org.eztarget.papeler.data.Being;
import org.eztarget.papeler.data.BeingBuilder;
import org.eztarget.papeler.data.FlowerStickBuilder;
import org.eztarget.papeler.data.FoliageBuilder;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by michelsievers on 23/01/2017.
 */

public class WayprService extends WallpaperService {

    private static final String TAG = WayprService.class.getSimpleName();

    private static final boolean CLEAR_FRAME = false;

    private static final int PAINT_ALPHA = CLEAR_FRAME ? 200 : 20;

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

        private Paint mPaint = new Paint();

        private Paint mBitmapPaint = new Paint();

        private int mAlphaOffset = 0;

        private int mBackgroundColor = Color.BLACK;

        private int mWidth;

        private int mHeight;

        private boolean mVisible = true;

        private boolean mIsTouching = false;

        private int mBeingsCounter = 0;

        private BeingBuilder mBeingBuilder;

//        private boolean mBlur;

        private boolean mDrawing = false;

        private long mFirstTouchMillis;

        private boolean mResetCanvasOnce = false;

        private Bitmap mBitmap;

        private Canvas mCanvas;

        WayprEngine() {

            if (VERBOSE) {
                Log.d(TAG, "WayprEngine()");
            }

            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(1f);

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

                final String secondMessage = getString(R.string.main_welcome_msg_2);

                final Toast secondToast;
                secondToast = Toast.makeText(WayprService.this, secondMessage, Toast.LENGTH_LONG);
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                secondToast.show();
                            }
                        },
                        3000L
                );
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

            if (new Random().nextInt(7) > 4) {
                mPaint.setStyle(Paint.Style.STROKE);
            } else {
                mPaint.setStyle(Paint.Style.FILL);
            }

            switch ((int) (Math.random() * 5)) {
                case 0:
                    mBeingBuilder = new FlowerStickBuilder(mHeight);
                    break;
                default:
                    mBeingBuilder = new FoliageBuilder(Math.min(mWidth, mHeight));
            }

//            mBlur = true;
//            if (new Random().nextInt(8) % 8 != 0) {

            if (mBeings != null) {
                stopAllPerformances();
                mBeings = new ArrayList<>();
                mBeingsCounter = 0;

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
                mIsTouching = true;
                if (mFirstTouchMillis == 0) {
                    mFirstTouchMillis = System.currentTimeMillis();
                }

                addBeing(event.getX(), event.getY());
                adjustPaint();

                mResetCanvasOnce = false;
                nextStep();

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsTouching = false;
                return;
            }

            super.onTouchEvent(event);
        }

        private void addBeing(final float x, final float y) {

            final long ageMinutes = (System.currentTimeMillis() - mFirstTouchMillis) / 1000L / 60L;
            if (ageMinutes > 30) {
                if (mAlphaOffset > -(PAINT_ALPHA * 0.75)) {
                    if (mAlphaOffset > (-PAINT_ALPHA * 0.75)) {
                        mAlphaOffset -= 10;
                    }
                }
            }

            if (mBeings.size() > 10) {
                return;
            }

            mPaint.setAlpha(PAINT_ALPHA + mAlphaOffset);

            mBeings.add(mBeingBuilder.build(x, y));
            ++mBeingsCounter;
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
                            being.draw(mCanvas, mPaint);
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

        private void adjustPaint() {

            if (mPaint.getStyle() == Paint.Style.FILL) {
                mAlphaOffset *= 0.5f;
            }

            final Random rnd = new Random();
            if (rnd.nextInt(mBeingsCounter) > 4) {
                mPaint.setColor(Color.BLACK);
                mPaint.setStrokeWidth(4f);
            } else {
                mPaint.setARGB(
                        PAINT_ALPHA + mAlphaOffset,
                        160 + rnd.nextInt(96),
                        160 + rnd.nextInt(96),
                        160 + rnd.nextInt(96)
                );
            }

//            if (mBlur) {
//                final float blurRadius = ((float) width) * 0.01f;
//                final MaskFilter blur = new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL);
//                mPaint.setMaskFilter(blur);
//                mPaint.setAlpha(mPaint.getAlpha() + 150);
//            }
        }

    }
}