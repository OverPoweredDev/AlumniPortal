package com.omkarp.alumniportal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    //permission constants
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int STORAGE_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_GALLERY_CODE = 300;
    public static final int IMAGE_PICK_CAMERA_CODE = 400;

    //declarations
    private TextView nameTV, emailTV, phoneTV, yearTV;
    private ImageView avatarIV;
    private FloatingActionButton fab;
    private Uri image_uri;

    //storage
    private StorageReference storageReference1;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    //path for image
    private String storagePath = "User_Profile_Image/";

    //progressDialog
    private ProgressDialog progressDialog;

    //array of permissions
    private String[] cameraPermissions;
    private String[] storagePermissions;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //initialise firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        storageReference1 = getInstance().getReference();

        //initialise permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //initialise views
        nameTV = view.findViewById(R.id.nameTV);
        emailTV = view.findViewById(R.id.emailTV);
        phoneTV = view.findViewById(R.id.phoneTV);
        yearTV = view.findViewById(R.id.yearTV);
        avatarIV = view.findViewById(R.id.avatarIV);
        fab = view.findViewById(R.id.fab);

        //initialise ProgressDialog
        progressDialog = new ProgressDialog(getActivity());

        Query query = reference.orderByChild("uid").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String email = "" + ds.child("email").getValue();
                    String name = "" + ds.child("name").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String profilePicture = "" + ds.child("profilePicture").getValue();
                    String yearPassedOut = "" + ds.child("yearPassedOut").getValue();

                    //set data
                    nameTV.setText(name);
                    emailTV.setText(email);
                    phoneTV.setText(phone);
                    yearTV.setText(yearPassedOut);
                    try {
                        Picasso.get().load(profilePicture).into(avatarIV);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_add_image).into(avatarIV);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //handle Floating Action Button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditUserProfileDialog();
            }
        });

        return view;
    }

    private void showEditUserProfileDialog() {

        //set options
        String[] options = {"Edit Name", "Edit Phone", "Edit Year of Passing Out", "Edit Photo"};

        //create ActionDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Options");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //name
                    progressDialog.setMessage("Updating Name");
                    showNamePhoneYearUpdateDialog("name");
                } else if (which == 1) {
                    //phone
                    progressDialog.setMessage("Updating Phone Number");
                    showNamePhoneYearUpdateDialog("phone");
                } else if (which == 2) {
                    //yearPassedOut
                    progressDialog.setMessage("Updating Year");
                    showNamePhoneYearUpdateDialog("yearPassedOut");
                } else if (which == 3) {
                    //photo
                    progressDialog.setMessage("Updating Profile Picture");
                    showEditPhotoDialog();
                }

            }
        });
        builder.create().show();

    }

    private void showNamePhoneYearUpdateDialog(final String keyName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update " + keyName);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter " + keyName);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)) {
                    progressDialog.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(keyName, value);

                    reference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Succesfully updated", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "" + keyName + " cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private boolean checkCameraPermission() {
        //checks if camera permissions are enabled
        //true if yes
        //false if no
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        //checks if storage permissions are enabled
        //true if yes
        //false if no
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private void showEditPhotoDialog() {

        //set options
        String[] options = {"Camera", "Gallery"};

        //create ActionDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Image from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //camera
                    if (checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                    progressDialog.setMessage("Updating Profile Pic");
                } else if (which == 1) {
                    //gallery
                    if (checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                    progressDialog.setMessage("Updating Profile Pic");
                }
            }
        });
        builder.create().show();

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
                        Toast.makeText(getActivity(), "Please enable Permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE: {
                //picking from gallery, check if storage permissions are enabled
                if (grantResults.length > 0) {
                    Toast.makeText(getActivity(), "in here", Toast.LENGTH_SHORT).show();

                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        //permissions enabled
                        pickFromGallery();
                    } else {
                        //permissions disabled
                        Toast.makeText(getActivity(), "Please enable Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //after picking image form camera/galeryt
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                UploadPost(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                UploadPost(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UploadPost(Uri imguri) {
        progressDialog.show();
        String userProfileAndPath = storagePath + "user_" + user.getUid();
        StorageReference storageReference2 = storageReference1.child(userProfileAndPath);
        storageReference2.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //add user image url to database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();

                        if (uriTask.isSuccessful()) {
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("profilePicture", downloadUri.toString());
                            reference.child(user.getUid()).updateChildren(result)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //problem
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "description");

        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);

    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //stay here
            //profileTV.setText(user.getEmail());
        } else {
            //goto mainactivity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    //options menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //handle item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout_action) {
            mAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}