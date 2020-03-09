package com.example.a368.ui.today;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a368.R;
import com.example.a368.ui.ScheduleData;

/*
Created by: Dave Rodrigues
Modified by: Jiwon Choi
Activity that allows users to add their daily schedule.
 */

public class AddToSchedule extends AppCompatActivity {

    private ScheduleData scheduleData;
    private TextView startDate, startTime, endDate, endTime;
    private int year, month, day, hour, minute;
    private Boolean startDateClicked = false, startTimeClicked = false, endDateClicked = false, endTimeClicked = false;
    private EditText title, location, description;

    // Add customized menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_action_menu_bar, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_schedule);

        // Customize action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Daily Schedule");
        actionBar.setDisplayHomeAsUpEnabled(false);

//        Button btCreate = findViewById(R.id.btCreateAppointment);
//
//        btCreate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(AddToSchedule.this, "Appointment Created", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });

    }
}
