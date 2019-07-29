package org.eztarget.papeler

import android.util.Log
import android.view.SurfaceHolder
import org.eztarget.papeler.engine.ServiceCoordinator

class WallpaperService : android.service.wallpaper.WallpaperService() {

    val engineCoordinator: ServiceCoordinator = ServiceCoordinator()

    override fun onCreateEngine(): android.service.wallpaper.WallpaperService.Engine {
        Log.d(tag, "onCreateEngine()")
        return Engine()
    }

    inner class Engine : android.service.wallpaper.WallpaperService.Engine() {

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            if (verbose) {
                Log.d(engineTag, "onSurfaceCreated()")
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            if (verbose) {
                Log.d(
                        engineTag,
                        "onSurfaceChanged(): format: $format, width: $width, height: $height"
                )
            }

            if (isVisible) {
                engineCoordinator.start(this)
            } else {
                engineCoordinator.stop()
            }

            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)

            if (verbose) {
                Log.d(engineTag, "onSurfaceDestroyed()")
            }

            engineCoordinator.stop()
        }

        override fun onVisibilityChanged(visible: Boolean) {

            if (verbose) {
                Log.d(engineTag, "onVisibilityChanged(): visible: $visible")
            }

            if (visible) {
                engineCoordinator.start(this)
            } else {
                engineCoordinator.stop()
            }

            super.onVisibilityChanged(visible)
        }


    }

    companion object {
        private val verbose = BuildConfig.DEBUG
        val tag: String = WallpaperService::class.java.simpleName
        val engineTag: String = Engine::class.java.simpleName
    }
}




