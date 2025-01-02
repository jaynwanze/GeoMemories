package com.example.ca3.ui.fullscreenimage;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ca3.R;
import com.example.ca3.databinding.FragmentFullScreenImageBinding;


public class FullScreenImageFragment extends DialogFragment {

    private FragmentFullScreenImageBinding binding;
    private static final String ARG_PHOTO_URL = "photo_url";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2001;
    private Bitmap loadedBitmap;
    private Bitmap bitmapToSave;
    private FullScreenImageViewModel viewModel;

    public static FullScreenImageFragment newInstance(String photoUrl) {
        FullScreenImageFragment fragment = new FullScreenImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_URL, photoUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFullScreenImageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

         viewModel = new ViewModelProvider(this).get(FullScreenImageViewModel.class);

        if (getArguments() != null) {
            String photoUrl = getArguments().getString(ARG_PHOTO_URL);
            Glide.with(this)
                    .asBitmap()
                    .load(photoUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            loadedBitmap = resource; // Store the loaded bitmap
                            binding.imageViewFullScreen.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle when image load is cleared
                            loadedBitmap = null;
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            // Handle load failure
                            loadedBitmap = null;
                            Toast.makeText(getContext(), "Failed to load image.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Handle close button click
        binding.buttonClose.setOnClickListener(v -> dismiss());

        // Handle download button click
        binding.buttonDownload.setOnClickListener(v -> {
            if (loadedBitmap != null) {
                saveImageToGallery(loadedBitmap);
            } else {
                Toast.makeText(getContext(), "Image not loaded yet.", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    private void saveImageToGallery(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, use MediaStore
            viewModel.saveImageToMediaStore(bitmap);
        } else {
            // For Android 9 and below, use legacy method with permissions
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request WRITE_EXTERNAL_STORAGE permission
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                bitmapToSave = bitmap; // Store bitmap to save after permission is granted
            } else {
                // Permission already granted, proceed to save
                viewModel.saveImageToExternalStorageLegacy(bitmap);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (bitmapToSave != null) {
                    viewModel.saveImageToExternalStorageLegacy(bitmapToSave);
                    bitmapToSave = null;
                }
            } else {
                Toast.makeText(getContext(), "Permission denied. Cannot save image.", Toast.LENGTH_SHORT).show();
                bitmapToSave = null;
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        loadedBitmap = null;
        bitmapToSave = null;
    }
}
