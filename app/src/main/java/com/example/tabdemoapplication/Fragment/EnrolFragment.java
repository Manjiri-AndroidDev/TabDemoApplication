package com.example.tabdemoapplication.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tabdemoapplication.Constants;
import com.example.tabdemoapplication.Model.UploadModel;
import com.example.tabdemoapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class EnrolFragment extends Fragment {

    EditText edFirstname, edLastname, edDob, edGender, edCountry, edState, edHome, edPhoneno, edTeleno;
    Button btnAdduser;
    ImageView imgProfile;
    String strFirstname, strLastname, strDob, strGender, strCountry, strState, strHome, strPhoneno, strTelephone,
            strImg;
    Uri photoURI;

    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider";
    private String mTempPhotoPath;
    private Bitmap mResultsBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enrol, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        edFirstname = (EditText) view.findViewById(R.id.edFirstname);
        edLastname = (EditText) view.findViewById(R.id.edLastname);
        edDob = (EditText) view.findViewById(R.id.edBirthdate);
        edGender = (EditText) view.findViewById(R.id.edGender);
        edCountry = (EditText) view.findViewById(R.id.edCountry);
        edState = (EditText) view.findViewById(R.id.edState);
        edHome = (EditText) view.findViewById(R.id.edHome);
        edPhoneno = (EditText) view.findViewById(R.id.edPhone);
        edTeleno = (EditText) view.findViewById(R.id.edTelephone);
        btnAdduser = (Button) view.findViewById(R.id.btnAdduser);
        imgProfile = (ImageView) view.findViewById(R.id.img_profile);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImgclick();
            }
        });

        btnAdduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAdduser(view);
            }
        });

        return view;
    }


    public void onImgclick() {

        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Launch the camera if the permission exists
            launchCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    launchCamera();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    /**
     * Creates a temporary image file and captures a picture to store in it.
     */
    private void launchCamera() {

        // Create the capture image intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(getContext());
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();

                // Get the content URI for the image file
                photoURI = FileProvider.getUriForFile(getContext(),
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);
                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If the image capture activity was called and was successful
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Process the image and set it to the TextView
            processAndSetImage();
        } else {
            // Otherwise, delete the temporary image file
            BitmapUtils.deleteImageFile(getContext(), mTempPhotoPath);
        }
    }

    /**
     * Method for processing the captured image and setting it to the imageview.
     */
    private void processAndSetImage() {
        // Resample the saved image to fit the ImageView
        mResultsBitmap = BitmapUtils.resamplePic(getContext(), mTempPhotoPath);
        // Set the new bitmap to the ImageView
        imgProfile.setImageBitmap(mResultsBitmap);
    }


    public void onAdduser(View view) {

        if (!validateName() | !validateLastName() | !validateDOB() | !validateGender() |
                !validateCountry() | !validateState() | !validateHomeTown() | !validatePhoneno() | !validateTelephone()) {
            return;
        }
        strFirstname = edFirstname.getText().toString();
        strLastname = edLastname.getText().toString();
        strDob = edDob.getText().toString();
        strGender = edGender.getText().toString();
        strCountry = edCountry.getText().toString();
        strState = edState.getText().toString();
        strHome = edHome.getText().toString();
        strPhoneno = edPhoneno.getText().toString();
        strTelephone = edTeleno.getText().toString();
        strImg = photoURI.toString();

        if (strPhoneno != strTelephone) {
            uploadFile();
        } else {
            Toast.makeText(getContext(), "Phone and Telephone number should not be same", Toast.LENGTH_SHORT);
        }

    }


    // all fields validation

    private boolean validateName() {
        if (edFirstname.getText().toString().isEmpty()) {
            edFirstname.setError("Field can't be empty");
            return false;
        } else if (edFirstname.getText().toString().length() > 15) {
            edFirstname.setError("Name too long");
            return false;
        } else {
            edFirstname.setError(null);
            return true;
        }
    }

    private boolean validateLastName() {
        if (edLastname.getText().toString().isEmpty()) {
            edLastname.setError("Field can't be empty");
            return false;
        } else if (edLastname.getText().toString().length() > 15) {
            edLastname.setError("Lastname too long");
            return false;
        } else {
            edLastname.setError(null);
            return true;
        }
    }

    private boolean validateDOB() {
        if (edDob.getText().toString().isEmpty()) {
            edDob.setError("Field can't be empty");
            return false;
        } else {
            edDob.setError(null);
            return true;
        }
    }

    private boolean validateGender() {
        if (edGender.getText().toString().isEmpty()) {
            edGender.setError("Field can't be empty");
            return false;
        } else {
            edGender.setError(null);
            return true;
        }
    }

    private boolean validateCountry() {
        if (edCountry.getText().toString().isEmpty()) {
            edCountry.setError("Field can't be empty");
            return false;
        } else {
            edCountry.setError(null);
            return true;
        }
    }

    private boolean validateState() {
        if (edState.getText().toString().isEmpty()) {
            edState.setError("Field can't be empty");
            return false;
        } else {
            edState.setError(null);
            return true;
        }
    }

    private boolean validateHomeTown() {
        if (edHome.getText().toString().isEmpty()) {
            edHome.setError("Field can't be empty");
            return false;
        } else {
            edHome.setError(null);
            return true;
        }
    }

    private boolean validatePhoneno() {
        if (edPhoneno.getText().toString().isEmpty()) {
            edPhoneno.setError("Field can't be empty");
            return false;
        } else {
            edPhoneno.setError(null);
            return true;
        }
    }

    private boolean validateTelephone() {
        if (edTeleno.getText().toString().isEmpty()) {
            edTeleno.setError("Field can't be empty");
            return false;
        } else {
            edTeleno.setError(null);
            return true;
        }
    }

    private void uploadFile() {
        //checking if file is available
        if (photoURI != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference
            StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + (photoURI));
            //adding the file to reference
            sRef.putFile(photoURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();
                            //displaying success toast
                            Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                            UploadModel upload = new UploadModel(
                                    (taskSnapshot.getStorage().getDownloadUrl().toString()),
                                    strFirstname
                                    , strPhoneno
                                    , strDob,
                                   strCountry);

                            //adding an upload to firebase database
                            String uploadId = mDatabase.push().getKey();
                            mDatabase.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }


    }
}
