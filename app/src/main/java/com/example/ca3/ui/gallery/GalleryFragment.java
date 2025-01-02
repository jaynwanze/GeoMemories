package com.example.ca3.ui.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ca3.activity.MemoryDetailActivity;
import com.example.ca3.adapter.MemoryAdapter;
import com.example.ca3.databinding.FragmentGalleryBinding;
import com.example.ca3.model.Memory;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private GalleryViewModel galleryViewModel;
    private MemoryAdapter memoryAdapter;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2001;
    private Bitmap loadedBitmap;
    private Bitmap bitmapToSave;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        //setup adapter based off gallery display type
        String galleryType = galleryViewModel.getGalleryTypeUserPreferences();
        if (galleryType != null) {
            if (galleryType.equals("Grid")) {
                binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            } else if (galleryType.equals("List")) {
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.recyclerView.setHasFixedSize(true);
                binding.recyclerView.setNestedScrollingEnabled(false);
                binding.recyclerView.setItemAnimator(null);
                binding.recyclerView.setClipToPadding(false);
            }
        }
        // Setup RecyclerView
        memoryAdapter = new MemoryAdapter();
        binding.recyclerView.setAdapter(memoryAdapter);

        // Set item click listener
        memoryAdapter.setOnItemClickListener(memory -> {
            Intent intent = new Intent(getContext(), MemoryDetailActivity.class);
            intent.putExtra("memory_id", memory.getId());
            startActivity(intent);
        });

        // Set item long click listener
        memoryAdapter.setOnItemLongClickListener(this::showMemoryOptionsDialog);

        // Setup SearchView
        setupSearchView();

        // Observe memories
        observeMemories();

        return root;
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Filter when the user submits the query
                memoryAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter in real-time as the user types
                memoryAdapter.getFilter().filter(newText);
                return false;
            }
        });

        binding.searchView.setOnCloseListener(() -> {
            // Reset the adapter to show all items when the search is closed
            memoryAdapter.getFilter().filter("");
            return false;
        });
    }


    private void observeMemories() {
        galleryViewModel.getMemories().observe(getViewLifecycleOwner(), memories -> {
            if (memories == null || memories.isEmpty()) {
                // Memories list is empty
                binding.recyclerView.setVisibility(View.GONE);
                binding.textViewGalleryEmpty.setVisibility(View.VISIBLE);
            } else {
                // Memories list is not empty
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.textViewGalleryEmpty.setVisibility(View.GONE);
                memoryAdapter.submitList(memories);
            }
        });
    }

    private void showMemoryOptionsDialog(Memory memory, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Memory Options")
                .setMessage("Choose an action for this memory.")
                .setPositiveButton("Download", (dialog, which) -> {
                    loadedBitmap = galleryViewModel.getMemoryPhoto(memory);
                    if (loadedBitmap == null) {
                        Toast.makeText(getContext(), "Failed to download memory photo", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveImageToGallery(loadedBitmap);
                    Toast.makeText(getContext(), "Memory Photo has been downloaded to camera roll", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Remove", (dialog, which) -> {
                    galleryViewModel.removeMemory(memory);
                    Toast.makeText(getContext(), "Remove Memory", Toast.LENGTH_SHORT).show();
                    memoryAdapter.notifyItemRemoved(position);
                })
                .setNeutralButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    // Notify the adapter to refresh the item (since it was swiped)
                    memoryAdapter.notifyItemChanged(position);
                })
                .setCancelable(false)
                .show();
    }

    private void saveImageToGallery(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, use MediaStore
            galleryViewModel.saveImageToMediaStore(bitmap);
        } else {
            // For Android 9 and below, use legacy method with permissions
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request WRITE_EXTERNAL_STORAGE permission
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                bitmapToSave = bitmap; // Store bitmap to save after permission is granted
            } else {
                // Permission already granted, proceed to save
                galleryViewModel.saveImageToExternalStorageLegacy(bitmap);
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
                    galleryViewModel.saveImageToExternalStorageLegacy(bitmapToSave);
                    bitmapToSave = null;
                }
            } else {
                Toast.makeText(getContext(), "Permission denied. Cannot save image.", Toast.LENGTH_SHORT).show();
                bitmapToSave = null;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //refresh the memories list
        galleryViewModel.refreshData();
        observeMemories();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
