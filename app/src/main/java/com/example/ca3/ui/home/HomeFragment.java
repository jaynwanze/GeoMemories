package com.example.ca3.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ca3.R;
import com.example.ca3.activity.CaptureMemoryActivity;
import com.example.ca3.activity.MapActivity;
import com.example.ca3.activity.MemoryDetailActivity;
import com.github.mikephil.charting.charts.PieChart;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ca3.adapter.RecentMemoriesAdapter;
import com.example.ca3.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private RecentMemoriesAdapter recentMemoriesAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observe Current User
        observeCurrentUser();

        // Setup Recent Memories RecyclerView
        setupRecentMemoriesRecyclerView();

        // Observe Recent Memories
        observeRecentMemories();

        // Setup Stats Chart
        observeStatisticsData();

        // Setup Quick Actions Click Listeners
        setupQuickActions();

        // HomeFragment.java
        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                // Display a Snackbar, Toast, or an error view
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }

    public void observeCurrentUser() {
        // Observe Current User Data
        //set welcome heading for current user
        homeViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.textViewWelcome.setText("Welcome, " + user.getName());
            }
        });
    }

    public void observeStatisticsData() {
        // Observe Statistics Data
        homeViewModel.getMemoriesStatistics().observe(getViewLifecycleOwner(), stats -> {
            if (stats == null || stats.isEmpty()) {
                return;
            }
            setupPieChart(stats);
        });
    }

    private void observeRecentMemories() {
        homeViewModel.getRecentMemories().observe(getViewLifecycleOwner(), memories -> {
            if (memories != null && !memories.isEmpty()) {
                // Memories list is not empty
                binding.recyclerViewRecentMemories.setVisibility(View.VISIBLE);
                binding.textViewRecentMemoriesEmpty.setVisibility(View.GONE);
                recentMemoriesAdapter.submitList(memories);
            } else {
                // Memories list is empty
                binding.recyclerViewRecentMemories.setVisibility(View.GONE);
                binding.textViewRecentMemoriesEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupRecentMemoriesRecyclerView() {
        recentMemoriesAdapter = new RecentMemoriesAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerViewRecentMemories.setLayoutManager(layoutManager);
        binding.recyclerViewRecentMemories.setAdapter(recentMemoriesAdapter);

        recentMemoriesAdapter.setOnItemClickListener(memory -> {
            // Handle memory item click, e.g., open MemoryDetailActivity
            Intent intent = new Intent(getContext(), MemoryDetailActivity.class);
            intent.putExtra("memory_id", memory.getId());
            startActivity(intent);
        });
    }

    private void setupPieChart(Map<String, Integer> stats) {
        PieChart pieChart = binding.pieChartMemories;
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Memories By Location");
        pieChart.setCenterTextSize(16f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Locations");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(getResources().getIntArray(R.array.chart_colors_2));

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(getResources().getColor(R.color.black));

        pieChart.setData(data);
        pieChart.invalidate(); // Refresh chart
    }

    private void setupQuickActions() {
        // Add Memory Card Click
        Glide.with(binding.imageViewAddMemory.getContext()).load(
                        android.R.drawable.ic_menu_gallery)
                .into(binding.imageViewAddMemory);


        binding.cardAddMemory.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CaptureMemoryActivity.class);
            startActivity(intent);
        });

        // View Map Card Click
        Glide.with(binding.imageViewMap.getContext()).load(
                android.R.drawable.ic_menu_mapmode)
                .into(binding.imageViewMap);

        binding.cardViewMap.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MapActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when the fragment is resumed
        homeViewModel.refreshData();
        observeRecentMemories();
        observeStatisticsData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}