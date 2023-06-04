package com.example.shalevjavaproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;

import utill.TaskApi;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton , createAccount;

    private AutoCompleteTextView emailAddress;
    private EditText password;

    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setElevation(0);
        //getSupportActionBar().hide();
        getSupportActionBar().setTitle("שלו | ניהול משימות.");
        getSupportActionBar().setSubtitle("הוסף שמור ונהל משימות יומיות.");
        //getSupportActionBar().setIcon(R.drawable.list);

        firebaseAuth = FirebaseAuth.getInstance();

        emailAddress = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.login_progress);

        loginButton = findViewById(R.id.email_sign_in_btn);
        createAccount = findViewById(R.id.create_acc_btn_login);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this , CreateAccountActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginEmailPasswordUser(emailAddress.getText().toString().trim(),password.getText().toString().trim());

            }
        });



    }

    private void loginEmailPasswordUser(String email, String pass) {

        progressBar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){

            firebaseAuth.signInWithEmailAndPassword(email , pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            assert user != null;
                            String currentUserId = user.getUid();

                            collectionReference.whereEqualTo("userId", currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
//                                            if(error != null) {
//
//                                            }
                                            assert queryDocumentSnapshots != null;
                                            if(!queryDocumentSnapshots.isEmpty()){

                                                progressBar.setVisibility(View.INVISIBLE);
                                                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                                                {
                                                TaskApi taskApi = TaskApi.getInstance();
                                                taskApi.setUsername(snapshot.getString("userName"));
                                                taskApi.setUserId(snapshot.getString("userId"));

                                                //goto all task activity
                                                startActivity(new Intent(LoginActivity.this , AllTasksActivity.class));

                                                }

                                            }
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });

        }else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this, "Please Enter Email And Password!" , Toast.LENGTH_LONG).show();
        }
    }
}