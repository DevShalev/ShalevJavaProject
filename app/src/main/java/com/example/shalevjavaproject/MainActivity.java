package com.example.shalevjavaproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button getStartedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setElevation(0);
        //getSupportActionBar().hide();
        getSupportActionBar().setTitle("שלו | ניהול משימות.");
        getSupportActionBar().setSubtitle("הוסף שמור ונהל משימות יומיות.");
        //getSupportActionBar().setIcon(R.drawable.list);



        getStartedBtn = findViewById(R.id.startBtn);

        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we go to LoginActivity
                startActivity(new Intent(MainActivity.this , LoginActivity.class));

            }
        });

    }
}