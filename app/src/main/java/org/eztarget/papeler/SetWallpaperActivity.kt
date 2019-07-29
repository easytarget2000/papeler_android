package org.eztarget.papeler

import android.app.Activity
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View

class SetWallpaperActivity : Activity() {

    private var didSetWallpaper = false
    var previewWallpaperIntent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
    var selectWallpaperIntent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)

    override fun onResume() {
        super.onResume()

        if (didSetWallpaper) {
            finish()
        } else {
            setWallpaper()
        }
    }

    private fun setWallpaper() {
        previewWallpaperIntent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this, WallpaperService::class.java)
        )

        try {
            startActivity(previewWallpaperIntent)
        } catch (e: ActivityNotFoundException) {
            startActivity(selectWallpaperIntent)
        }

        didSetWallpaper = true
    }

    fun onClick(view: View) {}
}
