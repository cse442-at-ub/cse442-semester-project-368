package com.example.a368.ui.today;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.a368.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

public class TodayFragment extends Fragment {

    private TodayViewModel todayViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        todayViewModel =
                ViewModelProviders.of(this).get(TodayViewModel.class);
        View root = inflater.inflate(R.layout.today_schedule, container, false);

        TextView remaining_time = (TextView)root.findViewById(R.id.remaining_time_textview);
        FloatingActionButton fab = (FloatingActionButton)root.findViewById(R.id.fabAddAppointment);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddToSchedule.class));
            }
        });


        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        remaining_time.setText(currentDateTimeString);

        return root;
    }
}