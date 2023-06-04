package com.example.shalevjavaproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.Task;
import utill.TaskApi;

public class TodoListsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button saveButton;
    private ProgressBar progressBar;
    private ImageView addPhotoBtn;
    private EditText title_et, task_details_et;
    private TextView currentUserTextView;
    private ImageView imageView;

    private String currentUserId;
    private String currentUserName;

    private int postActionId; //Check whether Activity Started from Create or Recycler Item Click

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private static final int GALLERY_CODE = 1;

    //connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Task");
    private Uri imageUri;
    private static final String Tag = "TodoListsActivity";

    private Task task_from_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_lists);

        getSupportActionBar().setElevation(0);
        //getSupportActionBar().hide();
        getSupportActionBar().setTitle("שלו | ניהול משימות.");
        getSupportActionBar().setSubtitle("הוסף שמור ונהל משימות יומיות.");
        //getSupportActionBar().setIcon(R.drawable.list);

        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        saveButton = findViewById(R.id.post_save_task_btn);

        progressBar = findViewById(R.id.post_progressBar);
        addPhotoBtn = findViewById(R.id.postCameraButton);
        imageView = findViewById(R.id.post_image_view);
        title_et = findViewById(R.id.post_title_et);
        task_details_et = findViewById(R.id.post_task_details_et);
        currentUserTextView = findViewById(R.id.post_username_tv);

        saveButton.setOnClickListener(this);

        addPhotoBtn.setOnClickListener(this);

        progressBar.setVisibility(View.INVISIBLE);


        postActionId = 0;

        task_from_list = getIntent().getParcelableExtra("task");
        postActionId = getIntent().getIntExtra("postActionId", 0);//If no intent default value zero
        if (postActionId == 1) {
            String imgUrl = task_from_list.getImageUrl();
            String title = task_from_list.getTitle();
            String details = task_from_list.getTask_details();
            title_et.setText(title.toString());
            task_details_et.setText(details);

            Picasso.get()
                    .load(imgUrl)
                    .placeholder(R.drawable.noimage)
                    .fit()
                    .into(imageView);
            saveButton.setText("Update");
        }


        if (TaskApi.getInstance() != null) {
            currentUserId = TaskApi.getInstance().getUserId();
            currentUserName = TaskApi.getInstance().getUsername().toString().trim();

            currentUserTextView.setText(currentUserName);
        }
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }

            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.post_save_task_btn:
                //saveTask
                if (imageUri != null && postActionId == 0)
                    saveTask();
                else if (postActionId == 1) {
                    updateTask();
                } else
                    Toast.makeText(this, "אנא צרפו תמונה מהגלריה דרך כפתור המצלמה.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.postCameraButton:
                //get image from gallery
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                //noinspection deprecation
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
        }
    }

    private void updateTask() {

        final String title = title_et.getText().toString().trim();
        final String details = task_details_et.getText().toString().trim();
        final String imageUrl = task_from_list.getImageUrl();
        //final String[] this_task_id = new String[1];

        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);


        //if user changeing the image! , if not go to else..
        if(imageUri != null){
            if (!TextUtils.isEmpty(title) &&
                    !TextUtils.isEmpty(details)){

            final StorageReference filepath = storageReference
                    .child("task_images")
                    .child("my_image_" + Timestamp.now().getSeconds());

            filepath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String imageUrl = uri.toString();
                                    updateEntry(title, details, imageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            saveButton.setEnabled(true);
//                                Log.e("Error: ","Image upload failure");
                        }
                    });


        } else emptyFields();
    }
    //if user doesn't change to a new image
        else
        {
            if (!TextUtils.isEmpty(title) &&
                    !TextUtils.isEmpty(details)) {

                updateEntry(title, details, imageUrl);

            }
            else emptyFields();
        }

}




    void updateEntry(String title,String details,String imageUrl) {


        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("task_details", details);
        data.put("imageUrl", imageUrl);
        data.put("timeAdded", new Timestamp(new Date()));

        collectionReference
                .whereEqualTo("userId" , currentUserId)
                .whereEqualTo("timeAdded", task_from_list.getTimeAdded())
                .addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error)
                    {

                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                        {
                            snapshot.getReference().update(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(TodoListsActivity.this, "עודכן בהצלחה!",
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            final Intent intent = new Intent(TodoListsActivity.this , AllTasksActivity.class);
                                            startActivity(intent);
                                            saveButton.setEnabled(true);
                                            finish();
                                        }

                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(TodoListsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }


    void emptyFields(){
        Toast.makeText(this, "שגיאה: שדות ריקים!", Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.INVISIBLE);
        saveButton.setEnabled(true);
    }



    private void saveTask() {
        String title = title_et.getText().toString().trim();
        String task_details = task_details_et.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if( !TextUtils.isEmpty(title) && !TextUtils.isEmpty(task_details) && imageView != null){

           final StorageReference filepath = storageReference.child("task_images")
                    .child("my_image_" + Timestamp.now().getSeconds());


            filepath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    Task task = new Task();
                                    task.setTitle(title);
                                    task.setTask_details(task_details);
                                    task.setTimeAdded(new Timestamp(new Date()));
                                    task.setImageUrl(imageUrl);
                                    task.setUserName(currentUserName.toString().trim());
                                    task.setUserId(currentUserId);


                                    collectionReference.add(task)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                            progressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(TodoListsActivity.this , AllTasksActivity.class));

                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(Tag , "onFailure: " + e.getMessage());
                                        }
                                    });

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });


        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if(data != null){
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        }

    }


}
