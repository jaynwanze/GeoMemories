package com.example.ca3.ui.memorydetail;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ca3.R;
import com.example.ca3.databinding.FragmentMemoryDetailBinding;
import com.example.ca3.ui.fullscreenimage.FullScreenImageFragment;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.annotations.Nullable;

@AndroidEntryPoint
public class MemoryDetailFragment extends Fragment {

    private FragmentMemoryDetailBinding binding;
    private MemoryDetailViewModel memoryDetailViewModel;
    private static final String ARG_MEMORY_ID = "memory_id";

    public static MemoryDetailFragment newInstance(String memoryId) {
        MemoryDetailFragment fragment = new MemoryDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEMORY_ID, memoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMemoryDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        memoryDetailViewModel = new ViewModelProvider(this).get(MemoryDetailViewModel.class);

        if (getArguments() != null) {
            String memoryId = getArguments().getString(ARG_MEMORY_ID);
            memoryDetailViewModel.fetchMemoryDetails(memoryId);
        }

        // Observe memory details
        memoryDetailViewModel.getMemory().observe(getViewLifecycleOwner(), memory -> {
            if (memory != null) {
                Glide.with(this)
                        .asBitmap()
                        .load(memory.getPhotoUrl())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                binding.imageViewMemoryDetail.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Handle when image load is cleared
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                Log.e("MemoryDetailFragment", "Failed to load image.");
                            }
                        });

                binding.imageViewMemoryDetail.setOnClickListener
                        ( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Show full screen image
                                FullScreenImageFragment fragment = FullScreenImageFragment.newInstance(memory.getPhotoUrl());
                                fragment.show(getParentFragmentManager(), "FullScreenImageFragment");
                                Log.d("MemoryDetailFragment", "Image clicked");
                            }
                        });

                binding.textViewDescriptionContent.setText(memory.getDescription());
                binding.textViewLocationContent.setText(getLocationName(new Geocoder(getContext()), memory.getLocation()));
                binding.textViewWeatherContent.setText(memory.getWeatherInfo());
            }
        });

        return root;
    }

    private String getLocationName(Geocoder geocoder, GeoPoint geoPoint) {
        double latitude = geoPoint.getLatitude();
        double longitude = geoPoint.getLongitude();
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAdminArea();
            }
        } catch (IOException e) {
            Log.e("HomeViewModel", "Geocoder IOException: ", e);
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
