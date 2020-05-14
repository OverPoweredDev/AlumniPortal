package com.omkarp.alumniportal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class SendPostActivity extends AppCompatActivity {

    //permission constants
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int STORAGE_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_GALLERY_CODE = 300;
    public static final int IMAGE_PICK_CAMERA_CODE = 400;

    ActionBar actionBar;

    //views
    EditText titleET, descriptionET;
    ImageView postIV, cameraIV, galleryIV;
    Button postBTN;

    //Firebase declaration
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference firebaseReference;
    DatabaseReference reference;

    String username, email, uid, timeStamp, userProfilePicture;

    StorageReference storageReference;

    //Path for Image
    private Uri image_uri = null;
    private String storagePath = "User_Post/";

    //storage
    private StorageReference storageReference1;

    //progressDialog
    private ProgressDialog progressDialog;

    //Array of Permissions
    private String[] cameraPermissions;
    private String[] storagePermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_post);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Upload New Post");

        //enable back buttons
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //firebase init
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseReference = firebaseDatabase.getReference();
        storageReference1 = getInstance().getReference();

        reference = firebaseDatabase.getReference("Users");

        Query query = reference.orderByChild("uid").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    username = "" + ds.child("name").getValue();
                    userProfilePicture = "" + ds.child("profilePicture").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //init views
        titleET = findViewById(R.id.postTitleET);
        descriptionET = findViewById(R.id.postDescET);
        postIV = findViewById(R.id.postImageIV);
        cameraIV = findViewById(R.id.cameraImageIV);
        galleryIV = findViewById(R.id.galleryImageIV);
        postBTN = findViewById(R.id.uploadButton);

        //initialise permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //get image
        cameraIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //camera
                if (checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
            }
        });

        galleryIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gallery
                if (checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            }
        });

        //button clicked
        postBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleET.getText().toString().trim();
                String description = descriptionET.getText().toString().trim();
                timeStamp = String.valueOf(System.currentTimeMillis());
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                    Toast.makeText(SendPostActivity.this, "Enter Title AND Description", Toast.LENGTH_SHORT).show();
                } else {
                    if (image_uri == null) {
                        //send post without image
                        sendPostNoImage(title, description, timeStamp, username, userProfilePicture);
                    } else {
                        //send post with image
                        sendPostWithImage(title, description, timeStamp, username, userProfilePicture, image_uri);
                    }
                }
            }
        });
    }

    private void sendPostWithImage(final String title, final String description, final String timeStamp, final String username, final String userProfilePicture, final Uri image_uri) {
        final DatabaseReference databaseReference = firebaseDatabase.getReference();

        String postPath = storagePath + "post_" + user.getUid() + timeStamp;
        StorageReference storageReference2 = storageReference1.child(postPath);
        storageReference2.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //add user image url to database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();

                        if (uriTask.isSuccessful()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("username", username);
                            hashMap.put("title", title);
                            hashMap.put("description", description);
                            hashMap.put("timeStamp", timeStamp);
                            hashMap.put("voteCount", 0);
                            hashMap.put("postImage", downloadUri.toString());
                            hashMap.put("userProfilePicture", userProfilePicture);
                            databaseReference.child("Posts").push().setValue(hashMap);
                        }
                    }
                });
    }

    private void sendPostNoImage(final String title, String description, String timeStamp, String username, String userProfilePicture) {
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", username);
        hashMap.put("title", title);
        hashMap.put("description", description);
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("voteCount", 0);
        hashMap.put("userProfilePicture", userProfilePicture);
        databaseReference.child("Posts").push().setValue(hashMap);
    }

    private boolean checkCameraPermission() {
        //checks if camera permissions are enabled
        //true if yes
        //false if no
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        //checks if storage permissions are enabled
        //true if yes
        //false if no
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //handle permission cases - Accept and Denied
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                //picking from camera, check if camera and storage permissions are enabled
                if (grantResults.length > 0) {

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        //permissions enabled
                        pickFromCamera();
                    } else {
                        //permissions disabled
                        Toast.makeText(SendPostActivity.this, "Please enable Permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE: {
                //picking from gallery, check if storage permissions are enabled
                if (grantResults.length > 0) {
                    Toast.makeText(SendPostActivity.this, "in here", Toast.LENGTH_SHORT).show();

                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        //permissions enabled
                        pickFromGallery();
                    } else {
                        //permissions disabled
                        Toast.makeText(SendPostActivity.this, "Please enable Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //after picking image form camera/gallery
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                postIV.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                postIV.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "description");

        //put image uri
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout_action) {
            mAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}
