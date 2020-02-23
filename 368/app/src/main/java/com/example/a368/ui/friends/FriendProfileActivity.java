package com.example.a368.ui.friends;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a368.R;

public class FriendProfileActivity extends AppCompatActivity {

    TextView name;
    TextView email;
    Button removeFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        removeFriend = findViewById(R.id.btRemoveFriend);

        name.setText(getIntent().getStringExtra("name"));
        email.setText(getIntent().getStringExtra("email"));

        removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FriendProfileActivity.this, "Friend Removed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
