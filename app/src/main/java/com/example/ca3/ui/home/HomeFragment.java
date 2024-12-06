package com.example.ca3.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ca3.R;
import com.example.ca3.activity.CaptureMemoryActivity;
import com.example.ca3.activity.MapActivity;
import com.example.ca3.activity.MemoryDetailActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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

        // Setup Recent Memories RecyclerView
        setupRecentMemoriesRecyclerView();

        // Observe Recent Memories
        homeViewModel.getRecentMemories().observe(getViewLifecycleOwner(), memories -> {
            recentMemoriesAdapter.submitList(memories);
        });

        // Observe Statistics Data
        homeViewModel.getMemoriesStatistics().observe(getViewLifecycleOwner(), stats -> {
            setupPieChart(stats);
        });

        // Setup Quick Actions Click Listeners
        setupQuickActions();

        return root;
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
        pieChart.setCenterText("Memories Distribution");
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
        dataSet.setColors(getResources().getIntArray(R.array.pie_chart_colors));

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(getResources().getColor(R.color.black));

        pieChart.setData(data);
        pieChart.invalidate(); // Refresh chart
    }

    private void setupQuickActions() {
        // Add Memory Card Click
        binding.cardAddMemory.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CaptureMemoryActivity.class);
            startActivity(intent);
        });

        // View Map Card Click
        binding.cardViewMap.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MapActivity.class);
            startActivity(intent);
        });

        // You can add more quick actions here
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}