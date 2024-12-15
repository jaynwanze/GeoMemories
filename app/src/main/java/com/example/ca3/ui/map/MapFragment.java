// File: com/example/ca3/ui/map/MapFragment.java

package com.example.ca3.ui.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ca3.R;
import com.example.ca3.activity.MemoryDetailActivity;
import com.example.ca3.databinding.FragmentMapBinding;
import com.example.ca3.model.Memory;
import com.bumptech.glide.Glide;
import com.example.ca3.utils.UserPreferencesManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private MapViewModel mapViewModel;
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private UserPreferencesManager userPreferencesManager;
    private Map<String, Marker> memoryMarkers = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userPreferencesManager = UserPreferencesManager.getInstance(getContext());

        // Obtain ViewModel
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        // Initialize Map
        initializeMap();

        // Setup Map Type Spinner
        setupMapTypeSpinner();

        // Observe Memories and Current Location
        observeViewModel();

        return root;
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(getContext(), "Error initializing map.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMapTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.map_types_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMapType.setAdapter(adapter);

        binding.spinnerMapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMap != null) {
                    String selectedMapType = parent.getItemAtPosition(position).toString();
                    switch (selectedMapType.toLowerCase()) {
                        case "satellite":
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            break;
                        case "terrain":
                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            break;
                        case "hybrid":
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            break;
                        default:
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default map type
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });
    }

    private void observeViewModel() {
        // Observe memories to add markers
        mapViewModel.getMemories().observe(getViewLifecycleOwner(), memories -> {
            if (mMap != null) {
                // Remove existing memory markers
                for (Marker marker : memoryMarkers.values()) {
                    marker.remove();
                }
                memoryMarkers.clear();

                for (Memory memory : memories) {
                    if (memory.getUserId().equals(userPreferencesManager.getUserId())) {
                        LatLng location = new LatLng(memory.getLocation().getLatitude(),
                                memory.getLocation().getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(location)
                                .title(memory.getDescription())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        Marker marker = mMap.addMarker(markerOptions);
                        if (marker != null) {
                            marker.setTag(memory.getId());
                            memoryMarkers.put(memory.getId(), marker);
                        }
                    }
                }
            }
        });

        // Observe current location to move camera
        mapViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), location -> {
            if (location != null && mMap != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f));
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        binding.progressBarMap.setVisibility(View.VISIBLE);

        // Enable My Location Layer if permissions are granted
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Set Marker Click Listener
        mMap.setOnMarkerClickListener(marker -> {
            String memoryId = (String) marker.getTag();
            if (memoryId != null) {
                Intent intent = new Intent(getContext(), MemoryDetailActivity.class);
                intent.putExtra("memory_id", memoryId);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Set Custom InfoWindowAdapter
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        binding.progressBarMap.setVisibility(View.GONE);
    }

    private void enableMyLocation() {
        if (mMap == null) return;

        try {
            mMap.setMyLocationEnabled(true);
            mapViewModel.fetchCurrentLocation();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean granted = false;
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        granted = true;
                        break;
                    }
                }
            }

            if (granted) {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(requireContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                }
            } else {
                // Check if user selected "Don't ask again"
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
                if (showRationale) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Location Permission Needed")
                            .setMessage("This app requires location access to display your memories on the map.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                requestPermissions(new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                }, LOCATION_PERMISSION_REQUEST_CODE);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                Toast.makeText(getContext(),
                                        "Location permissions are required to display your location.",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .create()
                            .show();
                } else {
                    Toast.makeText(getContext(),
                            "Location permissions are required to display your location.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapViewModel.refreshMemories();
        //mapViewModel.fetchCurrentLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Custom InfoWindowAdapter Class
    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View window;

        CustomInfoWindowAdapter() {
            window = LayoutInflater.from(getContext()).inflate(R.layout.custom_info_window, null);
        }

        private void renderWindowText(Memory memory) {
            TextView title = window.findViewById(R.id.textViewInfoTitle);
            TextView snippet = window.findViewById(R.id.textViewInfoSnippet);
            TextView weather = window.findViewById(R.id.textViewInfoWeather);
            ImageView image = window.findViewById(R.id.imageViewInfo);

            title.setText(memory.getDescription());
            snippet.setText("Lat: " + memory.getLocation().getLatitude() +
                    ", Lng: " + memory.getLocation().getLongitude());
            weather.setText("Weather: " + memory.getWeatherInfo());

            Glide.with(getContext())
                    .load(memory.getPhotoUrl())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(image);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            String memoryId = (String) marker.getTag();
            if (memoryId != null) {
                Memory memory = mapViewModel.getMemoryById(memoryId);
                if (memory != null) {
                    renderWindowText(memory);
                }
            }
            return window;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
}
