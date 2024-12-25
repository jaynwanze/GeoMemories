// File: com/example/ca3/ui/map/MapFragment.java

package com.example.ca3.ui.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
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
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
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
    private static final String ARG_PLACE_NAME = "place_name";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";
    private String selectedPlaceName = null;
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;

    public static MapFragment newInstance(String placeName, double latitude, double longitude) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_NAME, placeName);
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userPreferencesManager = UserPreferencesManager.getInstance(getContext());

        // Obtain ViewModel
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        // Retrieve arguments if present
        if (getArguments() != null) {
            selectedPlaceName = getArguments().getString(ARG_PLACE_NAME);
            selectedLatitude = getArguments().getDouble(ARG_LATITUDE);
            selectedLongitude = getArguments().getDouble(ARG_LONGITUDE);
        }

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

        mapViewModel.getSelectedPlace().observe(getViewLifecycleOwner(), place -> {
            if (place != null) {
                displaySelectedPlace(place.getName(), place.getGeometry().getLocation().getLat(),
                        place.getGeometry().getLocation().getLng());
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
                //set info window click listener
                //marker.showInfoWindow();
                // Handle marker click
                Intent intent = new Intent(getContext(), MemoryDetailActivity.class);
                intent.putExtra("memory_id", memoryId);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Set Info Window Click Listener
        mMap.setOnInfoWindowClickListener(marker -> {
            String memoryId = (String) marker.getTag();
            if (memoryId != null) {
                // Handle memory marker info window click
                Intent intent = new Intent(getContext(), MemoryDetailActivity.class);
                intent.putExtra("memory_id", memoryId);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Selected Place: " + marker.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set Custom InfoWindowAdapter
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Check if specific place details are provided
        if (selectedPlaceName != null && selectedLatitude != 0.0 && selectedLongitude != 0.0) {
            displaySelectedPlace(selectedPlaceName, selectedLatitude, selectedLongitude);
        }

        binding.progressBarMap.setVisibility(View.GONE);
    }

    private void displaySelectedPlace(String placeName, double lat, double lng) {
        LatLng placeLatLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(placeLatLng)
                .title(placeName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)); // Use a distinct color for selected place
        Marker marker = mMap.addMarker(markerOptions);
        if (marker != null) {
            // Do not set a tag for selected place markers
            marker.showInfoWindow(); // Automatically show default info window
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 15f)); // Zoom in on the selected place
        }
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

    private String getLocationName(Geocoder geocoder, GeoPoint geoPoint) {
        double latitude = geoPoint.getLatitude();
        double longitude = geoPoint.getLongitude();
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            Log.e("HomeViewModel", "Geocoder IOException: ", e);
        }
        return null;
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
            snippet.setText(getLocationName(new Geocoder(getContext()), memory.getLocation()));
            weather.setText("Weather: " + memory.getWeatherInfo());

            Glide.with(getContext())
                    .load(memory.getPhotoUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(image);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            String memoryId = (String) marker.getTag();
            if (memoryId != null) {
                Memory memory = mapViewModel.getMemoryById(memoryId);
                if (memory != null) {
                    renderWindowText(memory);
                    return window;
                }
            }
            // Return null for default info window
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            // Returning null uses the default contents
            return null;
        }
    }

}
