package org.eztarget.papeler;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by michelsievers on 23/01/2017.
 */

public class SetWallpaperActivity extends Activity {

    private boolean mDidSetWallpaper = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDidSetWallpaper) {
            finish();
        } else {
            setWallpaper();
        }
    }

    private void setWallpaper() {

        final Intent setLivePapelerIntent;
        setLivePapelerIntent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        setLivePapelerIntent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, WallpapelerService.class)
        );

        startActivity(setLivePapelerIntent);
        mDidSetWallpaper = true;
    }

    public void onClick(View view) {
    }
}
