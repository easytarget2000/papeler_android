package org.eztarget.papeler.engine

import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import org.eztarget.papeler.WallpaperService

class ServiceCoordinator {

    var engine: BitmapCanvasEngine = BitmapCanvasEngine()
    var updateAndDrawHandler: Handler = Handler()
    var updateAndDrawIntervalMillis = DEFAULT_UPDATE_AND_DRAW_INTERVAL_MILLIS
    private val visible: Boolean
        get() = serviceEngine?.isVisible ?: false
    private val surfaceHolder: SurfaceHolder?
        get() = serviceEngine?.surfaceHolder
    private var serviceEngine: WallpaperService.Engine? = null
    private val updateAndDrawRunnable = Runnable { updateAndDrawFrameAndSchedule() }

    fun start(serviceEngine: WallpaperService.Engine) {
        this.serviceEngine = serviceEngine
        updateAndDrawFrameAndSchedule()
    }

    fun stop() {
        cancelUpdateAndDrawSchedule()
        serviceEngine = null
    }

    private fun updateAndDrawFrameAndSchedule() {
        cancelUpdateAndDrawSchedule()
        updateAndDrawFrame()
        scheduleUpdateAndDraw()
    }

    private fun updateAndDrawFrame() {
        if (!visible) {
            Log.e(tag, "updateAndDrawFrame() called, while invisible.")
            return
        }

        val canvas = surfaceHolder?.lockCanvas() ?: return
        engine.setup(canvas)
//        engine.updateAndDrawFrameOnCanvas(canvas)
        surfaceHolder?.unlockCanvasAndPost(canvas)
    }

    private fun scheduleUpdateAndDraw() {
        updateAndDrawHandler.postDelayed(updateAndDrawRunnable, updateAndDrawIntervalMillis)
    }

    private fun cancelUpdateAndDrawSchedule() {
        updateAndDrawHandler.removeCallbacks(updateAndDrawRunnable)
    }

    companion object {
        const val DEFAULT_UPDATE_AND_DRAW_INTERVAL_MILLIS = 2000L
        val tag = ServiceCoordinator::class.java.simpleName
    }
}