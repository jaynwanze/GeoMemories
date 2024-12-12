package com.example.ca3.ui.fullscreenimage;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ca3.databinding.FragmentFullScreenImageBinding;

public class FullScreenImageFragment extends DialogFragment {

    private FragmentFullScreenImageBinding binding;
    private static final String ARG_PHOTO_URL = "photo_url";

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

        if (getArguments() != null) {
            String photoUrl = getArguments().getString(ARG_PHOTO_URL);
            Glide.with(this)
                    .asBitmap()
                    .load(photoUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            binding.imageViewFullScreen.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle when image load is cleared
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            // Handle load failure
                        }
                    });
        }

        // Handle close button click
        binding.buttonClose.setOnClickListener(v -> dismiss());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
