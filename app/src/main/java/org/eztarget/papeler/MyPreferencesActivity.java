package org.eztarget.papeler;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by michel@easy-target.org on 23/01/2017.
 *
 * Preferences Activity to be accessed from Live Wallpaper Selection Activity.
 * For selecting or deselecting a background image.
 *
 */

public class MyPreferencesActivity extends AppCompatActivity {

    private static final String TAG = MyPreferencesActivity.class.getSimpleName();

    private static final int SELECT_PHOTO = 1415;

    private boolean mIsBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (PreferenceAccess.with(this).hasBackgroundImage()) {
            showCopiedBackgroundImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case SELECT_PHOTO:
                final Uri imageUri = imageReturnedIntent.getData();
                new SaveImageTask().execute(imageUri);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mIsBusy) {
            super.onBackPressed();
        }
    }

    private void showProgressBar(final double progress) {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_prefs);
        progressBar.setVisibility(View.VISIBLE);
        final int maxProgress = progressBar.getMax();
        progressBar.setProgress((int) (progress * maxProgress));
        mIsBusy = true;
    }

    private void hideProgressBar() {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_prefs);
        progressBar.setVisibility(View.GONE);
        progressBar.setProgress(0);
        mIsBusy = false;
    }

    public void onGalleryButtonClick(View view) {
        if (mIsBusy) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            final int writePerm = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writePerm == PackageManager.PERMISSION_GRANTED) {

                startGalleryIntent();

            } else {
                showWriteAccessRequestDialog();
            }

        } else  {

            startGalleryIntent();

        }

    }

    public static final int READ_STORAGE_PERMISSION_CODe = 3912;

    private void showWriteAccessRequestDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.prefs_file_access_message);
        builder.setCancelable(false);
        builder.setPositiveButton(
                android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                MyPreferencesActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                READ_STORAGE_PERMISSION_CODe
                        );
                    }
                }
        );

        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults
    ) {
        // This is only being called by the Share Product Button,
        // since it is required to temporarily store Product Images.

        switch (requestCode) {
            case READ_STORAGE_PERMISSION_CODe: {
                // If product_enquire_button is cancelled, the result arrays are empty.
                if (grantResults.length < 1
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                startGalleryIntent();
            }
        }
    }

    private void startGalleryIntent() {
        final Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    private void showCopiedBackgroundImage() {

        hideProgressBar();

        final Bitmap image;
        try {
            image = BackgroundImageOpener.load();
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            removeImage();
            return;
        }

        final ImageView backgroundView = (ImageView) findViewById(R.id.image_prefs_bg);
        backgroundView.setImageBitmap(image);

        setRemoveButtonEnabled(true);
    }

    public void onRemoveImageButtonClick(View view) {
        if (!mIsBusy) {
            removeImage();
            Toast.makeText(this, R.string.prefs_img_del_msg, Toast.LENGTH_LONG).show();
        }
    }

    private void removeImage() {
        PreferenceAccess.with(this).setHasBackgroundImage(false);

        final ImageView backgroundView = (ImageView) findViewById(R.id.image_prefs_bg);
        backgroundView.setImageBitmap(null);
        setRemoveButtonEnabled(false);

        hideProgressBar();
    }

    private void setRemoveButtonEnabled(final boolean enabled) {
        final Button removeImageButton = (Button) findViewById(R.id.button_prefs_delete_img);
        removeImageButton.setEnabled(enabled);
    }

    private class SaveImageTask extends AsyncTask<Uri, Float, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar(0f);
        }

        @Override
        protected String doInBackground(Uri... uris) {
            return setAndShowSelectedImage(uris[0]);
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
            showProgressBar(values[0]);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            removeImage();
        }

        @Override
        protected void onPostExecute(String errorMessage) {
            super.onPostExecute(errorMessage);
            if (errorMessage == null) {
                PreferenceAccess.with(MyPreferencesActivity.this).setHasBackgroundImage(true);
                final Button removeImageButton = (Button) findViewById(R.id.button_prefs_delete_img);
                removeImageButton.setEnabled(true);

                Toast.makeText(
                        MyPreferencesActivity.this,
                        R.string.prefs_new_img_msg,
                        Toast.LENGTH_LONG
                ).show();

                showCopiedBackgroundImage();
            } else {
                Toast.makeText(
                        MyPreferencesActivity.this,
                        errorMessage,
                        Toast.LENGTH_LONG
                ).show();
                removeImage();
            }
        }


        private String setAndShowSelectedImage(@Nullable final Uri imageUri) {


            if (imageUri == null) {
                removeImage();
                return "Error 1209";
            }

            publishProgress(0f);

            final String copyErrorMessage = copyImageToAppDir(imageUri);
            if (copyErrorMessage != null) {
                removeImage();
                return copyErrorMessage;
            }

            publishProgress(0.5f);

            return null;
        }

        private String copyImageToAppDir(@NonNull Uri sourceUri) {

            final InputStream imageStream;
            try {

                imageStream = getContentResolver().openInputStream(sourceUri);

            } catch (final SecurityException secEx) {

                secEx.printStackTrace();
                return secEx.getLocalizedMessage();

            } catch (final FileNotFoundException fnfEx) {

                fnfEx.printStackTrace();
                return fnfEx.getLocalizedMessage();
            }

            final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

            final File destinationFile = BackgroundImageOpener.getFile();
            if (destinationFile == null) {
                return "Error 2193";
            }

            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(destinationFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return e.getLocalizedMessage();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }
    }
}
