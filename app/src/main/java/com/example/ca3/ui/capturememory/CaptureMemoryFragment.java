package com.example.ca3.ui.capturememory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ca3.activity.MainActivity;
import com.example.ca3.databinding.FragmentCaptureMemoryBinding;
import com.example.ca3.model.Memory;
import com.example.ca3.utils.UserPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import id.zelory.compressor.Compressor;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

@AndroidEntryPoint
public class CaptureMemoryFragment extends Fragment {

    private FragmentCaptureMemoryBinding binding;
    private CaptureMemoryViewModel captureMemoryViewModel;
    private Uri photoUri;
    private String currentPhotoPath;
    private UserPreferencesManager userPreferencesManager;

    private ProgressBar progressBar; // Ensure you have a ProgressBar in your layout

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    // Display the image from the Uri
                    binding.imageView.setImageURI(photoUri);
                    binding.imageView.invalidate(); // Refresh ImageView
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCaptureMemoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        captureMemoryViewModel = new ViewModelProvider(this).get(CaptureMemoryViewModel.class);

        // Request Camera Permission
        if (!allPermissionsGranted()) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // Capture Photo Button
        binding.buttonCapture.setOnClickListener(v -> openCamera());

        // Save Memory Button
        binding.buttonSaveMemory.setOnClickListener(v -> saveMemory());

        return root;
    }

    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA
            // Removed WRITE_EXTERNAL_STORAGE for API 29+; use app-specific directories
    };

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(getContext(), "Error creating image file.", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + ".fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                cameraLauncher.launch(intent);
            }
        } else {
            Toast.makeText(getContext(), "No camera app found.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + UUID.randomUUID().toString() + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveMemory() {
        String description = binding.editTextDescription.getText().toString().trim();
        if (description.isEmpty()) {
            binding.editTextDescription.setError("Description is required.");
            return;
        }

        if (photoUri == null) {
            Toast.makeText(getContext(), "Please capture a photo.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Optionally, verify that the file exists
        if (!fileExists(photoUri)) {
            Toast.makeText(getContext(), "Photo file does not exist.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current location
        Location currentLocation = captureMemoryViewModel.getCurrentLocation();

        if (currentLocation == null) {
            Toast.makeText(getContext(), "Unable to get current location.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user ID
        String currentUserId = userPreferencesManager.getUserId();
        if (currentUserId == null) {
            Toast.makeText(getContext(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Memory Object
        Memory memory = new Memory();
        memory.setDescription(description);
        memory.setLocation(new com.google.firebase.firestore.GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
        memory.setTimestamp(System.currentTimeMillis() / 1000); // Unix timestamp
        memory.setUserId(currentUserId);


        compressAndUploadImage(photoUri , currentUserId, memory);

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("PROCESSING_MEMORY", true);
        startActivity(intent);

    }

    private void compressAndUploadImage(Uri imageUri, String uid, Memory memory) {

        try {
        File imageFile = getFileFromUri(imageUri);

        if (!imageFile.exists() || !imageFile.isFile()) {
            Toast.makeText(getContext(), "Invalid image file.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with compression
        new Compressor(requireContext())
                .setMaxWidth(800)
                .setMaxHeight(800)
                .setQuality(80)
                .compressToFileAsFlowable(imageFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file -> {
                    Uri compressedUri = Uri.fromFile(file);
                    // Handle photo upload and memory saving
                    captureMemoryViewModel.saveMemory( uid, memory, compressedUri, new CaptureMemoryViewModel.SaveCallback() {
                        @Override
                        public void onSuccess() {
                            // Clear inputs
                            binding.imageView.setImageURI(null);
                            binding.editTextDescription.setText("");
                            photoUri = null;
                            currentPhotoPath = null;
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getContext(), "Failed to save memory.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });
                }, throwable -> {
                    // Handle compression error
                    throwable.printStackTrace();
                    Toast.makeText(getContext(), "Image compression failed.", Toast.LENGTH_SHORT).show();
                });
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to process image.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        finally {
            getActivity().finish();
        }
    }

    private File getFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Unable to open input stream from Uri");
        }
        File tempFile = File.createTempFile("compressed_", ".jpg", requireContext().getCacheDir());
        OutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
        return tempFile;
    }


    private boolean fileExists(Uri uri) {
        try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
            return cursor != null && cursor.getCount() > 0;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                // Permissions granted, proceed with capturing photo
                // Optionally, you can enable certain UI elements
            } else {
                Toast.makeText(getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                // Optionally, disable certain functionality
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}