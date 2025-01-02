package com.example.ca3.utils;

import static androidx.core.app.ActivityCompat.requestPermissions;
import android.Manifest;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadManager {

    private static DownloadManager instance;
    private Application application;
    private Bitmap bitmapToSave;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2001;
    private Bitmap loadedBitmap;



    private DownloadManager() {
        // Private constructor to prevent direct instantiation
    }

    public static DownloadManager getInstance(Application application) {
        if (instance == null) {
            instance = new DownloadManager();
        }
        return instance;
    }

    public void saveImageToMediaStore(Bitmap bitmap) {
        String displayName = "IMG_" + System.currentTimeMillis() + ".jpg"; // Unique filename
        String mimeType = "image/jpeg";
        String relativeLocation = Environment.DIRECTORY_DCIM + "/Camera";

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, relativeLocation);
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 1);

        ContentResolver resolver = application.getApplicationContext().getContentResolver();
        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri imageUri = resolver.insert(collection, contentValues);

        if (imageUri != null) {
            try {
                OutputStream outputStream = resolver.openOutputStream(imageUri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }

                // Update IS_PENDING to make the image available
                contentValues.clear();
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0);
                resolver.update(imageUri, contentValues, null, null);

                Toast.makeText(application.getApplicationContext(), "Image saved to gallery.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(application.getApplicationContext(),  "Failed to save image.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(application.getApplicationContext(),  "Failed to create media entry.", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveImageToExternalStorageLegacy(Bitmap bitmap) {
        String displayName = "IMG_" + System.currentTimeMillis() + ".jpg"; // Unique filename
        String mimeType = "image/jpeg";
        String picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera";

        File appDir = new File(picturesDir);
        if (!appDir.exists()) {
            boolean created = appDir.mkdirs();
            if (!created) {
                Toast.makeText(application.getApplicationContext(),  "Failed to create directory.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        File imageFile = new File(appDir, displayName);
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // Make the image available in the gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(imageFile);
            mediaScanIntent.setData(contentUri);
            application.getApplicationContext().sendBroadcast(mediaScanIntent);

            Toast.makeText(application.getApplicationContext(),  "Image saved to gallery.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(application.getApplicationContext(),  "Failed to save image.", Toast.LENGTH_SHORT).show();
        }
    }
}
