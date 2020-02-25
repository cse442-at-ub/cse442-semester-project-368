package com.example.a368.ui.friends;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a368.R;

import java.util.ArrayList;

public class FriendProfileActivity extends AppCompatActivity {

    TextView name;
    TextView email;
    RecyclerView availableTimes;
    RecyclerView.LayoutManager layoutManager;
    AvailableTimesAdapter mAdapter;
    Button removeFriend;
    ArrayList<String> list = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        availableTimes = findViewById(R.id.availableTimes);
        removeFriend = findViewById(R.id.btRemoveFriend);

        name.setText(getIntent().getStringExtra("name"));
        email.setText(getIntent().getStringExtra("email"));


        list.add("1:00pm-2:00pm\nAndroid Programming Tutorials");

        list.add("3:00pm-4:00pm\n442 Lecture");
        list.add("5:00pm-6:00pm\nHangout with girlfriend during the day");
        list.add("7:00pm-8:00pm\nHangout with 2nd girlfriend");

        mAdapter = new AvailableTimesAdapter(list, FriendProfileActivity.this);
        layoutManager = new LinearLayoutManager(FriendProfileActivity.this);

        availableTimes.setLayoutManager(layoutManager);
        availableTimes.setAdapter(mAdapter);




        removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FriendProfileActivity.this, "Friend Removed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
