package com.example.ca3.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private BitmapDownloadListener listener;

    public BitmapDownloaderTask(BitmapDownloadListener listener) {
        this.listener = listener;
    }

    // ... (doInBackground method remains the same) ...

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (listener != null) {
            listener.onBitmapDownloaded(bitmap);
        }
    }
    // Define an interface for the callback
    public interface BitmapDownloadListener {
        Bitmap onBitmapDownloaded(Bitmap bitmap);
    }

    // Modify your onSuccess method
    public void onSuccess(String downloadUrl, BitmapDownloadListener listener) {
        new BitmapDownloaderTask(listener).execute(downloadUrl);
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}