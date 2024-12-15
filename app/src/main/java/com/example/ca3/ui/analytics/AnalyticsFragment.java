package com.example.ca3.ui.analytics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ca3.R;
import com.example.ca3.databinding.FragmentAnalyticsBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AnalyticsFragment extends Fragment {

    private FragmentAnalyticsBinding binding;
    private AnalyticsViewModel analyticsViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observe data and update chart
        analyticsViewModel = new ViewModelProvider(this).get(AnalyticsViewModel.class);
        analyticsViewModel.getMemoriesPerMonth().observe(getViewLifecycleOwner() , memories -> {
            setupPieChartMemoriesByMonth(memories);
            setupLineChartTotalMemoriesOverTime(memories);
        });
        analyticsViewModel.getMemoriesPerLocation().observe(getViewLifecycleOwner(), this::setupPieChartMemoriesByLocation);

        return root;
    }

    private void setupPieChartMemoriesByMonth(Map<String, Integer> data) {
        PieChart pieChart = binding.pieChartMemoriesByMonth;
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Memories By Month");
        pieChart.setCenterTextSize(16f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "[Months]");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(getResources().getIntArray(R.array.chart_colors));

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(getResources().getColor(R.color.black));

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void setupLineChartTotalMemoriesOverTime(Map<String, Integer> data) {
        LineChart lineChart = binding.lineChartMemoriesOverTime;
        setupLineChartAppearance(lineChart);
        loadLineChartData(lineChart, data);
    }

    private void setupLineChartAppearance(LineChart lineChart) {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.animateX(1500);

        // Configure Legend
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
    }

    /**
     * Loads data into the LineChart for Total Memories Over Time.
     */
    private void loadLineChartData(LineChart lineChart, Map<String, Integer> data) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            entries.add(new Entry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        // Create LineDataSet for total memories
        LineDataSet dataSet = new LineDataSet(entries, "Total Memories");
        dataSet.setColor(ColorTemplate.MATERIAL_COLORS[0]); // Set a single color
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(ColorTemplate.MATERIAL_COLORS[0]);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Optional: Smooth lines

        // Create LineData with the dataset
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Configure XAxis
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setDrawAxisLine(true);

        // Configure YAxis
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);

        // Refresh chart
        lineChart.invalidate();
    }

    private void setupPieChartMemoriesByLocation(Map<String, Integer> stats) {
        PieChart pieChart = binding.pieChartMemoriesByLocation;
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

        PieDataSet dataSet = new PieDataSet(entries, "[Locations]");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(getResources().getIntArray(R.array.chart_colors));

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(getResources().getColor(R.color.black));

        pieChart.setData(data);
        pieChart.invalidate(); // Refresh chart
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
