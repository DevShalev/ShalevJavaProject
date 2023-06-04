package com.example.shalevjavaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import utill.TaskApi;

public class CreateAccountActivity extends AppCompatActivity {

    private Button createAccountBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //FireStore Connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    private EditText emailEditText , passwordEditText , userNameEditText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        getSupportActionBar().setElevation(0);
        //getSupportActionBar().hide();
        getSupportActionBar().setTitle("שלו | ניהול משימות.");
        getSupportActionBar().setSubtitle("הוסף שמור ונהל משימות יומיות.");
        //getSupportActionBar().setIcon(R.drawable.list);

        firebaseAuth = FirebaseAuth.getInstance();

        createAccountBtn = findViewById(R.id.create_acc_btn);
        progressBar = findViewById(R.id.create_progress);
        emailEditText = findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);
        userNameEditText = findViewById(R.id.username_account);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null){

                }else{

                }

            }
        };

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(emailEditText.getText().toString())
                    && !TextUtils.isEmpty(passwordEditText.getText().toString())
                    && !TextUtils.isEmpty(userNameEditText.getText().toString())){

                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String username = userNameEditText.getText().toString().trim();

                    createUserEmailAccount(email , password , username);

                }else {
                    Toast.makeText(CreateAccountActivity.this,
                            "Empty Fields Not Allowed",
                            Toast.LENGTH_SHORT)
                            .show();
                }


            }
        });

    }

    private void createUserEmailAccount(String email , String password , String username){
        if( !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username) ){

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email , password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                currentUser = firebaseAuth.getCurrentUser();
                                String currentUserId = currentUser.getUid();

                                //create a user Map so we can create a user in the User collection
                                Map<String , String> userObj = new HashMap<>();
                                userObj.put("password" , passwordEditText.getText().toString());
                                userObj.put("email" , emailEditText.getText().toString());
                                userObj.put("userId" , currentUserId);
                                userObj.put("userName" , username);





                                //save to our firestore database
                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if((task.getResult()).exists()){
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    String name = task.getResult()
                                                                            .getString("username");

                                                                    TaskApi taskApi = TaskApi.getInstance();
                                                                    taskApi.setUserId(currentUserId);
                                                                    taskApi.setUsername(name);



                                                                    Intent intent = new Intent(CreateAccountActivity.this , TodoListsActivity.class);
                                                                    intent.putExtra("userId" , currentUserId);
                                                                    intent.putExtra("username" , name);




                                                                    startActivity(intent);

                                                                }else{
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                }
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });


                            }else {

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }else{

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}