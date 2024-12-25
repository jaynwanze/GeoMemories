package com.example.ca3.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ca3.R;
import com.example.ca3.model.Place;
import com.example.ca3.ui.map.MapFragment;

import java.util.List;

public class NearbyPlacesAdapter extends RecyclerView.Adapter<NearbyPlacesAdapter.PlaceViewHolder> {

    private List<Place> placesList;
    private Context context;

    public NearbyPlacesAdapter(Context context, List<Place> placesList) {
        this.context = context;
        this.placesList = placesList;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nearby_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = placesList.get(position);
        holder.textViewPlaceName.setText(place.getName());
        holder.textViewPlaceVicinity.setText(place.getVicinity());

        // Set OnClickListener to navigate to MapFragment
        holder.itemView.setOnClickListener(v -> {
            if (context instanceof FragmentActivity) {
                FragmentActivity activity = (FragmentActivity) context;
                FragmentManager fragmentManager = activity.getSupportFragmentManager();

                // Create a new instance of MapFragment with place details
                MapFragment mapFragment = MapFragment.newInstance(
                        place.getName(),
                        place.getGeometry().getLocation().getLat(),
                        place.getGeometry().getLocation().getLng()
                );

                // Replace the current fragment with MapFragment
                Fragment existingFragment = fragmentManager.findFragmentByTag("MAP_FRAGMENT");
                fragmentManager.beginTransaction()
                        .replace(R.id.memory_detail_container, mapFragment , "MAP_FRAGMENT") // Ensure 'fragment_container' matches your Activity's container ID
                        .addToBackStack(null) // Add to back stack to enable navigation
                        .commit();
            } else {
                Toast.makeText(context, "Unable to open map.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return placesList != null ? placesList.size() : 0;
    }

    public void setPlacesList(List<Place> placesList) {
        this.placesList = placesList;
        notifyDataSetChanged();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPlaceName;
        TextView textViewPlaceVicinity;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPlaceName = itemView.findViewById(R.id.textViewPlaceName);
            textViewPlaceVicinity = itemView.findViewById(R.id.textViewPlaceVicinity);
        }
    }
}
