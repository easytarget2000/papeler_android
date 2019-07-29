package org.eztarget.papeler

import android.util.Log
import android.view.SurfaceHolder

class WallpaperService: android.service.wallpaper.WallpaperService() {

    val engine: org.eztarget.papeler.engine.Engine = org.eztarget.papeler.engine.Engine()

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
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)

            if (verbose) {
                Log.d(engineTag, "onSurfaceDestroyed()")
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (verbose) {
                Log.d(engineTag, "onVisibilityChanged(): visible: $visible")
            }
        }
    }

    companion object {
        private val verbose = BuildConfig.DEBUG
        val tag: String = WallpaperService::class.java.simpleName
        val engineTag: String = Engine::class.java.simpleName
    }
}




