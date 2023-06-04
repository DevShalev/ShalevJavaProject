package com.example.shalevjavaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import model.Task;
import ui.TaskRecyclerAdapter;
import utill.TaskApi;

public class AllTasksActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private List<Task> taskList;
    private RecyclerView recyclerView;
    private TaskRecyclerAdapter taskRecyclerAdapter;
    private CollectionReference collectionReference = db.collection("Task");
    private TextView noTaskEntry; // if not tasks added , show text no tasks .. please add..


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        getSupportActionBar().setElevation(0);
        //getSupportActionBar().hide();
        getSupportActionBar().setTitle("שלו | ניהול משימות.");
        getSupportActionBar().setSubtitle("הוסף שמור ונהל משימות יומיות.");
        //getSupportActionBar().setIcon(R.drawable.list);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        noTaskEntry = findViewById(R.id.list_no_tasks);
        taskList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_add:
                //take users to add task
                if(user != null && firebaseAuth != null){
                    startActivity(new Intent(AllTasksActivity.this , TodoListsActivity.class));
                    finish();
                }
                break;
            case R.id.action_signout:
                //sign the user out!
                if(user != null && firebaseAuth != null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(AllTasksActivity.this , LoginActivity.class));
                    finish();
                }
                break;
            case R.id.delete_info:
                Toast.makeText(this,"!* לחיצה ארוכה על משימה כדי למחוק *!" ,Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId" , TaskApi.getInstance().getUserId()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot tasks : queryDocumentSnapshots){
                                Task task = tasks.toObject(Task.class);
                                taskList.add(task);
                            }
                            taskRecyclerAdapter = new TaskRecyclerAdapter(AllTasksActivity.this , taskList);
                            recyclerView.setAdapter(taskRecyclerAdapter);
                            taskRecyclerAdapter.notifyDataSetChanged();

                        }else{
                            noTaskEntry.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        //finish();
//    }
}