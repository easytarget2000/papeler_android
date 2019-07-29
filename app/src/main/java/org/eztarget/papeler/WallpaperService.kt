package org.eztarget.papeler

import android.util.Log
import android.view.SurfaceHolder
import org.eztarget.papeler.engine.BitmapCanvasEngine

class WallpaperService: android.service.wallpaper.WallpaperService() {

    val engine: BitmapCanvasEngine = BitmapCanvasEngine()

    override fun onCreateEngine(): android.service.wallpaper.WallpaperService.Engine {
        Log.d(tag, "onCreateEngine()")
        return Engine()
    }

    private inner class Engine: android.service.wallpaper.WallpaperService.Engine() {

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            if (verbose) {
                Log.d(engineTag, "onSurfaceCreated()")
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)

            if (verbose) {
                Log.d(
                        engineTag,
                        "onSurfaceChanged(): format: $format, width: $width, height: $height"
                )
            }

            engine.clear()
            engine.start(width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)

            if (verbose) {
                Log.d(engineTag, "onSurfaceDestroyed()")
            }

            engine.pause()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (verbose) {
                Log.d(engineTag, "onVisibilityChanged(): visible: $visible")
            }

            if (visible) {
                val width = super.getDesiredMinimumWidth()
                val height = super.getDesiredMinimumHeight()
                engine.start(width, height)
            } else {
                engine.pause()
            }
        }
    }

    companion object {
        private val verbose = BuildConfig.DEBUG
        val tag: String = WallpaperService::class.java.simpleName
        val engineTag: String = Engine::class.java.simpleName
    }
}




