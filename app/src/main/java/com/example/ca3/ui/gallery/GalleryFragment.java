package com.example.ca3.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ca3.activity.MemoryDetailActivity;
import com.example.ca3.adapter.*;
import com.example.ca3.databinding.FragmentGalleryBinding;
import com.example.ca3.model.Memory;
import com.example.ca3.ui.gallery.GalleryViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private GalleryViewModel galleryViewModel;
    private MemoryAdapter memoryAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        // Setup RecyclerView
        memoryAdapter = new MemoryAdapter();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerView.setAdapter(memoryAdapter);

        // Set item click listener
        memoryAdapter.setOnItemClickListener(memory -> {
            Intent intent = new Intent(getContext(), MemoryDetailActivity.class);
            intent.putExtra("memory_id", memory.getId());
            startActivity(intent);
        });

        // Observe memories
        observeMemories();

        return root;
    }

    private void observeMemories() {
        galleryViewModel.getMemories().observe(getViewLifecycleOwner(), memories -> {
            if (memories != null && !memories.isEmpty()) {
                // Memories list is not empty
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.textViewGalleryEmpty.setVisibility(View.GONE);
                memoryAdapter.submitList(memories);
            } else {
                // Memories list is empty
                binding.recyclerView.setVisibility(View.GONE);
                binding.textViewGalleryEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        galleryViewModel.getMemories();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
