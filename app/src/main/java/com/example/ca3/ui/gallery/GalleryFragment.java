package com.example.ca3.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ca3.activity.MemoryDetailActivity;
import com.example.ca3.adapter.MemoryAdapter;
import com.example.ca3.databinding.FragmentGalleryBinding;

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
