package com.example.a368.ui.appointment_meeting;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a368.R;

import java.util.ArrayList;

public class createAppointment extends AppCompatActivity {

    RecyclerView availableTimes;
    RecyclerView.LayoutManager layoutManager;
    MeetingTimesAdapter mAdapter;
    ArrayList<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        availableTimes = findViewById(R.id.availableTimes);

        list.add("8:00am-9:00am");
        list.add("9:00am-10:00am");
        list.add("11:00am-12:00pm");
        list.add("12:00pm-1:00pm");
        list.add("1:00pm-2:00pm");
        list.add("2:00pm-3:00pm");
        list.add("3:00pm-4:00pm");
        list.add("4:00pm-5:00pm");
        list.add("6:00pm-7:00pm");

        mAdapter = new MeetingTimesAdapter(list, this);
        layoutManager = new LinearLayoutManager(this);

        availableTimes.setLayoutManager(layoutManager);
        availableTimes.setAdapter(mAdapter);
    }

}
